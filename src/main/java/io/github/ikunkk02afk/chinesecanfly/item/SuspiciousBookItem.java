package io.github.ikunkk02afk.chinesecanfly.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public final class SuspiciousBookItem extends Item {
    public SuspiciousBookItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        // This only permits the stack to occupy the vanilla enchanting-table input slot.
        // EnchantmentScreenHandlerMixin intercepts every attempt to enchant this item.
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.chinese_can_fly.suspicious_book.line_1").formatted(Formatting.DARK_GRAY));
        tooltip.add(Text.translatable("tooltip.chinese_can_fly.suspicious_book.line_2").formatted(Formatting.DARK_PURPLE));
        tooltip.add(Text.translatable("tooltip.chinese_can_fly.suspicious_book.line_3").formatted(Formatting.GOLD));
    }
}
