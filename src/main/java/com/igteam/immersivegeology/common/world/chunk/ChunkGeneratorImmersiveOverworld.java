package com.igteam.immersivegeology.common.world.chunk;

import net.minecraft.world.IWorld;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.OverworldChunkGenerator;
import net.minecraft.world.gen.OverworldGenSettings;

public class ChunkGeneratorImmersiveOverworld extends OverworldChunkGenerator {

	public ChunkGeneratorImmersiveOverworld(IWorld world, BiomeProvider provider,
			OverworldGenSettings settings) {
		super(world, provider, settings);
	}
	
	@Override
	public int getGroundHeight() {
	      return getSeaLevel() + 1;
	   }

	@Override
   public int getSeaLevel() {
      return 63;
   }
	
}
