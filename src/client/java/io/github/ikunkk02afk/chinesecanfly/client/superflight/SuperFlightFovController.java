package io.github.ikunkk02afk.chinesecanfly.client.superflight;

import io.github.ikunkk02afk.chinesecanfly.ability.superflight.SuperFlightTuning;
import net.minecraft.util.math.MathHelper;

/** Client-only temporary FOV modifier. It never changes the player's option value. */
public final class SuperFlightFovController {
    private static double previousBonus;
    private static double currentBonus;

    private SuperFlightFovController() {
    }

    static void tick(boolean active, double speed) {
        previousBonus = currentBonus;
        double target = 0.0;
        if (active) {
            double progress = Math.clamp(
                    (speed - SuperFlightTuning.INITIAL_SPEED)
                            / (SuperFlightTuning.MAX_SPEED - SuperFlightTuning.INITIAL_SPEED),
                    0.0,
                    1.0
            );
            target = 4.0 + (SuperFlightTuning.MAX_FOV_BONUS - 4.0) * progress;
        }
        currentBonus += (target - currentBonus) * 0.18;
        if (!active && currentBonus < 0.01) {
            currentBonus = 0.0;
        }
    }

    public static double getBonus(float tickDelta) {
        return MathHelper.lerp(tickDelta, previousBonus, currentBonus);
    }

    static void clear() {
        previousBonus = 0.0;
        currentBonus = 0.0;
    }
}
