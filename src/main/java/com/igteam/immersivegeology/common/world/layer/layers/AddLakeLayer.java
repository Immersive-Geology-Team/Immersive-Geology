package com.igteam.immersivegeology.common.world.layer.layers;

import com.igteam.immersivegeology.common.world.layer.IGLayerUtil;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum AddLakeLayer implements ICastleTransformer
{
    INSTANCE;

    @Override
    public int apply(INoiseRandom context, int north, int west, int south, int east, int center)
    {
        if (IGLayerUtil.isLakeCompatible(north) && IGLayerUtil.isLakeCompatible(west) && IGLayerUtil.isLakeCompatible(south) && IGLayerUtil.isLakeCompatible(east) && IGLayerUtil.isLakeCompatible(center))
        {
            if (context.random(15) == 0)
            {
                return IGLayerUtil.LAKE;
            }
        }
        return center;
    }
}