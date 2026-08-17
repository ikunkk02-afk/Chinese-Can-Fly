package io.github.ikunkk02afk.chinesecanfly.recipe;

import io.github.ikunkk02afk.chinesecanfly.registry.ModItems;
import io.github.ikunkk02afk.chinesecanfly.registry.ModRecipeSerializers;
import io.github.ikunkk02afk.chinesecanfly.rubbing.CharacterRubbingItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SuspiciousBookRecipe extends SpecialCraftingRecipe {
    public SuspiciousBookRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        return hasValidLayout(input);
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return hasValidLayout(input) ? new ItemStack(ModItems.SUSPICIOUS_BOOK) : ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.SUSPICIOUS_BOOK;
    }

    @Override
    public boolean fits(int width, int height) {
        return width == SuspiciousBookRecipeLayout.GRID_SIZE && height == SuspiciousBookRecipeLayout.GRID_SIZE;
    }

    private static boolean hasValidLayout(CraftingRecipeInput input) {
        if (input.getWidth() != SuspiciousBookRecipeLayout.GRID_SIZE
                || input.getHeight() != SuspiciousBookRecipeLayout.GRID_SIZE
                || input.getSize() != SuspiciousBookRecipeLayout.GRID_SIZE * SuspiciousBookRecipeLayout.GRID_SIZE) {
            return false;
        }

        List<Optional<String>> rubbingCharacters = new ArrayList<>(SuspiciousBookRecipeLayout.REQUIRED_RUBBING_COUNT);
        for (int y = 0; y < SuspiciousBookRecipeLayout.GRID_SIZE; y++) {
            for (int x = 0; x < SuspiciousBookRecipeLayout.GRID_SIZE; x++) {
                if (x == 1 && y == 1) {
                    continue;
                }
                rubbingCharacters.add(CharacterRubbingItem.getCharacter(input.getStackInSlot(x, y)));
            }
        }

        return SuspiciousBookRecipeLayout.matches(
                input.getWidth(),
                input.getHeight(),
                input.getSize(),
                input.getStackInSlot(1, 1).isOf(Items.BOOK),
                rubbingCharacters
        );
    }
}
