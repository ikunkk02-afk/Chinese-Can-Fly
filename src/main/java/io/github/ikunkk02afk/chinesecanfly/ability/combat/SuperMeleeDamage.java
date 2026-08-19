package io.github.ikunkk02afk.chinesecanfly.ability.combat;

import io.github.ikunkk02afk.chinesecanfly.network.CombatAnimation;
import io.github.ikunkk02afk.chinesecanfly.registry.ModComponents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

/** Defines the narrow set of vanilla player attacks enhanced by Chinese power. */
public final class SuperMeleeDamage {
    private SuperMeleeDamage() {
    }

    public static float modifyIncomingDamage(float amount, DamageSource source) {
        ServerPlayerEntity attacker = attackerFor(source);
        if (attacker == null) {
            return amount;
        }
        return CombatMath.enhancedMeleeDamage(amount, attacker.getMainHandStack().isEmpty());
    }

    public static void onSuccessfulHit(LivingEntity target, DamageSource source) {
        ServerPlayerEntity attacker = attackerFor(source);
        if (attacker == null || !(target.getWorld() instanceof ServerWorld world)) {
            return;
        }

        ItemStack held = attacker.getMainHandStack();
        double strength = held.isEmpty() ? CombatTuning.EMPTY_HAND_KNOCKBACK : CombatTuning.MELEE_KNOCKBACK;
        Vec3d direction = horizontalDirection(attacker, target);
        target.takeKnockback(strength, direction.x, direction.z);

        Vec3d impact = target.getPos().add(0.0, target.getHeight() * 0.55, 0.0);
        world.spawnParticles(ParticleTypes.CLOUD, impact.x, impact.y, impact.z,
                8, 0.18, 0.18, 0.18, 0.04);
        world.spawnParticles(ParticleTypes.CRIT, impact.x, impact.y, impact.z,
                8, 0.20, 0.20, 0.20, 0.18);
        world.spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0x9E1823).toVector3f(), 0.9F),
                impact.x, impact.y, impact.z, 3, 0.16, 0.16, 0.16, 0.02);
        world.spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0xE6B83D).toVector3f(), 0.9F),
                impact.x, impact.y, impact.z, 3, 0.16, 0.16, 0.16, 0.02);
        world.playSound(null, target.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG,
                SoundCategory.PLAYERS, 1.0F, 0.85F);
        world.playSound(null, target.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE.value(),
                SoundCategory.PLAYERS, 0.35F, 1.45F);
        CombatAnimationManager.broadcast(attacker, CombatAnimation.SUPER_PUNCH);
    }

    private static ServerPlayerEntity attackerFor(DamageSource source) {
        if (!source.isOf(DamageTypes.PLAYER_ATTACK)
                || !(source.getAttacker() instanceof ServerPlayerEntity attacker)
                || source.getSource() != attacker
                || !ModComponents.CHINESE_POWER.get(attacker).hasChinesePower()) {
            return null;
        }
        return attacker;
    }

    private static Vec3d horizontalDirection(ServerPlayerEntity attacker, LivingEntity target) {
        Vec3d offset = target.getPos().subtract(attacker.getPos());
        Vec3d horizontal = new Vec3d(offset.x, 0.0, offset.z);
        if (horizontal.lengthSquared() > 1.0E-6) {
            return horizontal.normalize();
        }
        Vec3d look = attacker.getRotationVector();
        horizontal = new Vec3d(look.x, 0.0, look.z);
        return horizontal.lengthSquared() > 1.0E-6 ? horizontal.normalize() : new Vec3d(0.0, 0.0, 1.0);
    }
}
