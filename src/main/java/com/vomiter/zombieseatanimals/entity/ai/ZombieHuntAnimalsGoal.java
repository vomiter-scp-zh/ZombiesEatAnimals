package com.vomiter.zombieseatanimals.entity.ai;

import com.vomiter.neurolib.common.entity.hunt.AbstractCappedHuntGoal;
import com.vomiter.zombieseatanimals.Config;
import com.vomiter.zombieseatanimals.data.ZEATags;
import com.vomiter.zombieseatanimals.entity.ZombieBasicHelpers;
import com.vomiter.zombieseatanimals.entity.ZombieFoodDropHelper;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Zombie;

public class ZombieHuntAnimalsGoal extends AbstractCappedHuntGoal<Zombie, Animal> {
    private static final long ONE_MC_DAY_TICKS = 24000L;

    public ZombieHuntAnimalsGoal(Zombie zombie) {
        super(zombie, Animal.class, 10, true, true);
    }

    @Override
    protected boolean isHuntEnabled() {
        return true;
    }

    @Override
    protected boolean bypassAllCaps() {
        return Config.BERSERKER_HUNTING;
    }

    @Override
    protected boolean shouldSearchForTarget() {
        return Config.ALWAYS_HUNTING || ZombieBasicHelpers.isNotMaxed(hunter);
    }

    @Override
    protected boolean isValidHuntTarget(Animal animal) {
        if (animal == null) return false;
        if (animal.getType().is(ZEATags.NOT_ZOMBIE_TARGET_ANIMAL)) return false;
        if (animal.getType().is(ZEATags.ZOMBIE_TARGET_ANIMAL)) return true;
        return ZombieFoodDropHelper.canDropZombieFood(hunter, animal);
    }

    @Override
    protected String getWorldHistoryKey() {
        // 不共用 cap 就用這個
        return "zombie_hunt";

        // 如果想跟 spider 共用，把這裡改成 "predator_hunt"
    }

    @Override
    protected long getHuntCooldownTicks() {
        return Config.HUNT_COOLDOWN_TICKS;
    }

    @Override
    protected int getLocalHuntCap() {
        return Config.HUNT_CAP_FOR_A_DAY;
    }

    @Override
    protected int getWorldHuntCap() {
        return Config.HUNT_CAP_FOR_A_DAY_TOTAL;
    }

    @Override
    protected long getHistoryWindowTicks() {
        return ONE_MC_DAY_TICKS;
    }

    @Override
    protected double getFollowDistanceFactor() {
        return Config.HUNT_FOLLOW_DISTANCE_FACTOR;
    }
}