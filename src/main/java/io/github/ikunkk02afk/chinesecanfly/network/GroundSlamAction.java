package io.github.ikunkk02afk.chinesecanfly.network;

public enum GroundSlamAction {
    START,
    IMPACT,
    CANCEL;

    public static GroundSlamAction byId(int id) {
        GroundSlamAction[] values = values();
        return id >= 0 && id < values.length ? values[id] : CANCEL;
    }
}
