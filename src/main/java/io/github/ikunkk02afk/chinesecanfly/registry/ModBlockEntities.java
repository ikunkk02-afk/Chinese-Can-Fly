package io.github.ikunkk02afk.chinesecanfly.registry;

import io.github.ikunkk02afk.chinesecanfly.ChineseCanFly;
import io.github.ikunkk02afk.chinesecanfly.block.entity.InscribedRockBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModBlockEntities {
    public static final BlockEntityType<InscribedRockBlockEntity> INSCRIBED_ROCK = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(ChineseCanFly.MOD_ID, "inscribed_rock"),
            BlockEntityType.Builder.create(InscribedRockBlockEntity::new, ModBlocks.INSCRIBED_ROCK).build(null)
    );

    private ModBlockEntities() {
    }

    public static void register() {
        // Class loading performs registry registration.
    }
}
