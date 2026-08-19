package io.github.ikunkk02afk.chinesecanfly.ability.superflight;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

/** Small deterministic helpers shared by probing and tests. */
final class SuperFlightTunnelMath {
    private SuperFlightTunnelMath() {
    }

    static List<BlockPos> sortAndDeduplicate(List<BlockPos> positions, Vec3d origin, Vec3d direction) {
        LinkedHashSet<BlockPos> unique = new LinkedHashSet<>(positions);
        List<BlockPos> ordered = new ArrayList<>(unique);
        ordered.sort(Comparator
                .comparingDouble((BlockPos pos) -> projection(origin, direction, Vec3d.ofCenter(pos)))
                .thenComparingInt(BlockPos::getX)
                .thenComparingInt(BlockPos::getY)
                .thenComparingInt(BlockPos::getZ));
        return ordered;
    }

    static <T> List<T> withinBlockBudget(List<T> entries) {
        return entries.subList(0, Math.min(entries.size(), SuperFlightTuning.MAX_BLOCKS_BROKEN_PER_TICK));
    }

    static double projection(Vec3d origin, Vec3d direction, Vec3d point) {
        return point.subtract(origin).dotProduct(direction);
    }
}
