package io.github.ikunkk02afk.chinesecanfly.network;

import io.github.ikunkk02afk.chinesecanfly.ChineseCanFly;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** C2S request; the server decides whether this means grab, throw, or a safe release. */
public record GrabRequestPayload(int targetEntityId, boolean safeRelease) implements CustomPayload {
    public static final Id<GrabRequestPayload> ID = new Id<>(Identifier.of(ChineseCanFly.MOD_ID, "grab_request"));
    public static final PacketCodec<RegistryByteBuf, GrabRequestPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, GrabRequestPayload::targetEntityId,
            PacketCodecs.BOOL, GrabRequestPayload::safeRelease,
            GrabRequestPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
