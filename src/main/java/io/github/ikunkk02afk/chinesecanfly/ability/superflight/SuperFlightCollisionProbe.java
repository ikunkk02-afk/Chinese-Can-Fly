package io.github.ikunkk02afk.chinesecanfly.ability.superflight;

import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

import java.util.ArrayList;
import java.util.List;

/** Checks only the player's next swept volume and never asks the server to load chunks. */
final class SuperFlightCollisionProbe {
    private SuperFlightCollisionProbe() {
    }

    static SuperFlightPathResult probe(ServerPlayerEntity player, Vec3d velocity, double speed) {
        ServerWorld world = player.getServerWorld();
        Box playerPath = player.getBoundingBox().stretch(velocity).expand(1.0E-3);
        if (!areChunksLoaded(world, playerPath)) {
            return SuperFlightPathResult.unloaded();
        }
        if (!world.getBlockCollisions(player, playerPath).iterator().hasNext()) {
            return SuperFlightPathResult.clear();
        }
        Box tunnelPath = tunnelBox(player.getBoundingBox()).stretch(velocity).expand(1.0E-3);
        if (!areChunksLoaded(world, tunnelPath)) {
            return SuperFlightPathResult.unloaded();
        }

        List<BlockPos> sampledPositions = collectSampledPositions(player, velocity);
        List<SuperFlightBlockCandidate> candidates = new ArrayList<>();
        Vec3d direction = velocity.normalize();
        for (BlockPos pos : SuperFlightTunnelMath.sortAndDeduplicate(sampledPositions, player.getPos(), direction)) {
            BlockState state = world.getBlockState(pos);
            VoxelShape shape = state.getCollisionShape(world, pos);
            if (shape.isEmpty() || !intersectsSampledTunnel(player, velocity, pos, shape)) {
                continue;
            }

            boolean intersectsPlayerPath = intersects(shape, pos, playerPath);
            double hardness = state.getHardness(world, pos);
            candidates.add(new SuperFlightBlockCandidate(
                    pos.toImmutable(), state, hardness,
                    SuperFlightTunnelMath.projection(player.getPos(), direction, Vec3d.ofCenter(pos)),
                    intersectsPlayerPath, SuperFlightBlockRules.canBreak(world, pos, state, speed)
            ));
        }

        return resultFor(candidates);
    }

    private static SuperFlightPathResult resultFor(List<SuperFlightBlockCandidate> candidates) {
        SuperFlightBlockCandidate hardBlocker = null;
        List<SuperFlightBlockCandidate> breakCandidates = new ArrayList<>();
        for (SuperFlightBlockCandidate candidate : candidates) {
            if (candidate.intersectsPlayerPath() && !candidate.breakable()) {
                hardBlocker = candidate;
                break;
            }
            if (candidate.breakable()) {
                breakCandidates.add(candidate);
            }
        }

        if (hardBlocker != null) {
            return new SuperFlightPathResult(SuperFlightPathStatus.UNBREAKABLE_OBSTRUCTION, breakCandidates, hardBlocker);
        }
        if (!breakCandidates.isEmpty()) {
            return new SuperFlightPathResult(SuperFlightPathStatus.BREAKABLE_OBSTRUCTION, breakCandidates, null);
        }
        return new SuperFlightPathResult(SuperFlightPathStatus.UNBREAKABLE_OBSTRUCTION, List.of(), null);
    }

    private static List<BlockPos> collectSampledPositions(ServerPlayerEntity player, Vec3d velocity) {
        int samples = Math.max(1, MathHelper.ceil(velocity.length() / SuperFlightTuning.TUNNEL_SAMPLE_STEP));
        List<BlockPos> positions = new ArrayList<>();
        for (int sample = 1; sample <= samples; sample++) {
            Box tunnel = tunnelBox(player.getBoundingBox().offset(velocity.multiply((double) sample / samples)));
            addPositions(tunnel, positions);
        }
        return positions;
    }

    private static boolean intersectsSampledTunnel(ServerPlayerEntity player, Vec3d velocity, BlockPos pos, VoxelShape shape) {
        int samples = Math.max(1, MathHelper.ceil(velocity.length() / SuperFlightTuning.TUNNEL_SAMPLE_STEP));
        for (int sample = 1; sample <= samples; sample++) {
            Box tunnel = tunnelBox(player.getBoundingBox().offset(velocity.multiply((double) sample / samples)));
            if (intersects(shape, pos, tunnel)) {
                return true;
            }
        }
        return false;
    }

    private static Box tunnelBox(Box playerBox) {
        return playerBox.expand(
                SuperFlightTuning.TUNNEL_HORIZONTAL_MARGIN,
                SuperFlightTuning.TUNNEL_VERTICAL_MARGIN,
                SuperFlightTuning.TUNNEL_HORIZONTAL_MARGIN
        );
    }

    private static boolean intersects(VoxelShape shape, BlockPos pos, Box target) {
        for (Box shapeBox : shape.getBoundingBoxes()) {
            if (shapeBox.offset(pos).intersects(target)) {
                return true;
            }
        }
        return false;
    }

    private static void addPositions(Box box, List<BlockPos> positions) {
        int minX = MathHelper.floor(box.minX);
        int minY = MathHelper.floor(box.minY);
        int minZ = MathHelper.floor(box.minZ);
        int maxX = MathHelper.floor(box.maxX);
        int maxY = MathHelper.floor(box.maxY);
        int maxZ = MathHelper.floor(box.maxZ);
        for (BlockPos pos : BlockPos.iterate(minX, minY, minZ, maxX, maxY, maxZ)) {
            positions.add(pos.toImmutable());
        }
    }

    private static boolean areChunksLoaded(ServerWorld world, Box box) {
        int minChunkX = MathHelper.floor(box.minX) >> 4;
        int maxChunkX = MathHelper.floor(box.maxX) >> 4;
        int minChunkZ = MathHelper.floor(box.minZ) >> 4;
        int maxChunkZ = MathHelper.floor(box.maxZ) >> 4;

        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                if (!world.isChunkLoaded(chunkX, chunkZ)) {
                    return false;
                }
            }
        }
        return true;
    }
}
