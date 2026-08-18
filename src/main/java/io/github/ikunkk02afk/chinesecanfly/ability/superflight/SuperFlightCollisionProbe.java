package io.github.ikunkk02afk.chinesecanfly.ability.superflight;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/** Checks only the player's next swept volume and never asks the server to load chunks. */
final class SuperFlightCollisionProbe {
    private SuperFlightCollisionProbe() {
    }

    static boolean isPathClear(ServerPlayerEntity player, Vec3d velocity) {
        ServerWorld world = player.getServerWorld();
        Box swept = player.getBoundingBox().stretch(velocity).expand(1.0E-3);
        if (!areChunksLoaded(world, swept)) {
            return false;
        }
        return !world.getBlockCollisions(player, swept).iterator().hasNext();
    }

    private static boolean areChunksLoaded(ServerWorld world, Box box) {
        int minChunkX = MathHelper.floor(box.minX) >> 4;
        int maxChunkX = MathHelper.floor(box.maxX) >> 4;
        int minChunkZ = MathHelper.floor(box.minZ) >> 4;
        int maxChunkZ = MathHelper.floor(box.maxZ) >> 4;

        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                if (!world.isChunkLoaded(chunkX, chunkZ)) {
                    return false;
                }
            }
        }
        return true;
    }
}
