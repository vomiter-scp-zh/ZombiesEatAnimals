package com.vomiter.zombieseatanimals.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.animal.equine.ZombieHorse;
import net.minecraft.world.entity.monster.zombie.Zombie;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MeleeAttackGoal.class)
public abstract class MeleeAttackGoal_MountedZombieMixin {

    @Shadow @Final protected PathfinderMob mob;

    @Shadow protected abstract void resetAttackCooldown();
    @Shadow protected abstract boolean isTimeToAttack();

    @Inject(method = "checkAndPerformAttack", at = @At("HEAD"), cancellable = true)
    private void zea$mountedAttack(LivingEntity target, double distToTargetSqr, CallbackInfo ci) {
        if (!(this.mob instanceof Zombie zombie) || !(zombie.getVehicle() instanceof ZombieHorse)) {
            return;
        }

        double dx = zombie.getX() - target.getX();
        double dz = zombie.getZ() - target.getZ();
        double horizontalSqr = dx * dx + dz * dz;
        double dy = Math.abs(zombie.getY() - target.getY());

        double maxHorizontal = 2.5D;
        double maxVertical = 3.25D;

        if (horizontalSqr <= maxHorizontal * maxHorizontal && dy <= maxVertical) {
            if (this.isTimeToAttack()) {
                this.resetAttackCooldown();
                this.mob.swing(InteractionHand.MAIN_HAND);
                if(mob.level() instanceof ServerLevel serverLevel) this.mob.doHurtTarget(serverLevel, target);
            }
            ci.cancel();
        }
    }

    @Inject(method = "getAttackReachSqr", at = @At("HEAD"), cancellable = true)
    private void zea$getAttackReachSqr(LivingEntity target, CallbackInfoReturnable<Double> cir) {
        if (this.mob instanceof Zombie zombie && zombie.getVehicle() instanceof ZombieHorse) {
            // 2.5 blocks
            cir.setReturnValue(6.25D);
        }
    }

}