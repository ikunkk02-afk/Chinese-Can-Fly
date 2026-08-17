package io.github.ikunkk02afk.chinesecanfly;

import io.github.ikunkk02afk.chinesecanfly.registry.ModBlockEntities;
import io.github.ikunkk02afk.chinesecanfly.registry.ModBlocks;
import io.github.ikunkk02afk.chinesecanfly.registry.ModDataComponents;
import io.github.ikunkk02afk.chinesecanfly.registry.ModFeatures;
import io.github.ikunkk02afk.chinesecanfly.registry.ModItems;
import io.github.ikunkk02afk.chinesecanfly.worldgen.ModWorldGeneration;
import net.fabricmc.api.ModInitializer;

public final class ChineseCanFly implements ModInitializer {
    public static final String MOD_ID = "chinese_can_fly";

    @Override
    public void onInitialize() {
        ModDataComponents.register();
        ModBlocks.register();
        ModItems.register();
        ModBlockEntities.register();
        ModFeatures.register();
        ModWorldGeneration.register();
    }
}
