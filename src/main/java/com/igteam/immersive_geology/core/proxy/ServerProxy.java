package com.igteam.immersive_geology.core.proxy;

import com.igteam.immersive_geology.common.fluid.IGFluid;
import com.igteam.immersive_geology.common.world.IGWorldGeneration;
import com.igteam.immersive_geology.core.data.generators.helpers.LootIG;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;

public class ServerProxy implements Proxy {

    @Override
    public void onSetup(FMLCommonSetupEvent event) {
        IGWorldGeneration.initialize();
        MinecraftForge.EVENT_BUS.register(new IGWorldGeneration());
        DataSerializers.registerSerializer(IGFluid.OPTIONAL_FLUID_STACK);
    }

    @Override
    public void onFinishSetup(FMLLoadCompleteEvent event) {

    }

    @Override
    public void onServerStarting(FMLServerStartingEvent event) {

    }

    @Override
    public void onServerStopped(FMLServerStoppedEvent event) {

    }
}
