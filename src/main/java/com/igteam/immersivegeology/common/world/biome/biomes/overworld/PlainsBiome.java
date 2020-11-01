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

public class PlainsBiome extends IGBiome
{
	private final float minHeight;
	private final float maxHeight;
	private final PlainsType plainType;
	
	public PlainsBiome(float minHeight, float maxHeight, PlainsType type)
	{
		super(new Builder().category(Category.PLAINS).precipitation(RainType.RAIN).downfall(0.4f).temperature(0.65f), 0.65f, 0.4f);
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;

		this.plainType = type;
		if(type != PlainsType.GLACIER) {
			IGDefaultBiomeFeatures.addCarvers(this);
			DefaultBiomeFeatures.addGrass(this);
			DefaultBiomeFeatures.addTaigaRocks(this);
		} else {
			DefaultBiomeFeatures.addIcebergs(this);
			DefaultBiomeFeatures.addBlueIce(this);
			DefaultBiomeFeatures.addFreezeTopLayer(this);
		}
		
		DefaultBiomeFeatures.addStructures(this);
	}

	@Nonnull
	@Override
	public INoise2D createNoiseLayer(long seed)
	{
		return new SimplexNoise2D(seed).octaves(6).spread(0.17f).scaled(SEA_LEVEL+minHeight, SEA_LEVEL+maxHeight);
	}

	@Override
	public BlockState returnBlockType(SurfaceBlockType part, float chunkTemp, float chunkRain)
	{
		switch(plainType) {
		case GLACIER:
			switch(part)
			{
				case grass:
					return Blocks.ICE.getDefaultState();
				default:
					return Blocks.PACKED_ICE.getDefaultState();
			}
		case DEFAULT:
		default:
			switch(part)
			{
				case grass:
					return Blocks.GRASS_BLOCK.getDefaultState();
				default:
					return Blocks.DIRT.getDefaultState();
			}
		}

	}
	
	public enum PlainsType{
		DEFAULT,
		GLACIER
	}
	
}