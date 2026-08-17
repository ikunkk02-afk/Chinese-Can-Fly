package io.github.ikunkk02afk.chinesecanfly.ability;

import io.github.ikunkk02afk.chinesecanfly.component.ChinesePowerComponent;
import io.github.ikunkk02afk.chinesecanfly.registry.ModComponents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Applies only vanilla-style flight permission. It never changes the player's game mode, speed, collision, or damage.
 */
public final class PlayerFlightAbilityManager {
    private static final int VERIFICATION_INTERVAL_TICKS = 20;
    private static int verificationTicker;

    private PlayerFlightAbilityManager() {
    }

    public static void register() {
        ServerPlayerEvents.JOIN.register(PlayerFlightAbilityManager::apply);
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> apply(newPlayer));
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> apply(player));
        ServerTickEvents.END_SERVER_TICK.register(PlayerFlightAbilityManager::verifyPeriodically);
    }

    public static void grant(ServerPlayerEntity player) {
        apply(player);
    }

    /** Removes the permission only when this player had unlocked it and is not in a vanilla flight-enabled mode. */
    public static void revokeForReset(ServerPlayerEntity player, boolean hadChinesePower) {
        if (!hadChinesePower || hasVanillaFlight(player)) {
            return;
        }

        PlayerAbilities abilities = player.getAbilities();
        if (abilities.allowFlying || abilities.flying) {
            abilities.allowFlying = false;
            abilities.flying = false;
            player.sendAbilitiesUpdate();
        }
    }

    private static void verifyPeriodically(MinecraftServer server) {
        verificationTicker++;
        if (verificationTicker % VERIFICATION_INTERVAL_TICKS != 0) {
            return;
        }

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            apply(player);
        }
    }

    private static void apply(ServerPlayerEntity player) {
        ChinesePowerComponent component = ModComponents.CHINESE_POWER.get(player);
        if (!component.hasChinesePower() || hasVanillaFlight(player)) {
            return;
        }

        PlayerAbilities abilities = player.getAbilities();
        if (!abilities.allowFlying) {
            abilities.allowFlying = true;
            player.sendAbilitiesUpdate();
        }
    }

    private static boolean hasVanillaFlight(ServerPlayerEntity player) {
        return player.isSpectator() || player.getAbilities().creativeMode;
    }
}
