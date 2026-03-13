package com.vomiter.zombieseatanimals.mixin;

import com.vomiter.zombieseatanimals.entity.IZombieEatAnimal;
import com.vomiter.zombieseatanimals.entity.ZombieBasicHelpers;
import com.vomiter.zombieseatanimals.entity.ai.ZombieEatMeatAndRegenGoal;
import com.vomiter.zombieseatanimals.entity.ai.ZombieHuntAnimalsGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Zombie.class)
public abstract class ZombieMixin extends Monster implements IZombieEatAnimal {
    @Shadow
    protected abstract boolean supportsBreakDoorGoal();

    protected ZombieMixin(EntityType<? extends Monster> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    @Unique
    ZombieEatMeatAndRegenGoal zea$eatMeatGoal;

    @Inject(method = "addBehaviourGoals", at = @At("HEAD"))
    private void addGoals(CallbackInfo ci){
        Zombie zombie = (Zombie)(Object)this;
        zea$eatMeatGoal = new ZombieEatMeatAndRegenGoal(zombie, 1.0, 20, 32);
        zea$huntAnimalGoal = new ZombieHuntAnimalsGoal(zombie);
        targetSelector.addGoal(3, zea$huntAnimalGoal);
        goalSelector.addGoal(3, zea$eatMeatGoal);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void cooldown(CallbackInfo ci){
        Zombie zombie = (Zombie)(Object)this;
        if(ZombieBasicHelpers.zombiesToUpgrade.remove(zombie)){
            zombie.setCanBreakDoors(supportsBreakDoorGoal());
        }
    }

    @Unique
    ZombieHuntAnimalsGoal zea$huntAnimalGoal;
    public ZombieHuntAnimalsGoal zea$getHuntAnimalGoal(){
        return zea$huntAnimalGoal;
    }
}
