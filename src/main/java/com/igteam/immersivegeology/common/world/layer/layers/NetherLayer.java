package com.igteam.immersivegeology.common.world.layer.layers;

import com.igteam.immersivegeology.common.world.layer.IGLayerUtil;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public class NetherLayer implements IAreaTransformer0
{
	private final int islandFrequency;

	public NetherLayer(int islandFrequency)
	{
		this.islandFrequency = islandFrequency;
	}

	@Override
	public int apply(INoiseRandom random, int x, int z)
	{
		return IGLayerUtil.MANTLE;
	}
}
