package com.vomiter.zombieseatanimals;

import net.minecraft.resources.ResourceLocation;

public class Helpers {
    public static ResourceLocation id(String namespace, String path){
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    public static ResourceLocation id(String path){
        return id(ZombiesEatAnimals.MOD_ID, path);
    }
    public static ResourceLocation minecraftId(String path){
        return id("minecraft", path);
    }

}
