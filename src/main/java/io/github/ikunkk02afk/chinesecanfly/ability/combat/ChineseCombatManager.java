package io.github.ikunkk02afk.chinesecanfly.ability.combat;

import io.github.ikunkk02afk.chinesecanfly.network.CombatAnimation;
import net.minecraft.server.network.ServerPlayerEntity;

/** Coordinates runtime-only Stage 10 managers and reset cleanup. */
public final class ChineseCombatManager {
    private ChineseCombatManager() {
    }

    public static void register() {
        HeldEntityManager.register();
        ThrownEntityManager.register();
        GroundSlamManager.register();
    }

    public static void clearForReset(ServerPlayerEntity player) {
        GroundSlamManager.clearFor(player);
        HeldEntityManager.clearFor(player);
        ThrownEntityManager.clearFor(player);
        CombatAnimationManager.broadcast(player, CombatAnimation.CLEAR);
    }
}
