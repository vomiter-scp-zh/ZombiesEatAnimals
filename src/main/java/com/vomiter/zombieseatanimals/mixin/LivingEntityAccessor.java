package com.vomiter.zombieseatanimals.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Invoker("dropAllDeathLoot")
    void dropAllDeathLoot(
            net.minecraft.server.level.ServerLevel p_level,
            net.minecraft.world.damagesource.DamageSource damageSource
    );
}
