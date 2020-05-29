package com.igteam.immersivegeology.common.world.gen.surface;

import java.util.Random;

import com.igteam.immersivegeology.common.world.gen.config.ImmersiveSurfaceBuilderConfig;
import com.igteam.immersivegeology.common.world.gen.surface.util.RockData;

import net.minecraft.world.chunk.IChunk;

public interface ISurfaceBuilder {

	ISurfacePart GRASS = ISurfacePart.grass();
	ISurfacePart DIRT = ISurfacePart.dirt();
	ISurfacePart SAND = ISurfacePart.sand();
	ISurfacePart GRAVEL = ISurfacePart.gravel();
	ISurfacePart CLAY = ISurfacePart.clay();
	
	ImmersiveSurfaceBuilderConfig GRASS_DIRT_GRAVEL_CLAY = new ImmersiveSurfaceBuilderConfig(GRASS, DIRT, GRAVEL, CLAY);
	ImmersiveSurfaceBuilderConfig SAND_SAND_GRAVEL = new ImmersiveSurfaceBuilderConfig(SAND, SAND, GRAVEL);
	
	ISurfaceBuilder DEFAULT = new CompositeSurfaceBuilder(75f, 125f, new DefaultSurfaceBuilder(GRASS_DIRT_GRAVEL_CLAY, 3), new DefaultSurfaceBuilder(SAND_SAND_GRAVEL, 3), new DefaultSurfaceBuilder(SAND_SAND_GRAVEL, 3), true);
	
	void buildSurface(Random random, IChunk chunkIn, int x, int z, int startHeight, float temperature, float rainfall, float noise);
}
