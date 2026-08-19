package io.github.ikunkk02afk.chinesecanfly.client.combat;

import dev.kosmx.playerAnim.api.IPlayable;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import io.github.ikunkk02afk.chinesecanfly.ChineseCanFly;
import io.github.ikunkk02afk.chinesecanfly.network.CombatAnimation;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Client-only upper-body animation layer for Stage 9/10 combat actions. */
final class ChineseCombatAnimationController {
    private static final Identifier LAYER_ID = Identifier.of(ChineseCanFly.MOD_ID, "combat_layer");
    private static final Identifier SUPER_PUNCH_ID = Identifier.of(ChineseCanFly.MOD_ID, "super_punch");
    private static final int LAYER_PRIORITY = 1_100;
    private static final int FADE_TICKS = 3;
    private static final int PUNCH_TICKS = 10;
    private static final Map<UUID, Integer> PUNCH_REMAINING = new HashMap<>();
    private static final Map<UUID, Boolean> APPLIED = new HashMap<>();

    private ChineseCombatAnimationController() {
    }

    static void register() {
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                LAYER_ID,
                LAYER_PRIORITY,
                player -> new ModifierLayer<>()
        );
    }

    static void play(UUID playerId, CombatAnimation animation) {
        if (animation == CombatAnimation.SUPER_PUNCH) {
            PUNCH_REMAINING.put(playerId, PUNCH_TICKS);
        }
    }

    static void tick(Map<UUID, ? extends AbstractClientPlayerEntity> players) {
        for (Map.Entry<UUID, Integer> entry : new HashMap<>(PUNCH_REMAINING).entrySet()) {
            AbstractClientPlayerEntity player = players.get(entry.getKey());
            if (player == null) {
                PUNCH_REMAINING.remove(entry.getKey());
                APPLIED.remove(entry.getKey());
                continue;
            }
            boolean active = entry.getValue() > 0;
            apply(player, active);
            if (active) {
                PUNCH_REMAINING.put(entry.getKey(), entry.getValue() - 1);
            } else {
                PUNCH_REMAINING.remove(entry.getKey());
            }
        }
    }

    static void forget(UUID playerId) {
        PUNCH_REMAINING.remove(playerId);
        APPLIED.remove(playerId);
    }

    static void clear() {
        PUNCH_REMAINING.clear();
        APPLIED.clear();
    }

    private static void apply(AbstractClientPlayerEntity player, boolean active) {
        UUID id = player.getUuid();
        if (Boolean.valueOf(active).equals(APPLIED.get(id))) {
            return;
        }
        ModifierLayer<IAnimation> layer = getLayer(player);
        IAnimation next = active ? createAnimation(SUPER_PUNCH_ID) : null;
        layer.replaceAnimationWithFade(
                AbstractFadeModifier.standardFadeIn(FADE_TICKS, Ease.INOUTSINE),
                next,
                true
        );
        APPLIED.put(id, active);
    }

    @SuppressWarnings("unchecked")
    private static ModifierLayer<IAnimation> getLayer(AbstractClientPlayerEntity player) {
        IAnimation animation = PlayerAnimationAccess.getPlayerAssociatedData(player).get(LAYER_ID);
        if (animation instanceof ModifierLayer<?> layer) {
            return (ModifierLayer<IAnimation>) layer;
        }
        throw new IllegalStateException("Player Animator did not create the Chinese combat animation layer");
    }

    private static IAnimation createAnimation(Identifier id) {
        IPlayable animation = PlayerAnimationRegistry.getAnimation(id);
        if (animation == null) {
            throw new IllegalStateException("Missing Player Animator resource " + id);
        }
        return animation.playAnimation();
    }
}
