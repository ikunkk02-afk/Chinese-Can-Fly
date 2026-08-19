package io.github.ikunkk02afk.chinesecanfly.ability.combat;

/** Code-owned Stage 9/10 combat limits. Configuration is deliberately deferred. */
public final class CombatTuning {
    public static final double MELEE_DAMAGE_MULTIPLIER = 5.0;
    public static final double EMPTY_HAND_MIN_DAMAGE = 20.0;
    public static final double MAX_SAFE_RAW_DAMAGE = 2048.0;
    public static final double MELEE_KNOCKBACK = 2.25;
    public static final double EMPTY_HAND_KNOCKBACK = 3.25;

    public static final double MIN_RAM_SPEED = 1.0;
    public static final double RAM_DAMAGE_BASE = 20.0;
    public static final double RAM_DAMAGE_PER_SPEED = 14.0;
    public static final double RAM_MIN_DAMAGE = 20.0;
    public static final double RAM_MAX_DAMAGE = 60.0;
    public static final double RAM_KNOCKBACK = 3.0;
    public static final int RAM_TARGET_COOLDOWN_TICKS = 10;
    public static final int MAX_RAM_TARGETS_PER_TICK = 12;
    public static final int RAM_QUEUE_TTL_TICKS = 2;

    private CombatTuning() {
    }
}
