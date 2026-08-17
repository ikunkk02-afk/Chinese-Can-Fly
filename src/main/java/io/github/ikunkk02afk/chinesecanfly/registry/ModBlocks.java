package io.github.ikunkk02afk.chinesecanfly.registry;

import io.github.ikunkk02afk.chinesecanfly.ChineseCanFly;
import io.github.ikunkk02afk.chinesecanfly.block.InscribedRockBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModBlocks {
    public static final Block INSCRIBED_ROCK = Registry.register(
            Registries.BLOCK,
            Identifier.of(ChineseCanFly.MOD_ID, "inscribed_rock"),
            new InscribedRockBlock(AbstractBlock.Settings.copy(Blocks.STONE))
    );

    private ModBlocks() {
    }

    public static void register() {
        // Class loading performs registry registration.
    }
}
