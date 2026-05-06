package com.vomiter.zombieseatanimals;

import com.mojang.logging.LogUtils;
import com.vomiter.zombieseatanimals.data.DataGenerator;
import com.vomiter.zombieseatanimals.event.HorseDeathEventHandler;
import com.vomiter.zombieseatanimals.event.ReloadHooks;
import com.vomiter.zombieseatanimals.event.ZombieDeathEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(ZombiesEatAnimals.MOD_ID)
public class ZombiesEatAnimals
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "zombieseatanimals";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ZombiesEatAnimals(ModContainer mod, IEventBus modBus) {
        modBus.addListener(this::commonSetup);
        modBus.addListener(DataGenerator::gatherData);
        mod.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        if(FMLEnvironment.dist.isClient()){
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            final IEventBus bus = NeoForge.EVENT_BUS;
            bus.addListener(ReloadHooks::onAddReloadListener);
            bus.addListener(HorseDeathEventHandler::onLivingDeath);
            bus.addListener(ZombieDeathEvent::onZombieDeath);
        });
    }
}
