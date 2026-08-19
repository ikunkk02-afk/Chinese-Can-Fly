package io.github.ikunkk02afk.chinesecanfly.registry;

import io.github.ikunkk02afk.chinesecanfly.ChineseCanFly;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

/** Shared data-driven protection tags used by destructive abilities. */
public final class ModBlockTags {
    public static final TagKey<Block> SUPER_FLIGHT_IMMUNE = TagKey.of(
            RegistryKeys.BLOCK,
            Identifier.of(ChineseCanFly.MOD_ID, "super_flight_immune")
    );

    private ModBlockTags() {
    }
}
