package io.github.ikunkk02afk.chinesecanfly.recipe;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SuspiciousBookRecipeLayoutTest {
    private static final List<Optional<String>> UNIQUE_RUBBINGS = List.of(
            Optional.of("中"), Optional.of("华"), Optional.of("人"), Optional.of("飞"),
            Optional.of("天"), Optional.of("地"), Optional.of("山"), Optional.of("河")
    );

    @Test
    void acceptsOnlyAnExactThreeByThreeLayoutWithEightUniqueCharacters() {
        assertTrue(SuspiciousBookRecipeLayout.matches(3, 3, 9, true, UNIQUE_RUBBINGS));
    }

    @Test
    void rejectsWrongDimensionsSlotCountAndBookPosition() {
        assertFalse(SuspiciousBookRecipeLayout.matches(2, 2, 4, true, UNIQUE_RUBBINGS));
        assertFalse(SuspiciousBookRecipeLayout.matches(3, 3, 8, true, UNIQUE_RUBBINGS));
        assertFalse(SuspiciousBookRecipeLayout.matches(3, 3, 9, false, UNIQUE_RUBBINGS));
    }

    @Test
    void rejectsDuplicateMissingAndNonRubbingCharacterValues() {
        assertFalse(SuspiciousBookRecipeLayout.matches(3, 3, 9, true, List.of(
                Optional.of("中"), Optional.of("华"), Optional.of("人"), Optional.of("飞"),
                Optional.of("天"), Optional.of("地"), Optional.of("山"), Optional.of("中")
        )));
        assertFalse(SuspiciousBookRecipeLayout.matches(3, 3, 9, true, List.of(
                Optional.of("中"), Optional.of("华"), Optional.of("人"), Optional.of("飞"),
                Optional.of("天"), Optional.of("地"), Optional.of("山"), Optional.empty()
        )));
        assertFalse(SuspiciousBookRecipeLayout.matches(3, 3, 9, true, UNIQUE_RUBBINGS.subList(0, 7)));
    }
}
