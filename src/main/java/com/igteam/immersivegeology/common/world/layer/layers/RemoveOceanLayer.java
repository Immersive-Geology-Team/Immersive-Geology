package com.igteam.immersivegeology.common.world.layer.layers;

import com.igteam.immersivegeology.common.world.layer.IGLayerUtil;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum RemoveOceanLayer implements ICastleTransformer
{
    INSTANCE;

    public int apply(INoiseRandom context, int top, int right, int bottom, int left, int center)
    {
        if (IGLayerUtil.isOcean(top) && IGLayerUtil.isOcean(right) && IGLayerUtil.isOcean(bottom) && IGLayerUtil.isOcean(left) && context.random(32) == 0)
        {
            if (context.random(3) == 0)
            {
                return IGLayerUtil.PLATEAU;
            }
            return IGLayerUtil.PLAINS;
        }
        if (IGLayerUtil.isOcean(center))
        {
            int replacement = center, count = 0;
            if (!IGLayerUtil.isOcean(top))
            {
                replacement = top;
                count++;
            }
            if (!IGLayerUtil.isOcean(left))
            {
                replacement = left;
                count++;
            }
            if (!IGLayerUtil.isOcean(right))
            {
                replacement = right;
                count++;
            }
            if (!IGLayerUtil.isOcean(bottom))
            {
                replacement = bottom;
                count++;
            }
            if (count == 4 || (count == 3 && context.random(3) == 0))
            {
                return replacement;
            }
        }
        return center;
    }
}