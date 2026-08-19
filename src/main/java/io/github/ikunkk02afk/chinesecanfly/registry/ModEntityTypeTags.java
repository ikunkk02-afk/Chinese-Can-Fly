package io.github.ikunkk02afk.chinesecanfly.registry;

import io.github.ikunkk02afk.chinesecanfly.ChineseCanFly;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

/** Extensible entity restrictions for grab without a growing hard-coded exception list. */
public final class ModEntityTypeTags {
    public static final TagKey<EntityType<?>> GRAB_IMMUNE = TagKey.of(
            RegistryKeys.ENTITY_TYPE,
            Identifier.of(ChineseCanFly.MOD_ID, "grab_immune")
    );

    private ModEntityTypeTags() {
    }
}
