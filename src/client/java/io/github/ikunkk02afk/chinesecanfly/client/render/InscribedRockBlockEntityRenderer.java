package io.github.ikunkk02afk.chinesecanfly.client.render;

import io.github.ikunkk02afk.chinesecanfly.block.InscribedRockBlock;
import io.github.ikunkk02afk.chinesecanfly.block.entity.InscribedRockBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

public final class InscribedRockBlockEntityRenderer implements BlockEntityRenderer<InscribedRockBlockEntity> {
    private static final float TEXT_COVERAGE = 0.72f;
    private static final float SURFACE_OFFSET = 0.501f;
    private static final int INK_COLOR = 0xFF6A382C;
    private static final int SHADOW_COLOR = 0xFF241916;

    private final TextRenderer textRenderer;

    public InscribedRockBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        textRenderer = context.getTextRenderer();
    }

    @Override
    public void render(InscribedRockBlockEntity blockEntity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockState state = blockEntity.getCachedState();
        Direction facing = state.get(InscribedRockBlock.FACING);
        String character = blockEntity.getCharacter();
        float scale = TEXT_COVERAGE / Math.max(textRenderer.getWidth(character), textRenderer.fontHeight);
        float x = -textRenderer.getWidth(character) / 2.0f;
        float y = -textRenderer.fontHeight / 2.0f;

        matrices.push();
        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(getRotationDegrees(facing)));
        matrices.translate(0.0f, 0.0f, SURFACE_OFFSET);
        matrices.scale(scale, -scale, scale);

        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
        textRenderer.draw(character, x + 0.65f, y + 0.65f, SHADOW_COLOR, false, positionMatrix,
                vertexConsumers, TextRenderer.TextLayerType.POLYGON_OFFSET, 0, light);
        textRenderer.draw(character, x, y, INK_COLOR, false, positionMatrix,
                vertexConsumers, TextRenderer.TextLayerType.POLYGON_OFFSET, 0, light);
        matrices.pop();
    }

    private static float getRotationDegrees(Direction facing) {
        return switch (facing) {
            case SOUTH -> 0.0f;
            case EAST -> 90.0f;
            case NORTH -> 180.0f;
            case WEST -> -90.0f;
            default -> throw new IllegalArgumentException("Inscribed rock must face horizontally: " + facing);
        };
    }
}
