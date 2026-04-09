package com.vomiter.zombieseatanimals.entity;

import com.vomiter.zombieseatanimals.Config;
import com.vomiter.zombieseatanimals.Helpers;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ZombieBasicHelpers {
    public static List<Zombie> zombiesToUpgrade = new ArrayList<>();
    public static TagKey<Item> ZOMBIE_FOOD = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), Helpers.id("zombie_food"));


    public static int getMaxHealthBoostCap(Zombie zombie){
        if(zombie.level().getDifficulty().equals(Difficulty.HARD)) return Config.MAX_HEALTH_BOOST_CAP + Config.MAX_HEALTH_BOOST_CAP_HARD_MODE_ADDITION;
        else return Config.MAX_HEALTH_BOOST_CAP;
    }

    public static boolean isNotMaxed(Zombie zombie){
        if(zombie.getMaxHealth() - zombie.getHealth() > 2) return true;
        AttributeInstance maxHealth = zombie.getAttribute(Attributes.MAX_HEALTH);
        if(maxHealth == null) return false;
        AttributeModifier zeaMod = maxHealth.getModifier(ZEA_HP_BOOST_UUID);
        if(zeaMod == null) return true;
        if(zeaMod.getAmount() < getMaxHealthBoostCap(zombie)) return true;
        return false;
    }
    static UUID ZEA_HP_BOOST_UUID = UUID.fromString("d9b48039-8973-4db2-8f31-0edb3ceb655e");
    public static UUID getZeaHpBoostUuid() {return ZEA_HP_BOOST_UUID;}
    static UUID ZEA_LEADER_REINFORCEMENT_UUID = UUID.fromString("22279b03-3cbb-4a1c-8f7d-98d06e8b4211");

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
            AttributeModifier zeaMod = maxHealth.getModifier(ZEA_HP_BOOST_UUID);
            if(zeaMod != null){
                if(zeaMod.getAmount() >= getMaxHealthBoostCap(zombie)) return;
                recovery += (int) zeaMod.getAmount();
                maxHealth.removeModifier(ZEA_HP_BOOST_UUID);
            }
            maxHealth.addPermanentModifier(new AttributeModifier(ZEA_HP_BOOST_UUID, "ZEA HEALTH BOOST", recovery, AttributeModifier.Operation.ADDITION));
            zombie.heal(recovery);
            if(Config.ZOMBIES_BECOME_PERSISTENT) zombie.setPersistenceRequired();

            var zeaMod2 = maxHealth.getModifier(ZEA_HP_BOOST_UUID);
            if(zeaMod2 == null) return;
            if(zeaMod2.getAmount() >= Config.MAX_HEALTH_BOOST_CAP){
                upgradeToLeader(zombie);
            }
        }
    }

    private static void upgradeToLeader(Zombie zombie){
        var src = zombie.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
        if(src == null) return;
        boolean isZEALeader = src.getModifier(ZEA_LEADER_REINFORCEMENT_UUID) != null;
        boolean isVanillaLeader = false;
        if(!isZEALeader){
            isVanillaLeader = src.getModifiers().stream()
                    .anyMatch(m -> "Leader zombie bonus".equals(m.getName()));
        }
        if(!isZEALeader && !isVanillaLeader){
            src.addPermanentModifier(
                    new AttributeModifier(
                            ZEA_LEADER_REINFORCEMENT_UUID,
                            "Leader zombie bonus",
                            zombie.getRandom().nextDouble() * 0.25D + 0.5D,
                            AttributeModifier.Operation.ADDITION)
            );
            zombiesToUpgrade.add(zombie);
        }
    }
}

