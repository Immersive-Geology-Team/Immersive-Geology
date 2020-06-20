package com.igteam.immersivegeology.common.world.gen.surface;

import java.util.Random;

import com.igteam.immersivegeology.common.world.gen.config.ImmersiveSurfaceBuilderConfig;

import net.minecraft.world.chunk.IChunk;

public interface ISurfaceBuilder {

	//sand is a 'grass' type (more top layer deco)
	ISurfacePart GRASS = ISurfacePart.grass();
	//sand is also a 'dirt' type
	ISurfacePart DIRT = ISurfacePart.dirt();
	//sandstone is included in rock
	ISurfacePart ROCK = ISurfacePart.rock();

	ISurfacePart GRAVEL = ISurfacePart.gravel();

	ISurfacePart CLAY = ISurfacePart.clay();
	
	
	ImmersiveSurfaceBuilderConfig GRASS_DIRT_GRAVEL_ROCK = new ImmersiveSurfaceBuilderConfig(GRASS, DIRT, GRAVEL, ROCK);
	ImmersiveSurfaceBuilderConfig ROCK_ROCK_ROCK = new ImmersiveSurfaceBuilderConfig(ROCK, ROCK, ROCK);
	
	ISurfaceBuilder DEFAULT = new DefaultSurfaceBuilder(GRASS_DIRT_GRAVEL_ROCK, 4);

	ISurfaceBuilder OCEAN = new DefaultSurfaceBuilder(ROCK_ROCK_ROCK, 2);
	
	void buildSurface(Random random, IChunk chunkIn, int x, int z, int startHeight, float temperature, float rainfall, float noise);
}
