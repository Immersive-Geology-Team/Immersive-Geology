package com.igteam.immersivegeology.common.world;

import com.igteam.immersivegeology.common.world.chunk.ChunkGeneratorImmersiveOverworld;
import com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.provider.OverworldBiomeProviderSettings;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.OverworldGenSettings;

public class WorldTypeImmersive extends WorldType {

	public WorldTypeImmersive() {
		super("immersive");
		// TODO Auto-generated constructor stub
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator(World world) {
		ImmersiveGenerationSettings overworldGenSettings = new ImmersiveGenerationSettings();
		overworldGenSettings.setWorldInfo(world.getWorldInfo());

		return new ChunkGeneratorImmersiveOverworld(world, new ImmersiveBiomeProvider(overworldGenSettings),
				overworldGenSettings);
	}
}	
