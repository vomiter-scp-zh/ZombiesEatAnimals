package com.vomiter.zombieseatanimals.mixin;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.monster.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Zombie.class)
public abstract class Zombie_MountedHeadClampMixin {

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void zea$softClampHeadToHorse(CallbackInfo ci) {
        Zombie self = (Zombie) (Object) this;

        if (!(self.getVehicle() instanceof ZombieHorse horse)) {
            return;
        }

        float horseYaw = horse.getYRot();
        float headYaw = self.getYHeadRot();

        // 頭相對馬頭允許的最大水平偏角
        float maxOffset = 40.0F;

        float delta = Mth.wrapDegrees(headYaw - horseYaw);

        // 只有超出範圍才介入
        if (delta > maxOffset) {
            float targetYaw = horseYaw + maxOffset;
            float newYaw = Mth.rotLerp(0.35F, headYaw, targetYaw);
            self.setYHeadRot(newYaw);
            self.yHeadRotO = newYaw;
        } else if (delta < -maxOffset) {
            float targetYaw = horseYaw - maxOffset;
            float newYaw = Mth.rotLerp(0.35F, headYaw, targetYaw);
            self.setYHeadRot(newYaw);
            self.yHeadRotO = newYaw;
        }
    }
}