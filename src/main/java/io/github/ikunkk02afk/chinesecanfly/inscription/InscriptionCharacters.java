package io.github.ikunkk02afk.chinesecanfly.inscription;

import net.minecraft.util.math.random.Random;

import java.util.List;
import java.util.Set;

public final class InscriptionCharacters {
    public static final String DEFAULT_CHARACTER = "中";

    private static final String[] CHARACTERS = {
            "中", "华", "人", "飞", "天", "地", "山", "河", "日", "月", "风", "云",
            "雷", "雨", "火", "水", "木", "金", "土", "石", "龙", "虎", "力", "道",
            "文", "字", "国", "民", "神", "州", "江", "海", "明", "光", "玄", "黄",
            "宇", "宙", "洪", "荒", "乾", "坤", "星", "辰", "古", "今", "破", "疾"
    };

    private static final List<String> ALL_CHARACTERS = List.of(CHARACTERS);
    private static final Set<String> CHARACTER_SET = Set.of(CHARACTERS);

    private InscriptionCharacters() {
    }

    public static String randomCharacter(Random random) {
        return CHARACTERS[random.nextInt(CHARACTERS.length)];
    }

    public static boolean isKnownCharacter(String character) {
        return CHARACTER_SET.contains(character);
    }

    public static List<String> allCharacters() {
        return ALL_CHARACTERS;
    }

    public static int size() {
        return CHARACTERS.length;
    }
}
