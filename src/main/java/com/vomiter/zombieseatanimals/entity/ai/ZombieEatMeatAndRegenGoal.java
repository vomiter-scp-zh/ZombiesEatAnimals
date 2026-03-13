package com.vomiter.zombieseatanimals.entity.ai;

import com.vomiter.neurolib.common.entity.eat.MobEatDroppedFoodGoal;
import com.vomiter.neurolib.common.entity.eat.MobEatingFx;
import com.vomiter.zombieseatanimals.Config;
import com.vomiter.zombieseatanimals.entity.ZombieBasicHelpers;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ZombieEatMeatAndRegenGoal extends MobEatDroppedFoodGoal<Zombie> {

    private static final int FAIL_TTL_TICKS = 600;
    private static final int STUCK_CHECK_INTERVAL_TICKS = 10;
    private static final int STUCK_MAX_TICKS = 80;
    private static final double STUCK_MIN_PROGRESS = 0.05D;
    private static final int LOSS_OF_SIGHT_MAX_TICKS = 200;
    private static final int REPATH_INTERVAL_TICKS = 10;
    private static final double EAT_HORIZONTAL_RANGE = 1.0D;
    private static final double EAT_VERTICAL_RANGE = 1.25D;
    private static final int EATING_FX_DURATION_TICKS = 40;

    public ZombieEatMeatAndRegenGoal(Zombie zombie, double speed, int scanIntervalTicks, double searchRadius) {
        super(
                zombie,
                speed,
                scanIntervalTicks,
                searchRadius,
                FAIL_TTL_TICKS,
                STUCK_CHECK_INTERVAL_TICKS,
                STUCK_MAX_TICKS,
                STUCK_MIN_PROGRESS,
                LOSS_OF_SIGHT_MAX_TICKS,
                REPATH_INTERVAL_TICKS,
                EAT_HORIZONTAL_RANGE,
                EAT_VERTICAL_RANGE,
                EATING_FX_DURATION_TICKS
        );
    }

    @Override
    protected boolean isGoalEnabled() {
        return Config.ENABLE_EAT_FOOD_ITEMS;
    }

    @Override
    protected boolean canStartEating() {
        return ZombieBasicHelpers.isNotMaxed(mob);
    }

    @Override
    protected boolean canContinueEating() {
        return ZombieBasicHelpers.isNotMaxed(mob);
    }

    @Override
    protected boolean isEdible(ItemStack stack, ItemEntity entity) {
        if (!stack.is(ZombieBasicHelpers.ZOMBIE_FOOD)) return false;
        return stack.getItem().getFoodProperties(stack, mob) != null;
    }

    @Override
    protected void onAteFood(ItemStack bite, ItemEntity source) {
        var fp = bite.getItem().getFoodProperties(bite, mob);
        int recovery = (fp == null) ? 0 : fp.getNutrition() * Config.RECOVERY_PER_NUTRITION;

        if (bite.is(Items.ROTTEN_FLESH) && Config.ROTTEN_FLESH_GIVE_RESISTANCE) {
            mob.addEffect(new MobEffectInstance(
                    MobEffects.DAMAGE_RESISTANCE,
                    20 * 30,
                    mob.getRandom().nextInt(3)
            ));
        }

        ZombieBasicHelpers.recoverAndBoostHealth(mob, recovery);
        MobEatingFx.playBiteSounds(mob, SoundEvents.GENERIC_EAT, 1.0F, 1.0F,
                net.minecraft.sounds.SoundEvents.ZOMBIE_AMBIENT, 0.2F, 0.8F);
    }

    @Override
    protected int getEatCooldownTicks() {
        return Config.EAT_COOLDOWN_TICKS;
    }

    @Override
    protected void onStart() {
        mob.setTarget(null);
    }

    @Override public boolean canUse() {
        if (mob.level().isClientSide) return false;
        if (!Config.ENABLE_EAT_FOOD_ITEMS) return false;
        if (!ZombieBasicHelpers.isNotMaxed((Zombie) mob)) return false;
        if (eatingFx.isEating()) return false;
        if (nextScanTick-- > 0) return false;
        nextScanTick = scanIntervalTicks;
        targetFood = findNearestFoodItem();
        return targetFood != null;
    }
    @Override public boolean canContinueToUse() {
        if (mob.level().isClientSide) return false;
        if (eatingFx.isEating()) return true;
        if (!Config.ENABLE_EAT_FOOD_ITEMS) return false;
        if (targetFood == null || !targetFood.isAlive()) return false;
        if (!ZombieBasicHelpers.isNotMaxed(mob)) return false;
        if (targetFood.getItem().isEmpty()) return false;
        if (lossOfSightTicks >= lossOfSightMaxTicks) {
            failCache.markFailed(targetFood, mob.level().getGameTime());
            lossOfSightTicks = 0; return false;
        }
        return mob.distanceToSqr(targetFood) <= (searchRadius * searchRadius);
    }
}