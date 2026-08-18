package io.github.ikunkk02afk.chinesecanfly.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.ikunkk02afk.chinesecanfly.ability.PlayerFlightAbilityManager;
import io.github.ikunkk02afk.chinesecanfly.ability.superflight.SuperFlightManager;
import io.github.ikunkk02afk.chinesecanfly.component.ChinesePowerComponent;
import io.github.ikunkk02afk.chinesecanfly.registry.ModComponents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

/** Development and verification commands for the Stage 6 player state. */
public final class ChinesePowerCommands {
    private ChinesePowerCommands() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                literal("chinesecanfly")
                        .then(literal("status").executes(ChinesePowerCommands::showStatus))
                        .then(literal("resetawakening")
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(ChinesePowerCommands::resetAwakening))
        ));
    }

    private static int showStatus(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        ChinesePowerComponent component = ModComponents.CHINESE_POWER.get(player);
        context.getSource().sendFeedback(() -> Text.translatable("command.chinese_can_fly.status",
                component.hasReadDictionary(), component.canUseChinese(), component.hasChinesePower(),
                SuperFlightManager.isActive(player)), false);
        return 1;
    }

    private static int resetAwakening(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        ChinesePowerComponent component = ModComponents.CHINESE_POWER.get(player);
        boolean hadChinesePower = component.hasChinesePower();
        SuperFlightManager.stopNow(player);
        component.resetAwakening();
        PlayerFlightAbilityManager.revokeForReset(player, hadChinesePower);
        ModComponents.CHINESE_POWER.sync(player);

        context.getSource().sendFeedback(() -> Text.translatable("command.chinese_can_fly.resetawakening.success"), false);
        return 1;
    }
}
