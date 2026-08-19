package io.github.ikunkk02afk.chinesecanfly.ability.superflight;

import io.github.ikunkk02afk.chinesecanfly.registry.ModBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/** Server-side protection and hardness rules for super-flight tunnelling. */
/** Server-side protection and hardness rules shared by the player and held-entity impact probes. */
public final class SuperFlightBlockRules {
    private SuperFlightBlockRules() {
    }

    public static boolean canBreak(ServerWorld world, BlockPos pos, BlockState state, double speed) {
        if (state.isAir() || state.isIn(ModBlockTags.SUPER_FLIGHT_IMMUNE) || world.getBlockEntity(pos) != null) {
            return false;
        }
        double hardness = state.getHardness(world, pos);
        return hardness >= 0.0 && hardness <= SuperFlightTuning.maximumBreakableHardness(speed);
    }
}
