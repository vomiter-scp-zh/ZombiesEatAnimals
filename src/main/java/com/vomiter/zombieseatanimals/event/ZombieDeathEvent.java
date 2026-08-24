package com.vomiter.zombieseatanimals.event;

import com.vomiter.zombieseatanimals.Config;
import com.vomiter.zombieseatanimals.entity.ZombieBasicHelpers;
import com.vomiter.zombieseatanimals.mixin.LivingEntityAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public class ZombieDeathEvent {
    public static void onZombieDeath(LivingDeathEvent event){
        if(!Config.ZOMBIES_DROP_MORE_LOOT) return;
        if(!(event.getEntity() instanceof Zombie zombie)) return;
        var maxHp = zombie.getAttribute(Attributes.MAX_HEALTH);
        if (maxHp == null) return;
        var boost = maxHp.getModifier(ZombieBasicHelpers.getZeaHpBoostRl());
        if (boost == null) return;
        var base = maxHp.getBaseValue();
        var dmg = event.getSource();
        if(dmg == null) dmg = zombie.damageSources().generic();
        for (int i = 0; i < boost.amount() / (base * Config.HP_LOOT_RATIO); i++) {
            if (zombie instanceof LivingEntityAccessor accessor && zombie.level() instanceof ServerLevel serverLevel){
                accessor.zea$dropAllDeathLoot(serverLevel, dmg);
            }
        }
    }
}
