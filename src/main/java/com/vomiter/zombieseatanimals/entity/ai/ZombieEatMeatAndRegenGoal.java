package com.vomiter.zombieseatanimals.entity.ai;

import com.vomiter.neurolib.common.entity.gather.eat.MobEatDroppedItemGoal;
import com.vomiter.zombieseatanimals.Config;
import com.vomiter.zombieseatanimals.entity.ZombieBasicHelpers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ZombieEatMeatAndRegenGoal extends MobEatDroppedItemGoal<Zombie> {

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
    protected boolean canStartAction() {
        return ZombieBasicHelpers.isNotMaxed(mob);
    }

    @Override
    protected boolean canContinueAction() {
        return ZombieBasicHelpers.isNotMaxed(mob);
    }

    @Override
    protected boolean isEdible(ItemStack stack, ItemEntity entity) {
        if (!stack.is(ZombieBasicHelpers.ZOMBIE_FOOD)) {
            return false;
        }
        return stack.get(DataComponents.FOOD) != null;
    }

    @Override
    protected void onAteFood(ItemStack bite, ItemEntity source) {
        var foodProperties = bite.get(DataComponents.FOOD);
        int recovery = foodProperties == null
                ? 0
                : foodProperties.nutrition() * Config.RECOVERY_PER_NUTRITION;

        if (bite.is(Items.ROTTEN_FLESH) && Config.ROTTEN_FLESH_GIVE_RESISTANCE) {
            mob.addEffect(new MobEffectInstance(
                    MobEffects.RESISTANCE,
                    20 * 30,
                    mob.getRandom().nextInt(3)
            ));
        }

        ZombieBasicHelpers.recoverAndBoostHealth(mob, recovery);
        playDefaultEatSound();
        mob.playSound(SoundEvents.ZOMBIE_AMBIENT, 0.2F, 0.8F);
    }

    @Override
    protected int getActionCooldownTicks() {
        return Config.EAT_COOLDOWN_TICKS;
    }

    @Override
    protected void onStart() {
        mob.setTarget(null);
    }

    @Override
    protected boolean isCloseEnoughToInteract(ItemEntity item) {
        if(mob.getVehicle() == null) return super.isCloseEnoughToInteract(item);
        double dx = mob.getX() - item.getX();
        double dz = mob.getZ() - item.getZ();
        double dy = Math.abs(mob.getVehicle().getY() - item.getY());

        return dx * dx + dz * dz <= interactHorizontalRange * interactHorizontalRange
                && dy <= interactVerticalRange;
    }

    @Override
    public boolean canUse(){
        return super.canUse();
    }

}