package io.github.ikunkk02afk.chinesecanfly.ability.combat;

import io.github.ikunkk02afk.chinesecanfly.ability.superflight.SuperFlightManager;
import io.github.ikunkk02afk.chinesecanfly.network.CombatAnimation;
import io.github.ikunkk02afk.chinesecanfly.network.GrabRequestPayload;
import io.github.ikunkk02afk.chinesecanfly.network.HeldEntityStatePayload;
import io.github.ikunkk02afk.chinesecanfly.registry.ModComponents;
import io.github.ikunkk02afk.chinesecanfly.registry.ModEntityTypeTags;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Server-owned grab state and physical positioning for the original target entity. */
public final class HeldEntityManager {
    private static final Map<UUID, HeldEntityState> HELD_BY_HOLDER = new HashMap<>();
    private static final Map<UUID, UUID> HOLDER_BY_TARGET = new HashMap<>();

    private HeldEntityManager() {
    }

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(GrabRequestPayload.ID, (payload, context) ->
                context.server().execute(() -> handleRequest(context.player(), payload))
        );
        ServerTickEvents.END_SERVER_TICK.register(HeldEntityManager::tick);
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> releaseByHolder(handler.player.getUuid(), server, true));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> syncTo(handler.player));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) ->
                releaseByHolder(oldPlayer.getUuid(), newPlayer.getServer(), true));
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) ->
                releaseByHolder(player.getUuid(), player.getServer(), true));
    }

    public static boolean isHolding(ServerPlayerEntity player) {
        return HELD_BY_HOLDER.containsKey(player.getUuid());
    }

    public static boolean isHeldBy(ServerPlayerEntity player, LivingEntity entity) {
        HeldEntityState state = HELD_BY_HOLDER.get(player.getUuid());
        return state != null && state.targetId().equals(entity.getUuid());
    }

    public static LivingEntity getHeldEntity(ServerPlayerEntity player) {
        HeldEntityState state = HELD_BY_HOLDER.get(player.getUuid());
        if (state == null) {
            return null;
        }
        Entity entity = findEntity(player.getServer(), state.targetId());
        return entity instanceof LivingEntity living ? living : null;
    }

    public static void release(ServerPlayerEntity player, boolean zeroVelocity) {
        releaseByHolder(player.getUuid(), player.getServer(), zeroVelocity);
    }

    public static void clearFor(ServerPlayerEntity player) {
        release(player, true);
    }

    private static void handleRequest(ServerPlayerEntity player, GrabRequestPayload payload) {
        if (!hasPower(player)) {
            return;
        }
        if (isHolding(player)) {
            if (payload.safeRelease()) {
                safeReleaseHeld(player);
            } else {
                throwHeld(player);
            }
            return;
        }
        if (payload.targetEntityId() >= 0) {
            tryGrab(player, payload.targetEntityId());
        }
    }

    private static void tryGrab(ServerPlayerEntity player, int entityId) {
        Entity candidate = player.getServerWorld().getEntityById(entityId);
        if (!(candidate instanceof LivingEntity target) || !isValidGrabTarget(player, target)) {
            return;
        }

        HeldEntityState state = new HeldEntityState(player.getUuid(), target.getUuid(), target.getId(), target.hasNoGravity());
        HELD_BY_HOLDER.put(player.getUuid(), state);
        HOLDER_BY_TARGET.put(target.getUuid(), player.getUuid());
        positionHeldEntity(player, target);
        broadcastHolding(player, state, true);
        CombatAnimationManager.broadcast(player, CombatAnimation.GRAB);
    }

    private static void throwHeld(ServerPlayerEntity player) {
        LivingEntity target = getHeldEntity(player);
        if (target == null) {
            release(player, true);
            return;
        }
        positionHeldEntity(player, target);
        release(player, false);
        Vec3d velocity = player.getRotationVector().normalize().multiply(CombatTuning.THROW_SPEED)
                .add(0.0, CombatTuning.THROW_VERTICAL_SPEED, 0.0);
        target.setVelocity(velocity);
        target.fallDistance = 0.0F;
        ThrownEntityManager.track(target, player, velocity);
        CombatAnimationManager.broadcast(player, CombatAnimation.THROW);
    }

    private static void safeReleaseHeld(ServerPlayerEntity player) {
        LivingEntity target = getHeldEntity(player);
        if (target != null) {
            // Match the authority position to the current hand-side pose before the normal renderer returns.
            positionHeldEntity(player, target);
        }
        release(player, true);
    }

    private static void tick(MinecraftServer server) {
        for (UUID holderId : new ArrayList<>(HELD_BY_HOLDER.keySet())) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(holderId);
            if (player == null) {
                releaseByHolder(holderId, server, true);
                continue;
            }
            HeldEntityState state = HELD_BY_HOLDER.get(holderId);
            Entity entity = state == null ? null : findEntity(server, state.targetId());
            if (!(entity instanceof LivingEntity target) || !isStillValid(player, target)) {
                release(player, true);
                continue;
            }
            positionHeldEntity(player, target);
        }
    }

    private static boolean isValidGrabTarget(ServerPlayerEntity player, LivingEntity target) {
        if (!isStillValid(player, target)
                || target instanceof PlayerEntity
                || target.getType().isIn(ModEntityTypeTags.GRAB_IMMUNE)
                || HOLDER_BY_TARGET.containsKey(target.getUuid())
                || player.squaredDistanceTo(target) > CombatTuning.GRAB_RANGE * CombatTuning.GRAB_RANGE) {
            return false;
        }
        HitResult sight = player.getWorld().raycast(new RaycastContext(
                player.getEyePos(), target.getBoundingBox().getCenter(),
                RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player
        ));
        return sight.getType() == Type.MISS;
    }

    private static boolean isStillValid(ServerPlayerEntity player, LivingEntity target) {
        return hasPower(player)
                && player.isAlive()
                && !player.isSpectator()
                && target.isAlive()
                && !target.isRemoved()
                && !target.isSpectator()
                && target.getWorld() == player.getWorld();
    }

    private static boolean hasPower(ServerPlayerEntity player) {
        return ModComponents.CHINESE_POWER.get(player).hasChinesePower();
    }

    private static void positionHeldEntity(ServerPlayerEntity player, LivingEntity target) {
        double distance = SuperFlightManager.isActive(player) || GroundSlamManager.isActive(player)
                ? CombatTuning.SUPER_FLIGHT_HOLD_DISTANCE
                : CombatTuning.HOLD_DISTANCE;
        Vec3d position = player.getEyePos()
                .add(player.getRotationVector().normalize().multiply(distance))
                .subtract(0.0, target.getHeight() * 0.5, 0.0);
        target.refreshPositionAndAngles(position.x, position.y, position.z, target.getYaw(), target.getPitch());
        target.setVelocity(Vec3d.ZERO);
        target.fallDistance = 0.0F;
        target.setNoGravity(true);
    }

    private static void releaseByHolder(UUID holderId, MinecraftServer server, boolean zeroVelocity) {
        HeldEntityState state = HELD_BY_HOLDER.remove(holderId);
        if (state == null) {
            return;
        }
        HOLDER_BY_TARGET.remove(state.targetId());
        Entity entity = findEntity(server, state.targetId());
        if (entity instanceof LivingEntity target) {
            target.setNoGravity(state.previousNoGravity());
            target.fallDistance = 0.0F;
            if (zeroVelocity) {
                target.setVelocity(Vec3d.ZERO);
            }
        }
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(holderId);
        if (player != null) {
            broadcastHolding(player, state, false);
        }
    }

    private static void broadcastHolding(ServerPlayerEntity player, HeldEntityState state, boolean holding) {
        HeldEntityStatePayload payload = new HeldEntityStatePayload(
                player.getUuid(), state.targetId(), state.targetEntityId(), holding
        );
        Set<ServerPlayerEntity> recipients = new HashSet<>(PlayerLookup.tracking(player));
        recipients.add(player);
        for (ServerPlayerEntity recipient : recipients) {
            ServerPlayNetworking.send(recipient, payload);
        }
    }

    private static void syncTo(ServerPlayerEntity recipient) {
        for (UUID holderId : HELD_BY_HOLDER.keySet()) {
            ServerPlayerEntity holder = recipient.getServer().getPlayerManager().getPlayer(holderId);
            if (holder != null && holder.getWorld() == recipient.getWorld()) {
                HeldEntityState state = HELD_BY_HOLDER.get(holderId);
                if (state != null) {
                    ServerPlayNetworking.send(recipient, new HeldEntityStatePayload(
                            holderId, state.targetId(), state.targetEntityId(), true
                    ));
                }
            }
        }
    }

    private static Entity findEntity(MinecraftServer server, UUID uuid) {
        for (var world : server.getWorlds()) {
            Entity entity = world.getEntity(uuid);
            if (entity != null) {
                return entity;
            }
        }
        return null;
    }
}
