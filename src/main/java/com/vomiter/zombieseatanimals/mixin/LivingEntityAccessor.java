package com.vomiter.zombieseatanimals.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Invoker("dropAllDeathLoot")
    void zea$dropAllDeathLoot(
            ServerLevel p_level,
            DamageSource damageSource
    );
}
