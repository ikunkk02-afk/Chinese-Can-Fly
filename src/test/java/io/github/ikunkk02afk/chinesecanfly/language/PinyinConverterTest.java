package io.github.ikunkk02afk.chinesecanfly.language;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PinyinConverterTest {
    @Test
    void convertsChineseToTonelessPinyin() {
        assertEquals("ni hao", PinyinConverter.convert("你好"));
        assertEquals("zhong guo ren neng fei", PinyinConverter.convert("中国人能飞"));
        assertEquals("wo jin tian wan Minecraft", PinyinConverter.convert("我今天玩Minecraft"));
    }

    @Test
    void preservesMixedTextPunctuationAndEmoji() {
        assertEquals("Hello zhong guo 123", PinyinConverter.convert("Hello中国123"));
        assertEquals("ni hao！", PinyinConverter.convert("你好！"));
        assertEquals("ni hao😂", PinyinConverter.convert("你好😂"));
        assertEquals("Minecraft 114514, OK!", PinyinConverter.convert("Minecraft 114514, OK!"));
    }
}
