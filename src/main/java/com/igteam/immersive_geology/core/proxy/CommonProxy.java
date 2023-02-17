package com.igteam.immersive_geology.core.proxy;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.common.configuration.CommonConfiguration;
import com.igteam.immersive_geology.core.material.helper.MaterialInterface;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class CommonProxy implements Proxy {
    protected final Logger logger = ImmersiveGeology.getNewLogger();

    @Override
    public void onCommonSetup(FMLCommonSetupEvent event) {
        logger.log(Level.INFO, "Initializing Items and Blocks");
        logger.log(Level.INFO, "---Initial Setup Completed---");
    }

    @Override
    public void onFinishSetup(FMLLoadCompleteEvent event) {

    }



}
