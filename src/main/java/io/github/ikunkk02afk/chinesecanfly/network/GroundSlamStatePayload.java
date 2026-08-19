package io.github.ikunkk02afk.chinesecanfly.network;

import io.github.ikunkk02afk.chinesecanfly.ChineseCanFly;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record GroundSlamStatePayload(UUID playerId, GroundSlamAction action) implements CustomPayload {
    public static final Id<GroundSlamStatePayload> ID = new Id<>(Identifier.of(ChineseCanFly.MOD_ID, "ground_slam_state"));
    private static final PacketCodec<ByteBuf, GroundSlamAction> ACTION_CODEC = PacketCodecs.VAR_INT
            .xmap(GroundSlamAction::byId, GroundSlamAction::ordinal);
    public static final PacketCodec<RegistryByteBuf, GroundSlamStatePayload> CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC, GroundSlamStatePayload::playerId,
            ACTION_CODEC, GroundSlamStatePayload::action,
            GroundSlamStatePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
