package com.igteam.immersivegeology.common.world;

import static com.igteam.immersivegeology.ImmersiveGeology.MODID;

import com.igteam.immersivegeology.common.world.chunk.ChunkGeneratorImmersiveOverworld;
import com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings;
import com.igteam.immersivegeology.common.world.help.Helpers;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraftforge.registries.ObjectHolder;

public class WorldTypeImmersive extends WorldType {

	public WorldTypeImmersive() {
		super("immersive");
		// TODO Auto-generated constructor stub
	}
	
	@ObjectHolder(MODID + ":overworld")
    public static final ChunkGeneratorType<ImmersiveGenerationSettings, ChunkGeneratorImmersiveOverworld> CHUNK_GENERATOR_TYPE = Helpers.getNull();

    @ObjectHolder(MODID + ":overworld")
    public static final BiomeProviderType<ImmersiveGenerationSettings, ImmersiveBiomeProvider> BIOME_PROVIDER_TYPE = Helpers.getNull();
	
	
	@Override
	public ChunkGenerator<?> createChunkGenerator(World world) {
		ImmersiveGenerationSettings settings = CHUNK_GENERATOR_TYPE.createSettings();
		settings.setWorldInfo(world.getWorldInfo());
		BiomeProvider biomeProvider = BIOME_PROVIDER_TYPE.create(settings);
		return CHUNK_GENERATOR_TYPE.create(world, biomeProvider, settings);
	}
}	
