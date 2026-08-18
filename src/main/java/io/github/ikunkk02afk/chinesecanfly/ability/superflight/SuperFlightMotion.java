package io.github.ikunkk02afk.chinesecanfly.ability.superflight;

import net.minecraft.util.math.Vec3d;

/** Shared, deterministic speed and steering maths used by both logical sides. */
public final class SuperFlightMotion {
    private SuperFlightMotion() {
    }

    public static double nextSpeed(double speed, boolean accelerating) {
        if (accelerating) {
            return Math.min(SuperFlightTuning.MAX_SPEED, speed + SuperFlightTuning.ACCELERATION);
        }
        return Math.max(0.0, speed - SuperFlightTuning.DECELERATION);
    }

    public static boolean hasFinishedDecelerating(double speed) {
        return speed <= SuperFlightTuning.EXIT_SPEED;
    }

    public static double steeringFactor(double speed) {
        double range = SuperFlightTuning.MAX_SPEED - SuperFlightTuning.INITIAL_SPEED;
        double progress = Math.clamp((speed - SuperFlightTuning.INITIAL_SPEED) / range, 0.0, 1.0);
        return SuperFlightTuning.SLOW_STEERING_FACTOR
                + (SuperFlightTuning.FAST_STEERING_FACTOR - SuperFlightTuning.SLOW_STEERING_FACTOR) * progress;
    }

    public static Vec3d steer(Vec3d currentDirection, Vec3d lookDirection, double speed) {
        Vec3d desired = lookDirection.normalize();
        if (currentDirection.lengthSquared() < 1.0E-8) {
            return desired;
        }
        Vec3d blended = currentDirection.normalize().lerp(desired, steeringFactor(speed));
        return blended.lengthSquared() < 1.0E-8 ? desired : blended.normalize();
    }
}
