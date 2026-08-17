package io.github.ikunkk02afk.chinesecanfly.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public final class ChineseDictionaryItem extends Item {
    public ChineseDictionaryItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.chinese_can_fly.chinese_dictionary.line_1").formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("tooltip.chinese_can_fly.chinese_dictionary.line_2").formatted(Formatting.DARK_PURPLE));
    }
}
