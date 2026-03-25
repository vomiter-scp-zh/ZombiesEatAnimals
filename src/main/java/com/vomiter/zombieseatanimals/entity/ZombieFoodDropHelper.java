package com.vomiter.zombieseatanimals.entity;

import com.vomiter.neurolib.common.entity.loot.LootMatchSpec;
import com.vomiter.neurolib.common.entity.loot.LootTableContainsHelper;
import com.vomiter.zombieseatanimals.data.ZEATags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public final class ZombieFoodDropHelper {
    private ZombieFoodDropHelper() {
    }

    private static final String ZOMBIE_FOOD_TAG_ID = ZEATags.ZOMBIE_FOOD.location().toString();
    private static volatile LootMatchSpec zombieFoodSpec;

    public static boolean canDropZombieFood(Entity zombie, Entity candidate) {
        if (zombie == null || candidate == null) return false;

        MinecraftServer server = zombie.level().getServer();
        if (server == null) return false;

        return LootTableContainsHelper.entityDefaultLootTableContains(
                server,
                candidate.getType(),
                ZombieFoodDropHelper::getZombieFoodSpec
        );
    }

    public static LootMatchSpec getZombieFoodSpec() {
        LootMatchSpec local = zombieFoodSpec;
        if (local != null) {
            return local;
        }

        synchronized (ZombieFoodDropHelper.class) {
            if (zombieFoodSpec == null) {
                Set<String> itemIds = Arrays.stream(Ingredient.of(ZombieBasicHelpers.ZOMBIE_FOOD).getItems())
                        .filter(item -> item.getFoodProperties(null) != null)
                        .map(ItemStack::getItem)
                        .map(BuiltInRegistries.ITEM::getKey)
                        .map(ResourceLocation::toString)
                        .collect(Collectors.toUnmodifiableSet());

                zombieFoodSpec = new LootMatchSpec(itemIds, ZOMBIE_FOOD_TAG_ID);
            }

            return zombieFoodSpec;
        }
    }

    public static void clear() {
        zombieFoodSpec = null;
        LootTableContainsHelper.clear();
    }
}