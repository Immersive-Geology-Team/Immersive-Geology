package com.igteam.immersive_geology;

import com.igteam.immersive_geology.common.configuration.CommonConfiguration;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.material.MetalEnum;
import com.igteam.immersive_geology.core.material.helper.MaterialInterface;
import com.igteam.immersive_geology.core.proxy.ClientProxy;
import com.igteam.immersive_geology.core.proxy.Proxy;
import com.igteam.immersive_geology.core.proxy.ServerProxy;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Mod(IGLib.MODID)
public class ImmersiveGeology {

    private final Logger logger = getNewLogger();
    private static final Proxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public ImmersiveGeology()
    {
        logger.log(Level.INFO, "Starting Immersive Geology");

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        logger.info("Initializing Configs");
        initializeConfiguration();

        logger.info("Registering Items and Blocks");
        IGRegistrationHolder.initialize();
        IGRegistrationHolder.getDeferredItems().register(modBus);
        IGRegistrationHolder.getDeferredBlocks().register(modBus);

        modBus.addListener(this::setup);
        modBus.addListener(this::onClientSetup);
        modBus.addListener(this::onFinishSetup);
    }

    public void setup(final FMLCommonSetupEvent event)
    {
        logger.log(Level.INFO, "Common Setup of Immersive Geology");
        proxy.onCommonSetup(event);
    }

    private void onFinishSetup(final FMLLoadCompleteEvent event)
    {
        logger.log(Level.INFO, "Finishing Immersive Geology Setup");
        proxy.onFinishSetup(event);
    }
    private void onClientSetup(final FMLClientSetupEvent event)
    {
        logger.log(Level.INFO," ");
        proxy.onClientSetup(event);
    }

    private void initializeConfiguration() {
        CommonConfiguration.initialize();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CommonConfiguration.SPEC, "immersive_geology-client.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfiguration.SPEC, "immersive_geology-common.toml");
        for (MaterialInterface<?> material : ImmersiveGeology.getGeologyMaterials()) {
            material.instance().initializeFlags(); // Used to Grab the Item and Block Flags from the Config File
        }
    }


    public static Logger getNewLogger()
    {
        return LogManager.getLogger("Immersive Geology");
    }
    public static List<MaterialInterface<?>> getGeologyMaterials(){
        return List.of(MetalEnum.values());
    }
}
