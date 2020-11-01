package com.igteam.immersivegeology.common.world.layer.layers.overworld;

import com.google.common.base.Predicate;
import com.igteam.immersivegeology.common.world.layer.IGLayerUtil;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset0Transformer;

import java.util.function.IntPredicate;

import static com.igteam.immersivegeology.common.world.layer.IGLayerUtil.SHORE;

public enum MixBeachLayer implements IAreaTransformer2, IDimOffset0Transformer
{
	INSTANCE;

	public int apply(INoiseRandom context, IArea mainArea, IArea beachArea, int x, int z)
	{
		int mainValue = mainArea.getValue(func_215721_a(x), func_215722_b(z));
		int beachValue = beachArea.getValue(func_215721_a(x), func_215722_b(z));

		if(beachValue==SHORE)
		{
			return beachValue;
		}
		else
		{
			return mainValue;
		}
	}
}