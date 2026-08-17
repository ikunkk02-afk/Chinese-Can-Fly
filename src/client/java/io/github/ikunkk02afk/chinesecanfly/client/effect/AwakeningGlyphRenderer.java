package io.github.ikunkk02afk.chinesecanfly.client.effect;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Renders real Unicode glyphs as camera-facing text without creating entities. */
public final class AwakeningGlyphRenderer {
    private static final String[] GLYPHS = {"中", "华", "人", "能", "飞", "天", "地", "龙", "魂"};
    private static final List<AwakeningGlyphEffect> ACTIVE_EFFECTS = new ArrayList<>();

    private AwakeningGlyphRenderer() {
    }

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(AwakeningGlyphRenderer::render);
    }

    static void trigger(AwakeningGlyphEffect effect) {
        ACTIVE_EFFECTS.removeIf(active -> active.playerId().equals(effect.playerId()));
        ACTIVE_EFFECTS.add(effect);
    }

    private static void render(WorldRenderContext context) {
        if (ACTIVE_EFFECTS.isEmpty() || context.matrixStack() == null || context.consumers() == null) {
            return;
        }

        long worldTime = context.world().getTime();
        Iterator<AwakeningGlyphEffect> iterator = ACTIVE_EFFECTS.iterator();
        while (iterator.hasNext()) {
            AwakeningGlyphEffect effect = iterator.next();
            if (effect.hasExpired(worldTime)) {
                iterator.remove();
                continue;
            }

            PlayerEntity player = context.world().getPlayerByUuid(effect.playerId());
            if (player != null) {
                renderEffect(context, player, effect.ageAt(worldTime));
            }
        }
    }

    private static void renderEffect(WorldRenderContext context, PlayerEntity player, int age) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        MatrixStack matrices = context.matrixStack();
        VertexConsumerProvider consumers = context.consumers();
        Vec3d cameraPos = context.camera().getPos();
        float progress = age / (float) AwakeningGlyphEffect.DURATION_TICKS;
        int alpha = Math.max(0, Math.round(255.0F * (1.0F - progress)));

        for (int index = 0; index < GLYPHS.length; index++) {
            double angle = age * 0.20 + index * (Math.PI * 2.0 / GLYPHS.length);
            double radius = 0.82 + (index % 3) * 0.12;
            double x = player.getX() + Math.cos(angle) * radius;
            double y = player.getY() + 0.48 + (index % 3) * 0.20 + progress * 1.65;
            double z = player.getZ() + Math.sin(angle) * radius;
            int color = (alpha << 24) | (index % 2 == 0 ? 0xE6B83D : 0xA5202A);
            drawGlyph(textRenderer, matrices, consumers, context, cameraPos, GLYPHS[index], x, y, z, color);
        }
    }

    private static void drawGlyph(TextRenderer textRenderer, MatrixStack matrices, VertexConsumerProvider consumers,
                                  WorldRenderContext context, Vec3d cameraPos, String glyph,
                                  double x, double y, double z, int color) {
        matrices.push();
        matrices.translate(x - cameraPos.x, y - cameraPos.y, z - cameraPos.z);
        matrices.multiply(context.camera().getRotation());
        matrices.scale(-0.026F, -0.026F, 0.026F);

        float textX = -textRenderer.getWidth(glyph) / 2.0F;
        float textY = -textRenderer.fontHeight / 2.0F;
        textRenderer.draw(glyph, textX, textY, color, false, matrices.peek().getPositionMatrix(), consumers,
                TextRenderer.TextLayerType.SEE_THROUGH, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
        matrices.pop();
    }
}
