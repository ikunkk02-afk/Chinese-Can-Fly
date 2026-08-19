package io.github.ikunkk02afk.chinesecanfly.mixin;

import io.github.ikunkk02afk.chinesecanfly.ability.combat.SuperMeleeDamage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** A narrow damage-pipeline adjustment for direct awakened-player melee attacks. */
@Mixin(LivingEntity.class)
abstract class LivingEntityMixin {
    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float chineseCanFly$modifyDirectPlayerMeleeDamage(float amount, DamageSource source) {
        return SuperMeleeDamage.modifyIncomingDamage(amount, source);
    }

    @Inject(method = "damage", at = @At("RETURN"))
    private void chineseCanFly$applySuperMeleeFeedback(DamageSource source, float amount,
                                                       CallbackInfoReturnable<Boolean> callback) {
        if (callback.getReturnValue()) {
            SuperMeleeDamage.onSuccessfulHit((LivingEntity) (Object) this, source);
        }
    }
}
