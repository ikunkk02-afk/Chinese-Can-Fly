package io.github.ikunkk02afk.chinesecanfly.language;

import com.github.promeg.pinyinhelper.Pinyin;

import java.util.Locale;

public final class PinyinConverter {
    private PinyinConverter() {
    }

    public static String convert(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder output = new StringBuilder(input.length());
        int index = 0;
        while (index < input.length()) {
            if (Pinyin.isChinese(input.charAt(index))) {
                int end = index + 1;
                while (end < input.length() && Pinyin.isChinese(input.charAt(end))) {
                    end++;
                }

                appendPinyin(output, Pinyin.toPinyin(input.substring(index, end), " ")
                        .toLowerCase(Locale.ROOT));
                index = end;
                continue;
            }

            int codePoint = input.codePointAt(index);
            if (isWordCharacter(codePoint) && previousCharacterIsChinese(input, index)
                    && endsWithWordCharacter(output)) {
                output.append(' ');
            }
            output.appendCodePoint(codePoint);
            index += Character.charCount(codePoint);
        }
        return output.toString();
    }

    public static boolean containsChinese(String input) {
        if (input == null) {
            return false;
        }

        for (int index = 0; index < input.length(); index++) {
            if (Pinyin.isChinese(input.charAt(index))) {
                return true;
            }
        }
        return false;
    }

    private static void appendPinyin(StringBuilder output, String pinyin) {
        if (endsWithWordCharacter(output)) {
            output.append(' ');
        }
        output.append(pinyin);
    }

    private static boolean previousCharacterIsChinese(String input, int index) {
        return index > 0 && Pinyin.isChinese(input.charAt(index - 1));
    }

    private static boolean endsWithWordCharacter(StringBuilder output) {
        return !output.isEmpty() && isWordCharacter(output.codePointBefore(output.length()));
    }

    private static boolean isWordCharacter(int codePoint) {
        return Character.isLetterOrDigit(codePoint);
    }
}
