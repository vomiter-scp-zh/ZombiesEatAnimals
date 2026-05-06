package com.vomiter.zombieseatanimals.entity.ai;

import com.vomiter.zombieseatanimals.Config;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.animal.equine.ZombieHorse;

import java.util.EnumSet;
import java.util.List;

public class ZombieMountNearbyZombieHorseGoal extends Goal {
    private final Zombie zombie;
    private final double speedModifier;
    private final double searchRadius;
    private final double mountDistanceSqr;

    private final int scanIntervalTicks;
    private final int repathIntervalTicks;
    private final int failedPathCooldownTicks;

    private ZombieHorse targetHorse;
    private long nextScanTick = 0L;
    private long nextRepathTick = 0L;
    private long failedPathCooldownUntil = 0L;

    public ZombieMountNearbyZombieHorseGoal(
            Zombie zombie,
            double speedModifier,
            double searchRadius,
            double mountDistance
    ) {
        this(zombie, speedModifier, searchRadius, mountDistance, 20, 12, 60);
    }

    public ZombieMountNearbyZombieHorseGoal(
            Zombie zombie,
            double speedModifier,
            double searchRadius,
            double mountDistance,
            int scanIntervalTicks,
            int repathIntervalTicks,
            int failedPathCooldownTicks
    ) {
        this.zombie = zombie;
        this.speedModifier = speedModifier;
        this.searchRadius = searchRadius;
        this.mountDistanceSqr = mountDistance * mountDistance;
        this.scanIntervalTicks = Math.max(1, scanIntervalTicks);
        this.repathIntervalTicks = Math.max(1, repathIntervalTicks);
        this.failedPathCooldownTicks = Math.max(1, failedPathCooldownTicks);
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!Config.FIND_NEARBY_ZOMBIE_HORSE_AND_RIDE) return false;
        if (!zombie.isAlive()) return false;
        if (zombie.isPassenger()) return false;
        if (zombie.getVehicle() != null) return false;
        if (zombie.getTarget() != null) return false;

        long now = zombie.level().getGameTime();
        if (now < failedPathCooldownUntil) return false;
        if (now < nextScanTick) return false;

        nextScanTick = now + scanIntervalTicks + (zombie.getId() % 5L);

        ZombieHorse horse = findNearestMountableHorse();
        if (horse == null) return false;

        this.targetHorse = horse;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (!Config.FIND_NEARBY_ZOMBIE_HORSE_AND_RIDE) return false;
        if (targetHorse == null) return false;
        if (!targetHorse.isAlive()) return false;
        if (zombie.isPassenger()) return false;
        if (targetHorse.isVehicle()) return false; // 已經有乘客
        if (zombie.distanceToSqr(targetHorse) > searchRadius * searchRadius * 1.5D) return false;

        return true;
    }

    @Override
    public void start() {
        long now = zombie.level().getGameTime();
        nextRepathTick = 0L;
        tryMoveToTarget(now);
    }

    @Override
    public void stop() {
        zombie.getNavigation().stop();
        targetHorse = null;
        nextRepathTick = 0L;
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
            zombie.startRiding(targetHorse);
            zombie.getNavigation().stop();
            return;
        }

        long now = zombie.level().getGameTime();
        if (now >= nextRepathTick) {
            tryMoveToTarget(now);
        }
    }

    private void tryMoveToTarget(long now) {
        if (targetHorse == null || !targetHorse.isAlive()) return;

        boolean accepted = zombie.getNavigation().moveTo(targetHorse, speedModifier);
        if (accepted) {
            nextRepathTick = now + repathIntervalTicks;
        } else {
            failedPathCooldownUntil = now + failedPathCooldownTicks;
            nextRepathTick = now + failedPathCooldownTicks;
            zombie.getNavigation().stop();
            targetHorse = null;
        }
    }

    private ZombieHorse findNearestMountableHorse() {
        List<ZombieHorse> horses = zombie.level().getEntitiesOfClass(
                ZombieHorse.class,
                zombie.getBoundingBox().inflate(searchRadius),
                horse -> horse.isAlive() && !horse.isVehicle()
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