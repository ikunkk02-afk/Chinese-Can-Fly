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
import io.github.ikunkk02afk.chinesecanfly.network.GroundSlamAction;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Client-only upper-body animation layer for combat state and one-shot actions. */
final class ChineseCombatAnimationController {
    private static final Identifier LAYER_ID = Identifier.of(ChineseCanFly.MOD_ID, "combat_layer");
    private static final Identifier SUPER_PUNCH_ID = Identifier.of(ChineseCanFly.MOD_ID, "super_punch");
    private static final Identifier GRAB_ID = Identifier.of(ChineseCanFly.MOD_ID, "grab_entity");
    private static final Identifier HOLD_ID = Identifier.of(ChineseCanFly.MOD_ID, "hold_entity");
    private static final Identifier THROW_ID = Identifier.of(ChineseCanFly.MOD_ID, "throw_entity");
    private static final Identifier SLAM_ID = Identifier.of(ChineseCanFly.MOD_ID, "ground_slam");
    private static final Identifier SLAM_IMPACT_ID = Identifier.of(ChineseCanFly.MOD_ID, "ground_slam_impact");
    private static final int LAYER_PRIORITY = 1_100;
    private static final int FADE_TICKS = 3;
    private static final Map<UUID, TimedAnimation> TIMED = new HashMap<>();
    private static final Map<UUID, Boolean> HOLDING = new HashMap<>();
    private static final Map<UUID, Boolean> SLAMMING = new HashMap<>();
    private static final Map<UUID, Integer> IMPACT_REMAINING = new HashMap<>();
    private static final Set<UUID> CLEAR_PENDING = new HashSet<>();
    private static final Map<UUID, Identifier> APPLIED = new HashMap<>();

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
        switch (animation) {
            case SUPER_PUNCH -> TIMED.put(playerId, new TimedAnimation(SUPER_PUNCH_ID, 10));
            case GRAB -> TIMED.put(playerId, new TimedAnimation(GRAB_ID, 10));
            case THROW -> TIMED.put(playerId, new TimedAnimation(THROW_ID, 10));
            case CLEAR -> requestClear(playerId);
        }
    }

    static void setHolding(UUID playerId, boolean holding) {
        if (holding) {
            HOLDING.put(playerId, true);
        } else {
            HOLDING.remove(playerId);
            CLEAR_PENDING.add(playerId);
        }
    }

    static void applyGroundSlam(UUID playerId, GroundSlamAction action) {
        switch (action) {
            case START -> SLAMMING.put(playerId, true);
            case IMPACT -> {
                SLAMMING.remove(playerId);
                IMPACT_REMAINING.put(playerId, 10);
            }
            case CANCEL -> {
                SLAMMING.remove(playerId);
                CLEAR_PENDING.add(playerId);
            }
        }
    }

    static void tick(Map<UUID, ? extends AbstractClientPlayerEntity> players) {
        Set<UUID> ids = new HashSet<>();
        ids.addAll(TIMED.keySet());
        ids.addAll(HOLDING.keySet());
        ids.addAll(SLAMMING.keySet());
        ids.addAll(IMPACT_REMAINING.keySet());
        ids.addAll(CLEAR_PENDING);
        for (UUID id : ids) {
            AbstractClientPlayerEntity player = players.get(id);
            if (player == null) {
                forget(id);
                continue;
            }
            CLEAR_PENDING.remove(id);
            apply(player, selectAnimation(id));
            tickTimers(id);
        }
    }

    static void forget(UUID playerId) {
        TIMED.remove(playerId);
        HOLDING.remove(playerId);
        SLAMMING.remove(playerId);
        IMPACT_REMAINING.remove(playerId);
        CLEAR_PENDING.remove(playerId);
        APPLIED.remove(playerId);
    }

    static void clear() {
        TIMED.clear();
        HOLDING.clear();
        SLAMMING.clear();
        IMPACT_REMAINING.clear();
        CLEAR_PENDING.clear();
        APPLIED.clear();
    }

    private static Identifier selectAnimation(UUID id) {
        if (IMPACT_REMAINING.getOrDefault(id, 0) > 0) {
            return SLAM_IMPACT_ID;
        }
        TimedAnimation timed = TIMED.get(id);
        if (timed != null && timed.remainingTicks() > 0) {
            return timed.animationId();
        }
        if (SLAMMING.containsKey(id)) {
            return SLAM_ID;
        }
        return HOLDING.containsKey(id) ? HOLD_ID : null;
    }

    private static void requestClear(UUID playerId) {
        TIMED.remove(playerId);
        HOLDING.remove(playerId);
        SLAMMING.remove(playerId);
        IMPACT_REMAINING.remove(playerId);
        CLEAR_PENDING.add(playerId);
    }

    private static void tickTimers(UUID id) {
        TimedAnimation timed = TIMED.get(id);
        if (timed != null) {
            if (timed.remainingTicks() <= 1) {
                TIMED.remove(id);
                CLEAR_PENDING.add(id);
            } else {
                TIMED.put(id, timed.withOneLessTick());
            }
        }
        int impact = IMPACT_REMAINING.getOrDefault(id, 0);
        if (impact <= 1) {
            if (IMPACT_REMAINING.remove(id) != null) {
                CLEAR_PENDING.add(id);
            }
        } else {
            IMPACT_REMAINING.put(id, impact - 1);
        }
    }

    private static void apply(AbstractClientPlayerEntity player, Identifier animationId) {
        UUID id = player.getUuid();
        if (animationId == null ? !APPLIED.containsKey(id) : animationId.equals(APPLIED.get(id))) {
            return;
        }
        ModifierLayer<IAnimation> layer = getLayer(player);
        IAnimation next = animationId == null ? null : createAnimation(animationId);
        layer.replaceAnimationWithFade(
                AbstractFadeModifier.standardFadeIn(FADE_TICKS, Ease.INOUTSINE),
                next,
                true
        );
        if (animationId == null) {
            APPLIED.remove(id);
        } else {
            APPLIED.put(id, animationId);
        }
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

    private record TimedAnimation(Identifier animationId, int remainingTicks) {
        private TimedAnimation withOneLessTick() {
            return new TimedAnimation(animationId, remainingTicks - 1);
        }
    }
}
