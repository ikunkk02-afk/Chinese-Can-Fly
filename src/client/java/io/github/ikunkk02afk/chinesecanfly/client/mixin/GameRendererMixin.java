package io.github.ikunkk02afk.chinesecanfly.client.mixin;

import io.github.ikunkk02afk.chinesecanfly.client.superflight.SuperFlightClientController;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Adds only our transient FOV bonus at the vanilla method's return point. */
@Mixin(GameRenderer.class)
abstract class GameRendererMixin {
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void chineseCanFly$addSuperFlightFov(Camera camera, float tickDelta, boolean changingFov,
                                                  CallbackInfoReturnable<Double> cir) {
        if (changingFov) {
            cir.setReturnValue(cir.getReturnValue() + SuperFlightClientController.getFovBonus(tickDelta));
        }
    }
}
