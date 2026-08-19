package io.github.ikunkk02afk.chinesecanfly.client.mixin;

import io.github.ikunkk02afk.chinesecanfly.client.combat.HeldEntityVisualManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Suppresses the ordinary world pass only while an entity has an active held-model proxy. */
@Mixin(EntityRenderDispatcher.class)
abstract class EntityRenderDispatcherMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void chineseCanFly$hideHeldEntityWorldRender(Entity entity, double x, double y, double z, float yaw,
                                                         float tickDelta, MatrixStack matrices,
                                                         VertexConsumerProvider vertexConsumers, int light,
                                                         CallbackInfo ci) {
        if (HeldEntityVisualManager.shouldHideWorldEntity(entity)) {
            ci.cancel();
        }
    }
}
