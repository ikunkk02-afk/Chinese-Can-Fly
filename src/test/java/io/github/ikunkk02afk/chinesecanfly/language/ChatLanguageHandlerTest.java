package io.github.ikunkk02afk.chinesecanfly.language;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChatLanguageHandlerTest {
    @Test
    void leavesAllCommandsUntouched() {
        assertEquals("/gamemode creative", ChatLanguageHandler.transformOutgoingChat("/gamemode creative", false));
        assertEquals("/tp @s 0 100 0", ChatLanguageHandler.transformOutgoingChat("/tp @s 0 100 0", false));
        assertEquals("/say 你好", ChatLanguageHandler.transformOutgoingChat("/say 你好", false));
    }

    @Test
    void leavesChatUntouchedAfterChineseIsUnlocked() {
        assertEquals("中国人能飞", ChatLanguageHandler.transformOutgoingChat("中国人能飞", true));
    }

    @Test
    void convertsNormalChatBeforeChineseIsUnlocked() {
        assertEquals("zhong guo ren neng fei",
                ChatLanguageHandler.transformOutgoingChat("中国人能飞", false));
    }
}
