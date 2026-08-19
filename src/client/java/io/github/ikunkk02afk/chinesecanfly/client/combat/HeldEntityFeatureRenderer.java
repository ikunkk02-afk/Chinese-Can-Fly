package io.github.ikunkk02afk.chinesecanfly.client.combat;

import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RotationAxis;

/** Renders the original target entity from the fully posed right-arm model matrix, without creating a clone. */
public final class HeldEntityFeatureRenderer
        extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    private static final float MAX_VISUAL_HEIGHT = 2.8F;

    private HeldEntityFeatureRenderer(
            FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
    }

    public static void register() {
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, renderer, registrationHelper, context) -> {
            if (renderer instanceof PlayerEntityRenderer playerRenderer) {
                registrationHelper.register(new HeldEntityFeatureRenderer(playerRenderer));
            }
        });
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                       AbstractClientPlayerEntity holder, float limbAngle, float limbDistance, float tickDelta,
                       float animationProgress, float headYaw, float headPitch) {
        LivingEntity target = HeldEntityVisualManager.getHeldTarget(holder);
        if (target == null || shouldSkipLocalFirstPerson(holder)) {
            return;
        }

        matrices.push();
        // ModelPart.rotate includes Player Animator's final arm pose for this exact render frame.
        getContextModel().rightArm.rotate(matrices);
        matrices.translate(0.0F, 0.56F, 0.03F);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
        float scale = Math.min(1.0F, MAX_VISUAL_HEIGHT / Math.max(0.1F, target.getHeight()));
        matrices.scale(scale, scale, scale);
        matrices.translate(0.0F, -target.getHeight() * 0.48F, 0.0F);
        renderTarget(target, tickDelta, matrices, vertexConsumers, light);
        matrices.pop();
    }

    private static boolean shouldSkipLocalFirstPerson(AbstractClientPlayerEntity holder) {
        MinecraftClient client = MinecraftClient.getInstance();
        return holder == client.player && client.options.getPerspective().isFirstPerson();
    }

    @SuppressWarnings("unchecked")
    private static void renderTarget(LivingEntity target, float tickDelta, MatrixStack matrices,
                                     VertexConsumerProvider vertexConsumers, int light) {
        EntityRenderDispatcher dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        EntityRenderer<Entity> renderer = (EntityRenderer<Entity>) dispatcher.getRenderer(target);
        HeldEntityVisualManager.renderProxy(() ->
                renderer.render(target, target.getYaw(), tickDelta, matrices, vertexConsumers, light));
    }
}
