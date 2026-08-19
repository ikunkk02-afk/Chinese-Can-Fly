package io.github.ikunkk02afk.chinesecanfly.ability.superflight;

import net.minecraft.util.math.Vec3d;

/** Pure validation policy shared by the server entry point and its unit tests. */
public final class SuperFlightEligibility {
    private SuperFlightEligibility() {
    }

    public static boolean canStart(Conditions conditions) {
        return meetsBaseRequirements(conditions)
                && !conditions.onGround();
    }

    /**
     * Continuing a flight is intentionally not the same as starting one. A player who is already diving at
     * tunnel speed may be marked on-ground for one tick before the Stage 8 probe gets a chance to clear the path.
     */
    public static boolean canContinue(Conditions conditions, boolean requested, double speed, Vec3d direction) {
        if (!meetsBaseRequirements(conditions)) {
            return false;
        }
        if (!conditions.onGround()) {
            return true;
        }
        return requested
                && speed >= SuperFlightTuning.MIN_TUNNEL_SPEED
                && direction.y < -SuperFlightTuning.MIN_DOWNWARD_TUNNEL_DIRECTION;
    }

    public static boolean canContinueRequest(Conditions conditions, boolean predictionActive, double predictedSpeed,
                                             Vec3d predictedDirection) {
        if (!meetsBaseRequirements(conditions)) {
            return false;
        }
        if (!conditions.onGround()) {
            return true;
        }
        return predictionActive
                && predictedSpeed >= SuperFlightTuning.MIN_TUNNEL_SPEED
                && predictedDirection.y < -SuperFlightTuning.MIN_DOWNWARD_TUNNEL_DIRECTION;
    }

    private static boolean meetsBaseRequirements(Conditions conditions) {
        return conditions.hasChinesePower()
                && conditions.alive()
                && !conditions.spectator()
                && !conditions.riding()
                && !conditions.elytraFlying()
                && !conditions.touchingWater()
                && !conditions.inLava()
                && conditions.allowFlying()
                && conditions.flying();
    }

    public record Conditions(boolean hasChinesePower, boolean alive, boolean spectator, boolean riding,
                             boolean elytraFlying, boolean touchingWater, boolean inLava, boolean onGround,
                             boolean allowFlying, boolean flying) {
    }
}
