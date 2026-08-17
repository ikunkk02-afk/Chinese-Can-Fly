package io.github.ikunkk02afk.chinesecanfly.component;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChinesePowerComponentImplTest {
    @Test
    void awakeningSetsAllStageSixFlagsTogetherAndResetClearsOnlyThoseFlags() {
        ChinesePowerComponent component = new ChinesePowerComponentImpl();

        assertFalse(component.hasReadDictionary());
        assertFalse(component.canUseChinese());
        assertFalse(component.hasChinesePower());

        component.awaken();
        assertTrue(component.hasReadDictionary());
        assertTrue(component.canUseChinese());
        assertTrue(component.hasChinesePower());

        component.resetAwakening();
        assertFalse(component.hasReadDictionary());
        assertFalse(component.canUseChinese());
        assertFalse(component.hasChinesePower());
    }
}
