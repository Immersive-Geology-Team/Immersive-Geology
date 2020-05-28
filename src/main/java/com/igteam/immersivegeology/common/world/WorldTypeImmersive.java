package com.igteam.immersivegeology.common.world;

import com.igteam.immersivegeology.common.world.chunk.ChunkGeneratorImmersiveOverworld;
import com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProviderSettings;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.NetherChunkGenerator;
import net.minecraft.world.gen.NetherGenSettings;

public class WorldTypeImmersive extends WorldType {

	public WorldTypeImmersive() {
		super("immersive");
		// TODO Auto-generated constructor stub
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator(World world) {
		if(!world.getDimension().isNether()) {
		ImmersiveGenerationSettings overworldGenSettings = new ImmersiveGenerationSettings();
		overworldGenSettings.setWorldInfo(world.getWorldInfo());

		return new ChunkGeneratorImmersiveOverworld(world, new ImmersiveBiomeProvider(overworldGenSettings),
				overworldGenSettings);
		} else {
			NetherGenSettings netherGenSettings = new NetherGenSettings();
			SingleBiomeProviderSettings netherSettings = new SingleBiomeProviderSettings();
			BiomeProvider biomeProvider = new SingleBiomeProvider(netherSettings);
			return new NetherChunkGenerator(world, biomeProvider, netherGenSettings);
		}
	}
}	
