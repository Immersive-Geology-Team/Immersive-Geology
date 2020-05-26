package com.igteam.immersivegeology.common.world.gen.surface;

import java.util.Random;

import com.igteam.immersivegeology.common.world.gen.config.ImmersiveSurfaceBuilderConfig;
import com.igteam.immersivegeology.common.world.gen.surface.util.RockData;

import net.minecraft.world.chunk.IChunk;

public interface ISurfaceBuilder {
	
	ISurfacePart ROCK = ISurfacePart.rock();
	
	ImmersiveSurfaceBuilderConfig SAND_SAND_GRAVEL = new ImmersiveSurfaceBuilderConfig(ROCK, ROCK, ROCK);
	
	ISurfaceBuilder DEFAULT = new CompositeSurfaceBuilder(75f, 125f, new DefaultSurfaceBuilder(SAND_SAND_GRAVEL, 4), new DefaultSurfaceBuilder(SAND_SAND_GRAVEL, 4), new DefaultSurfaceBuilder(SAND_SAND_GRAVEL, 4), true);
	
	void buildSurface(Random random, IChunk chunkIn, RockData data, int x, int z, int startHeight, float temperature, float rainfall, float noise);
}
