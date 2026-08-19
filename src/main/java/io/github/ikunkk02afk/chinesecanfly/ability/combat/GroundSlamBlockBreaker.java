package io.github.ikunkk02afk.chinesecanfly.ability.combat;

import io.github.ikunkk02afk.chinesecanfly.ability.superflight.SuperFlightTuning;
import io.github.ikunkk02afk.chinesecanfly.registry.ModBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/** Capped, no-drop bowl-shaped terrain damage for a completed ground slam. */
final class GroundSlamBlockBreaker {
    private GroundSlamBlockBreaker() {
    }

    static void createImpact(ServerPlayerEntity player, Vec3d impact, double distance) {
        ServerWorld world = player.getServerWorld();
        BlockPos center = BlockPos.ofFloored(impact);
        BlockState centerState = world.getBlockState(center);
        double radius = CombatMath.slamCraterRadius(distance);
        int depth = CombatMath.slamCraterDepth(distance);
        int broken = 0;
        int bound = MathHelper.ceil(radius);

        outer:
        for (int layer = 0; layer < depth; layer++) {
            double layerRadius = radius * (1.0 - (double) layer / (depth + 0.35));
            for (int x = -bound; x <= bound; x++) {
                for (int z = -bound; z <= bound; z++) {
                    if (x * x + z * z > layerRadius * layerRadius) {
                        continue;
                    }
                    BlockPos pos = center.add(x, -layer, z);
                    BlockState state = world.getBlockState(pos);
                    if (!canBreak(world, pos, state)) {
                        continue;
                    }
                    if (world.breakBlock(pos, false, player, SuperFlightTuning.BLOCK_UPDATE_MAX_DEPTH)) {
                        broken++;
                        if (broken >= CombatTuning.MAX_SLAM_BLOCKS) {
                            break outer;
                        }
                    }
                }
            }
        }

        emitFeedback(world, impact, centerState, radius, broken);
    }

    private static boolean canBreak(ServerWorld world, BlockPos pos, BlockState state) {
        if (state.isAir() || state.isIn(ModBlockTags.SUPER_FLIGHT_IMMUNE) || world.getBlockEntity(pos) != null) {
            return false;
        }
        double hardness = state.getHardness(world, pos);
        return hardness >= 0.0 && hardness <= CombatTuning.MAX_SLAM_BLOCK_HARDNESS;
    }

    private static void emitFeedback(ServerWorld world, Vec3d impact, BlockState state, double radius, int broken) {
        if (!state.isAir()) {
            world.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, state),
                    impact.x, impact.y, impact.z, Math.min(18, Math.max(6, broken / 3)), 0.45, 0.20, 0.45, 0.18);
        }
        world.spawnParticles(ParticleTypes.CLOUD, impact.x, impact.y, impact.z,
                18, 0.55, 0.12, 0.55, 0.10);
        for (int point = 0; point < 32; point++) {
            double angle = Math.PI * 2.0 * point / 32.0;
            double x = impact.x + Math.cos(angle) * radius;
            double z = impact.z + Math.sin(angle) * radius;
            world.spawnParticles(ParticleTypes.WHITE_ASH, x, impact.y + 0.08, z, 1, 0.02, 0.02, 0.02, 0.10);
        }
        world.spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0xE6B83D).toVector3f(), 1.1F),
                impact.x, impact.y + 0.15, impact.z, 5, 0.45, 0.08, 0.45, 0.03);
        world.playSound(null, BlockPos.ofFloored(impact), SoundEvents.ENTITY_GENERIC_EXPLODE.value(),
                SoundCategory.PLAYERS, 1.15F, 0.75F);
        world.playSound(null, BlockPos.ofFloored(impact), SoundEvents.BLOCK_ANVIL_LAND,
                SoundCategory.PLAYERS, 0.85F, 0.62F);
    }
}
