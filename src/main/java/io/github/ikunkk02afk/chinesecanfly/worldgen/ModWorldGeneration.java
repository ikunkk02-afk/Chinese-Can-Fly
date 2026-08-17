package io.github.ikunkk02afk.chinesecanfly.worldgen;

import io.github.ikunkk02afk.chinesecanfly.ChineseCanFly;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

public final class ModWorldGeneration {
    private static final RegistryKey<PlacedFeature> ANCIENT_ROCK_INSCRIPTION = RegistryKey.of(
            RegistryKeys.PLACED_FEATURE,
            Identifier.of(ChineseCanFly.MOD_ID, "ancient_rock_inscription")
    );

    private ModWorldGeneration() {
    }

    public static void register() {
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.LOCAL_MODIFICATIONS,
                ANCIENT_ROCK_INSCRIPTION
        );
    }
}
