package com.vomiter.zombieseatanimals.event;

import com.vomiter.zombieseatanimals.entity.ZombieFoodDropHelper;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

public final class ReloadHooks {
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener((barrier, rm, prep, reload, bg, game) ->
                java.util.concurrent.CompletableFuture.runAsync(ZombieFoodDropHelper::clear, game)
                        .thenCompose(barrier::wait)
        );
    }
}