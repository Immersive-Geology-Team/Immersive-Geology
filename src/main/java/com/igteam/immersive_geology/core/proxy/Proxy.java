package com.igteam.immersive_geology.core.proxy;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

public interface Proxy {
    void onCommonSetup(final FMLCommonSetupEvent event);
    default void onFinishSetup(final FMLLoadCompleteEvent event){};
    default void onClientSetup(final FMLClientSetupEvent event){};
}
