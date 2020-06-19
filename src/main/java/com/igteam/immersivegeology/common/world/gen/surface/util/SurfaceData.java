package com.igteam.immersivegeology.common.world.gen.surface.util;

import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.biome.biomes.BadlandsBiome;
import com.igteam.immersivegeology.common.world.gen.surface.IGSurfaceBlock;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class SurfaceData {

	private final IChunk chunkData;
	
	public SurfaceData(IChunk chunk) {
		chunkData = chunk;
	}
	
	public BlockState getGrassBlock(int x, int z) {
		//TODO Test on Servers
		BlockPos pos = new BlockPos(x, chunkData.getHeight(), z);
		Biome biome = chunkData.getBiome(pos);
		if(biome instanceof IGBiome) {
			IGBiome igBiome = (IGBiome) biome;
			float currentTemp = igBiome.getBtemp();
			float currentRainfall = igBiome.getBrain();
			
			return IGSurfaceBlock.getGrassState(igBiome, currentTemp, currentRainfall);
		
		} else {
			return Blocks.GRASS_BLOCK.getDefaultState();
		}
	}
	
	public BlockState getDirtBlock(int x, int z) {
		//TODO Test on Servers
		BlockPos pos = new BlockPos(x, chunkData.getHeight(), z);
		Biome biome = chunkData.getBiome(pos);
		if(biome instanceof IGBiome) {
			IGBiome igBiome = (IGBiome) biome;
			float currentTemp = igBiome.getBtemp();
			float currentRainfall = igBiome.getBrain();
			return IGSurfaceBlock.getDirtState(igBiome, currentTemp, currentRainfall);
		} else {
			return Blocks.DIRT.getDefaultState();
		}
	}
	
} 