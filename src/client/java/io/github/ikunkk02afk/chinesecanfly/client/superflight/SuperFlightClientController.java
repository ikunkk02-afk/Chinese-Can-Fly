package io.github.ikunkk02afk.chinesecanfly.client.superflight;

import io.github.ikunkk02afk.chinesecanfly.ability.superflight.SuperFlightMotion;
import io.github.ikunkk02afk.chinesecanfly.ability.superflight.SuperFlightEligibility;
import io.github.ikunkk02afk.chinesecanfly.ability.superflight.SuperFlightTuning;
import io.github.ikunkk02afk.chinesecanfly.network.SonicBoomPayload;
import io.github.ikunkk02afk.chinesecanfly.network.SuperFlightIntentPayload;
import io.github.ikunkk02afk.chinesecanfly.network.SuperFlightStatePayload;
import io.github.ikunkk02afk.chinesecanfly.registry.ModComponents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/** Client input, short-lived prediction, and visual state. All authority remains on the server. */
public final class SuperFlightClientController {
    private static final Map<UUID, VisualState> VISUAL_STATES = new HashMap<>();
    private static boolean lastSentIntent;
    private static boolean predictionActive;
    private static double predictedSpeed;
    private static Vec3d predictedDirection = Vec3d.ZERO;

    private SuperFlightClientController() {
    }

    public static void register() {
        SuperFlightAnimationController.register();
        ClientTickEvents.END_CLIENT_TICK.register(SuperFlightClientController::tick);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> clear());
        ClientPlayNetworking.registerGlobalReceiver(SuperFlightStatePayload.ID,
                (payload, context) -> context.client().execute(() -> applyState(payload)));
        ClientPlayNetworking.registerGlobalReceiver(SonicBoomPayload.ID,
                (payload, context) -> context.client().execute(() -> SuperFlightEffects.triggerSonicBoom(
                        context.client(), payload.position(), payload.direction())));
    }

    public static double getFovBonus(float tickDelta) {
        return SuperFlightFovController.getBonus(tickDelta);
    }

    private static void tick(MinecraftClient client) {
        ClientPlayerEntity localPlayer = client.player;
        if (localPlayer == null || client.world == null) {
            clear();
            return;
        }

        boolean inputActive = canContinueRequest(localPlayer, client);
        if (inputActive != lastSentIntent) {
            ClientPlayNetworking.send(new SuperFlightIntentPayload(inputActive));
            lastSentIntent = inputActive;
            if (inputActive) {
                predictionActive = true;
                predictedSpeed = SuperFlightTuning.INITIAL_SPEED;
                predictedDirection = localPlayer.getRotationVector().normalize();
            }
        }

        tickPrediction(localPlayer, inputActive);
        SuperFlightFovController.tick(predictionActive, predictedSpeed);
        tickVisuals(client, localPlayer);
    }

    private static boolean canContinueRequest(ClientPlayerEntity player, MinecraftClient client) {
        boolean requested = client.options.sprintKey.isPressed() && client.options.forwardKey.isPressed();
        if (!requested) {
            return false;
        }
        SuperFlightEligibility.Conditions conditions = new SuperFlightEligibility.Conditions(
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
        return SuperFlightEligibility.canContinueRequest(conditions, predictionActive, predictedSpeed, predictedDirection);
    }

    private static void tickPrediction(ClientPlayerEntity player, boolean inputActive) {
        if (!predictionActive) {
            return;
        }
        predictedSpeed = SuperFlightMotion.nextSpeed(predictedSpeed, inputActive);
        if (!inputActive && SuperFlightMotion.hasFinishedDecelerating(predictedSpeed)) {
            predictionActive = false;
            predictedSpeed = 0.0;
            return;
        }
        predictedDirection = SuperFlightMotion.steer(predictedDirection, player.getRotationVector(), predictedSpeed);
        player.setVelocity(predictedDirection.multiply(predictedSpeed));
    }

    private static void tickVisuals(MinecraftClient client, ClientPlayerEntity localPlayer) {
        Iterator<Map.Entry<UUID, VisualState>> iterator = VISUAL_STATES.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, VisualState> entry = iterator.next();
            if (!(client.world.getPlayerByUuid(entry.getKey()) instanceof AbstractClientPlayerEntity player)) {
                SuperFlightAnimationController.forget(entry.getKey());
                iterator.remove();
                continue;
            }
            VisualState state = entry.getValue();
            SuperFlightAnimationController.apply(player, state.active(), state.fast());
            if (state.active()) {
                double speed = player == localPlayer && predictionActive
                        ? predictedSpeed
                        : state.fast() ? SuperFlightTuning.MAX_SPEED : SuperFlightTuning.INITIAL_SPEED;
                SuperFlightEffects.emitTrail(client, player, state.fast(), speed);
            }
        }
    }

    private static void applyState(SuperFlightStatePayload payload) {
        VISUAL_STATES.put(payload.playerId(), new VisualState(payload.active(), payload.fast()));
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && payload.playerId().equals(client.player.getUuid()) && !payload.active()) {
            predictionActive = false;
            predictedSpeed = 0.0;
            SuperFlightFovController.clear();
        }
    }

    private static void clear() {
        VISUAL_STATES.clear();
        SuperFlightAnimationController.clear();
        lastSentIntent = false;
        predictionActive = false;
        predictedSpeed = 0.0;
        predictedDirection = Vec3d.ZERO;
        SuperFlightFovController.clear();
    }

    private record VisualState(boolean active, boolean fast) {
    }
}
