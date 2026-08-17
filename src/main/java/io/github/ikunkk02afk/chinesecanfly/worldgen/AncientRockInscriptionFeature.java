package io.github.ikunkk02afk.chinesecanfly.worldgen;

import com.mojang.serialization.Codec;
import io.github.ikunkk02afk.chinesecanfly.block.InscribedRockBlock;
import io.github.ikunkk02afk.chinesecanfly.block.entity.InscribedRockBlockEntity;
import io.github.ikunkk02afk.chinesecanfly.inscription.InscriptionCharacters;
import io.github.ikunkk02afk.chinesecanfly.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.ArrayList;
import java.util.List;

public final class AncientRockInscriptionFeature extends Feature<DefaultFeatureConfig> {
    private static final int SEARCH_DEPTH = 16;
    private static final Direction[] HORIZONTAL_DIRECTIONS = {
            Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST
    };

    public AncientRockInscriptionFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();

        for (int depth = 1; depth <= SEARCH_DEPTH; depth++) {
            if (tryPlaceInscription(world, origin.down(depth), context.getRandom())) {
                return true;
            }
        }

        return false;
    }

    private static boolean tryPlaceInscription(StructureWorldAccess world, BlockPos pos, Random random) {
        BlockState targetState = world.getBlockState(pos);
        if (!targetState.isIn(BlockTags.BASE_STONE_OVERWORLD) || world.getBlockEntity(pos) != null) {
            return false;
        }

        List<Direction> exposedFaces = new ArrayList<>(4);
        for (Direction direction : HORIZONTAL_DIRECTIONS) {
            BlockPos front = pos.offset(direction);
            if (world.isAir(front) && world.isAir(front.up())) {
                exposedFaces.add(direction);
            }
        }

        if (exposedFaces.isEmpty()) {
            return false;
        }

        Direction facing = exposedFaces.get(random.nextInt(exposedFaces.size()));
        if (!hasRockWallScale(world, pos, facing)) {
            return false;
        }

        BlockState inscriptionState = ModBlocks.INSCRIBED_ROCK.getDefaultState().with(InscribedRockBlock.FACING, facing);
        if (!world.setBlockState(pos, inscriptionState, Block.NOTIFY_LISTENERS)) {
            return false;
        }

        if (world.getBlockEntity(pos) instanceof InscribedRockBlockEntity inscription) {
            inscription.setCharacter(InscriptionCharacters.randomCharacter(random));
            return true;
        }

        return false;
    }

    private static boolean hasRockWallScale(StructureWorldAccess world, BlockPos pos, Direction facing) {
        Direction left = facing.rotateYCounterclockwise();
        Direction right = facing.rotateYClockwise();
        BlockPos[] wallNeighbors = {
                pos.up(),
                pos.down(),
                pos.offset(left),
                pos.offset(right)
        };

        int naturalRockNeighbors = 0;
        for (BlockPos neighbor : wallNeighbors) {
            if (world.getBlockState(neighbor).isIn(BlockTags.BASE_STONE_OVERWORLD)) {
                naturalRockNeighbors++;
            }
        }

        return naturalRockNeighbors >= 3;
    }
}
