package com.igteam.immersivegeology.common.world.util;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC1Transformer;

public enum ElevationLayer implements IC1Transformer
{
    INSTANCE;

    @Override
    public int apply(INoiseRandom context, int value)
    {
        if (!IGLayerUtil.isOcean(value))
        {
            int rand = context.random(4);
            if (rand == 0)
            {
                return IGLayerUtil.PLAINS;
            }
            else if (rand == 1)
            {
                return IGLayerUtil.HILLS;
            }
            else if (rand == 2)
            {
                return IGLayerUtil.MOUNTAINS;
            }
        }
        return value;
    }
}
