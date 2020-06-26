package com.igteam.immersivegeology.common.world.gen.surface.util;

import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.chunk.data.ChunkDataProvider;
import com.igteam.immersivegeology.common.world.gen.surface.IIGSurfaceBlock;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome; 
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap.Type;

public class SurfaceData {

	private final IChunk chunkData;
	
	public SurfaceData(IChunk chunk) {
		chunkData = chunk;
	}
	
	public BlockState getGrassBlock(int x, int z, float ChunkTemp, float ChunkRain) {
		//Works on servers Yay
		BlockPos pos = new BlockPos(x,chunkData.getTopBlockY(Type.WORLD_SURFACE, x, z),z);
		
		Biome biome = chunkData.getBiome(pos);
		if(biome instanceof IGBiome) {
			IGBiome igBiome = (IGBiome) biome;
			float biomeTemp = igBiome.getTemperature(pos);
			float biomeRainfall = igBiome.getBrain();

			return IIGSurfaceBlock.getGrassState(igBiome, biomeTemp, biomeRainfall, ChunkTemp, ChunkRain);
		} else {
			return Blocks.WHITE_WOOL.getDefaultState(); 
		} 
	}
	
	public BlockState getDirtBlock(int x, int z) {
		//Works on servers Yay
		BlockPos pos = new BlockPos(x, chunkData.getHeight(), z);
		Biome biome = chunkData.getBiome(pos);
		if(biome instanceof IGBiome) {
			IGBiome igBiome = (IGBiome) biome;
			float currentTemp = igBiome.getTemperature(pos);
			float currentRainfall = igBiome.getBrain();
			return IIGSurfaceBlock.getDirtState(igBiome, currentTemp, currentRainfall);
		} else {
			return Blocks.DIRT.getDefaultState();
		}
	}
	
} 
