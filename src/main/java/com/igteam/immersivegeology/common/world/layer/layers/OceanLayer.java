package com.igteam.immersivegeology.common.world.layer.layers;

import com.igteam.immersivegeology.common.world.layer.IGLayerUtil;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum OceanLayer implements ICastleTransformer
{
    INSTANCE;

    @Override
    public int apply(INoiseRandom context, int north, int west, int south, int east, int center)
    {
        if (IGLayerUtil.isOcean(center))
        {
            if (!IGLayerUtil.isOcean(north) || !IGLayerUtil.isOcean(west) || !IGLayerUtil.isOcean(south) || !IGLayerUtil.isOcean(east))
            {
                return IGLayerUtil.OCEAN;
            }
            else if (north == IGLayerUtil.DEEP_OCEAN_VOLCANIC || west == IGLayerUtil.DEEP_OCEAN_VOLCANIC || south == IGLayerUtil.DEEP_OCEAN_VOLCANIC || east == IGLayerUtil.DEEP_OCEAN_VOLCANIC)
            {
                if (context.random(3) == 0)
                {
                    return IGLayerUtil.DEEP_OCEAN_VOLCANIC;
                }
            }
        }
        return center;
    }
}
