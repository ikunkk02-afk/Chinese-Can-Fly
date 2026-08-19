package io.github.ikunkk02afk.chinesecanfly.client.combat;

import io.github.ikunkk02afk.chinesecanfly.ability.combat.CombatTuning;
import io.github.ikunkk02afk.chinesecanfly.network.CombatAnimationPayload;
import io.github.ikunkk02afk.chinesecanfly.network.GrabRequestPayload;
import io.github.ikunkk02afk.chinesecanfly.network.GroundSlamAction;
import io.github.ikunkk02afk.chinesecanfly.network.GroundSlamRequestPayload;
import io.github.ikunkk02afk.chinesecanfly.network.GroundSlamStatePayload;
import io.github.ikunkk02afk.chinesecanfly.network.HeldEntityStatePayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Client input, raycast selection, visual state, and short local ground-slam prediction. */
public final class ChineseCombatClientController {
    private static final KeyBinding GRAB_THROW_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.chinese_can_fly.grab_throw", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G,
            "key.category.chinese_can_fly.combat"
    ));
    private static final KeyBinding GROUND_SLAM_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.chinese_can_fly.ground_slam", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_X,
            "key.category.chinese_can_fly.combat"
    ));
    private static boolean localSlamPrediction;

    private ChineseCombatClientController() {
    }

    public static void register() {
        ChineseCombatAnimationController.register();
        ClientPlayNetworking.registerGlobalReceiver(CombatAnimationPayload.ID,
                (payload, context) -> context.client().execute(() ->
                        ChineseCombatAnimationController.play(payload.playerId(), payload.animation())));
        ClientPlayNetworking.registerGlobalReceiver(HeldEntityStatePayload.ID,
                (payload, context) -> context.client().execute(() -> {
                    HeldEntityVisualManager.applyState(payload);
                    ChineseCombatAnimationController.setHolding(payload.holderId(), payload.holding());
                }));
        ClientPlayNetworking.registerGlobalReceiver(GroundSlamStatePayload.ID,
                (payload, context) -> context.client().execute(() -> applyGroundSlam(context.client(), payload)));
        ClientTickEvents.END_CLIENT_TICK.register(ChineseCombatClientController::tick);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> clear());
        HeldEntityVisualManager.register();
    }

    private static void tick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null) {
            clear();
            return;
        }
        while (GRAB_THROW_KEY.wasPressed()) {
            ClientPlayNetworking.send(new GrabRequestPayload(findGrabTarget(player), player.isSneaking()));
        }
        while (GROUND_SLAM_KEY.wasPressed()) {
            ClientPlayNetworking.send(GroundSlamRequestPayload.INSTANCE);
        }
        if (localSlamPrediction) {
            Vec3d velocity = player.getVelocity();
            player.setVelocity(velocity.x * 0.15, -CombatTuning.SLAM_DESCENT_SPEED, velocity.z * 0.15);
        }

        Map<UUID, AbstractClientPlayerEntity> players = new HashMap<>();
        for (AbstractClientPlayerEntity tracked : client.world.getPlayers()) {
            players.put(tracked.getUuid(), tracked);
        }
        ChineseCombatAnimationController.tick(players);
    }

    private static int findGrabTarget(ClientPlayerEntity player) {
        Vec3d start = player.getEyePos();
        Vec3d end = start.add(player.getRotationVector().normalize().multiply(CombatTuning.GRAB_RANGE));
        Box search = player.getBoundingBox().stretch(end.subtract(start)).expand(1.0);
        EntityHitResult result = ProjectileUtil.raycast(player, start, end, search,
                candidate -> candidate instanceof LivingEntity
                        && candidate != player
                        && !candidate.isSpectator(),
                CombatTuning.GRAB_RANGE * CombatTuning.GRAB_RANGE);
        return result == null ? -1 : result.getEntity().getId();
    }

    private static void applyGroundSlam(MinecraftClient client, GroundSlamStatePayload payload) {
        ChineseCombatAnimationController.applyGroundSlam(payload.playerId(), payload.action());
        if (client.player != null && client.player.getUuid().equals(payload.playerId())) {
            localSlamPrediction = payload.action() == GroundSlamAction.START;
        }
    }

    private static void clear() {
        localSlamPrediction = false;
        ChineseCombatAnimationController.clear();
        HeldEntityVisualManager.clear();
    }
}
