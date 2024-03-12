package com.igteam.immersivegeology.core.proxy;

import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;

public interface Proxy {
    void onCommonSetup(final FMLCommonSetupEvent event);
    default void onFinishSetup(final FMLLoadCompleteEvent event){};
    default void onClientSetup(final FMLClientSetupEvent event){};
}
