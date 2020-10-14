package com.igteam.immersivegeology.common.world.gen.surface;

import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.gen.surface.util.SurfaceBlockType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;

public interface IIGSurfaceBlock
{


	static BlockState getGrassState(float chunkTemp, float chunkRain)
	{
		return getGrassState(null, chunkTemp, chunkRain);
	}

	static BlockState getGrassState(Biome biome, float chunkTemp, float chunkRain)
	{
		//TODO Re-Factor to allow the following use cases!
		//use biome to change block type
		//use biome temp and rainfall as a bases for the block type
		//use chunk temp and chunk rain block state info or block color info for biome blending perhaps?

		if(biome instanceof IGBiome)
		{
			IGBiome igBiome = (IGBiome)biome;
			return igBiome.returnBlockType(SurfaceBlockType.grass, chunkTemp, chunkRain);
		}
		else
		{
			return Blocks.WHITE_WOOL.getDefaultState();
		}
	}

	static BlockState getDirtState(Biome biome, float chunkTemp, float chunkRain)
	{
		if(biome instanceof IGBiome)
		{
			IGBiome igBiome = (IGBiome)biome;
			return igBiome.returnBlockType(SurfaceBlockType.dirt, chunkTemp, chunkRain);
		}
		else
		{
			return Blocks.WHITE_WOOL.getDefaultState();
		}
	}

	static BlockState getPeakState(Biome biome, float chunkTemp, float chunkRain)
	{
		if(biome instanceof IGBiome)
		{
			IGBiome igBiome = (IGBiome)biome;
			return igBiome.returnBlockType(SurfaceBlockType.peak, chunkTemp, chunkRain);
		}
		else
		{
			return Blocks.WHITE_WOOL.getDefaultState();
		}
	}
}