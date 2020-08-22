package com.igteam.immersivegeology.common.world.biome.biomes;

import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.biome.IGDefaultBiomeFeatures;
import com.igteam.immersivegeology.common.world.gen.surface.util.SurfaceBlockType;
import com.igteam.immersivegeology.common.world.noise.INoise2D;
import com.igteam.immersivegeology.common.world.noise.SimplexNoise2D;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.DefaultBiomeFeatures;

import javax.annotation.Nonnull;

import static com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings.SEA_LEVEL;

public class LowlandsBiome extends IGBiome
{
	public LowlandsBiome()
	{
		super(new Builder().category(Category.PLAINS).precipitation(RainType.RAIN).downfall(0.65f).temperature(0.4f), .4f, .65f);
		IGDefaultBiomeFeatures.addCarvers(this);
		DefaultBiomeFeatures.addSwampVegetation(this);
	}

	@Nonnull
	@Override
	public INoise2D createNoiseLayer(long seed)
	{
		return new SimplexNoise2D(seed).octaves(6).spread(0.55f).scaled(SEA_LEVEL-6, SEA_LEVEL+7).flattened(SEA_LEVEL-4, SEA_LEVEL+3);
	}


	@Override
	public BlockState returnBlockType(SurfaceBlockType part, float chunkTemp, float chunkRain)
	{
		switch(part)
		{
			case grass:
				return Blocks.GRASS_BLOCK.getDefaultState();
			case dirt:
				return Blocks.DIRT.getDefaultState();
			default:
				return Blocks.DIRT.getDefaultState();
		}
	}
}