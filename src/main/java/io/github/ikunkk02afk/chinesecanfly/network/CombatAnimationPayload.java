package io.github.ikunkk02afk.chinesecanfly.network;

import io.netty.buffer.ByteBuf;
import io.github.ikunkk02afk.chinesecanfly.ChineseCanFly;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record CombatAnimationPayload(UUID playerId, CombatAnimation animation) implements CustomPayload {
    public static final Id<CombatAnimationPayload> ID = new Id<>(
            Identifier.of(ChineseCanFly.MOD_ID, "combat_animation")
    );
    private static final PacketCodec<ByteBuf, CombatAnimation> ANIMATION_CODEC = PacketCodecs.VAR_INT
            .xmap(CombatAnimation::byId, CombatAnimation::ordinal);
    public static final PacketCodec<RegistryByteBuf, CombatAnimationPayload> CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC, CombatAnimationPayload::playerId,
            ANIMATION_CODEC, CombatAnimationPayload::animation,
            CombatAnimationPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
