package io.github.ikunkk02afk.chinesecanfly.client.mixin;

import io.github.ikunkk02afk.chinesecanfly.client.combat.HeldEntityVisualManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** The direct proxy pass uses the normal renderer but deliberately omits labels and leash rendering. */
@Mixin(EntityRenderer.class)
abstract class EntityRendererMixin {
    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void chineseCanFly$skipHeldProxyLabel(Entity entity, Text text, MatrixStack matrices,
                                                  VertexConsumerProvider vertexConsumers, int light,
                                                  float tickDelta, CallbackInfo ci) {
        if (HeldEntityVisualManager.isRenderingHeldProxy()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderLeash", at = @At("HEAD"), cancellable = true)
    private void chineseCanFly$skipHeldProxyLeash(Entity entity, float tickDelta, MatrixStack matrices,
                                                  VertexConsumerProvider vertexConsumers, Entity leashHolder,
                                                  CallbackInfo ci) {
        if (HeldEntityVisualManager.isRenderingHeldProxy()) {
            ci.cancel();
        }
    }
}
