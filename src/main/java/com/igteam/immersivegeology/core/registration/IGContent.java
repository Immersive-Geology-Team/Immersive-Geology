package com.igteam.immersivegeology.core.registration;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import com.igteam.immersivegeology.common.blocks.multiblocks.IGCrystalizerMultiblock;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.ParallelDispatchEvent;

public class IGContent {

    public static void modContruction(IEventBus modEventBus){
        IGMultiblockProvider.forceClassLoad();
    }

    public static void initialize(ParallelDispatchEvent event){
        IGLib.IG_LOGGER.info("Registering Multiblocks to Immersive Engineering");
        MultiblockHandler.registerMultiblock(IGCrystalizerMultiblock.INSTANCE);
    }
}
