package io.github.ikunkk02afk.chinesecanfly.client.superflight;

import dev.kosmx.playerAnim.api.IPlayable;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import io.github.ikunkk02afk.chinesecanfly.ChineseCanFly;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Manages one Player Animator layer for every locally known player entity. */
final class SuperFlightAnimationController {
    private static final Identifier LAYER_ID = Identifier.of(ChineseCanFly.MOD_ID, "super_flight_layer");
    private static final Identifier CRUISE_ANIMATION_ID = Identifier.of(ChineseCanFly.MOD_ID, "super_fly");
    private static final Identifier FAST_ANIMATION_ID = Identifier.of(ChineseCanFly.MOD_ID, "super_fly_fast");
    private static final int LAYER_PRIORITY = 1_000;
    private static final int FADE_TICKS = 6;
    private static final Map<UUID, AppliedState> APPLIED_STATES = new HashMap<>();

    private SuperFlightAnimationController() {
    }

    static void register() {
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                LAYER_ID,
                LAYER_PRIORITY,
                player -> new ModifierLayer<>()
        );
    }

    static void apply(AbstractClientPlayerEntity player, boolean active, boolean fast) {
        UUID id = player.getUuid();
        AppliedState target = new AppliedState(active, fast);
        if (target.equals(APPLIED_STATES.get(id))) {
            return;
        }

        ModifierLayer<IAnimation> layer = getLayer(player);
        IAnimation next = active ? createAnimation(fast ? FAST_ANIMATION_ID : CRUISE_ANIMATION_ID) : null;
        layer.replaceAnimationWithFade(
                AbstractFadeModifier.standardFadeIn(FADE_TICKS, Ease.INOUTSINE),
                next,
                true
        );
        APPLIED_STATES.put(id, target);
    }

    static void forget(UUID playerId) {
        APPLIED_STATES.remove(playerId);
    }

    static void clear() {
        APPLIED_STATES.clear();
    }

    @SuppressWarnings("unchecked")
    private static ModifierLayer<IAnimation> getLayer(AbstractClientPlayerEntity player) {
        IAnimation animation = PlayerAnimationAccess.getPlayerAssociatedData(player).get(LAYER_ID);
        if (animation instanceof ModifierLayer<?> layer) {
            return (ModifierLayer<IAnimation>) layer;
        }
        throw new IllegalStateException("Player Animator did not create the Super Flight animation layer");
    }

    private static IAnimation createAnimation(Identifier id) {
        IPlayable animation = PlayerAnimationRegistry.getAnimation(id);
        if (animation == null) {
            throw new IllegalStateException("Missing Player Animator resource " + id);
        }
        return animation.playAnimation();
    }

    private record AppliedState(boolean active, boolean fast) {
    }
}
