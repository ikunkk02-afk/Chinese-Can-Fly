package io.github.ikunkk02afk.chinesecanfly.registry;

import io.github.ikunkk02afk.chinesecanfly.ChineseCanFly;
import io.github.ikunkk02afk.chinesecanfly.item.SuspiciousBookItem;
import io.github.ikunkk02afk.chinesecanfly.rubbing.CharacterRubbingItem;
import io.github.ikunkk02afk.chinesecanfly.rubbing.RubbingPaperItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModItems {
    public static final Item RUBBING_PAPER = Registry.register(
            Registries.ITEM,
            Identifier.of(ChineseCanFly.MOD_ID, "rubbing_paper"),
            new RubbingPaperItem(new Item.Settings().maxCount(64))
    );

    public static final Item CHARACTER_RUBBING = Registry.register(
            Registries.ITEM,
            Identifier.of(ChineseCanFly.MOD_ID, "character_rubbing"),
            new CharacterRubbingItem(new Item.Settings().maxCount(64))
    );

    public static final Item INSCRIBED_ROCK = Registry.register(
            Registries.ITEM,
            Identifier.of(ChineseCanFly.MOD_ID, "inscribed_rock"),
            new BlockItem(ModBlocks.INSCRIBED_ROCK, new Item.Settings())
    );

    public static final Item SUSPICIOUS_BOOK = Registry.register(
            Registries.ITEM,
            Identifier.of(ChineseCanFly.MOD_ID, "suspicious_book"),
            new SuspiciousBookItem(new Item.Settings().maxCount(1))
    );

    private ModItems() {
    }

    public static void register() {
        // Class loading performs registry registration.
    }
}
