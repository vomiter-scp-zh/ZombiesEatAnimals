package com.vomiter.zombieseatanimals.entity;

import com.vomiter.neurolib.common.entity.loot.LootMatchSpec;
import com.vomiter.neurolib.common.entity.loot.LootTableContainsHelper;
import com.vomiter.zombieseatanimals.data.ZEATags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.Objects;
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
                        .map(ItemStack::getItem)
                        .filter(Item::isEdible)
                        .map(ForgeRegistries.ITEMS::getKey)
                        .filter(Objects::nonNull)
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