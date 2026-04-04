package com.vomiter.zombieseatanimals.event;

import com.vomiter.zombieseatanimals.ZombiesEatAnimals;
import com.vomiter.zombieseatanimals.entity.ZombieFoodDropHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.Unit;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

import java.util.concurrent.CompletableFuture;

public final class ReloadHooks {
    public static void onAddReloadListener(AddServerReloadListenersEvent event) {
        event.addListener(
                Identifier.fromNamespaceAndPath(ZombiesEatAnimals.MOD_ID, "zombie_food_cache_clear"),
                (PreparableReloadListener) (sharedState, taskExecutor, barrier, reloadExecutor) ->
                        CompletableFuture
                                .runAsync(ZombieFoodDropHelper::clear, reloadExecutor)
                                .thenCompose(ignored -> barrier.wait(Unit.INSTANCE))
                                .thenApply(ignored -> null)
        );
    }
}