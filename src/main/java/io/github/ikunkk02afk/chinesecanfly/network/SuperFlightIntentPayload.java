package io.github.ikunkk02afk.chinesecanfly.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** C2S edge-triggered request; clients never supply velocity, position, or damage. */
public record SuperFlightIntentPayload(boolean active) implements CustomPayload {
    public static final CustomPayload.Id<SuperFlightIntentPayload> ID = new CustomPayload.Id<>(
            Identifier.of("chinese_can_fly", "super_flight_intent")
    );
    public static final PacketCodec<RegistryByteBuf, SuperFlightIntentPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL,
            SuperFlightIntentPayload::active,
            SuperFlightIntentPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
