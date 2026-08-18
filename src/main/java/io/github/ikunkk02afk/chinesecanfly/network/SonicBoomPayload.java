package io.github.ikunkk02afk.chinesecanfly.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

/** One-shot nearby S2C event for the sonic ring and sound. */
public record SonicBoomPayload(UUID playerId, Vec3d position, Vec3d direction) implements CustomPayload {
    private static final PacketCodec<RegistryByteBuf, Vec3d> VEC3D_CODEC = PacketCodec.ofStatic(
            (buffer, value) -> {
                buffer.writeDouble(value.x);
                buffer.writeDouble(value.y);
                buffer.writeDouble(value.z);
            },
            buffer -> new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble())
    );

    public static final CustomPayload.Id<SonicBoomPayload> ID = new CustomPayload.Id<>(
            Identifier.of("chinese_can_fly", "sonic_boom")
    );
    public static final PacketCodec<RegistryByteBuf, SonicBoomPayload> CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC,
            SonicBoomPayload::playerId,
            VEC3D_CODEC,
            SonicBoomPayload::position,
            VEC3D_CODEC,
            SonicBoomPayload::direction,
            SonicBoomPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
