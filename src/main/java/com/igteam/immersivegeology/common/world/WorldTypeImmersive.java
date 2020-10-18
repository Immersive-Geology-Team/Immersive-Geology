package com.igteam.immersivegeology.common.world;

import com.igteam.immersivegeology.common.world.chunk.ChunkGeneratorImmersiveNether;
import com.igteam.immersivegeology.common.world.chunk.ChunkGeneratorImmersiveOverworld;
import com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings;
import com.igteam.immersivegeology.common.world.gen.config.ImmersiveNetherGenSettings;
import com.igteam.immersivegeology.common.world.help.Helpers;
import net.minecraft.util.concurrent.ITaskQueue;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProviderSettings;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.NetherChunkGenerator;
import net.minecraft.world.gen.NetherGenSettings;
import net.minecraftforge.registries.ObjectHolder;

import static com.igteam.immersivegeology.ImmersiveGeology.MODID;

public class WorldTypeImmersive extends WorldType
{

	public WorldTypeImmersive()
	{
		super("immersive");
		// TODO Auto-generated constructor stub
	}

	@ObjectHolder(MODID+":overworld")
	public static final ChunkGeneratorType<ImmersiveGenerationSettings, ChunkGeneratorImmersiveOverworld> CHUNK_GENERATOR_TYPE = Helpers.getNull();

	@ObjectHolder(MODID+":overworld")
	public static final BiomeProviderType<ImmersiveGenerationSettings, ImmersiveBiomeProvider> BIOME_PROVIDER_TYPE = Helpers.getNull();

	@ObjectHolder(MODID+":nether")
	public static final ChunkGeneratorType<ImmersiveNetherGenSettings, ChunkGeneratorImmersiveNether> NETHER_CHUNK_GENERATOR_TYPE = Helpers.getNull();

	@ObjectHolder(MODID+":nether")
	public static final BiomeProviderType<ImmersiveNetherGenSettings, ImmersiveNetherBiomeProvider> NETHER_PROVIDER_TYPE = Helpers.getNull();

	@Override
	public ChunkGenerator<?> createChunkGenerator(World world)
	{
		if(world.getDimension().getType()==DimensionType.OVERWORLD)
		{
			ImmersiveGenerationSettings settings = CHUNK_GENERATOR_TYPE.createSettings();
			settings.setWorldInfo(world.getWorldInfo());
			BiomeProvider biomeProvider = BIOME_PROVIDER_TYPE.create(settings);
			return CHUNK_GENERATOR_TYPE.create(world, biomeProvider, settings);
		}
		else if(world.getDimension().getType() == DimensionType.THE_NETHER){
			ImmersiveNetherGenSettings settings = NETHER_CHUNK_GENERATOR_TYPE.createSettings();
			settings.setWorldInfo(world.getWorldInfo());
			BiomeProvider biomeProvider = NETHER_PROVIDER_TYPE.create(settings);
			return NETHER_CHUNK_GENERATOR_TYPE.create(world, biomeProvider, settings);
		}
		else
		{
			return super.createChunkGenerator(world);
		}
	}
}	
