package io.github.ikunkk02afk.chinesecanfly.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

/** S2C visual state sent only when a state transition occurs. */
public record SuperFlightStatePayload(UUID playerId, boolean active, boolean fast) implements CustomPayload {
    public static final CustomPayload.Id<SuperFlightStatePayload> ID = new CustomPayload.Id<>(
            Identifier.of("chinese_can_fly", "super_flight_state")
    );
    public static final PacketCodec<RegistryByteBuf, SuperFlightStatePayload> CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC,
            SuperFlightStatePayload::playerId,
            PacketCodecs.BOOL,
            SuperFlightStatePayload::active,
            PacketCodecs.BOOL,
            SuperFlightStatePayload::fast,
            SuperFlightStatePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
