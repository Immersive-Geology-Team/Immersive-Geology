package com.igteam.immersivegeology.common.world.gen.surface;

import com.igteam.immersivegeology.common.world.biome.biomes.OasisBiome;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;

public interface IIGSurfaceBlock {

	
	public static BlockState getGrassState(float temp, float rain){
		return getGrassState(null, temp, rain);
	}
	
	public static BlockState getGrassState(Biome biome, float temp, float rain){
		if(temp > 0.9) {
			return (biome instanceof OasisBiome) ? Blocks.RED_SAND.getDefaultState() : Blocks.SAND.getDefaultState();
		} else if(temp < 0.9 && temp > 0.7) {
			if(rain > 0.8) {
				return Blocks.PODZOL.getDefaultState(); //flooded mountains are usually found here
			} else {
				return Blocks.TERRACOTTA.getDefaultState();
			}
		} else {
			return Blocks.GRASS_BLOCK.getDefaultState();
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
