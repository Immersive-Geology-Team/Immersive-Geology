package com.igteam.immersive_geology;

import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.proxy.ClientProxy;
import com.igteam.immersive_geology.core.proxy.Proxy;
import com.igteam.immersive_geology.core.proxy.ServerProxy;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.SlurryEnum;
import igteam.immersive_geology.materials.StoneEnum;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.tags.IGTags;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;

import java.util.Arrays;

@Mod(IGLib.MODID)
public class ImmersiveGeology {

    private final Logger logger = getNewLogger();
    private static Proxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public ImmersiveGeology()
    {
        logger.log(Level.INFO, "Starting Immersive Geology");

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        IGRegistrationHolder.initialize();
        IGTags.initialize();
        IGRegistrationHolder.buildRecipes();

        modBus.addListener(this::setup);
        modBus.addListener(this::onClientSetup);
        modBus.addListener(this::onFinishSetup);

        modBus.register(IGRegistrationHolder.class);
    }

    public void setup(final FMLCommonSetupEvent event) {
        logger.log(Level.INFO, "Initial Setup of Immersive Geology");
        proxy.onSetup(event);
    }

    private void onFinishSetup(final FMLLoadCompleteEvent event){
        logger.log(Level.INFO, "Finishing Immersive Geology Setup");
        proxy.onFinishSetup(event);
    }

    private void onClientSetup(final FMLClientSetupEvent event){
        logger.log(Level.INFO,"Setting up Client Stuff");
        proxy.onClientSetup(event);
    }

    public static Logger getNewLogger() {
        return LogManager.getLogger(StackLocatorUtil.getCallerClass(2));
    }
}
