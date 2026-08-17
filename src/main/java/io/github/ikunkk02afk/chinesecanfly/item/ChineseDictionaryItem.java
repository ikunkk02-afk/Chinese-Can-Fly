package io.github.ikunkk02afk.chinesecanfly.item;

import io.github.ikunkk02afk.chinesecanfly.awakening.DictionaryAwakeningService;
import io.github.ikunkk02afk.chinesecanfly.component.ChinesePowerComponent;
import io.github.ikunkk02afk.chinesecanfly.registry.ModComponents;
import io.github.ikunkk02afk.chinesecanfly.registry.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public final class ChineseDictionaryItem extends Item {
    private static final int READING_TICKS = 200;
    private static final int PROGRESS_UPDATE_INTERVAL = 10;
    private static final int PAGE_TURN_INTERVAL = 50;

    public ChineseDictionaryItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient && user instanceof ServerPlayerEntity player) {
            ChinesePowerComponent component = ModComponents.CHINESE_POWER.get(player);
            if (component.hasReadDictionary()) {
                player.sendMessage(Text.translatable("actionbar.chinese_can_fly.chinese_dictionary.already_read"), true);
                return TypedActionResult.fail(stack);
            }
        }

        user.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return READING_TICKS;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (world.isClient || !(user instanceof ServerPlayerEntity player)
                || !player.isAlive() || !player.isUsingItem()
                || !player.getActiveItem().isOf(ModItems.CHINESE_DICTIONARY)) {
            return;
        }

        int elapsedTicks = READING_TICKS - remainingUseTicks;
        if (elapsedTicks <= 0) {
            return;
        }

        if (elapsedTicks % PROGRESS_UPDATE_INTERVAL == 0) {
            int percentage = Math.min(99, elapsedTicks * 100 / READING_TICKS);
            player.sendMessage(Text.translatable("actionbar.chinese_can_fly.chinese_dictionary.reading", percentage), true);
            spawnReadingParticles((ServerWorld) world, player, elapsedTicks);
        }

        if (elapsedTicks % PAGE_TURN_INTERVAL == 0) {
            world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 0.28F, 0.92F);
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient && user instanceof ServerPlayerEntity player
                && player.isAlive() && player.isUsingItem()
                && player.getActiveItem().isOf(ModItems.CHINESE_DICTIONARY)
                && stack.isOf(ModItems.CHINESE_DICTIONARY)
                && DictionaryAwakeningService.completeReading(player, stack)) {
            player.sendMessage(Text.translatable("actionbar.chinese_can_fly.chinese_dictionary.reading", 100), true);
        }
        return stack;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.chinese_can_fly.chinese_dictionary.line_1").formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("tooltip.chinese_can_fly.chinese_dictionary.line_2").formatted(Formatting.DARK_PURPLE));
    }

    private static void spawnReadingParticles(ServerWorld world, ServerPlayerEntity player, int elapsedTicks) {
        float progress = elapsedTicks / (float) READING_TICKS;
        int count = progress < 0.5F ? 1 : 2;
        double x = player.getX();
        double y = player.getY() + 0.9;
        double z = player.getZ();

        world.spawnParticles(ParticleTypes.ENCHANT, x, y, z, count, 0.32, 0.5, 0.32, 0.015);
        world.spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0x9E1823).toVector3f(), 0.75F),
                x, y, z, 1, 0.28, 0.42, 0.28, 0.005);
        if (progress >= 0.5F) {
            world.spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0xE6B83D).toVector3f(), 0.72F),
                    x, y + 0.18, z, 1, 0.25, 0.48, 0.25, 0.006);
        }
    }
}
