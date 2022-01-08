package com.igteam.immersive_geology.core.proxy;

import com.igteam.immersive_geology.client.IGColorHandlers;
import igteam.immersive_geology.menu.CreativeMenuHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

public class ClientProxy implements Proxy {
    @Override
    public void onCommonSetup(FMLCommonSetupEvent event) {

    }

    @Override
    public void onFinishSetup(FMLLoadCompleteEvent event) {

    }

    @Override
    public void onClientSetup(FMLClientSetupEvent event) {
        IGColorHandlers.register();
        MinecraftForge.EVENT_BUS.register(new CreativeMenuHandler());
    }
}
