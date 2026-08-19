package io.github.ikunkk02afk.chinesecanfly.network;

/** Low-frequency combat animation events understood by every modded client. */
public enum CombatAnimation {
    SUPER_PUNCH;

    public static CombatAnimation byId(int id) {
        CombatAnimation[] values = values();
        return id >= 0 && id < values.length ? values[id] : SUPER_PUNCH;
    }
}
