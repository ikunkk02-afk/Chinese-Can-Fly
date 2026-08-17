package io.github.ikunkk02afk.chinesecanfly.registry;

import io.github.ikunkk02afk.chinesecanfly.ChineseCanFly;
import io.github.ikunkk02afk.chinesecanfly.inscription.InscriptionCharacters;
import io.github.ikunkk02afk.chinesecanfly.rubbing.CharacterRubbingItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ModItemGroups {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChineseCanFly.MOD_ID);

    public static final RegistryKey<ItemGroup> MAIN = RegistryKey.of(
            RegistryKeys.ITEM_GROUP,
            Identifier.of(ChineseCanFly.MOD_ID, "main")
    );

    public static final ItemGroup MAIN_GROUP = Registry.register(
            Registries.ITEM_GROUP,
            MAIN,
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemGroup.chinese_can_fly.main"))
                    .icon(() -> new ItemStack(ModItems.CHINESE_DICTIONARY))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.INSCRIBED_ROCK);
                        entries.add(ModItems.RUBBING_PAPER);
                        InscriptionCharacters.allCharacters().forEach(character -> CharacterRubbingItem.createStack(character)
                                .ifPresentOrElse(entries::add, () -> LOGGER.warn(
                                        "Skipping invalid inscription character {} while building the creative item group",
                                        character
                                )));
                        entries.add(ModItems.SUSPICIOUS_BOOK);
                        entries.add(ModItems.CHINESE_DICTIONARY);
                    })
                    .build()
    );

    private ModItemGroups() {
    }

    public static void register() {
        // Class loading performs registry registration.
    }
}
