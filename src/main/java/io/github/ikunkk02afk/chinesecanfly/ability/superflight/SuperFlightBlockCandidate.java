package io.github.ikunkk02afk.chinesecanfly.ability.superflight;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

/** A block whose real collision shape intersects the sampled tunnelling volume. */
record SuperFlightBlockCandidate(
        BlockPos pos,
        BlockState state,
        double hardness,
        double projection,
        boolean intersectsPlayerPath,
        boolean breakable
) {
}
