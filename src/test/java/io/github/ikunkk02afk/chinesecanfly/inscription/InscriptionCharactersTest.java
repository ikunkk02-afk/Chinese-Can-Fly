package io.github.ikunkk02afk.chinesecanfly.inscription;

import net.minecraft.util.math.random.Random;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InscriptionCharactersTest {
    @Test
    void hasExactlyFortyEightRealChineseCharacters() {
        assertEquals(48, InscriptionCharacters.size());

        Random random = Random.create(12345L);
        for (int index = 0; index < 128; index++) {
            String character = InscriptionCharacters.randomCharacter(random);
            assertEquals(1, character.codePointCount(0, character.length()));
            assertTrue(InscriptionCharacters.isKnownCharacter(character));
        }
    }

    @Test
    void acceptsTheDefaultCharacterOnlyWhenItIsInThePool() {
        assertTrue(InscriptionCharacters.isKnownCharacter(InscriptionCharacters.DEFAULT_CHARACTER));
        assertTrue(!InscriptionCharacters.isKnownCharacter("A"));
    }
}
