package com.igteam.immersivegeology;


import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.proxy.ClientProxy;
import com.igteam.immersivegeology.core.proxy.CommonProxy;
import com.igteam.immersivegeology.core.proxy.Proxy;
import com.igteam.immersivegeology.core.registration.IGContent;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(IGLib.MODID)
public class ImmersiveGeology {

    private static final Proxy proxy = FMLEnvironment.dist.isClient() ? new ClientProxy() : new CommonProxy();

    public ImmersiveGeology(IEventBus modEventBus){
        IGLib.IG_LOGGER.info("Immersive Geology Starting");
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::clientSetup);

        IGContent.modContruction(modEventBus);

        IGRegistrationHolder.addRegistersToEventBus(modEventBus);
    }

    public void setup(final FMLCommonSetupEvent event){
        proxy.onCommonSetup(event);
    }

    public void clientSetup(final FMLClientSetupEvent event){
        proxy.onClientSetup(event);
    }

}
