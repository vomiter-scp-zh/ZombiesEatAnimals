package com.vomiter.zombieseatanimals.event;

import com.vomiter.zombieseatanimals.Config;
import com.vomiter.zombieseatanimals.entity.ZombieBasicHelpers;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class ZombieDeathEvent {
    public static void onZombieDeath(LivingDeathEvent event){
        if(!Config.ZOMBIES_DROP_MORE_LOOT) return;
        if(!(event.getEntity() instanceof Zombie zombie)) return;
        var maxHp = zombie.getAttribute(Attributes.MAX_HEALTH);
        if (maxHp == null) return;
        var boost = maxHp.getModifier(ZombieBasicHelpers.getZeaHpBoostUuid());
        if (boost == null) return;
        var base = maxHp.getBaseValue();
        var dmg = event.getSource();
        if(dmg == null) dmg = zombie.damageSources().generic();
        for (int i = 0; i < boost.getAmount() / (base * Config.HP_LOOT_RATIO); i++) {
            var zombie0 = (Zombie)zombie.getType().create(zombie.level());
            if(zombie0 == null) continue;
            zombie0.setNoAi(true);
            zombie0.setInvisible(true);
            zombie0.setPos(zombie.getEyePosition());
            zombie0.die(dmg);
        }
    }
}
