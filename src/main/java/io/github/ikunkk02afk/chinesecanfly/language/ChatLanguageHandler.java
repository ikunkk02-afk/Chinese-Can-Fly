package io.github.ikunkk02afk.chinesecanfly.language;

public final class ChatLanguageHandler {
    private ChatLanguageHandler() {
    }

    public static String transformOutgoingChat(String message, boolean canUseChinese) {
        if (message == null || message.isEmpty() || message.startsWith("/") || canUseChinese
                || !PinyinConverter.containsChinese(message)) {
            return message;
        }
        return PinyinConverter.convert(message);
    }
}
