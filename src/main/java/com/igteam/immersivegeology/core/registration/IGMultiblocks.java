package com.igteam.immersivegeology.core.registration;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import com.igteam.immersivegeology.common.blocks.multiblocks.IGCrystalizerMultiblock;
import com.igteam.immersivegeology.common.blocks.multiblocks.IGTemplateMultiblock;


public class IGMultiblocks {
    public static IGTemplateMultiblock CRYSTALLIZER;

    public static void initialize(){
        CRYSTALLIZER = register(new IGCrystalizerMultiblock());
    }

    private static <T extends MultiblockHandler.IMultiblock>
    T register(T multiblock) {
        MultiblockHandler.registerMultiblock(multiblock);
        return multiblock;
    }
}
