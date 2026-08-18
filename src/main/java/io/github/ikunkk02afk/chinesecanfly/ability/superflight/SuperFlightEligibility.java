package io.github.ikunkk02afk.chinesecanfly.ability.superflight;

/** Pure validation policy shared by the server entry point and its unit tests. */
final class SuperFlightEligibility {
    private SuperFlightEligibility() {
    }

    static boolean canStart(Conditions conditions) {
        return conditions.hasChinesePower()
                && conditions.alive()
                && !conditions.spectator()
                && !conditions.riding()
                && !conditions.elytraFlying()
                && !conditions.touchingWater()
                && !conditions.inLava()
                && !conditions.onGround()
                && conditions.allowFlying()
                && conditions.flying();
    }

    record Conditions(boolean hasChinesePower, boolean alive, boolean spectator, boolean riding,
                      boolean elytraFlying, boolean touchingWater, boolean inLava, boolean onGround,
                      boolean allowFlying, boolean flying) {
    }
}
