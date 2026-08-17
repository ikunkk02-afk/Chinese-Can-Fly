package io.github.ikunkk02afk.chinesecanfly.component;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.CopyableComponent;

/**
 * NBT-backed implementation shared by the server player and its synchronized client-side counterpart.
 */
public final class ChinesePowerComponentImpl implements ChinesePowerComponent, CopyableComponent<ChinesePowerComponent> {
    private static final String DICTIONARY_READ_KEY = "dictionaryRead";
    private static final String CHINESE_UNLOCKED_KEY = "chineseUnlocked";
    private static final String POWER_UNLOCKED_KEY = "powerUnlocked";

    private boolean dictionaryRead;
    private boolean chineseUnlocked;
    private boolean powerUnlocked;

    @Override
    public boolean hasReadDictionary() {
        return dictionaryRead;
    }

    @Override
    public boolean canUseChinese() {
        return chineseUnlocked;
    }

    @Override
    public boolean hasChinesePower() {
        return powerUnlocked;
    }

    @Override
    public void awaken() {
        dictionaryRead = true;
        chineseUnlocked = true;
        powerUnlocked = true;
    }

    @Override
    public void resetAwakening() {
        dictionaryRead = false;
        chineseUnlocked = false;
        powerUnlocked = false;
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        dictionaryRead = tag.getBoolean(DICTIONARY_READ_KEY);
        chineseUnlocked = tag.getBoolean(CHINESE_UNLOCKED_KEY);
        powerUnlocked = tag.getBoolean(POWER_UNLOCKED_KEY);
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putBoolean(DICTIONARY_READ_KEY, dictionaryRead);
        tag.putBoolean(CHINESE_UNLOCKED_KEY, chineseUnlocked);
        tag.putBoolean(POWER_UNLOCKED_KEY, powerUnlocked);
    }

    @Override
    public void copyFrom(ChinesePowerComponent other, RegistryWrapper.WrapperLookup registryLookup) {
        dictionaryRead = other.hasReadDictionary();
        chineseUnlocked = other.canUseChinese();
        powerUnlocked = other.hasChinesePower();
    }
}
