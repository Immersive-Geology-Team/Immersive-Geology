package com.igteam.immersivegeology.common.world.gen.surface;

import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.biome.biomes.OasisBiome;
import com.igteam.immersivegeology.common.world.gen.surface.util.SurfaceBlockType;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;

public interface IIGSurfaceBlock {


	
	
	public static BlockState getGrassState(float chunkTemp, float chunkRain){
		return getGrassState(null, chunkTemp, chunkRain);
	}
	
	public static BlockState getGrassState(Biome biome, float chunkTemp, float chunkRain){
		//TODO Re-Factor to allow the following use cases!
		//use biome to change block type
		//use biome temp and rainfall as a bases for the block type
		//use chunk temp and chunk rain block state info or block color info for biome blending perhaps?
		
		if(biome instanceof IGBiome) {
			IGBiome igBiome = (IGBiome) biome;
			return igBiome.returnBlockType(SurfaceBlockType.grass, chunkTemp, chunkRain);
		} else {
			return Blocks.WHITE_WOOL.getDefaultState();
		}
	}
	
	public static BlockState getDirtState(Biome biome, float temp, float rain){
		if(temp > 0.9) {
			return (biome instanceof OasisBiome) ? Blocks.RED_SAND.getDefaultState() : Blocks.SAND.getDefaultState();
		} else if(temp < 0.9 && temp > 0.7) {
			if(rain > 0.8) {
				return Blocks.DIRT.getDefaultState();
			} else {
				return Blocks.BROWN_TERRACOTTA.getDefaultState();
			}
		} else {
			return Blocks.DIRT.getDefaultState();
		}
	}
}