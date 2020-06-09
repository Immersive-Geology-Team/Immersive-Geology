package com.igteam.immersivegeology.common.world.layer.layers;

import com.igteam.immersivegeology.common.world.layer.IGLayerUtil;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

public class IslandLayer implements IAreaTransformer0
{
    private final int islandFrequency;

    public IslandLayer(int islandFrequency)
    {
        this.islandFrequency = islandFrequency;
    }

    @Override
    public int apply(INoiseRandom random, int x, int z)
    {
        if (x == 0 && z == 0)
        {
            return IGLayerUtil.PLAINS;
        }
        else
        {
            return random.random(islandFrequency) == 0 ? IGLayerUtil.PLAINS : IGLayerUtil.DEEP_OCEAN;
        }
    }
}