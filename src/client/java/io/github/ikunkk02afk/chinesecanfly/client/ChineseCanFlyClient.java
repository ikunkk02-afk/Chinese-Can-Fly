package io.github.ikunkk02afk.chinesecanfly.client;

import io.github.ikunkk02afk.chinesecanfly.client.effect.AwakeningGlyphEffect;
import io.github.ikunkk02afk.chinesecanfly.client.effect.AwakeningGlyphRenderer;
import io.github.ikunkk02afk.chinesecanfly.language.ChatLanguageHandler;
import io.github.ikunkk02afk.chinesecanfly.language.ChineseLanguageAccess;
import io.github.ikunkk02afk.chinesecanfly.client.render.InscribedRockBlockEntityRenderer;
import io.github.ikunkk02afk.chinesecanfly.network.AwakeningEffectPayload;
import io.github.ikunkk02afk.chinesecanfly.registry.ModBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public final class ChineseCanFlyClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientSendMessageEvents.MODIFY_CHAT.register(message -> {
            MinecraftClient client = MinecraftClient.getInstance();
            boolean canUseChinese = client.player != null && ChineseLanguageAccess.canUseChinese(client.player);
            return ChatLanguageHandler.transformOutgoingChat(message, canUseChinese);
        });
        BlockEntityRendererFactories.register(ModBlockEntities.INSCRIBED_ROCK, InscribedRockBlockEntityRenderer::new);
        ClientPlayNetworking.registerGlobalReceiver(AwakeningEffectPayload.ID,
                (payload, context) -> AwakeningGlyphEffect.trigger(payload.playerId()));
        AwakeningGlyphRenderer.register();
    }
}
