package io.github.ikunkk02afk.chinesecanfly.client.superflight;

import io.github.ikunkk02afk.chinesecanfly.ability.superflight.SuperFlightTuning;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

/** Local-only air trails and one-shot sonic visuals. */
final class SuperFlightEffects {
    private static final DustParticleEffect WHITE_TRAIL = dust(0xDDE3EA, 0.78F);
    private static final DustParticleEffect RED_TRAIL = dust(0x9E1823, 0.68F);
    private static final DustParticleEffect GOLD_TRAIL = dust(0xE6B83D, 0.68F);
    private static final DustParticleEffect SONIC_RING = dust(0xE2E5E8, 1.05F);
    private static final DustParticleEffect SONIC_GOLD = dust(0xE6B83D, 0.86F);

    private SuperFlightEffects() {
    }

    static void emitTrail(MinecraftClient client, AbstractClientPlayerEntity player, boolean fast, double speed) {
        if (client.world == null || client.gameRenderer.getCamera().getPos().squaredDistanceTo(player.getPos())
                > SuperFlightTuning.TRAIL_CUTOFF_DISTANCE * SuperFlightTuning.TRAIL_CUTOFF_DISTANCE) {
            return;
        }

        double cameraDistance = client.gameRenderer.getCamera().getPos().distanceTo(player.getPos());
        double progress = Math.clamp(speed / SuperFlightTuning.MAX_SPEED, 0.0, 1.0);
        int count = SuperFlightTuning.CRUISE_TRAIL_PARTICLES
                + (int) Math.round((SuperFlightTuning.MAX_TRAIL_PARTICLES - SuperFlightTuning.CRUISE_TRAIL_PARTICLES) * progress);
        if (!fast) {
            count = SuperFlightTuning.CRUISE_TRAIL_PARTICLES;
        }
        if (cameraDistance > SuperFlightTuning.TRAIL_FULL_DISTANCE) {
            count = Math.max(1, count / 2);
        }

        Vec3d direction = player.getRotationVector().normalize();
        for (int index = 0; index < count; index++) {
            double lateralX = (client.world.random.nextDouble() - 0.5) * 0.9;
            double lateralY = (client.world.random.nextDouble() - 0.5) * 1.2;
            double lateralZ = (client.world.random.nextDouble() - 0.5) * 0.9;
            Vec3d position = player.getPos().add(0.0, player.getHeight() * 0.48, 0.0)
                    .add(direction.multiply(0.45))
                    .add(lateralX, lateralY, lateralZ);
            Vec3d velocity = direction.multiply(-0.16 - progress * 0.24)
                    .add(lateralX * 0.06, lateralY * 0.04, lateralZ * 0.06);
            DustParticleEffect particle = index % 12 == 0 ? GOLD_TRAIL : index % 8 == 0 ? RED_TRAIL : WHITE_TRAIL;
            client.world.addParticle(particle, position.x, position.y, position.z, velocity.x, velocity.y, velocity.z);
        }
    }

    static void triggerSonicBoom(MinecraftClient client, Vec3d position, Vec3d direction) {
        if (client.world == null) {
            return;
        }
        Vec3d normal = direction.normalize();
        Vec3d reference = Math.abs(normal.y) < 0.9 ? new Vec3d(0.0, 1.0, 0.0) : new Vec3d(1.0, 0.0, 0.0);
        Vec3d axisA = normal.crossProduct(reference).normalize();
        Vec3d axisB = normal.crossProduct(axisA).normalize();
        for (int index = 0; index < 28; index++) {
            double angle = Math.PI * 2.0 * index / 28.0;
            Vec3d radial = axisA.multiply(Math.cos(angle)).add(axisB.multiply(Math.sin(angle)));
            Vec3d ringPosition = position.add(radial.multiply(0.35));
            Vec3d velocity = radial.multiply(0.28);
            client.world.addParticle(index % 7 == 0 ? SONIC_GOLD : SONIC_RING,
                    ringPosition.x, ringPosition.y, ringPosition.z, velocity.x, velocity.y, velocity.z);
        }
        client.world.playSound(position.x, position.y, position.z, SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST,
                SoundCategory.PLAYERS, 1.0F, 0.72F, false);
        client.world.playSound(position.x, position.y, position.z, SoundEvents.ENTITY_GENERIC_EXPLODE.value(),
                SoundCategory.PLAYERS, 0.18F, 0.58F, false);
    }

    private static DustParticleEffect dust(int rgb, float scale) {
        return new DustParticleEffect(Vec3d.unpackRgb(rgb).toVector3f(), scale);
    }
}
