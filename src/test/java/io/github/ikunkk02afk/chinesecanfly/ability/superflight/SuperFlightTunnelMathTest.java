package io.github.ikunkk02afk.chinesecanfly.ability.superflight;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SuperFlightTunnelMathTest {
    @Test
    void sampledPositionsAreDeduplicatedAndOrderedFromNearToFar() {
        List<BlockPos> ordered = SuperFlightTunnelMath.sortAndDeduplicate(
                List.of(new BlockPos(3, 0, 0), new BlockPos(1, 0, 0), new BlockPos(3, 0, 0), new BlockPos(2, 0, 0)),
                Vec3d.ZERO,
                new Vec3d(1.0, 0.0, 0.0)
        );

        assertEquals(List.of(new BlockPos(1, 0, 0), new BlockPos(2, 0, 0), new BlockPos(3, 0, 0)), ordered);
    }

    @Test
    void perTickCandidateBudgetNeverExceedsSixtyFour() {
        List<Integer> values = new ArrayList<>();
        for (int value = 0; value < 70; value++) {
            values.add(value);
        }

        List<Integer> limited = SuperFlightTunnelMath.withinBlockBudget(values);
        assertEquals(SuperFlightTuning.MAX_BLOCKS_BROKEN_PER_TICK, limited.size());
        assertEquals(0, limited.getFirst());
        assertEquals(63, limited.getLast());
    }
}
