package com.igteam.immersivegeology.core.proxy;

import com.igteam.immersivegeology.core.lib.IGLib;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void onCommonSetup(FMLCommonSetupEvent event) {
        super.onCommonSetup(event);
    }

    @Override
    public void onClientSetup(FMLClientSetupEvent event) {
        IGLib.IG_LOGGER.info("Starting Client Setup");
        // TODO Implement Render Handler and Creative Menu Handlers

    }
}
