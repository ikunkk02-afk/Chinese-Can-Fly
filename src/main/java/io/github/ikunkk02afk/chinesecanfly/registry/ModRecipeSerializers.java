package io.github.ikunkk02afk.chinesecanfly.registry;

import io.github.ikunkk02afk.chinesecanfly.ChineseCanFly;
import io.github.ikunkk02afk.chinesecanfly.recipe.SuspiciousBookRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModRecipeSerializers {
    public static final RecipeSerializer<SuspiciousBookRecipe> SUSPICIOUS_BOOK = Registry.register(
            Registries.RECIPE_SERIALIZER,
            Identifier.of(ChineseCanFly.MOD_ID, "suspicious_book"),
            new SpecialRecipeSerializer<>(SuspiciousBookRecipe::new)
    );

    private ModRecipeSerializers() {
    }

    public static void register() {
        // Class loading performs registry registration.
    }
}
