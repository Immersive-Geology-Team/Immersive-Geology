package com.igteam.immersive_geology.core.proxy;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.client.IGClientRenderHandler;
import com.igteam.immersive_geology.client.menu.CreativeMenuHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class ClientProxy extends CommonProxy {

    @Override
    public void onCommonSetup(FMLCommonSetupEvent event) {
        super.onCommonSetup(event);
    }

    @Override
    public void onFinishSetup(FMLLoadCompleteEvent event) {

    }

    @Override
    public void onClientSetup(FMLClientSetupEvent event) {
        IGClientRenderHandler.register();
        IGClientRenderHandler.init(event);
        MinecraftForge.EVENT_BUS.register(new CreativeMenuHandler());
    }
}
