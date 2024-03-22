package com.igteam.immersivegeology.core.proxy;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.core.registration.IGMultiblockHolder;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class CommonProxy implements Proxy {
    protected final Logger logger = ImmersiveGeology.getNewLogger();

    @Override
    public void onCommonSetup(FMLCommonSetupEvent event) {
        logger.log(Level.INFO, "Initializing Items and Blocks");
        IGMultiblockHolder.initialize();
        logger.log(Level.INFO, "---Initial Setup Completed---");
    }

    @Override
    public void onFinishSetup(FMLLoadCompleteEvent event) {

    }



}
