package io.github.ikunkk02afk.chinesecanfly.ability.combat;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/** Tracks an original thrown entity until its first solid impact or a short timeout. */
public final class ThrownEntityManager {
    private static final Map<UUID, ThrownEntityState> THROWN = new HashMap<>();

    private ThrownEntityManager() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(ThrownEntityManager::tick);
    }

    public static void track(LivingEntity target, ServerPlayerEntity thrower, Vec3d velocity) {
        THROWN.put(target.getUuid(), new ThrownEntityState(target.getUuid(), thrower.getUuid(),
                target.getWorld().getRegistryKey(), velocity, CombatTuning.THROW_TRACKING_TICKS));
    }

    public static void clearFor(ServerPlayerEntity player) {
        THROWN.entrySet().removeIf(entry -> entry.getValue().throwerId().equals(player.getUuid()));
    }

    private static void tick(MinecraftServer server) {
        Iterator<Map.Entry<UUID, ThrownEntityState>> iterator = THROWN.entrySet().iterator();
        while (iterator.hasNext()) {
            ThrownEntityState state = iterator.next().getValue();
            Entity entity = findEntity(server, state.targetId());
            if (!(entity instanceof LivingEntity target)
                    || target.isRemoved()
                    || !target.isAlive()
                    || target.getWorld().getRegistryKey() != state.worldKey()
                    || state.remainingTicks() <= 0) {
                iterator.remove();
                continue;
            }

            ServerWorld world = (ServerWorld) target.getWorld();
            boolean collided = state.remainingTicks() < CombatTuning.THROW_TRACKING_TICKS
                    && (target.horizontalCollision || target.verticalCollision || target.isOnGround()
                    || intersectsSolid(world, target, state.previousVelocity()));
            if (collided && state.previousVelocity().lengthSquared() > 0.04) {
                ServerPlayerEntity thrower = server.getPlayerManager().getPlayer(state.throwerId());
                Entity attacker = thrower != null ? thrower : target;
                target.damage(CombatDamageTypes.causedBy(target, CombatDamageTypes.THROWN_IMPACT, target, attacker),
                        CombatMath.thrownImpactDamage(state.previousVelocity().length()));
                Vec3d impact = target.getPos().add(0.0, target.getHeight() * 0.5, 0.0);
                world.spawnParticles(net.minecraft.particle.ParticleTypes.CLOUD, impact.x, impact.y, impact.z,
                        14, 0.25, 0.25, 0.25, 0.10);
                world.playSound(null, target.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE.value(),
                        SoundCategory.PLAYERS, 0.75F, 1.05F);
                iterator.remove();
                continue;
            }
            THROWN.put(state.targetId(), state.next(target.getVelocity()));
        }
    }

    private static boolean intersectsSolid(ServerWorld world, LivingEntity target, Vec3d previousVelocity) {
        if (previousVelocity.lengthSquared() < 1.0E-6) {
            return false;
        }
        Box previousBox = target.getBoundingBox().offset(previousVelocity.negate());
        Box swept = previousBox.stretch(previousVelocity).expand(1.0E-3);
        return world.getBlockCollisions(target, swept).iterator().hasNext();
    }

    private static Entity findEntity(MinecraftServer server, UUID uuid) {
        for (var world : server.getWorlds()) {
            Entity entity = world.getEntity(uuid);
            if (entity != null) {
                return entity;
            }
        }
        return null;
    }
}
