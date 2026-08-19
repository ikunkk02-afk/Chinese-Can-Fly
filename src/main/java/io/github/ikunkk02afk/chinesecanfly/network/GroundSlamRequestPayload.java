package io.github.ikunkk02afk.chinesecanfly.network;

import io.github.ikunkk02afk.chinesecanfly.ChineseCanFly;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** C2S request to turn the currently held entity into a downward slam. */
public record GroundSlamRequestPayload() implements CustomPayload {
    public static final GroundSlamRequestPayload INSTANCE = new GroundSlamRequestPayload();
    public static final Id<GroundSlamRequestPayload> ID = new Id<>(Identifier.of(ChineseCanFly.MOD_ID, "ground_slam_request"));
    public static final PacketCodec<RegistryByteBuf, GroundSlamRequestPayload> CODEC = PacketCodec.unit(INSTANCE);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
