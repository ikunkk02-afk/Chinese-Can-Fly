package io.github.ikunkk02afk.chinesecanfly.ability.combat;

import io.github.ikunkk02afk.chinesecanfly.ChineseCanFly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/** Registry keys and source factories for combat damage which must not count as normal melee. */
public final class CombatDamageTypes {
    public static final RegistryKey<DamageType> SUPER_FLIGHT_IMPACT = key("super_flight_impact");
    public static final RegistryKey<DamageType> THROWN_IMPACT = key("thrown_impact");
    public static final RegistryKey<DamageType> GROUND_SLAM = key("ground_slam");
    public static final RegistryKey<DamageType> HELD_ENTITY_IMPACT = key("held_entity_impact");

    private CombatDamageTypes() {
    }

    public static DamageSource causedBy(Entity victimContext, RegistryKey<DamageType> key, Entity attacker) {
        return victimContext.getDamageSources().create(key, attacker);
    }

    public static DamageSource causedBy(Entity victimContext, RegistryKey<DamageType> key, Entity source, Entity attacker) {
        return victimContext.getDamageSources().create(key, source, attacker);
    }

    private static RegistryKey<DamageType> key(String path) {
        return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(ChineseCanFly.MOD_ID, path));
    }
}
