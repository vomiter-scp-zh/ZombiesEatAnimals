package com.vomiter.zombieseatanimals.data;

import com.vomiter.zombieseatanimals.Helpers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {
    public static TagKey<Item> ZOMBIE_FOOD = TagKey.create(BuiltInRegistries.ITEM.key(), Helpers.id("zombie_food"));

    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId) {
        super(output, lookupProvider, modId);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider p_256380_) {
        tag(ZOMBIE_FOOD)
                .add(
                        Items.PORKCHOP,
                        Items.COOKED_PORKCHOP,
                        Items.BEEF,
                        Items.COOKED_BEEF,
                        Items.MUTTON,
                        Items.COOKED_MUTTON,
                        Items.CHICKEN,
                        Items.COOKED_CHICKEN,
                        Items.RABBIT,
                        Items.COOKED_RABBIT,
                        Items.ROTTEN_FLESH
                );
    }
}
