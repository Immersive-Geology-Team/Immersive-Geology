package com.igteam.immersivegeology.common.world.util;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

public enum RandomLayer implements IAreaTransformer0
{
    INSTANCE;

    @Override
    public int apply(INoiseRandom context, int x, int z)
    {
        return context.random(Integer.MAX_VALUE / 4);
    }
}
