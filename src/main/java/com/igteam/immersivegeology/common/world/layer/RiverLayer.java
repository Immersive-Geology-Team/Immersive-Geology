package com.igteam.immersivegeology.common.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

import static com.igteam.immersivegeology.common.world.util.IGLayerUtil.RIVER;
import static com.igteam.immersivegeology.common.world.util.IGLayerUtil.OCEAN;

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