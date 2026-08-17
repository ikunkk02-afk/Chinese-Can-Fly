package io.github.ikunkk02afk.chinesecanfly.recipe;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

final class SuspiciousBookRecipeLayout {
    static final int GRID_SIZE = 3;
    static final int REQUIRED_RUBBING_COUNT = 8;

    private SuspiciousBookRecipeLayout() {
    }

    static boolean matches(int width, int height, int slotCount, boolean hasCenteredBook, List<Optional<String>> rubbingCharacters) {
        if (width != GRID_SIZE || height != GRID_SIZE || slotCount != GRID_SIZE * GRID_SIZE || !hasCenteredBook
                || rubbingCharacters.size() != REQUIRED_RUBBING_COUNT) {
            return false;
        }

        Set<String> characters = new HashSet<>();
        for (Optional<String> character : rubbingCharacters) {
            if (character.isEmpty() || !characters.add(character.get())) {
                return false;
            }
        }

        return characters.size() == REQUIRED_RUBBING_COUNT;
    }
}
