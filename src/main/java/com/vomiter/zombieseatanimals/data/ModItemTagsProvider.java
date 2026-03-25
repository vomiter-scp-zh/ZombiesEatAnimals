package com.vomiter.zombieseatanimals.data;

import com.vomiter.zombieseatanimals.Helpers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagLookup<Block>> p_275322_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, p_275322_, modId, existingFileHelper);
    }
    public static TagKey<Item> ZOMBIE_FOOD = TagKey.create(BuiltInRegistries.ITEM.key(), Helpers.id("zombie_food"));

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
