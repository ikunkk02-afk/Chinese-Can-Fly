package io.github.ikunkk02afk.chinesecanfly.ability.combat;

import io.github.ikunkk02afk.chinesecanfly.network.CombatAnimation;
import io.github.ikunkk02afk.chinesecanfly.network.CombatAnimationPayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashSet;
import java.util.Set;

/** Sends only visual state transitions; entity movement remains vanilla server synchronization. */
public final class CombatAnimationManager {
    private CombatAnimationManager() {
    }

    public static void broadcast(ServerPlayerEntity player, CombatAnimation animation) {
        CombatAnimationPayload payload = new CombatAnimationPayload(player.getUuid(), animation);
        Set<ServerPlayerEntity> recipients = new HashSet<>(PlayerLookup.tracking(player));
        recipients.add(player);
        for (ServerPlayerEntity recipient : recipients) {
            ServerPlayNetworking.send(recipient, payload);
        }
    }
}
