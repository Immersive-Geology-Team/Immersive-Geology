package com.igteam.immersive_geology.core.proxy;

import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;

public interface Proxy {

    void onSetup(final FMLCommonSetupEvent event);
    default void onFinishSetup(final FMLLoadCompleteEvent event){};
    default void onClientSetup(final FMLClientSetupEvent event){};
    default void onServerStarting(FMLServerStartingEvent event){};
    default void onServerStopped(FMLServerStoppedEvent event){};

    @SuppressWarnings({"unused", "RedundantSuppression"})
    default void onEnqueueModComs(final InterModEnqueueEvent event){};

    @SuppressWarnings({"unused", "RedundantSuppression"})
    default void onProcessModComs(final InterModProcessEvent event){};
}
