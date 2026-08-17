package io.github.ikunkk02afk.chinesecanfly.registry;

import io.github.ikunkk02afk.chinesecanfly.ChineseCanFly;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModItems {
    public static final Item INSCRIBED_ROCK = Registry.register(
            Registries.ITEM,
            Identifier.of(ChineseCanFly.MOD_ID, "inscribed_rock"),
            new BlockItem(ModBlocks.INSCRIBED_ROCK, new Item.Settings())
    );

    private ModItems() {
    }

    public static void register() {
        // Class loading performs registry registration.
    }
}
