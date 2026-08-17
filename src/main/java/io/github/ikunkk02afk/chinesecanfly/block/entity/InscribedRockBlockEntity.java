package io.github.ikunkk02afk.chinesecanfly.block.entity;

import io.github.ikunkk02afk.chinesecanfly.inscription.InscriptionCharacters;
import io.github.ikunkk02afk.chinesecanfly.registry.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

public final class InscribedRockBlockEntity extends BlockEntity {
    private static final String CHARACTER_KEY = "character";

    private String character = InscriptionCharacters.DEFAULT_CHARACTER;

    public InscribedRockBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INSCRIBED_ROCK, pos, state);
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        if (!InscriptionCharacters.isKnownCharacter(character) || this.character.equals(character)) {
            return;
        }

        this.character = character;
        markDirty();

        if (world != null && !world.isClient) {
            BlockState state = getCachedState();
            world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putString(CHARACTER_KEY, character);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        String storedCharacter = nbt.getString(CHARACTER_KEY);
        character = InscriptionCharacters.isKnownCharacter(storedCharacter)
                ? storedCharacter
                : InscriptionCharacters.DEFAULT_CHARACTER;
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return createNbt(registries);
    }
}
