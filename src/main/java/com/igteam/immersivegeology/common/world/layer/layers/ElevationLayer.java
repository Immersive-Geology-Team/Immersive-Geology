package com.igteam.immersivegeology.common.world.layer.layers;

import com.igteam.immersivegeology.common.world.layer.IGLayerUtil;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC1Transformer;

import static com.igteam.immersivegeology.common.world.layer.layers.TemperateLayer.*;

import java.util.List;

import com.igteam.immersivegeology.api.util.IGMathHelper;

public enum ElevationLayer implements IC1Transformer
{
	INSTANCE;

	@Override
	public int apply(INoiseRandom context, int value)
	{
		if(!IGLayerUtil.isOcean(value))
		{
			int rand = context.random(4);
			if(rand==0)
			{
				List<Integer> returnID = IGMathHelper.intersection(TEMPERATE, low);
				if(isBiomeWarm(value)) {
					returnID = IGMathHelper.intersection(WARM, low);
				}
				if(isBiomeCold(value)) {
					returnID = IGMathHelper.intersection(COLD, low);
				}
				if(isBiomeHot(value)) {
					returnID = IGMathHelper.intersection(HOT, low);
				}
				if(isBiomeFrozen(value)) {
					returnID = IGMathHelper.intersection(FROZEN, low);
				}
				return returnID.get(context.random(returnID.size()));
			}
			else if(rand==1)
			{
				List<Integer> returnID = IGMathHelper.intersection(TEMPERATE, mid);
				if(isBiomeWarm(value)) {
					returnID = IGMathHelper.intersection(WARM, mid);
				}
				if(isBiomeCold(value)) {
					returnID = IGMathHelper.intersection(COLD, mid);
				}
				if(isBiomeHot(value)) {
					returnID = IGMathHelper.intersection(HOT, mid);
				}
				if(isBiomeFrozen(value)) {
					returnID = IGMathHelper.intersection(FROZEN, mid);
				}
				return returnID.get(context.random(returnID.size()));
			}
			else if(rand==2)
			{
				List<Integer> returnID = IGMathHelper.intersection(TEMPERATE, high);
				if(isBiomeWarm(value)) {
					returnID = IGMathHelper.intersection(WARM, high);
				}
				if(isBiomeCold(value)) {
					returnID = IGMathHelper.intersection(COLD, high);
				}
				if(isBiomeHot(value)) {
					returnID = IGMathHelper.intersection(HOT, high);
				}
				if(isBiomeFrozen(value)) {
					returnID = IGMathHelper.intersection(FROZEN, high);
				}
				return returnID.get(context.random(returnID.size()));
			}
		}
		return value;
	}
}
