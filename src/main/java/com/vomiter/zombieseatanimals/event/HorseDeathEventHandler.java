package com.vomiter.zombieseatanimals.event;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public class HorseDeathEventHandler {
    public static void onLivingDeath(LivingDeathEvent event){
        LivingEntity entity = event.getEntity();
        Level level = entity.level();
        if(level.isClientSide()) return;
        if(entity instanceof Horse horse){
            var attacker = event.getSource().getEntity();
            if(attacker instanceof Zombie){
                if ((level.getDifficulty() == Difficulty.NORMAL || level.getDifficulty() == Difficulty.HARD)
                        && EventHooks.canLivingConvert(entity, EntityType.ZOMBIE_HORSE, (timer) -> {})) {
                    if (level.getDifficulty() != Difficulty.HARD && level.random.nextBoolean()) {
                        return;
                    }

                    // 先保存要繼承的資料
                    double jumpStrength = horse.getAttributeBaseValue(Attributes.JUMP_STRENGTH);
                    double moveSpeed = horse.getAttributeBaseValue(Attributes.MOVEMENT_SPEED);
                    float health = Math.min(horse.getHealth(), horse.getMaxHealth());

                    boolean tamed = horse.isTamed();
                    boolean saddled = horse.isSaddled();

                    var ownerUUID = horse.getOwnerUUID();
                    var customName = horse.getCustomName();
                    boolean customNameVisible = horse.isCustomNameVisible();
                    boolean baby = horse.isBaby();
                    ItemStack horseArmor = horse.getBodyArmorItem();

                    ZombieHorse zombieHorse = horse.convertTo(EntityType.ZOMBIE_HORSE, false);
                    if (zombieHorse != null) {

                        zombieHorse.finalizeSpawn(
                                (ServerLevelAccessor) level,
                                level.getCurrentDifficultyAt(zombieHorse.blockPosition()),
                                MobSpawnType.CONVERSION,
                                null
                        );

                        // 再把保留資料寫回去
                        if (zombieHorse.getAttribute(Attributes.JUMP_STRENGTH) != null) {
                            zombieHorse.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(jumpStrength);
                        }
                        if (zombieHorse.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
                            zombieHorse.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(moveSpeed);
                        }

                        if (tamed) {
                            zombieHorse.setTamed(true);
                            zombieHorse.setOwnerUUID(ownerUUID);
                        }

                        if (customName != null) {
                            zombieHorse.setCustomName(customName);
                            zombieHorse.setCustomNameVisible(customNameVisible);
                        }

                        if (baby) {
                            zombieHorse.setBaby(true);
                        }

                        if (saddled) {
                            zombieHorse.equipSaddle(new ItemStack(Items.SADDLE),null);
                        }

                        zombieHorse.spawnAtLocation(horseArmor);
                        EventHooks.onLivingConvert(horse, zombieHorse);
                        if (!attacker.isSilent()) {
                            level.levelEvent(null, 1026, attacker.blockPosition(), 0);
                        }
                    }
                }
            }
        }
    }

}
