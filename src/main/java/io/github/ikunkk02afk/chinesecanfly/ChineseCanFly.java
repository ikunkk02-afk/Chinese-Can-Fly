package io.github.ikunkk02afk.chinesecanfly;

import io.github.ikunkk02afk.chinesecanfly.ability.PlayerFlightAbilityManager;
import io.github.ikunkk02afk.chinesecanfly.awakening.AwakeningEffectController;
import io.github.ikunkk02afk.chinesecanfly.command.ChinesePowerCommands;
import io.github.ikunkk02afk.chinesecanfly.network.AwakeningEffectPayload;
import io.github.ikunkk02afk.chinesecanfly.registry.ModBlockEntities;
import io.github.ikunkk02afk.chinesecanfly.registry.ModBlocks;
import io.github.ikunkk02afk.chinesecanfly.registry.ModDataComponents;
import io.github.ikunkk02afk.chinesecanfly.registry.ModFeatures;
import io.github.ikunkk02afk.chinesecanfly.registry.ModItemGroups;
import io.github.ikunkk02afk.chinesecanfly.registry.ModItems;
import io.github.ikunkk02afk.chinesecanfly.registry.ModRecipeSerializers;
import io.github.ikunkk02afk.chinesecanfly.worldgen.ModWorldGeneration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public final class ChineseCanFly implements ModInitializer {
    public static final String MOD_ID = "chinese_can_fly";

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(AwakeningEffectPayload.ID, AwakeningEffectPayload.CODEC);
        ModDataComponents.register();
        ModBlocks.register();
        ModItems.register();
        ModRecipeSerializers.register();
        ModItemGroups.register();
        ModBlockEntities.register();
        ModFeatures.register();
        ModWorldGeneration.register();
        PlayerFlightAbilityManager.register();
        AwakeningEffectController.register();
        ChinesePowerCommands.register();
    }
}
