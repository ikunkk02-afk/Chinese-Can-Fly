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

    public static final double GRAB_RANGE = 6.0;
    public static final double HOLD_DISTANCE = 1.6;
    public static final double SUPER_FLIGHT_HOLD_DISTANCE = 1.3;
    public static final double THROW_SPEED = 2.5;
    public static final double THROW_VERTICAL_SPEED = 0.15;
    public static final int THROW_TRACKING_TICKS = 70;
    public static final double THROW_DAMAGE_BASE = 12.0;
    public static final double THROW_DAMAGE_PER_SPEED = 10.0;
    public static final double THROW_MIN_DAMAGE = 12.0;
    public static final double THROW_MAX_DAMAGE = 40.0;

    public static final double SLAM_DESCENT_SPEED = 2.8;
    public static final int MAX_SLAM_DURATION_TICKS = 100;
    public static final double MAX_SLAM_DISTANCE = 80.0;
    public static final double SLAM_PRIMARY_BASE_DAMAGE = 24.0;
    public static final double SLAM_PRIMARY_DISTANCE_CAP = 50.0;
    public static final double SLAM_PRIMARY_DAMAGE_PER_BLOCK = 0.8;
    public static final double SLAM_AOE_RADIUS = 5.5;
    public static final double SLAM_AOE_DAMAGE = 16.0;
    public static final double SLAM_AOE_KNOCKBACK = 2.75;
    public static final double SLAM_MAX_RADIUS = 4.0;
    public static final int MAX_SLAM_BLOCKS = 96;
    public static final double MAX_SLAM_BLOCK_HARDNESS = 6.0;
    public static final double SLAM_SAMPLE_STEP = 0.20;

    private CombatTuning() {
    }
}
