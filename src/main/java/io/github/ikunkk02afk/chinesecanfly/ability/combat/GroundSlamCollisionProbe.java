package io.github.ikunkk02afk.chinesecanfly.ability.combat;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/** Samples the downward swept player box to prevent a high-speed slam from tunnelling through terrain or fluids. */
final class GroundSlamCollisionProbe {
    private GroundSlamCollisionProbe() {
    }

    static Result probe(ServerPlayerEntity player, Vec3d velocity) {
        ServerWorld world = player.getServerWorld();
        Box base = player.getBoundingBox();
        int samples = Math.max(1, MathHelper.ceil(velocity.length() / CombatTuning.SLAM_SAMPLE_STEP));
        Vec3d safePosition = player.getPos();
        for (int sample = 1; sample <= samples; sample++) {
            double progress = (double) sample / samples;
            Box box = base.offset(velocity.multiply(progress));
            if (touchesFluid(world, box)) {
                return Result.fluid();
            }
            if (world.getBlockCollisions(player, box).iterator().hasNext()) {
                Vec3d impact = new Vec3d((box.minX + box.maxX) * 0.5, box.minY, (box.minZ + box.maxZ) * 0.5);
                return Result.impact(safePosition, impact);
            }
            safePosition = player.getPos().add(velocity.multiply(progress));
        }
        return Result.clear();
    }

    private static boolean touchesFluid(ServerWorld world, Box box) {
        int minX = MathHelper.floor(box.minX);
        int minY = MathHelper.floor(box.minY);
        int minZ = MathHelper.floor(box.minZ);
        int maxX = MathHelper.floor(box.maxX);
        int maxY = MathHelper.floor(box.maxY);
        int maxZ = MathHelper.floor(box.maxZ);
        for (BlockPos pos : BlockPos.iterate(minX, minY, minZ, maxX, maxY, maxZ)) {
            if (!world.getFluidState(pos).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    record Result(Kind kind, Vec3d safePosition, Vec3d impactPosition) {
        static Result clear() {
            return new Result(Kind.CLEAR, Vec3d.ZERO, Vec3d.ZERO);
        }

        static Result fluid() {
            return new Result(Kind.FLUID, Vec3d.ZERO, Vec3d.ZERO);
        }

        static Result impact(Vec3d safePosition, Vec3d impactPosition) {
            return new Result(Kind.IMPACT, safePosition, impactPosition);
        }
    }

    enum Kind {
        CLEAR,
        FLUID,
        IMPACT
    }
}
