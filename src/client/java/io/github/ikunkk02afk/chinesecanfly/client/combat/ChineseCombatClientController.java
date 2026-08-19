package io.github.ikunkk02afk.chinesecanfly.client.combat;

import io.github.ikunkk02afk.chinesecanfly.network.CombatAnimationPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Receives server-authoritative combat animation events and applies them to tracked players. */
public final class ChineseCombatClientController {
    private ChineseCombatClientController() {
    }

    public static void register() {
        ChineseCombatAnimationController.register();
        ClientPlayNetworking.registerGlobalReceiver(CombatAnimationPayload.ID,
                (payload, context) -> context.client().execute(() ->
                        ChineseCombatAnimationController.play(payload.playerId(), payload.animation())));
        ClientTickEvents.END_CLIENT_TICK.register(ChineseCombatClientController::tick);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ChineseCombatAnimationController.clear());
    }

    private static void tick(MinecraftClient client) {
        if (client.world == null) {
            ChineseCombatAnimationController.clear();
            return;
        }
        Map<UUID, AbstractClientPlayerEntity> players = new HashMap<>();
        for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
            players.put(player.getUuid(), player);
        }
        ChineseCombatAnimationController.tick(players);
    }
}
