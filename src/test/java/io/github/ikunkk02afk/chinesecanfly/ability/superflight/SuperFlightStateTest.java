package io.github.ikunkk02afk.chinesecanfly.ability.superflight;

import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SuperFlightStateTest {
    @Test
    void allStartRequirementsMustBeMet() {
        assertTrue(SuperFlightEligibility.canStart(new SuperFlightEligibility.Conditions(
                true, true, false, false, false, false, false, false, true, true
        )));
        assertFalse(SuperFlightEligibility.canStart(new SuperFlightEligibility.Conditions(
                false, true, false, false, false, false, false, false, true, true
        )));
        assertFalse(SuperFlightEligibility.canStart(new SuperFlightEligibility.Conditions(
                true, true, false, false, false, false, false, false, true, false
        )));
        assertFalse(SuperFlightEligibility.canStart(new SuperFlightEligibility.Conditions(
                true, true, false, false, true, false, false, false, true, true
        )));
        assertFalse(SuperFlightEligibility.canStart(new SuperFlightEligibility.Conditions(
                true, true, false, true, false, false, false, false, true, true
        )));
    }

    @Test
    void continuationAllowsOnlyAHighSpeedDiveToSurviveTransientGroundContact() {
        SuperFlightEligibility.Conditions grounded = new SuperFlightEligibility.Conditions(
                true, true, false, false, false, false, false, true, true, true
        );

        assertFalse(SuperFlightEligibility.canStart(grounded));
        assertTrue(SuperFlightEligibility.canContinue(grounded, true,
                SuperFlightTuning.MIN_TUNNEL_SPEED, new Vec3d(0.0, -0.16, 0.0)));
        assertFalse(SuperFlightEligibility.canContinue(grounded, true,
                SuperFlightTuning.MIN_TUNNEL_SPEED, new Vec3d(0.0, -0.15, 0.0)));
        assertFalse(SuperFlightEligibility.canContinue(grounded, false,
                SuperFlightTuning.MAX_SPEED, new Vec3d(0.0, -1.0, 0.0)));
        assertFalse(SuperFlightEligibility.canContinue(grounded, true,
                SuperFlightTuning.MIN_TUNNEL_SPEED - 0.01, new Vec3d(0.0, -1.0, 0.0)));
    }

    @Test
    void sonicBoomOnlyFiresOnThresholdCrossingAndRearmsAfterSustainedSlowFlight() {
        SuperFlightState state = new SuperFlightState(new Vec3d(0.0, 0.0, 1.0));
        state.setSpeed(1.46);
        assertTrue(state.consumeSonicCrossing(1.44));
        assertFalse(state.consumeSonicCrossing(1.46));

        state.setSpeed(SuperFlightTuning.SONIC_REARM_SPEED);
        for (int index = 0; index < SuperFlightTuning.SONIC_REARM_TICKS; index++) {
            assertFalse(state.consumeSonicCrossing(SuperFlightTuning.SONIC_REARM_SPEED));
        }

        state.setSpeed(1.46);
        assertTrue(state.consumeSonicCrossing(1.44));
    }

    @Test
    void fastVisualStateOnlyChangesAtItsDedicatedThreshold() {
        SuperFlightState state = new SuperFlightState(new Vec3d(1.0, 0.0, 0.0));
        state.setSpeed(1.19);
        assertFalse(state.updateFast());
        assertFalse(state.fast());

        state.setSpeed(1.20);
        assertTrue(state.updateFast());
        assertTrue(state.fast());
    }

    @Test
    void tunnellingStopsAfterThreeConsecutiveTicksWithoutProgress() {
        SuperFlightState state = new SuperFlightState(new Vec3d(1.0, 0.0, 0.0));

        assertFalse(state.recordTunnelProgress(false));
        assertFalse(state.recordTunnelProgress(false));
        assertTrue(state.recordTunnelProgress(false));

        state.recordTunnelProgress(true);
        assertFalse(state.recordTunnelProgress(false));
    }
}
