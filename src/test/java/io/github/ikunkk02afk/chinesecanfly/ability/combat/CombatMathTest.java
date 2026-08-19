package io.github.ikunkk02afk.chinesecanfly.ability.combat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CombatMathTest {
    private static final double EPSILON = 1.0E-6;

    @Test
    void enhancedMeleeScalesWeaponDamageAndCapsSafely() {
        assertEquals(20.0, CombatMath.enhancedMeleeDamage(4.0F, false), EPSILON);
        assertEquals(2048.0, CombatMath.enhancedMeleeDamage(1000.0F, false), EPSILON);
    }

    @Test
    void enhancedMeleeGivesEmptyHandItsMinimumWithoutInvalidNumbers() {
        assertEquals(20.0, CombatMath.enhancedMeleeDamage(1.0F, true), EPSILON);
        assertEquals(0.0, CombatMath.enhancedMeleeDamage(Float.NaN, true), EPSILON);
        assertEquals(0.0, CombatMath.enhancedMeleeDamage(-1.0F, true), EPSILON);
    }

    @Test
    void ramDamageFollowsTheConfiguredCurveAndCap() {
        assertEquals(34.0, CombatMath.ramDamage(1.0), EPSILON);
        assertEquals(55.0, CombatMath.ramDamage(2.5), EPSILON);
        assertEquals(60.0, CombatMath.ramDamage(100.0), EPSILON);
    }

    @Test
    void thrownImpactAndGroundSlamValuesAreBounded() {
        assertEquals(37.0, CombatMath.thrownImpactDamage(2.5), EPSILON);
        assertEquals(40.0, CombatMath.thrownImpactDamage(100.0), EPSILON);
        assertEquals(32.0, CombatMath.slamPrimaryDamage(10.0), EPSILON);
        assertEquals(64.0, CombatMath.slamPrimaryDamage(100.0), EPSILON);
        assertEquals(4.0, CombatMath.slamCraterRadius(100.0), EPSILON);
        assertEquals(3, CombatMath.slamCraterDepth(100.0));
    }
}
