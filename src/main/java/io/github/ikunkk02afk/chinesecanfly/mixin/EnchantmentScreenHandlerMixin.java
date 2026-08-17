package io.github.ikunkk02afk.chinesecanfly.mixin;

import io.github.ikunkk02afk.chinesecanfly.registry.ModItems;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentScreenHandler.class)
public abstract class EnchantmentScreenHandlerMixin {
    private static final int REQUIRED_ENCHANTING_POWER = 15;
    private static final int HIGHEST_ENCHANTMENT_OPTION = 2;
    private static final int HIGHEST_ENCHANTMENT_LEVEL = 30;
    private static final int HIGHEST_ENCHANTMENT_COST = 3;

    @Shadow
    @Final
    private Inventory inventory;

    @Shadow
    @Final
    private ScreenHandlerContext context;

    @Shadow
    @Final
    private Property seed;

    @Shadow
    public int[] enchantmentPower;

    @Shadow
    public int[] enchantmentId;

    @Shadow
    public int[] enchantmentLevel;

    @Shadow
    public abstract boolean canUse(PlayerEntity player);

    @Inject(method = "onContentChanged", at = @At("HEAD"), cancellable = true)
    private void chineseCanFly$setSuspiciousBookOptions(Inventory changedInventory, CallbackInfo ci) {
        if (changedInventory != this.inventory || !this.inventory.getStack(0).isOf(ModItems.SUSPICIOUS_BOOK)) {
            return;
        }

        this.clearEnchantmentOptions();
        if (this.getVanillaEnchantingPower() >= REQUIRED_ENCHANTING_POWER) {
            this.enchantmentPower[HIGHEST_ENCHANTMENT_OPTION] = HIGHEST_ENCHANTMENT_LEVEL;
        }

        this.sendContentUpdates();
        ci.cancel();
    }

    @Inject(method = "onButtonClick", at = @At("HEAD"), cancellable = true)
    private void chineseCanFly$convertSuspiciousBook(PlayerEntity player, int id, CallbackInfoReturnable<Boolean> cir) {
        if (!this.inventory.getStack(0).isOf(ModItems.SUSPICIOUS_BOOK)) {
            return;
        }

        // The client only uses this return value to decide whether to send the normal vanilla button packet.
        // The server below independently repeats every meaningful validation before changing any inventory.
        if (player.getWorld().isClient) {
            cir.setReturnValue(id == HIGHEST_ENCHANTMENT_OPTION && this.enchantmentPower[id] == HIGHEST_ENCHANTMENT_LEVEL);
            return;
        }

        if (id != HIGHEST_ENCHANTMENT_OPTION
                || !this.canUse(player)
                || this.enchantmentPower[HIGHEST_ENCHANTMENT_OPTION] != HIGHEST_ENCHANTMENT_LEVEL
                || this.getVanillaEnchantingPower() < REQUIRED_ENCHANTING_POWER) {
            cir.setReturnValue(false);
            return;
        }

        ItemStack lapis = this.inventory.getStack(1);
        boolean creativeMode = player.getAbilities().creativeMode;
        if (!creativeMode && (player.experienceLevel < HIGHEST_ENCHANTMENT_LEVEL || lapis.getCount() < HIGHEST_ENCHANTMENT_COST)) {
            cir.setReturnValue(false);
            return;
        }

        this.context.run((world, pos) -> {
            ItemStack currentInput = this.inventory.getStack(0);
            ItemStack currentLapis = this.inventory.getStack(1);
            if (!currentInput.isOf(ModItems.SUSPICIOUS_BOOK)
                    || this.getVanillaEnchantingPower(world, pos) < REQUIRED_ENCHANTING_POWER
                    || (!creativeMode && (player.experienceLevel < HIGHEST_ENCHANTMENT_LEVEL
                    || currentLapis.getCount() < HIGHEST_ENCHANTMENT_COST))) {
                return;
            }

            player.applyEnchantmentCosts(currentInput, creativeMode ? 0 : HIGHEST_ENCHANTMENT_COST);
            currentLapis.decrementUnlessCreative(HIGHEST_ENCHANTMENT_COST, player);
            if (currentLapis.isEmpty()) {
                this.inventory.setStack(1, ItemStack.EMPTY);
            }

            ItemStack dictionary = new ItemStack(ModItems.CHINESE_DICTIONARY);
            this.inventory.setStack(0, dictionary);
            this.seed.set(player.getEnchantmentTableSeed());
            player.incrementStat(Stats.ENCHANT_ITEM);
            if (player instanceof ServerPlayerEntity serverPlayer) {
                Criteria.ENCHANTED_ITEM.trigger(serverPlayer, dictionary, HIGHEST_ENCHANTMENT_COST);
            }

            world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.15F, 0.9F);
            if (world instanceof ServerWorld serverWorld) {
                double x = pos.getX() + 0.5;
                double y = pos.getY() + 1.0;
                double z = pos.getZ() + 0.5;
                serverWorld.spawnParticles(ParticleTypes.ENCHANT, x, y, z, 12, 0.35, 0.35, 0.35, 0.02);
                serverWorld.spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0x9E1823).toVector3f(), 1.1F), x, y, z, 7, 0.28, 0.24, 0.28, 0.01);
                serverWorld.spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0xE6B83D).toVector3f(), 1.0F), x, y, z, 7, 0.28, 0.24, 0.28, 0.01);
            }

            player.sendMessage(Text.translatable("actionbar.chinese_can_fly.enchanting.success"), true);
            this.inventory.markDirty();
            this.sendContentUpdates();
        });
        cir.setReturnValue(true);
    }

    private void clearEnchantmentOptions() {
        for (int slot = 0; slot < this.enchantmentPower.length; slot++) {
            this.enchantmentPower[slot] = 0;
            this.enchantmentId[slot] = -1;
            this.enchantmentLevel[slot] = -1;
        }
    }

    private void sendContentUpdates() {
        ((ScreenHandler)(Object)this).sendContentUpdates();
    }

    private int getVanillaEnchantingPower() {
        return this.context.get(EnchantmentScreenHandlerMixin::getVanillaEnchantingPower).orElse(0);
    }

    private static int getVanillaEnchantingPower(World world, BlockPos tablePos) {
        int enchantingPower = 0;
        for (BlockPos offset : EnchantingTableBlock.POWER_PROVIDER_OFFSETS) {
            if (EnchantingTableBlock.canAccessPowerProvider(world, tablePos, offset)) {
                enchantingPower++;
            }
        }
        return enchantingPower;
    }
}
