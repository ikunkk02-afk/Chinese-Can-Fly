package io.github.ikunkk02afk.chinesecanfly.rubbing;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RubbingCharacterValidationTest {
    @Test
    void codecAcceptsOnlyTheExistingInscriptionCharacterPool() {
        assertEquals("飞", RubbingCharacterValidation.CODEC
                .parse(JsonOps.INSTANCE, new JsonPrimitive("飞"))
                .result()
                .orElseThrow());
        assertTrue(RubbingCharacterValidation.CODEC
                .parse(JsonOps.INSTANCE, new JsonPrimitive(""))
                .result()
                .isEmpty());
        assertTrue(RubbingCharacterValidation.CODEC
                .parse(JsonOps.INSTANCE, new JsonPrimitive("测试"))
                .result()
                .isEmpty());
    }

    @Test
    void safeValidationRejectsMissingAndForgedCharacters() {
        assertEquals("山", RubbingCharacterValidation.getValidCharacter("山").orElseThrow());
        assertTrue(RubbingCharacterValidation.getValidCharacter(null).isEmpty());
        assertTrue(RubbingCharacterValidation.getValidCharacter("A").isEmpty());
        assertTrue(RubbingCharacterValidation.getValidCharacter("飞山").isEmpty());
    }
}
