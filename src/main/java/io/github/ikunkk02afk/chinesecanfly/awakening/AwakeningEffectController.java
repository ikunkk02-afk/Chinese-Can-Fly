package io.github.ikunkk02afk.chinesecanfly.awakening;

import io.github.ikunkk02afk.chinesecanfly.network.AwakeningEffectPayload;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** A short-lived server-side particle controller; it has no permanent player tick work. */
public final class AwakeningEffectController {
    private static final int EFFECT_DURATION_TICKS = 70;
    private static final Map<UUID, Integer> ACTIVE_EFFECTS = new HashMap<>();

    private AwakeningEffectController() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(AwakeningEffectController::tick);
    }

    public static void start(ServerPlayerEntity player) {
        ACTIVE_EFFECTS.put(player.getUuid(), 0);
        showTitle(player);
        sendVisualTrigger(player);

        ServerWorld world = player.getServerWorld();
        world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 0.65F, 0.82F);
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.78F, 0.9F);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 25, 0, false, false, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 80, 0, false, false, false));
    }

    private static void showTitle(ServerPlayerEntity player) {
        player.networkHandler.sendPacket(new TitleFadeS2CPacket(10, 50, 20));
        player.networkHandler.sendPacket(new TitleS2CPacket(
                Text.literal("中国人能飞").formatted(Formatting.GOLD, Formatting.BOLD)
        ));
        player.networkHandler.sendPacket(new SubtitleS2CPacket(
                Text.translatable("subtitle.chinese_can_fly.awakening").formatted(Formatting.RED)
        ));
    }

    private static void sendVisualTrigger(ServerPlayerEntity player) {
        AwakeningEffectPayload payload = new AwakeningEffectPayload(player.getUuid());
        Set<UUID> recipients = new HashSet<>();
        for (ServerPlayerEntity watcher : PlayerLookup.tracking(player)) {
            ServerPlayNetworking.send(watcher, payload);
            recipients.add(watcher.getUuid());
        }
        if (recipients.add(player.getUuid())) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    private static void tick(MinecraftServer server) {
        if (ACTIVE_EFFECTS.isEmpty()) {
            return;
        }

        Iterator<Map.Entry<UUID, Integer>> iterator = ACTIVE_EFFECTS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Integer> entry = iterator.next();
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
            if (player == null || !player.isAlive()) {
                iterator.remove();
                continue;
            }

            int age = entry.getValue();
            if (age >= EFFECT_DURATION_TICKS) {
                iterator.remove();
                continue;
            }

            emitParticles(player.getServerWorld(), player, age);
            entry.setValue(age + 1);
        }
    }

    private static void emitParticles(ServerWorld world, ServerPlayerEntity player, int age) {
        double centerX = player.getX();
        double centerY = player.getY() + 0.28 + age * 0.018;
        double centerZ = player.getZ();

        if (age % 2 == 0) {
            double baseAngle = age * 0.32;
            for (int index = 0; index < 4; index++) {
                double angle = baseAngle + index * Math.PI / 2.0;
                world.spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0xE6B83D).toVector3f(), 1.05F),
                        centerX + Math.cos(angle) * 1.15, centerY, centerZ + Math.sin(angle) * 1.15,
                        1, 0.0, 0.0, 0.0, 0.0);
            }
            world.spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0x9E1823).toVector3f(), 0.92F),
                    centerX, centerY + 0.3, centerZ, 2, 0.55, 0.36, 0.55, 0.01);
        }

        if (age % 3 == 0) {
            world.spawnParticles(ParticleTypes.ENCHANT, centerX, centerY + 0.45, centerZ,
                    3, 0.5, 0.45, 0.5, 0.04);
        }
        if (age % 5 == 0) {
            world.spawnParticles(ParticleTypes.END_ROD, centerX, centerY + 0.7, centerZ,
                    1, 0.68, 0.45, 0.68, 0.01);
            world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, centerX, centerY + 0.82, centerZ,
                    1, 0.48, 0.35, 0.48, 0.01);
        }
    }
}
