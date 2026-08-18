package io.github.ikunkk02afk.chinesecanfly.ability.superflight;

/**
 * Stage 7's deliberately code-owned tuning values. Config UI support is deferred.
 */
public final class SuperFlightTuning {
    public static final double INITIAL_SPEED = 0.65;
    public static final double ACCELERATION = 0.08;
    public static final double MAX_SPEED = 2.50;
    public static final double DECELERATION = 0.12;
    public static final double EXIT_SPEED = 0.25;
    public static final double MAX_SERVER_SPEED = MAX_SPEED * 1.15;

    public static final double SLOW_STEERING_FACTOR = 0.20;
    public static final double FAST_STEERING_FACTOR = 0.15;
    public static final double FAST_ANIMATION_THRESHOLD = 1.20;
    public static final double SONIC_THRESHOLD = 1.45;
    public static final double SONIC_REARM_SPEED = 1.20;
    public static final int SONIC_REARM_TICKS = 10;
    public static final double SONIC_RADIUS = 80.0;

    public static final int CRUISE_TRAIL_PARTICLES = 4;
    public static final int MAX_TRAIL_PARTICLES = 12;
    public static final double TRAIL_FULL_DISTANCE = 32.0;
    public static final double TRAIL_CUTOFF_DISTANCE = 64.0;
    public static final double MAX_FOV_BONUS = 18.0;

    private SuperFlightTuning() {
    }
}
