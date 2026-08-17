package io.github.ikunkk02afk.chinesecanfly.rubbing;

import io.github.ikunkk02afk.chinesecanfly.registry.ModDataComponents;
import io.github.ikunkk02afk.chinesecanfly.registry.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Optional;

public final class CharacterRubbingItem extends Item {
    public CharacterRubbingItem(Settings settings) {
        super(settings);
    }

    public static Optional<ItemStack> createStack(String character) {
        if (!RubbingCharacterValidation.isValid(character)) {
            return Optional.empty();
        }

        ItemStack stack = new ItemStack(ModItems.CHARACTER_RUBBING);
        stack.set(ModDataComponents.INSCRIPTION_CHARACTER, character);
        return Optional.of(stack);
    }

    public static Optional<String> getCharacter(ItemStack stack) {
        if (!stack.isOf(ModItems.CHARACTER_RUBBING)) {
            return Optional.empty();
        }

        return Optional.ofNullable(stack.get(ModDataComponents.INSCRIPTION_CHARACTER))
                .filter(RubbingCharacterValidation::isValid);
    }

    @Override
    public Text getName(ItemStack stack) {
        return getCharacter(stack)
                .<Text>map(character -> Text.translatable("item.chinese_can_fly.character_rubbing.with_character", character))
                .orElseGet(() -> super.getName(stack));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        getCharacter(stack).ifPresent(character -> {
            tooltip.add(Text.translatable("tooltip.chinese_can_fly.character_rubbing.character", character));
            tooltip.add(Text.translatable("tooltip.chinese_can_fly.character_rubbing.origin").formatted(Formatting.GRAY));
        });
    }
}
