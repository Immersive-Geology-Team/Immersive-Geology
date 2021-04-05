package com.igteam.immersive_geology.core.proxy;

import com.igteam.immersive_geology.client.menu.helper.CreativeMenuHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientProxy extends ServerProxy {

    @Override
    public void onClientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new CreativeMenuHandler());
    }
}
