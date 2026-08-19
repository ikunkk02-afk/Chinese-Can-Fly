package io.github.ikunkk02afk.chinesecanfly.ability.superflight;

import net.minecraft.block.BlockState;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

/** Performs capped, server-authoritative terrain breaking and its lightweight feedback. */
final class SuperFlightBlockBreaker {
    private SuperFlightBlockBreaker() {
    }

    static BreakResult breakCandidates(ServerPlayerEntity player, List<SuperFlightBlockCandidate> candidates, double speed) {
        ServerWorld world = player.getServerWorld();
        List<SuperFlightBlockCandidate> broken = new ArrayList<>();
        double totalResistance = 0.0;

        for (SuperFlightBlockCandidate candidate : SuperFlightTunnelMath.withinBlockBudget(candidates)) {
            BlockState currentState = world.getBlockState(candidate.pos());
            if (!currentState.equals(candidate.state())
                    || !SuperFlightBlockRules.canBreak(world, candidate.pos(), currentState, speed)) {
                continue;
            }

            double hardness = currentState.getHardness(world, candidate.pos());
            if (world.breakBlock(candidate.pos(), false, player, SuperFlightTuning.BLOCK_UPDATE_MAX_DEPTH)) {
                broken.add(new SuperFlightBlockCandidate(
                        candidate.pos(), currentState, hardness, candidate.projection(),
                        candidate.intersectsPlayerPath(), true
                ));
                totalResistance += Math.max(hardness, SuperFlightTuning.MIN_RESISTANCE_HARDNESS);
            }
        }

        return new BreakResult(broken, totalResistance,
                candidates.size() > SuperFlightTuning.MAX_BLOCKS_BROKEN_PER_TICK);
    }

    static void emitBreakFeedback(ServerPlayerEntity player, List<SuperFlightBlockCandidate> broken,
                                  Vec3d direction, boolean playSound) {
        if (broken.isEmpty()) {
            return;
        }

        ServerWorld world = player.getServerWorld();
        int sampleCount = Math.min(SuperFlightTuning.MAX_TUNNEL_DEBRIS_SAMPLES, broken.size());
        for (int sample = 0; sample < sampleCount; sample++) {
            int index = sampleCount == 1 ? 0 : sample * (broken.size() - 1) / (sampleCount - 1);
            SuperFlightBlockCandidate candidate = broken.get(index);
            Vec3d debrisOrigin = Vec3d.ofCenter(candidate.pos()).add(direction.negate().multiply(0.18));
            world.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, candidate.state()),
                    debrisOrigin.x, debrisOrigin.y, debrisOrigin.z,
                    SuperFlightTuning.TUNNEL_DEBRIS_PER_SAMPLE, 0.12, 0.12, 0.12, 0.08);
        }

        if (playSound) {
            SuperFlightBlockCandidate representative = broken.get(0);
            world.playSound(null, representative.pos(), representative.state().getSoundGroup().getBreakSound(),
                    SoundCategory.BLOCKS, 0.75F, representative.state().getSoundGroup().getPitch());
        }
    }

    static void emitHardWallFeedback(ServerPlayerEntity player, SuperFlightBlockCandidate blocker, Vec3d direction) {
        if (blocker == null) {
            return;
        }

        ServerWorld world = player.getServerWorld();
        Vec3d impact = Vec3d.ofCenter(blocker.pos()).add(direction.negate().multiply(0.08));
        world.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, blocker.state()),
                impact.x, impact.y, impact.z, 5, 0.14, 0.14, 0.14, 0.06);
        world.spawnParticles(ParticleTypes.WHITE_ASH, impact.x, impact.y, impact.z, 5, 0.18, 0.18, 0.18, 0.02);
        world.spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0x9E1823).toVector3f(), 0.95F),
                impact.x, impact.y, impact.z, 3, 0.16, 0.16, 0.16, 0.01);
        world.spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0xE6B83D).toVector3f(), 0.95F),
                impact.x, impact.y, impact.z, 3, 0.16, 0.16, 0.16, 0.01);
        world.playSound(null, blocker.pos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 0.9F, 0.68F);
    }

    record BreakResult(List<SuperFlightBlockCandidate> broken, double totalResistance, boolean budgetExhausted) {
        BreakResult {
            broken = List.copyOf(broken);
        }

        boolean madeProgress() {
            return !broken.isEmpty();
        }
    }
}
