package io.github.ikunkk02afk.chinesecanfly.ability.superflight;

import net.minecraft.util.math.Vec3d;

/** Ephemeral server-authoritative state. Never serialize this into CCA or NBT. */
final class SuperFlightState {
    private double speed = SuperFlightTuning.INITIAL_SPEED;
    private Vec3d direction;
    private boolean requested = true;
    private boolean fast;
    private boolean sonicTriggered;
    private int sonicRearmTicks;
    private int tunnelStallTicks;
    private int tunnelSoundCooldown;

    SuperFlightState(Vec3d direction) {
        this.direction = direction.normalize();
    }

    double speed() {
        return speed;
    }

    void setSpeed(double speed) {
        this.speed = speed;
    }

    Vec3d direction() {
        return direction;
    }

    void setDirection(Vec3d direction) {
        this.direction = direction;
    }

    boolean requested() {
        return requested;
    }

    void setRequested(boolean requested) {
        this.requested = requested;
    }

    boolean fast() {
        return fast;
    }

    boolean updateFast() {
        boolean previous = fast;
        fast = speed >= SuperFlightTuning.FAST_ANIMATION_THRESHOLD;
        return previous != fast;
    }

    boolean consumeSonicCrossing(double previousSpeed) {
        if (!sonicTriggered && previousSpeed < SuperFlightTuning.SONIC_THRESHOLD
                && speed >= SuperFlightTuning.SONIC_THRESHOLD) {
            sonicTriggered = true;
            sonicRearmTicks = 0;
            return true;
        }

        if (speed <= SuperFlightTuning.SONIC_REARM_SPEED) {
            sonicRearmTicks++;
            if (sonicRearmTicks >= SuperFlightTuning.SONIC_REARM_TICKS) {
                sonicTriggered = false;
            }
        } else {
            sonicRearmTicks = 0;
        }
        return false;
    }

    boolean recordTunnelProgress(boolean progressed) {
        if (progressed) {
            tunnelStallTicks = 0;
            return false;
        }
        tunnelStallTicks++;
        return tunnelStallTicks >= SuperFlightTuning.MAX_TUNNEL_STALL_TICKS;
    }

    void clearTunnelStall() {
        tunnelStallTicks = 0;
    }

    boolean consumeTunnelSound() {
        if (tunnelSoundCooldown > 0) {
            return false;
        }
        tunnelSoundCooldown = SuperFlightTuning.TUNNEL_SOUND_COOLDOWN_TICKS;
        return true;
    }

    void tickTunnelSoundCooldown() {
        if (tunnelSoundCooldown > 0) {
            tunnelSoundCooldown--;
        }
    }
}
