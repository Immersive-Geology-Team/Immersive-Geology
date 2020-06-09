package com.igteam.immersivegeology.common.world.layer.layers;

import static com.igteam.immersivegeology.common.world.layer.IGLayerUtil.OCEAN;
import static com.igteam.immersivegeology.common.world.layer.IGLayerUtil.RIVER;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum RiverLayer implements ICastleTransformer
{
    INSTANCE;

    public int apply(INoiseRandom context, int north, int west, int south, int east, int center)
    {
        if (center != north || center != south || center != east || center != west)
        {
            return RIVER;
        }
        return OCEAN;
    }
}