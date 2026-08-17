package io.github.ikunkk02afk.chinesecanfly.registry;

import io.github.ikunkk02afk.chinesecanfly.ChineseCanFly;
import io.github.ikunkk02afk.chinesecanfly.rubbing.RubbingCharacterValidation;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModDataComponents {
    public static final ComponentType<String> INSCRIPTION_CHARACTER = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(ChineseCanFly.MOD_ID, "inscription_character"),
            ComponentType.<String>builder()
                    .codec(RubbingCharacterValidation.CODEC)
                    .packetCodec(PacketCodecs.STRING)
                    .build()
    );

    private ModDataComponents() {
    }

    public static void register() {
        // Class loading performs registry registration.
    }

}
