package io.github.ikunkk02afk.chinesecanfly.ability.combat;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

/** Runtime-only throw trajectory retained just long enough to detect the first wall impact. */
record ThrownEntityState(UUID targetId, UUID throwerId, RegistryKey<World> worldKey, Vec3d previousVelocity,
                         int remainingTicks) {
    ThrownEntityState next(Vec3d velocity) {
        return new ThrownEntityState(targetId, throwerId, worldKey, velocity, remainingTicks - 1);
    }
}
