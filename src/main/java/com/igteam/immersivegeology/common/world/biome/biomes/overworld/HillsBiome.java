package com.igteam.immersivegeology.common.world.biome.biomes.overworld;

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

public class HillsBiome extends IGBiome
{
	private final float height;

	public HillsBiome(float height)
	{
		super(new Builder().category(Category.PLAINS).precipitation(RainType.RAIN).downfall(0.6f).temperature(0.5f), 0.5f, .6f);
		this.height = height;

		IGDefaultBiomeFeatures.addCarvers(this);
		IGDefaultBiomeFeatures.addTrees(this);

		DefaultBiomeFeatures.addStructures(this);
		DefaultBiomeFeatures.addTaigaRocks(this);
		DefaultBiomeFeatures.addBerryBushes(this);
	}

	@Nonnull
	@Override
	public INoise2D createNoiseLayer(long seed)
	{
		return new SimplexNoise2D(seed).octaves(4).spread(0.06f).scaled(SEA_LEVEL-5, SEA_LEVEL+height);
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
