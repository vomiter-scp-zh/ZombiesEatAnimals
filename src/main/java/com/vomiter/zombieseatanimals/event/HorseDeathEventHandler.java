package com.vomiter.zombieseatanimals.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.animal.equine.ZombieHorse;
import net.minecraft.world.entity.monster.zombie.Zombie;
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
                    if (level.getDifficulty() != Difficulty.HARD && level.getRandom().nextBoolean()) {
                        return;
                    }

                    // 先保存要繼承的資料
                    double jumpStrength = horse.getAttributeBaseValue(Attributes.JUMP_STRENGTH);
                    double moveSpeed = horse.getAttributeBaseValue(Attributes.MOVEMENT_SPEED);
                    float health = Math.min(horse.getHealth(), horse.getMaxHealth());

                    boolean tamed = horse.isTamed();
                    boolean saddled = horse.isSaddled();

                    var owner = horse.getOwner();
                    var customName = horse.getCustomName();
                    boolean customNameVisible = horse.isCustomNameVisible();
                    boolean baby = horse.isBaby();
                    ItemStack horseArmor = horse.getBodyArmorItem();

                    ZombieHorse zombieHorse = horse
                            .convertTo(
                                    EntityType.ZOMBIE_HORSE,
                                    ConversionParams.single(horse, false, false),
                                    newZombieHorse -> {
                                        newZombieHorse.finalizeSpawn(
                                                (ServerLevelAccessor) level,
                                                ((ServerLevelAccessor) level).getCurrentDifficultyAt(newZombieHorse.blockPosition()),
                                                EntitySpawnReason.CONVERSION,
                                                null
                                        );
                                        // 再把保留資料寫回去
                                        if (newZombieHorse.getAttribute(Attributes.JUMP_STRENGTH) != null) {
                                            newZombieHorse.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(jumpStrength);
                                        }
                                        if (newZombieHorse.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
                                            newZombieHorse.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(moveSpeed);
                                        }

                                        if (tamed) {
                                            newZombieHorse.setTamed(true);
                                            newZombieHorse.setOwner(owner);
                                        }

                                        if (customName != null) {
                                            newZombieHorse.setCustomName(customName);
                                            newZombieHorse.setCustomNameVisible(customNameVisible);
                                        }

                                        if (baby) {
                                            newZombieHorse.setBaby(true);
                                        }

                                        if (saddled) {
                                            newZombieHorse.setItemSlot (EquipmentSlot.SADDLE, new ItemStack(Items.SADDLE));
                                        }

                                        newZombieHorse.spawnAtLocation((ServerLevel) level, horseArmor);
                                        EventHooks.onLivingConvert(horse, newZombieHorse);
                                        if (!attacker.isSilent()) {
                                            level.levelEvent(null, 1026, attacker.blockPosition(), 0);
                                        }

                                    });
                }
            }
        }
    }

}
