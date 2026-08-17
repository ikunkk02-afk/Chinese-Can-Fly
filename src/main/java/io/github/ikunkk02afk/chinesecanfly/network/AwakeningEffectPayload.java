package io.github.ikunkk02afk.chinesecanfly.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

/** One-shot S2C trigger for the client-only floating glyph presentation. */
public record AwakeningEffectPayload(UUID playerId) implements CustomPayload {
    public static final CustomPayload.Id<AwakeningEffectPayload> ID = new CustomPayload.Id<>(
            Identifier.of("chinese_can_fly", "awakening_effect")
    );
    public static final PacketCodec<RegistryByteBuf, AwakeningEffectPayload> CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC,
            AwakeningEffectPayload::playerId,
            AwakeningEffectPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
