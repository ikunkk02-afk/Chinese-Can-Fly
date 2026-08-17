package io.github.ikunkk02afk.chinesecanfly.rubbing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.github.ikunkk02afk.chinesecanfly.inscription.InscriptionCharacters;

import java.util.Optional;

public final class RubbingCharacterValidation {
    public static final Codec<String> CODEC = Codec.STRING.validate(RubbingCharacterValidation::validateForCodec);

    private RubbingCharacterValidation() {
    }

    public static boolean isValid(String character) {
        return character != null && InscriptionCharacters.isKnownCharacter(character);
    }

    public static Optional<String> getValidCharacter(String character) {
        return isValid(character) ? Optional.of(character) : Optional.empty();
    }

    private static DataResult<String> validateForCodec(String character) {
        return getValidCharacter(character)
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(() -> "Unknown inscription character"));
    }
}
