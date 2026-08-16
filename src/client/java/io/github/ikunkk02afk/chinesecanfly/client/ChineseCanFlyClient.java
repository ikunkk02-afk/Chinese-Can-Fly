package io.github.ikunkk02afk.chinesecanfly.client;

import io.github.ikunkk02afk.chinesecanfly.language.ChatLanguageHandler;
import io.github.ikunkk02afk.chinesecanfly.language.ChineseLanguageAccess;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;

public final class ChineseCanFlyClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientSendMessageEvents.MODIFY_CHAT.register(message -> {
            MinecraftClient client = MinecraftClient.getInstance();
            boolean canUseChinese = client.player != null && ChineseLanguageAccess.canUseChinese(client.player);
            return ChatLanguageHandler.transformOutgoingChat(message, canUseChinese);
        });
    }
}
