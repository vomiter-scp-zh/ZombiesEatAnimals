package com.vomiter.zombieseatanimals.entity;

import com.vomiter.zombieseatanimals.Config;
import com.vomiter.zombieseatanimals.Helpers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;


public class ZombieBasicHelpers {
    public static List<Zombie> zombiesToUpgrade = new ArrayList<>();
    public static TagKey<Item> ZOMBIE_FOOD = TagKey.create(BuiltInRegistries.ITEM.key(), Helpers.id("zombie_food"));
    static Identifier ZEA_HP_BOOST_RL = Identifier.fromNamespaceAndPath("zombieseatanimals", "hp_boost");
    static Identifier ZEA_LEADER_REINFORCEMENT_RL = Identifier.fromNamespaceAndPath("zombieseatanimals", "leader_reinforce_boost");
    static Identifier LEADER_ZOMBIE_BONUS_ID = Identifier.withDefaultNamespace("leader_zombie_bonus");

    public static Identifier getZeaHpBoostRl(){
        return ZEA_HP_BOOST_RL;
    }

    public static int getMaxHealthBoostCap(Zombie zombie){
        if(zombie.level().getDifficulty().equals(Difficulty.HARD)) return Config.MAX_HEALTH_BOOST_CAP + Config.MAX_HEALTH_BOOST_CAP_HARD_MODE_ADDITION;
        else return Config.MAX_HEALTH_BOOST_CAP;
    }

    public static boolean isNotMaxed(Zombie zombie){
        if(zombie.getMaxHealth() - zombie.getHealth() > 2) return true;
        AttributeInstance maxHealth = zombie.getAttribute(Attributes.MAX_HEALTH);
        if(maxHealth == null) return false;
        AttributeModifier zeaMod = maxHealth.getModifier(ZEA_HP_BOOST_RL);
        if(zeaMod == null) return true;
        if(zeaMod.amount() < getMaxHealthBoostCap(zombie)) return true;
        return false;
    }


    public static void recoverAndBoostHealth(Zombie zombie, int recovery){
        if(isNotMaxed(zombie)){
            if(zombie.getHealth() < zombie.getMaxHealth()){
                int healthDeficit = (int) (zombie.getMaxHealth() - zombie.getHealth());
                zombie.heal(Math.min(healthDeficit, recovery));
                recovery -= healthDeficit;
            }
            if(recovery <= 0) return;
            AttributeInstance maxHealth = zombie.getAttribute(Attributes.MAX_HEALTH);
            if(maxHealth == null) return;
            AttributeModifier zeaMod = maxHealth.getModifier(ZEA_HP_BOOST_RL);
            if(zeaMod != null){
                if(zeaMod.amount() >= getMaxHealthBoostCap(zombie)) return;
                recovery += (int) zeaMod.amount();
                maxHealth.removeModifier(ZEA_HP_BOOST_RL);
            }
            maxHealth.addPermanentModifier(new AttributeModifier(ZEA_HP_BOOST_RL, recovery, AttributeModifier.Operation.ADD_VALUE));
            zombie.heal(recovery);
            if(Config.ZOMBIES_BECOME_PERSISTENT) zombie.setPersistenceRequired();

            var zeaMod2 = maxHealth.getModifier(ZEA_HP_BOOST_RL);
            if(zeaMod2 == null) return;
            if(zeaMod2.amount() >= Config.MAX_HEALTH_BOOST_CAP){
                upgradeToLeader(zombie);
            }
        }
    }

    private static void upgradeToLeader(Zombie zombie){
        var src = zombie.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
        if(src == null) return;
        boolean isZEALeader = src.getModifier(ZEA_LEADER_REINFORCEMENT_RL) != null;
        boolean isVanillaLeader = false;
        if(!isZEALeader){
            isVanillaLeader = src.getModifiers().stream()
                    .anyMatch(m -> m.is(LEADER_ZOMBIE_BONUS_ID));
        }
        if(!isZEALeader && !isVanillaLeader){
            src.addPermanentModifier(
                    new AttributeModifier(
                            ZEA_LEADER_REINFORCEMENT_RL,
                            zombie.getRandom().nextDouble() * 0.25D + 0.5D,
                            AttributeModifier.Operation.ADD_VALUE)
            );
            zombiesToUpgrade.add(zombie);
        }
    }
}

