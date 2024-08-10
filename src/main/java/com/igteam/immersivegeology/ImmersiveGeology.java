package com.igteam.immersivegeology;


import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.proxy.ClientProxy;
import com.igteam.immersivegeology.core.proxy.CommonProxy;
import com.igteam.immersivegeology.core.registration.IGContent;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import com.igteam.immersivegeology.core.registration.IGMultiblocks;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;

@Mod(IGLib.MODID)
public class ImmersiveGeology {

    public static final CommonProxy proxy = makeProxy();
    private static CommonProxy makeProxy(){
        return FMLLoader.getDist() == Dist.CLIENT ? new ClientProxy() : new CommonProxy();
    }

    public ImmersiveGeology(IEventBus modEventBus){
        IGLib.IG_LOGGER.info("Immersive Geology Starting");
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::clientSetup);

        IGRegistrationHolder.addRegistersToEventBus(modEventBus);

        IGContent.modContruction(modEventBus);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        //ItemBlockRenderTypes.setRenderLayer(IGMultiblockProvider.CRYSTALLIZER.block().get(), RenderType.cutoutMipped());
    }

    public void setup(final FMLCommonSetupEvent event){
        proxy.setup();
        proxy.preInit();

        // Pre init for IG Content?

        proxy.preInitEnd();
        IGContent.initialize(event);

        proxy.init();

        proxy.postInit();
    }

}
