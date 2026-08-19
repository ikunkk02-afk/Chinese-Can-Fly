package io.github.ikunkk02afk.chinesecanfly.ability.superflight;

import io.github.ikunkk02afk.chinesecanfly.registry.ModBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/** Server-side protection and hardness rules for super-flight tunnelling. */
final class SuperFlightBlockRules {
    private SuperFlightBlockRules() {
    }

    static boolean canBreak(ServerWorld world, BlockPos pos, BlockState state, double speed) {
        if (state.isAir() || state.isIn(ModBlockTags.SUPER_FLIGHT_IMMUNE) || world.getBlockEntity(pos) != null) {
            return false;
        }
        double hardness = state.getHardness(world, pos);
        return hardness >= 0.0 && hardness <= SuperFlightTuning.maximumBreakableHardness(speed);
    }
}
