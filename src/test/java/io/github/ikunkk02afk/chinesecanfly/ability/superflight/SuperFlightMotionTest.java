package io.github.ikunkk02afk.chinesecanfly.ability.superflight;

import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SuperFlightMotionTest {
    private static final double EPSILON = 1.0E-9;

    @Test
    void accelerationIsSmoothAndCapped() {
        assertEquals(0.73, SuperFlightMotion.nextSpeed(0.65, true), EPSILON);
        assertEquals(SuperFlightTuning.MAX_SPEED,
                SuperFlightMotion.nextSpeed(SuperFlightTuning.MAX_SPEED - 0.01, true), EPSILON);
    }

    @Test
    void decelerationReachesTheConfiguredExitRange() {
        assertEquals(2.38, SuperFlightMotion.nextSpeed(2.50, false), EPSILON);
        assertFalse(SuperFlightMotion.hasFinishedDecelerating(0.26));
        assertTrue(SuperFlightMotion.hasFinishedDecelerating(0.25));
    }

    @Test
    void steeringBecomesLessResponsiveAtTopSpeed() {
        assertEquals(0.20, SuperFlightMotion.steeringFactor(SuperFlightTuning.INITIAL_SPEED), EPSILON);
        assertEquals(0.15, SuperFlightMotion.steeringFactor(SuperFlightTuning.MAX_SPEED), EPSILON);

        Vec3d turned = SuperFlightMotion.steer(new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), 1.0);
        assertTrue(turned.x > 0.0 && turned.z > 0.0, "steering must turn gradually instead of snapping 90 degrees");
        assertEquals(1.0, turned.length(), EPSILON);
    }

    @Test
    void tuningKeepsTheServerToleranceAboveTheMaximumButBelowThreeBlocksPerTick() {
        assertEquals(2.875, SuperFlightTuning.MAX_SERVER_SPEED, EPSILON);
        assertTrue(SuperFlightTuning.MAX_SERVER_SPEED < 3.0);
    }

    @Test
    void tunnellingHardnessAndResistanceStayWithinTheirConfiguredBounds() {
        assertEquals(4.55, SuperFlightTuning.maximumBreakableHardness(SuperFlightTuning.MIN_TUNNEL_SPEED), EPSILON);
        assertEquals(11.75, SuperFlightTuning.maximumBreakableHardness(SuperFlightTuning.MAX_SPEED), EPSILON);
        assertEquals(SuperFlightTuning.ABSOLUTE_MAX_BREAK_HARDNESS,
                SuperFlightTuning.maximumBreakableHardness(10.0), EPSILON);
        assertEquals(0.004, SuperFlightTuning.tunnelSpeedLoss(2.0), EPSILON);
        assertEquals(SuperFlightTuning.MAX_TUNNEL_SPEED_LOSS_PER_TICK,
                SuperFlightTuning.tunnelSpeedLoss(1000.0), EPSILON);
    }
}
