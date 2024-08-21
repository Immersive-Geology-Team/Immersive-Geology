package com.igteam.immersivegeology.core.registration;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import com.igteam.immersivegeology.common.blocks.multiblocks.IGCrystalizerMultiblock;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;

public class IGContent {

    public static void modContruction(IEventBus event){
        IGLib.IG_LOGGER.info("Registering Multiblocks to Immersive Engineering");
        IGMultiblockProvider.forceClassLoad();
        IGRegistrationHolder.initialize();
    }

    public static void initialize(ParallelDispatchEvent event){

    }
}
