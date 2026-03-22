package com.vomiter.zombieseatanimals;

import com.mojang.logging.LogUtils;
import com.vomiter.zombieseatanimals.data.DataGenerator;
import com.vomiter.zombieseatanimals.event.HorseDeathEventHandler;
import com.vomiter.zombieseatanimals.event.ReloadHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(ZombiesEatAnimals.MOD_ID)
public class ZombiesEatAnimals
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "zombieseatanimals";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ZombiesEatAnimals(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();
        modBus.addListener(this::commonSetup);
        modBus.addListener(DataGenerator::gatherData);
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        if(FMLEnvironment.dist.isClient()){
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            final IEventBus bus = MinecraftForge.EVENT_BUS;
            bus.addListener(ReloadHooks::onAddReloadListener);
            bus.addListener(HorseDeathEventHandler::onLivingDeath);
        });
    }
}
