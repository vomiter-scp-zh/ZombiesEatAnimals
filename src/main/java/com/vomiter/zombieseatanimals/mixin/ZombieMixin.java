package com.vomiter.zombieseatanimals.mixin;

import com.vomiter.zombieseatanimals.Config;
import com.vomiter.zombieseatanimals.entity.IZombieEatAnimal;
import com.vomiter.zombieseatanimals.entity.ai.ZombieEatMeatAndRegenGoal;
import com.vomiter.zombieseatanimals.entity.ai.ZombieHuntAnimalsGoal;
import com.vomiter.zombieseatanimals.entity.ai.ZombieMountNearbyZombieHorseGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Zombie.class)
public abstract class ZombieMixin extends Monster implements IZombieEatAnimal {

    protected ZombieMixin(EntityType<? extends Monster> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    @Unique
    ZombieEatMeatAndRegenGoal zea$eatMeatGoal;

    @Inject(method = "addBehaviourGoals", at = @At("HEAD"))
    private void zea$addGoals(CallbackInfo ci){
        //ZombiesEatAnimals.LOGGER.info("[ZEA] Goal Injection");
        Zombie zombie = (Zombie)(Object)this;
        zea$eatMeatGoal = new ZombieEatMeatAndRegenGoal(zombie, 1.0, 20, 32);
        zea$huntAnimalGoal = new ZombieHuntAnimalsGoal(zombie);
        targetSelector.addGoal(3, zea$huntAnimalGoal);
        goalSelector.addGoal(4, zea$eatMeatGoal);
        if(Config.FIND_NEARBY_ZOMBIE_HORSE_AND_RIDE) goalSelector.addGoal(4, new ZombieMountNearbyZombieHorseGoal(zombie, 1, 32, 3));
    }

    @Override
    public void zea$setCanBreakDoor() {
        Zombie zombie = (Zombie)(Object)this;
        zombie.setCanBreakDoors(true);
    }

    @Unique
    ZombieHuntAnimalsGoal zea$huntAnimalGoal;
    public ZombieHuntAnimalsGoal zea$getHuntAnimalGoal(){
        return zea$huntAnimalGoal;
    }
}
