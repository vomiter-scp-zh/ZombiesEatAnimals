package com.vomiter.zombieseatanimals.data;

import com.vomiter.zombieseatanimals.ZombiesEatAnimals;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public class DataGenerator {
    public static void gatherData(GatherDataEvent event){
        net.minecraft.data.DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        var emptyBlockTags = new BlockTagsProvider(output, lookupProvider, ZombiesEatAnimals.MOD_ID, existingFileHelper) {
            @Override
            protected void addTags(HolderLookup.Provider p_256380_) {}
        };
        generator.addProvider(event.includeServer(), emptyBlockTags);
        generator.addProvider(
                event.includeServer(),
                new ModItemTagsProvider(
                        output,
                        lookupProvider,
                        emptyBlockTags.contentsGetter(),
                        ZombiesEatAnimals.MOD_ID,
                        existingFileHelper
                        )
        );
    }
}
