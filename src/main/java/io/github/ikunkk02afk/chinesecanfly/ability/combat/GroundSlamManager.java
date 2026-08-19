package io.github.ikunkk02afk.chinesecanfly.ability.combat;

import io.github.ikunkk02afk.chinesecanfly.ability.superflight.SuperFlightManager;
import io.github.ikunkk02afk.chinesecanfly.network.GroundSlamAction;
import io.github.ikunkk02afk.chinesecanfly.network.GroundSlamRequestPayload;
import io.github.ikunkk02afk.chinesecanfly.network.GroundSlamStatePayload;
import io.github.ikunkk02afk.chinesecanfly.registry.ModComponents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Server-authoritative held-entity descent controller and impact resolver. */
public final class GroundSlamManager {
    private static final Map<UUID, GroundSlamState> ACTIVE = new HashMap<>();

    private GroundSlamManager() {
    }

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(GroundSlamRequestPayload.ID, (payload, context) ->
                context.server().execute(() -> start(context.player()))
        );
        ServerTickEvents.START_SERVER_TICK.register(GroundSlamManager::tick);
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> cancelById(handler.player.getUuid(), server, true));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> syncTo(handler.player));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) ->
                cancelById(oldPlayer.getUuid(), newPlayer.getServer(), true));
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) ->
                cancelById(player.getUuid(), player.getServer(), true));
    }

    public static boolean isActive(ServerPlayerEntity player) {
        return ACTIVE.containsKey(player.getUuid());
    }

    public static void clearFor(ServerPlayerEntity player) {
        cancelById(player.getUuid(), player.getServer(), true);
    }

    private static void start(ServerPlayerEntity player) {
        if (ACTIVE.containsKey(player.getUuid())
                || !hasPower(player)
                || !HeldEntityManager.isHolding(player)
                || !player.isAlive()
                || player.isSpectator()
                || player.isOnGround()
                || player.isTouchingWater()
                || player.isInLava()) {
            return;
        }
        SuperFlightManager.stopNow(player);
        ACTIVE.put(player.getUuid(), new GroundSlamState(player.getY()));
        broadcast(player, GroundSlamAction.START);
    }

    private static void tick(MinecraftServer server) {
        for (UUID playerId : new ArrayList<>(ACTIVE.keySet())) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
            GroundSlamState state = ACTIVE.get(playerId);
            if (player == null || state == null) {
                cancelById(playerId, server, true);
                continue;
            }
            LivingEntity primaryTarget = HeldEntityManager.getHeldEntity(player);
            if (primaryTarget == null || !hasPower(player) || !player.isAlive() || player.isSpectator()
                    || player.isTouchingWater() || player.isInLava()
                    || state.incrementAndGetElapsedTicks() > CombatTuning.MAX_SLAM_DURATION_TICKS) {
                cancelById(playerId, server, true);
                continue;
            }

            Vec3d currentVelocity = player.getVelocity();
            Vec3d slamVelocity = new Vec3d(currentVelocity.x * 0.15, -CombatTuning.SLAM_DESCENT_SPEED,
                    currentVelocity.z * 0.15);
            GroundSlamCollisionProbe.Result collision = GroundSlamCollisionProbe.probe(player, slamVelocity);
            if (collision.kind() == GroundSlamCollisionProbe.Kind.FLUID) {
                cancelById(playerId, server, true);
                continue;
            }
            if (collision.kind() == GroundSlamCollisionProbe.Kind.IMPACT) {
                player.refreshPositionAndAngles(collision.safePosition().x, collision.safePosition().y,
                        collision.safePosition().z, player.getYaw(), player.getPitch());
                player.setVelocity(Vec3d.ZERO);
                player.fallDistance = 0.0F;
                resolveImpact(player, primaryTarget, state, collision.impactPosition());
                ACTIVE.remove(playerId);
                HeldEntityManager.release(player, true);
                broadcast(player, GroundSlamAction.IMPACT);
                continue;
            }
            player.setVelocity(slamVelocity);
            player.fallDistance = 0.0F;
        }
    }

    private static void resolveImpact(ServerPlayerEntity player, LivingEntity primaryTarget, GroundSlamState state,
                                      Vec3d impact) {
        ServerWorld world = player.getServerWorld();
        double distance = CombatMath.clampedSlamDistance(state.startY() - impact.y);
        primaryTarget.damage(CombatDamageTypes.causedBy(primaryTarget, CombatDamageTypes.GROUND_SLAM, player),
                CombatMath.slamPrimaryDamage(distance));

        Box area = new Box(impact.x, impact.y, impact.z, impact.x, impact.y, impact.z)
                .expand(CombatTuning.SLAM_AOE_RADIUS);
        for (Entity entity : world.getOtherEntities(player, area, candidate -> candidate instanceof LivingEntity)) {
            if (!(entity instanceof LivingEntity target) || target == primaryTarget || !target.isAlive() || target.isSpectator()) {
                continue;
            }
            target.damage(CombatDamageTypes.causedBy(target, CombatDamageTypes.GROUND_SLAM, player),
                    (float) CombatTuning.SLAM_AOE_DAMAGE);
            Vec3d direction = target.getPos().subtract(impact);
            Vec3d horizontal = new Vec3d(direction.x, 0.0, direction.z);
            if (horizontal.lengthSquared() < 1.0E-6) {
                horizontal = new Vec3d(0.0, 0.0, 1.0);
            } else {
                horizontal = horizontal.normalize();
            }
            target.takeKnockback(CombatTuning.SLAM_AOE_KNOCKBACK, horizontal.x, horizontal.z);
        }
        GroundSlamBlockBreaker.createImpact(player, impact, distance);
    }

    private static boolean hasPower(ServerPlayerEntity player) {
        return ModComponents.CHINESE_POWER.get(player).hasChinesePower();
    }

    private static void cancelById(UUID playerId, MinecraftServer server, boolean releaseHeld) {
        if (ACTIVE.remove(playerId) == null) {
            return;
        }
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
        if (player != null) {
            player.setVelocity(Vec3d.ZERO);
            player.fallDistance = 0.0F;
            if (releaseHeld) {
                HeldEntityManager.release(player, true);
            }
            broadcast(player, GroundSlamAction.CANCEL);
        }
    }

    private static void broadcast(ServerPlayerEntity player, GroundSlamAction action) {
        GroundSlamStatePayload payload = new GroundSlamStatePayload(player.getUuid(), action);
        Set<ServerPlayerEntity> recipients = new HashSet<>(PlayerLookup.tracking(player));
        recipients.add(player);
        for (ServerPlayerEntity recipient : recipients) {
            ServerPlayNetworking.send(recipient, payload);
        }
    }

    private static void syncTo(ServerPlayerEntity recipient) {
        for (UUID playerId : ACTIVE.keySet()) {
            ServerPlayerEntity player = recipient.getServer().getPlayerManager().getPlayer(playerId);
            if (player != null && player.getWorld() == recipient.getWorld()) {
                ServerPlayNetworking.send(recipient, new GroundSlamStatePayload(playerId, GroundSlamAction.START));
            }
        }
    }
}
