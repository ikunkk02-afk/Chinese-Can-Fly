package io.github.ikunkk02afk.chinesecanfly.ability.superflight;

import io.github.ikunkk02afk.chinesecanfly.network.SonicBoomPayload;
import io.github.ikunkk02afk.chinesecanfly.network.SuperFlightIntentPayload;
import io.github.ikunkk02afk.chinesecanfly.network.SuperFlightStatePayload;
import io.github.ikunkk02afk.chinesecanfly.registry.ModComponents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * The server-authoritative Stage 7/8 velocity controller. It deliberately owns no persistent player data.
 */
public final class SuperFlightManager {
    private static final Map<UUID, SuperFlightState> ACTIVE_FLIGHTS = new HashMap<>();

    private SuperFlightManager() {
    }

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(SuperFlightIntentPayload.ID, (payload, context) ->
                context.server().execute(() -> handleIntent(context.player(), payload.active()))
        );
        ServerTickEvents.END_SERVER_TICK.register(SuperFlightManager::tick);
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> stopNow(handler.player));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> syncActiveFlightsTo(handler.player));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> stopNow(newPlayer));
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> stopNow(player));
    }

    public static boolean isActive(ServerPlayerEntity player) {
        return ACTIVE_FLIGHTS.containsKey(player.getUuid());
    }

    public static void stopNow(ServerPlayerEntity player) {
        if (ACTIVE_FLIGHTS.remove(player.getUuid()) != null) {
            player.setVelocity(Vec3d.ZERO);
            broadcastState(player, false, false);
        }
    }

    private static void handleIntent(ServerPlayerEntity player, boolean active) {
        SuperFlightState state = ACTIVE_FLIGHTS.get(player.getUuid());
        if (!active) {
            if (state != null) {
                state.setRequested(false);
            }
            return;
        }

        if (!canStart(player)) {
            sendState(player, new SuperFlightStatePayload(player.getUuid(), false, false));
            return;
        }
        if (state == null) {
            ACTIVE_FLIGHTS.put(player.getUuid(), new SuperFlightState(player.getRotationVector()));
            broadcastState(player, true, false);
        } else {
            state.setRequested(true);
        }
    }

    private static void tick(MinecraftServer server) {
        if (ACTIVE_FLIGHTS.isEmpty()) {
            return;
        }

        Set<UUID> expired = new HashSet<>();
        for (Map.Entry<UUID, SuperFlightState> entry : ACTIVE_FLIGHTS.entrySet()) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
            if (player == null) {
                expired.add(entry.getKey());
                continue;
            }
            tickPlayer(player, entry.getValue(), expired);
        }
        expired.forEach(ACTIVE_FLIGHTS::remove);
    }

    private static void tickPlayer(ServerPlayerEntity player, SuperFlightState state, Set<UUID> expired) {
        if (!canContinue(player)) {
            player.setVelocity(Vec3d.ZERO);
            expired.add(player.getUuid());
            broadcastState(player, false, false);
            return;
        }
        state.tickTunnelSoundCooldown();

        Vec3d realVelocity = player.getVelocity();
        if (realVelocity.length() > SuperFlightTuning.MAX_SERVER_SPEED) {
            player.setVelocity(realVelocity.normalize().multiply(SuperFlightTuning.MAX_SERVER_SPEED));
        }

        double previousSpeed = state.speed();
        double nextSpeed = SuperFlightMotion.nextSpeed(previousSpeed, state.requested());
        if (!state.requested() && SuperFlightMotion.hasFinishedDecelerating(nextSpeed)) {
            player.setVelocity(state.direction().multiply(SuperFlightTuning.EXIT_SPEED));
            expired.add(player.getUuid());
            broadcastState(player, false, false);
            return;
        }

        state.setDirection(SuperFlightMotion.steer(state.direction(), player.getRotationVector(), nextSpeed));
        Vec3d velocity = state.direction().multiply(nextSpeed);
        SuperFlightPathResult initialPath = SuperFlightCollisionProbe.probe(player, velocity, nextSpeed);
        if (initialPath.isClear()) {
            movePlayer(player, state, velocity, nextSpeed, previousSpeed);
            return;
        }
        if (initialPath.status() == SuperFlightPathStatus.UNLOADED_CHUNK) {
            stopForUnloadedPath(player, expired);
            return;
        }
        if (nextSpeed < SuperFlightTuning.MIN_TUNNEL_SPEED) {
            stopForCollision(player, state, expired, null);
            return;
        }

        SuperFlightBlockBreaker.BreakResult breakResult = SuperFlightBlockBreaker.breakCandidates(
                player, initialPath.breakCandidates(), nextSpeed
        );
        if (breakResult.madeProgress()) {
            SuperFlightBlockBreaker.emitBreakFeedback(
                    player, breakResult.broken(), state.direction(), state.consumeTunnelSound()
            );
        }
        double tunneledSpeed = Math.max(0.0, nextSpeed - SuperFlightTuning.tunnelSpeedLoss(breakResult.totalResistance()));
        Vec3d tunneledVelocity = state.direction().multiply(tunneledSpeed);
        SuperFlightPathResult finalPath = SuperFlightCollisionProbe.probe(player, tunneledVelocity, tunneledSpeed);
        if (finalPath.isClear()) {
            state.clearTunnelStall();
            movePlayer(player, state, tunneledVelocity, tunneledSpeed, previousSpeed);
            return;
        }
        if (finalPath.status() == SuperFlightPathStatus.UNLOADED_CHUNK) {
            stopForUnloadedPath(player, expired);
            return;
        }
        if (breakResult.budgetExhausted()) {
            pauseForRemainingTunnel(player, state, tunneledSpeed, breakResult.madeProgress(), expired);
            return;
        }
        if (finalPath.status() == SuperFlightPathStatus.UNBREAKABLE_OBSTRUCTION) {
            stopForCollision(player, state, expired, finalPath.hardBlocker());
            return;
        }
        if (tunneledSpeed < SuperFlightTuning.MIN_TUNNEL_SPEED) {
            stopForCollision(player, state, expired, null);
            return;
        }
        pauseForRemainingTunnel(player, state, tunneledSpeed, breakResult.madeProgress(), expired);
    }

    private static void pauseForRemainingTunnel(ServerPlayerEntity player, SuperFlightState state, double speed,
                                                boolean madeProgress, Set<UUID> expired) {
        player.setVelocity(Vec3d.ZERO);
        player.fallDistance = 0.0F;
        state.setSpeed(speed);
        if (state.updateFast()) {
            broadcastState(player, true, state.fast());
        }
        if (state.recordTunnelProgress(madeProgress)) {
            expired.add(player.getUuid());
            broadcastState(player, false, false);
        }
    }

    private static void movePlayer(ServerPlayerEntity player, SuperFlightState state, Vec3d velocity,
                                   double speed, double previousSpeed) {
        state.setSpeed(speed);
        player.setVelocity(velocity);
        player.fallDistance = 0.0F;
        if (state.updateFast()) {
            broadcastState(player, true, state.fast());
        }
        if (state.consumeSonicCrossing(previousSpeed)) {
            broadcastSonicBoom(player, state.direction());
        }
    }

    private static void stopForUnloadedPath(ServerPlayerEntity player, Set<UUID> expired) {
        player.setVelocity(Vec3d.ZERO);
        expired.add(player.getUuid());
        broadcastState(player, false, false);
    }

    private static void stopForCollision(ServerPlayerEntity player, SuperFlightState state, Set<UUID> expired,
                                         SuperFlightBlockCandidate hardBlocker) {
        if (hardBlocker == null) {
            player.setVelocity(Vec3d.ZERO);
        } else {
            SuperFlightBlockBreaker.emitHardWallFeedback(player, hardBlocker, state.direction());
            Vec3d recoil = state.direction().negate().multiply(SuperFlightTuning.UNBREAKABLE_RECOIL_SPEED);
            SuperFlightPathResult recoilPath = SuperFlightCollisionProbe.probe(player, recoil, 0.0);
            player.setVelocity(recoilPath.isClear() ? recoil : Vec3d.ZERO);
        }
        player.fallDistance = 0.0F;
        expired.add(player.getUuid());
        broadcastState(player, false, false);
    }

    private static boolean canStart(ServerPlayerEntity player) {
        return SuperFlightEligibility.canStart(conditionsFor(player));
    }

    private static boolean canContinue(ServerPlayerEntity player) {
        return SuperFlightEligibility.canStart(conditionsFor(player));
    }

    private static SuperFlightEligibility.Conditions conditionsFor(ServerPlayerEntity player) {
        return new SuperFlightEligibility.Conditions(
                ModComponents.CHINESE_POWER.get(player).hasChinesePower(),
                player.isAlive(),
                player.isSpectator(),
                player.hasVehicle(),
                player.isFallFlying(),
                player.isTouchingWater(),
                player.isInLava(),
                player.isOnGround(),
                player.getAbilities().allowFlying,
                player.getAbilities().flying
        );
    }

    private static void syncActiveFlightsTo(ServerPlayerEntity recipient) {
        for (Map.Entry<UUID, SuperFlightState> entry : ACTIVE_FLIGHTS.entrySet()) {
            ServerPlayerEntity player = recipient.getServer().getPlayerManager().getPlayer(entry.getKey());
            if (player != null && player.getWorld() == recipient.getWorld()) {
                sendState(recipient, new SuperFlightStatePayload(player.getUuid(), true, entry.getValue().fast()));
            }
        }
    }

    private static void broadcastState(ServerPlayerEntity player, boolean active, boolean fast) {
        SuperFlightStatePayload payload = new SuperFlightStatePayload(player.getUuid(), active, fast);
        Set<UUID> sent = new HashSet<>();
        for (ServerPlayerEntity watcher : PlayerLookup.tracking(player)) {
            sendState(watcher, payload);
            sent.add(watcher.getUuid());
        }
        if (sent.add(player.getUuid())) {
            sendState(player, payload);
        }
    }

    private static void broadcastSonicBoom(ServerPlayerEntity player, Vec3d direction) {
        SonicBoomPayload payload = new SonicBoomPayload(player.getUuid(), player.getPos(), direction);
        for (ServerPlayerEntity watcher : PlayerLookup.around(player.getServerWorld(), player.getPos(), SuperFlightTuning.SONIC_RADIUS)) {
            sendState(watcher, payload);
        }
    }

    private static void sendState(ServerPlayerEntity recipient, net.minecraft.network.packet.CustomPayload payload) {
        ServerPlayNetworking.send(recipient, payload);
    }
}
