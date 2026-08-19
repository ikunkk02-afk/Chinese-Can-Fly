package io.github.ikunkk02afk.chinesecanfly.network;

import io.github.ikunkk02afk.chinesecanfly.ChineseCanFly;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

/** Syncs the exact holder-target relationship so every client can render the original entity as a hand proxy. */
public record HeldEntityStatePayload(UUID holderId, UUID targetId, int targetEntityId, boolean holding) implements CustomPayload {
    public static final Id<HeldEntityStatePayload> ID = new Id<>(Identifier.of(ChineseCanFly.MOD_ID, "held_entity_state"));
    public static final PacketCodec<RegistryByteBuf, HeldEntityStatePayload> CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC, HeldEntityStatePayload::holderId,
            Uuids.PACKET_CODEC, HeldEntityStatePayload::targetId,
            PacketCodecs.VAR_INT, HeldEntityStatePayload::targetEntityId,
            PacketCodecs.BOOL, HeldEntityStatePayload::holding,
            HeldEntityStatePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
