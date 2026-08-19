package io.github.ikunkk02afk.chinesecanfly.ability.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Swept entity collision for server-authoritative high-speed flight. */
public final class SuperFlightEntityImpactManager {
    private static final Map<UUID, PlayerImpactState> STATES = new HashMap<>();

    private SuperFlightEntityImpactManager() {
    }

    public static void process(ServerPlayerEntity player, Vec3d velocity, double speed) {
        if (speed < CombatTuning.MIN_RAM_SPEED || velocity.lengthSquared() < 1.0E-6) {
            return;
        }

        ServerWorld world = player.getServerWorld();
        long tick = world.getTime();
        PlayerImpactState state = STATES.computeIfAbsent(player.getUuid(), ignored -> new PlayerImpactState());
        Vec3d direction = velocity.normalize();
        collectTargets(player, world, state, velocity, direction, speed, tick);
        processQueuedTargets(player, world, state, tick);
    }

    public static void clear(ServerPlayerEntity player) {
        STATES.remove(player.getUuid());
    }

    private static void collectTargets(ServerPlayerEntity player, ServerWorld world, PlayerImpactState state,
                                       Vec3d velocity, Vec3d direction, double speed, long tick) {
        Box swept = player.getBoundingBox().stretch(velocity).expand(0.15);
        List<LivingEntity> targets = new ArrayList<>();
        for (Entity entity : world.getOtherEntities(player, swept, candidate -> isValidTarget(player, candidate))) {
            targets.add((LivingEntity) entity);
        }
        targets.sort(Comparator.comparingDouble(target -> projection(player.getPos(), direction, target.getPos())));

        for (LivingEntity target : targets) {
            if (state.queued.add(target.getUuid())) {
                state.queue.addLast(new QueuedImpact(target.getUuid(), direction, speed, tick + CombatTuning.RAM_QUEUE_TTL_TICKS));
            }
        }
    }

    private static void processQueuedTargets(ServerPlayerEntity player, ServerWorld world, PlayerImpactState state, long tick) {
        int processed = 0;
        while (!state.queue.isEmpty() && processed < CombatTuning.MAX_RAM_TARGETS_PER_TICK) {
            QueuedImpact queued = state.queue.removeFirst();
            state.queued.remove(queued.targetId());
            if (queued.expiresAt() < tick) {
                continue;
            }

            Entity entity = world.getEntity(queued.targetId());
            if (!(entity instanceof LivingEntity target) || !isValidTarget(player, target)) {
                continue;
            }
            long lastImpact = state.lastImpactTick.getOrDefault(queued.targetId(), Long.MIN_VALUE / 4);
            if (tick - lastImpact < CombatTuning.RAM_TARGET_COOLDOWN_TICKS) {
                continue;
            }

            processed++;
            float damage = CombatMath.ramDamage(queued.speed());
            if (target.damage(CombatDamageTypes.causedBy(target, CombatDamageTypes.SUPER_FLIGHT_IMPACT, player), damage)) {
                target.takeKnockback(CombatTuning.RAM_KNOCKBACK, queued.direction().x, queued.direction().z);
                state.lastImpactTick.put(queued.targetId(), tick);
                Vec3d impact = target.getPos().add(0.0, target.getHeight() * 0.5, 0.0);
                world.spawnParticles(net.minecraft.particle.ParticleTypes.CLOUD, impact.x, impact.y, impact.z,
                        10, 0.25, 0.25, 0.25, 0.12);
                world.playSound(null, target.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE.value(),
                        SoundCategory.PLAYERS, 0.6F, 1.15F);
            }
        }
        state.lastImpactTick.entrySet().removeIf(entry -> tick - entry.getValue() > CombatTuning.RAM_TARGET_COOLDOWN_TICKS * 4L);
    }

    private static boolean isValidTarget(ServerPlayerEntity player, Entity entity) {
        return entity instanceof LivingEntity living
                && living != player
                && living.isAlive()
                && !living.isRemoved()
                && !living.isSpectator()
                && !HeldEntityManager.isHeldBy(player, living);
    }

    private static double projection(Vec3d origin, Vec3d direction, Vec3d point) {
        return point.subtract(origin).dotProduct(direction);
    }

    private record QueuedImpact(UUID targetId, Vec3d direction, double speed, long expiresAt) {
    }

    private static final class PlayerImpactState {
        private final Deque<QueuedImpact> queue = new ArrayDeque<>();
        private final Set<UUID> queued = new HashSet<>();
        private final Map<UUID, Long> lastImpactTick = new HashMap<>();
    }
}
