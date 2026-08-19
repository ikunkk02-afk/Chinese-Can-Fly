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

    public static final double MIN_TUNNEL_SPEED = 0.90;
    public static final double TUNNEL_SAMPLE_STEP = 0.25;
    public static final double TUNNEL_HORIZONTAL_MARGIN = 0.65;
    public static final double TUNNEL_VERTICAL_MARGIN = 0.35;
    public static final int MAX_BLOCKS_BROKEN_PER_TICK = 64;
    public static final int MAX_TUNNEL_STALL_TICKS = 3;
    public static final double ABSOLUTE_MAX_BREAK_HARDNESS = 12.0;
    public static final double HARDNESS_BASE = 0.5;
    public static final double HARDNESS_SPEED_FACTOR = 4.5;
    public static final double MIN_RESISTANCE_HARDNESS = 0.2;
    public static final double RESISTANCE_FACTOR = 0.002;
    public static final double MAX_TUNNEL_SPEED_LOSS_PER_TICK = 0.16;
    public static final double UNBREAKABLE_RECOIL_SPEED = 0.20;
    public static final int BLOCK_UPDATE_MAX_DEPTH = 512;
    public static final int MAX_TUNNEL_DEBRIS_SAMPLES = 6;
    public static final int TUNNEL_DEBRIS_PER_SAMPLE = 4;
    public static final int TUNNEL_SOUND_COOLDOWN_TICKS = 5;

    public static final int CRUISE_TRAIL_PARTICLES = 4;
    public static final int MAX_TRAIL_PARTICLES = 12;
    public static final double TRAIL_FULL_DISTANCE = 32.0;
    public static final double TRAIL_CUTOFF_DISTANCE = 64.0;
    public static final double MAX_FOV_BONUS = 18.0;

    public static double maximumBreakableHardness(double speed) {
        return Math.min(ABSOLUTE_MAX_BREAK_HARDNESS, HARDNESS_BASE + speed * HARDNESS_SPEED_FACTOR);
    }

    public static double tunnelSpeedLoss(double totalResistance) {
        return Math.clamp(totalResistance * RESISTANCE_FACTOR, 0.0, MAX_TUNNEL_SPEED_LOSS_PER_TICK);
    }

    private SuperFlightTuning() {
    }
}
