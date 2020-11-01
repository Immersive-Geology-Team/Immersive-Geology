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

public class DesertBiome extends IGBiome
{
	private final float minHeight;
	private final float maxHeight;

	private final boolean isCold;

	public DesertBiome(float minHeight, float maxHeight, boolean cold)
	{
		super(new Builder().category((cold) ? Category.ICY : Category.DESERT).precipitation((cold) ? RainType.SNOW : RainType.NONE).downfall(0f).temperature((cold) ? 0.05f : 0.95f), 0.95f, 0.1f);
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		this.isCold = cold;

		IGDefaultBiomeFeatures.addCarvers(this);
		DefaultBiomeFeatures.addStructures(this);

		if(!isCold)
		{
			DefaultBiomeFeatures.addDeadBushes(this);
			DefaultBiomeFeatures.addDesertFeatures(this);
		}
		else
		{
			DefaultBiomeFeatures.addLakes(this);
			DefaultBiomeFeatures.addIcebergs(this);
			DefaultBiomeFeatures.addFreezeTopLayer(this);			
		}
	}

	@Nonnull
	@Override
	public INoise2D createNoiseLayer(long seed)
	{
		return new SimplexNoise2D(seed).octaves(4).spread(0.06f).scaled(SEA_LEVEL+minHeight, SEA_LEVEL+maxHeight);
	}

	@Override
	public BlockState returnBlockType(SurfaceBlockType part, float chunkTemp, float chunkRain)
	{
		if(!isCold) {
			switch(part)
			{
				case grass:
					return Blocks.SAND.getDefaultState();
				case dirt:
					return Blocks.SANDSTONE.getDefaultState();
				default:
					return Blocks.SANDSTONE.getDefaultState();
			}
		} else {
			switch(part)
			{
				case grass:
					return Blocks.SNOW_BLOCK.getDefaultState();
				case dirt:
					return Blocks.PACKED_ICE.getDefaultState();
				default:
					return Blocks.ICE.getDefaultState();
			}
		}
	}
}