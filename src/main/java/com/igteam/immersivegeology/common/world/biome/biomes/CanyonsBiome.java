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

public class CanyonsBiome extends IGBiome
{
	private final float minHeight;
	private final float maxHeight;

	public CanyonsBiome(float minHeight, float maxHeight)
	{
		super(new Builder().category(Category.EXTREME_HILLS).precipitation(RainType.RAIN).downfall(0.05f).temperature(0.75f), 0.75f, 0.05f);

		this.minHeight = minHeight;
		this.maxHeight = maxHeight;

		DefaultBiomeFeatures.addStructures(this);
		IGDefaultBiomeFeatures.addCarvers(this);
		DefaultBiomeFeatures.addMonsterRooms(this);
		DefaultBiomeFeatures.addMushrooms(this);

	}

	@Nonnull
	@Override
	public INoise2D createNoiseLayer(long seed)
	{
		final INoise2D warpX = new SimplexNoise2D(seed).octaves(4).spread(0.1f).scaled(-30, 30);
		final INoise2D warpZ = new SimplexNoise2D(seed+1).octaves(4).spread(0.1f).scaled(-30, 30);
		return new SimplexNoise2D(seed).octaves(4).spread(0.2f).warped(warpX, warpZ).map(x -> x > 0.4?x-0.8f: -x).scaled(-0.4f, 0.8f, SEA_LEVEL+minHeight, SEA_LEVEL+maxHeight).spread(0.3f);
	}

	@Override
	public BlockState returnBlockType(SurfaceBlockType part, float chunkTemp, float chunkRain)
	{
		switch(part)
		{
			case grass:
				return Blocks.TERRACOTTA.getDefaultState();
			case dirt:
				return Blocks.BROWN_TERRACOTTA.getDefaultState();
			default:
				return Blocks.TERRACOTTA.getDefaultState();
		}
	}
}
