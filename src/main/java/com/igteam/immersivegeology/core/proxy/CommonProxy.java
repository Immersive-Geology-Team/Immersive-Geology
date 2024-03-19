package com.igteam.immersivegeology.core.proxy;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGContent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonProxy implements Proxy {
    @Override
    public void onCommonSetup(FMLCommonSetupEvent event) {
        IGLib.IG_LOGGER.info("Starting Common Setup");

        IGContent.initialize(event);
    }
}
