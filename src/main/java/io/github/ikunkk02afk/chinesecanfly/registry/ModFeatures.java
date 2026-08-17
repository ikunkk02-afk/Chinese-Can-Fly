package io.github.ikunkk02afk.chinesecanfly.registry;

import io.github.ikunkk02afk.chinesecanfly.ChineseCanFly;
import io.github.ikunkk02afk.chinesecanfly.worldgen.AncientRockInscriptionFeature;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public final class ModFeatures {
    public static final Feature<DefaultFeatureConfig> ANCIENT_ROCK_INSCRIPTION = Registry.register(
            Registries.FEATURE,
            Identifier.of(ChineseCanFly.MOD_ID, "ancient_rock_inscription"),
            new AncientRockInscriptionFeature(DefaultFeatureConfig.CODEC)
    );

    private ModFeatures() {
    }

    public static void register() {
        // Class loading performs registry registration.
    }
}
