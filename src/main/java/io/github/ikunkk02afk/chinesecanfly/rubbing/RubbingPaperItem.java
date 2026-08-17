package io.github.ikunkk02afk.chinesecanfly.rubbing;

import io.github.ikunkk02afk.chinesecanfly.block.InscribedRockBlock;
import io.github.ikunkk02afk.chinesecanfly.block.entity.InscribedRockBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public final class RubbingPaperItem extends Item {
    private static final double SURFACE_OFFSET = 0.506D;
    private static final int PARTICLE_COUNT = 6;

    public RubbingPaperItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        if (!(world.getBlockEntity(pos) instanceof InscribedRockBlockEntity inscription)) {
            return ActionResult.PASS;
        }

        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof InscribedRockBlock)) {
            return ActionResult.PASS;
        }

        Direction facing = state.get(InscribedRockBlock.FACING);
        if (context.getSide() != facing) {
            sendActionBar(context, "actionbar.chinese_can_fly.rubbing.wrong_side");
            return ActionResult.FAIL;
        }

        String character = inscription.getCharacter();
        if (!RubbingCharacterValidation.isValid(character)) {
            sendActionBar(context, "actionbar.chinese_can_fly.rubbing.invalid_character");
            return ActionResult.FAIL;
        }

        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        if (!(context.getPlayer() instanceof ServerPlayerEntity player)) {
            return ActionResult.PASS;
        }

        ItemStack rubbing = CharacterRubbingItem.createStack(character).orElseThrow();
        if (!player.getAbilities().creativeMode) {
            context.getStack().decrement(1);
        }

        ItemStack fallbackDrop = canInsertIntoMainInventory(player.getInventory(), rubbing) ? ItemStack.EMPTY : rubbing.copy();
        player.getInventory().insertStack(rubbing);
        if (!rubbing.isEmpty()) {
            world.spawnEntity(new ItemEntity(world, player.getX(), player.getY(), player.getZ(), rubbing));
        } else if (!fallbackDrop.isEmpty()) {
            world.spawnEntity(new ItemEntity(world, player.getX(), player.getY(), player.getZ(), fallbackDrop));
        }

        world.playSound(null, pos, SoundEvents.ITEM_BRUSH_BRUSHING_GENERIC, SoundCategory.BLOCKS, 0.7F, 1.0F);
        spawnRubbingParticles((ServerWorld) world, pos, facing);
        player.sendMessage(Text.translatable("actionbar.chinese_can_fly.rubbing.success", character), true);
        return ActionResult.SUCCESS;
    }

    private static void sendActionBar(ItemUsageContext context, String translationKey) {
        if (!context.getWorld().isClient && context.getPlayer() instanceof ServerPlayerEntity player) {
            player.sendMessage(Text.translatable(translationKey), true);
        }
    }

    private static void spawnRubbingParticles(ServerWorld world, BlockPos pos, Direction facing) {
        double x = pos.getX() + 0.5D + facing.getOffsetX() * SURFACE_OFFSET;
        double y = pos.getY() + 0.5D;
        double z = pos.getZ() + 0.5D + facing.getOffsetZ() * SURFACE_OFFSET;
        world.spawnParticles(ParticleTypes.ASH, x, y, z, PARTICLE_COUNT, 0.12D, 0.16D, 0.12D, 0.005D);
    }

    private static boolean canInsertIntoMainInventory(PlayerInventory inventory, ItemStack stack) {
        for (ItemStack existing : inventory.main) {
            if (existing.isEmpty() || (ItemStack.areItemsAndComponentsEqual(existing, stack)
                    && existing.getCount() < existing.getMaxCount())) {
                return true;
            }
        }

        return false;
    }
}
