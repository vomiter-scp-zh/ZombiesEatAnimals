package com.vomiter.zombieseatanimals.entity.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.animal.horse.ZombieHorse;

import java.util.EnumSet;
import java.util.List;

public class ZombieMountNearbyZombieHorseGoal extends Goal {
    private final Zombie zombie;
    private final double speedModifier;
    private final double searchRadius;
    private final double mountDistanceSqr;

    private ZombieHorse targetHorse;
    //TODO: Mounted zombie combat still has minor close-range look jitter; acceptable for now unless a dedicated mounted-combat layer is introduced.
    public ZombieMountNearbyZombieHorseGoal(Zombie zombie, double speedModifier, double searchRadius, double mountDistance) {
        this.zombie = zombie;
        this.speedModifier = speedModifier;
        this.searchRadius = searchRadius;
        this.mountDistanceSqr = mountDistance * mountDistance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!zombie.isAlive()) return false;
        if (zombie.isPassenger()) return false;
        if (zombie.getVehicle() != null) return false;
        if (zombie.getTarget() != null) return false;

        ZombieHorse horse = findNearestMountableHorse();
        if (horse == null) return false;

        this.targetHorse = horse;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (targetHorse == null) return false;
        if (!targetHorse.isAlive()) return false;
        if (zombie.isPassenger()) return false;
        if (targetHorse.isVehicle()) return false; // 已經有乘客
        if (zombie.distanceToSqr(targetHorse) > searchRadius * searchRadius * 1.5D) return false;

        return true;
    }

    @Override
    public void start() {
        if (targetHorse != null) {
            zombie.getNavigation().moveTo(targetHorse, speedModifier);
        }
    }

    @Override
    public void stop() {
        zombie.getNavigation().stop();
        targetHorse = null;
    }

    @Override
    public void tick() {
        if (targetHorse == null) return;

        zombie.getLookControl().setLookAt(targetHorse, 30.0F, 30.0F);

        if (targetHorse.isVehicle()) {
            stop();
            return;
        }

        double distSqr = zombie.distanceToSqr(targetHorse);
        if (distSqr <= mountDistanceSqr) {
            zombie.startRiding(targetHorse, true);
            zombie.getNavigation().stop();
        } else {
            zombie.getNavigation().moveTo(targetHorse, speedModifier);
        }
    }

    private ZombieHorse findNearestMountableHorse() {
        List<ZombieHorse> horses = zombie.level().getEntitiesOfClass(
                ZombieHorse.class,
                zombie.getBoundingBox().inflate(searchRadius),
                horse -> horse.isAlive()
                        && !horse.isVehicle()
        );

        ZombieHorse nearest = null;
        double bestDist = Double.MAX_VALUE;

        for (ZombieHorse horse : horses) {
            double dist = zombie.distanceToSqr(horse);
            if (dist < bestDist) {
                bestDist = dist;
                nearest = horse;
            }
        }

        return nearest;
    }
}