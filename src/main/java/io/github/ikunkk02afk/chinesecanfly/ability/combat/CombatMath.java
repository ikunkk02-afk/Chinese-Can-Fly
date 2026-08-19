package io.github.ikunkk02afk.chinesecanfly.ability.combat;

/** Pure combat calculations kept independently testable from Minecraft runtime classes. */
public final class CombatMath {
    private CombatMath() {
    }

    public static float enhancedMeleeDamage(float baseDamage, boolean emptyHand) {
        if (!Float.isFinite(baseDamage) || baseDamage < 0.0F) {
            return 0.0F;
        }

        double scaled = baseDamage * CombatTuning.MELEE_DAMAGE_MULTIPLIER;
        if (emptyHand) {
            scaled = Math.max(scaled, CombatTuning.EMPTY_HAND_MIN_DAMAGE);
        }
        return (float) Math.clamp(scaled, 0.0, CombatTuning.MAX_SAFE_RAW_DAMAGE);
    }

    public static float ramDamage(double speed) {
        if (!Double.isFinite(speed) || speed < 0.0) {
            return (float) CombatTuning.RAM_MIN_DAMAGE;
        }
        return (float) Math.clamp(
                CombatTuning.RAM_DAMAGE_BASE + speed * CombatTuning.RAM_DAMAGE_PER_SPEED,
                CombatTuning.RAM_MIN_DAMAGE,
                CombatTuning.RAM_MAX_DAMAGE
        );
    }

    public static float thrownImpactDamage(double speed) {
        if (!Double.isFinite(speed) || speed < 0.0) {
            return (float) CombatTuning.THROW_MIN_DAMAGE;
        }
        return (float) Math.clamp(
                CombatTuning.THROW_DAMAGE_BASE + speed * CombatTuning.THROW_DAMAGE_PER_SPEED,
                CombatTuning.THROW_MIN_DAMAGE,
                CombatTuning.THROW_MAX_DAMAGE
        );
    }

    public static double clampedSlamDistance(double distance) {
        return !Double.isFinite(distance) ? 0.0 : Math.clamp(distance, 0.0, CombatTuning.MAX_SLAM_DISTANCE);
    }

    public static float slamPrimaryDamage(double distance) {
        double safeDistance = Math.min(clampedSlamDistance(distance), CombatTuning.SLAM_PRIMARY_DISTANCE_CAP);
        return (float) (CombatTuning.SLAM_PRIMARY_BASE_DAMAGE
                + safeDistance * CombatTuning.SLAM_PRIMARY_DAMAGE_PER_BLOCK);
    }

    public static double slamCraterRadius(double distance) {
        return Math.min(CombatTuning.SLAM_MAX_RADIUS, 2.0 + Math.min(clampedSlamDistance(distance), 60.0) / 30.0);
    }

    public static int slamCraterDepth(double distance) {
        return Math.clamp(1 + (int) Math.floor(Math.min(clampedSlamDistance(distance), 60.0) / 30.0), 1, 3);
    }
}
