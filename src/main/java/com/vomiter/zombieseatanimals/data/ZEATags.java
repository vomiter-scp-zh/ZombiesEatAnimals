package com.vomiter.zombieseatanimals.data;

import com.vomiter.zombieseatanimals.Helpers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public class ZEATags {
    public static TagKey<Item> ZOMBIE_FOOD = TagKey.create(BuiltInRegistries.ITEM.key(), Helpers.id("zombie_food"));
    public static TagKey<EntityType<?>> ZOMBIE_TARGET_ANIMAL = TagKey.create(BuiltInRegistries.ENTITY_TYPE.key(), Helpers.id("zombie_target_animal"));
    public static TagKey<EntityType<?>> NOT_ZOMBIE_TARGET_ANIMAL = TagKey.create(BuiltInRegistries.ENTITY_TYPE.key(), Helpers.id("not_zombie_target_animal"));
}
