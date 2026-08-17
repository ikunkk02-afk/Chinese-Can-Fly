package io.github.ikunkk02afk.chinesecanfly.component;

import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

/**
 * Persistent, server-authoritative knowledge and power unlocked by reading the Grand Chinese Dictionary.
 */
public interface ChinesePowerComponent extends AutoSyncedComponent {
    boolean hasReadDictionary();

    boolean canUseChinese();

    boolean hasChinesePower();

    /**
     * Performs the one Stage 6 state transition. Callers must synchronize the owning player afterwards.
     */
    void awaken();

    void resetAwakening();
}
