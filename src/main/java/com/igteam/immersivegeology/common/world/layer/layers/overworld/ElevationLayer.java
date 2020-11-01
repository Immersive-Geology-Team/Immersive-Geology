package com.igteam.immersivegeology.common.world.layer.layers.overworld;

import static com.igteam.immersivegeology.common.world.layer.layers.overworld.TemperateLayer.COLD;
import static com.igteam.immersivegeology.common.world.layer.layers.overworld.TemperateLayer.FROZEN;
import static com.igteam.immersivegeology.common.world.layer.layers.overworld.TemperateLayer.HOT;
import static com.igteam.immersivegeology.common.world.layer.layers.overworld.TemperateLayer.TEMPERATE;
import static com.igteam.immersivegeology.common.world.layer.layers.overworld.TemperateLayer.WARM;

import com.igteam.immersivegeology.common.world.layer.IGLayerUtil;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;
import net.minecraft.world.gen.layer.traits.IDimOffset0Transformer;

public enum ElevationLayer implements IAreaTransformer1, IDimOffset0Transformer
{
	INSTANCE;
	
	@Override
	public int func_215728_a(IExtendedNoiseRandom<?> context, IArea mainArea, int x, int z) {
		int value = mainArea.getValue(func_215721_a(x), func_215722_b(z));
		if(!IGLayerUtil.isOcean(value))
		{
			int temp = TEMPERATE.get(context.random(TEMPERATE.size()));
			
			int ht   = HOT.get(context.random(HOT.size()));
			int cld  = COLD.get(context.random(COLD.size()));
			int fzn  = FROZEN.get(context.random(FROZEN.size()));
			int wrm  = WARM.get(context.random(WARM.size()));
			
			int TEMPERATURE_BIOME = temp;
			int zmod = (z >= 0) ? z % 6 : z % -6;
			
			if(zmod == 2) {
				TEMPERATURE_BIOME = wrm;
			}
			
			if(zmod == -2) {
				TEMPERATURE_BIOME = cld;
			}
			
			if(zmod == 3) {
				TEMPERATURE_BIOME = ht;
			}
			
			if(zmod == -3) {
				TEMPERATURE_BIOME = fzn;
			}
			
			if(z == 0 || z == 1 || z == -1) {
				TEMPERATURE_BIOME = (context.random(1) == 0) ? temp : (context.random(1) == 0) ? cld : wrm;
			}
			
			return TEMPERATURE_BIOME;
		}
		return value;
	}
}
