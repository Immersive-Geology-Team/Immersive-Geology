package com.igteam.immersivegeology.common.world.layer.layers;

import static com.igteam.immersivegeology.common.world.layer.IGLayerUtil.RIVER;

import com.igteam.immersivegeology.common.world.layer.IGLayerUtil;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset0Transformer;

public enum MixRiverLayer implements IAreaTransformer2, IDimOffset0Transformer
{
    INSTANCE;

    public int apply(INoiseRandom context, IArea mainArea, IArea riverArea, int x, int z)
    {
        int mainValue = mainArea.getValue(func_215721_a(x), func_215722_b(z));
        int riverValue = riverArea.getValue(func_215721_a(x), func_215722_b(z));
        if (IGLayerUtil.isOcean(mainValue) || mainValue == IGLayerUtil.FLOODED_MOUNTAINS || mainValue == IGLayerUtil.LAKE)
        {
            return mainValue;
        }
        else if (riverValue == RIVER)
        {
            return riverValue;
        }
        else
        {
            return mainValue;
        }
    }
}