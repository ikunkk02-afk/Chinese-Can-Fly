package io.github.ikunkk02afk.chinesecanfly.ability.combat;

import io.github.ikunkk02afk.chinesecanfly.ability.superflight.SuperFlightBlockRules;
import io.github.ikunkk02afk.chinesecanfly.ability.superflight.SuperFlightManager;
import io.github.ikunkk02afk.chinesecanfly.ability.superflight.SuperFlightTuning;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Applies one rate-limited wall impact to a living entity currently carried at super-flight speed. */
public final class HeldEntityImpactManager {
    private static final Map<ImpactKey, Long> NEXT_ALLOWED_IMPACT = new HashMap<>();

    private HeldEntityImpactManager() {
    }

    public static void process(ServerPlayerEntity holder, Vec3d velocity, double speed) {
        if (!SuperFlightManager.isActive(holder)
                || GroundSlamManager.isActive(holder)
                || !HeldEntityManager.isHolding(holder)
                || speed < SuperFlightTuning.MIN_TUNNEL_SPEED
                || velocity.lengthSquared() < 1.0E-6) {
            return;
        }

        LivingEntity target = HeldEntityManager.getHeldEntity(holder);
        if (target == null || !target.isAlive()) {
            return;
        }
        ServerWorld world = holder.getServerWorld();
        ImpactKey key = new ImpactKey(holder.getUuid(), target.getUuid());
        long tick = world.getTime();
        if (tick < NEXT_ALLOWED_IMPACT.getOrDefault(key, Long.MIN_VALUE / 4)) {
            return;
        }

        BlockImpact impact = findFirstBlockImpact(world, target, velocity);
        if (impact == null) {
            return;
        }

        boolean hardBlocker = !SuperFlightBlockRules.canBreak(world, impact.pos(), impact.state(), speed);
        float damage = CombatMath.heldImpactDamage(speed, hardBlocker);
        NEXT_ALLOWED_IMPACT.put(key, tick + CombatTuning.HELD_IMPACT_COOLDOWN_TICKS);
        target.damage(CombatDamageTypes.causedBy(target, CombatDamageTypes.HELD_ENTITY_IMPACT, holder), damage);
        emitFeedback(world, impact, velocity.normalize());
        if (!target.isAlive()) {
            HeldEntityManager.release(holder, true);
        }
        NEXT_ALLOWED_IMPACT.entrySet().removeIf(entry -> entry.getValue() + CombatTuning.HELD_IMPACT_COOLDOWN_TICKS < tick);
    }

    public static void clear(ServerPlayerEntity holder) {
        NEXT_ALLOWED_IMPACT.keySet().removeIf(key -> key.holderId().equals(holder.getUuid()));
    }

    private static BlockImpact findFirstBlockImpact(ServerWorld world, LivingEntity target, Vec3d velocity) {
        Box baseBox = target.getBoundingBox();
        int samples = Math.max(1, MathHelper.ceil(velocity.length() / CombatTuning.HELD_IMPACT_SAMPLE_STEP));
        for (int sample = 1; sample <= samples; sample++) {
            Box sampleBox = baseBox.offset(velocity.multiply((double) sample / samples));
            if (!world.getBlockCollisions(target, sampleBox).iterator().hasNext()) {
                continue;
            }
            BlockImpact impact = findIntersectingBlock(world, sampleBox);
            if (impact != null) {
                return impact;
            }
        }
        return null;
    }

    private static BlockImpact findIntersectingBlock(ServerWorld world, Box box) {
        int minX = MathHelper.floor(box.minX);
        int minY = MathHelper.floor(box.minY);
        int minZ = MathHelper.floor(box.minZ);
        int maxX = MathHelper.floor(box.maxX);
        int maxY = MathHelper.floor(box.maxY);
        int maxZ = MathHelper.floor(box.maxZ);
        for (BlockPos pos : BlockPos.iterate(minX, minY, minZ, maxX, maxY, maxZ)) {
            BlockState state = world.getBlockState(pos);
            VoxelShape shape = state.getCollisionShape(world, pos);
            for (Box shapeBox : shape.getBoundingBoxes()) {
                if (shapeBox.offset(pos).intersects(box)) {
                    return new BlockImpact(pos.toImmutable(), state);
                }
            }
        }
        return null;
    }

    private static void emitFeedback(ServerWorld world, BlockImpact impact, Vec3d direction) {
        Vec3d point = Vec3d.ofCenter(impact.pos()).add(direction.negate().multiply(0.12));
        world.spawnParticles(ParticleTypes.CRIT, point.x, point.y, point.z, 8, 0.18, 0.18, 0.18, 0.08);
        world.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, impact.state()),
                point.x, point.y, point.z, 6, 0.16, 0.16, 0.16, 0.06);
        world.spawnParticles(ParticleTypes.WHITE_ASH, point.x, point.y, point.z, 5, 0.18, 0.18, 0.18, 0.02);
        world.spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0xA41422).toVector3f(), 0.9F),
                point.x, point.y, point.z, 4, 0.13, 0.13, 0.13, 0.01);
        world.spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0xE7BC45).toVector3f(), 0.9F),
                point.x, point.y, point.z, 3, 0.13, 0.13, 0.13, 0.01);
        world.playSound(null, impact.pos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 0.85F, 0.82F);
    }

    private record ImpactKey(UUID holderId, UUID targetId) {
    }

    private record BlockImpact(BlockPos pos, BlockState state) {
    }
}
