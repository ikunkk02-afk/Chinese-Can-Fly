package io.github.ikunkk02afk.chinesecanfly.ability.superflight;

import io.github.ikunkk02afk.chinesecanfly.ChineseCanFly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

/** Server-side protection and hardness rules for super-flight tunnelling. */
final class SuperFlightBlockRules {
    static final TagKey<Block> IMMUNE_BLOCKS = TagKey.of(
            RegistryKeys.BLOCK,
            Identifier.of(ChineseCanFly.MOD_ID, "super_flight_immune")
    );

    private SuperFlightBlockRules() {
    }

    static boolean canBreak(ServerWorld world, BlockPos pos, BlockState state, double speed) {
        if (state.isAir() || state.isIn(IMMUNE_BLOCKS) || world.getBlockEntity(pos) != null) {
            return false;
        }
        double hardness = state.getHardness(world, pos);
        return hardness >= 0.0 && hardness <= SuperFlightTuning.maximumBreakableHardness(speed);
    }
}
