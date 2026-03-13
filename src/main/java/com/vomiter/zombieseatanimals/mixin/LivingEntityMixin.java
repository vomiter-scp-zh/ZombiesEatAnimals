package com.vomiter.zombieseatanimals.mixin;

import com.vomiter.zombieseatanimals.entity.IZombieEatAnimal;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "die", at = @At("TAIL"))
    private void zea$addHuntTime(DamageSource damageSource, CallbackInfo ci){
        if(damageSource == null) return;
        if(damageSource.getEntity() instanceof IZombieEatAnimal zombie){
            var goal = zombie.zea$getHuntAnimalGoal();
            if(goal != null) {
                goal.recordSuccessfulHunt(damageSource.getEntity().level().getGameTime());
            }
        }
    }
}
