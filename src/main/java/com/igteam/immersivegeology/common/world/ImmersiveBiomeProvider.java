package com.igteam.immersivegeology.common.world;

import com.igteam.immersivegeology.common.world.biome.BiomeFactory;
import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.biome.IGBiomes;
import com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings;
import com.igteam.immersivegeology.common.world.layer.IGLayerUtil;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.storage.WorldInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class ImmersiveBiomeProvider extends BiomeProvider
{
	/**
	 * Before the final Voronoi Zoom - In OverworldBiomeProvider this is genBiomes
	 */
	private final BiomeFactory biomeFactory;
	/**
	 * After the final Voronoi Zoom - In OverworldBiomeProvider this is biomeFactoryLayer
	 */
	private final BiomeFactory biomeFactoryActual;
	
	Biome[] biomes = IGBiomes.getBiomes().stream().toArray(Biome[]::new);
	
	public ImmersiveBiomeProvider(ImmersiveGenerationSettings settings)
	{
		WorldInfo worldInfo = settings.getWorldInfo();
		List<IAreaFactory<LazyArea>> areaFactory = IGLayerUtil.createOverworldBiomeLayer(worldInfo.getSeed(), settings);

		this.biomeFactory = new BiomeFactory(areaFactory.get(0));
		this.biomeFactoryActual = new BiomeFactory(areaFactory.get(1));
	}

	/**
	 * Override the default spawn enabled biome list, as we don't use any of the applicable vanilla biomes.
	 */
	@Override
	public List<Biome> getBiomesToSpawnIn() {
		return Arrays.asList(biomes).stream().filter((biome) -> (biome != IGBiomes.OCEAN && 
				  biome != IGBiomes.DEEP_OCEAN &&
				  biome != IGBiomes.DEEP_OCEAN_VOLCANIC &&
				  biome != IGBiomes.OCEAN_EDGE)).collect(Collectors.toList());
	} 
	
	/**
	 * Gets the biome from the provided coordinates
	 *
	 * @param x The x coordinate to get the biome from.
	 * @param z The z coordinate to get the biome from.
	 */
	@Override
	@Nonnull
	public Biome getBiome(int x, int z)
	{
		return biomeFactoryActual.getBiome(x, z);
	}

	@Override
	@Nonnull
	public IGBiome[] getBiomes(int x, int z, int width, int length, boolean cacheFlag)
	{
		return biomeFactoryActual.getBiomes(x, z, width, length);
	}

	@Override
	@Nonnull
	public Set<Biome> getBiomesInSquare(int centerX, int centerZ, int sideLength)
	{
		Set<Biome> set = new HashSet<>();
		int startX = centerX-sideLength >> 2;
		int startZ = centerZ-sideLength >> 2;
		Collections.addAll(set, biomeFactory.getBiomes(startX, startZ, (centerX+sideLength >> 2)-startX+1, (centerZ+sideLength >> 2)-startZ+1));
		return set;
	}

	@Nullable
	@Override
	public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomes, Random random)
	{
		int startX = x-range >> 2;
		int startZ = z-range >> 2;
		int width = (x+range >> 2)-startX+1;
		int height = (z+range >> 2)-startZ+1;
		Biome[] biomeInArea = biomeFactory.getBiomes(startX, startZ, width, height);
		BlockPos blockpos = null;

		int counter = 0;
		for(int i = 0; i < width*height; ++i)
		{
			int xPos = startX+i%width<<2;
			int zPos = startZ+i/width<<2;
			if(biomes.contains(biomeInArea[i]))
			{
				if(blockpos==null||random.nextInt(counter+1)==0)
				{
					blockpos = new BlockPos(xPos, 0, zPos);
				}
				++counter;
			}
		}

		return blockpos;
	}

	@Override
	public boolean hasStructure(Structure<?> structureIn)
	{
		return this.hasStructureCache.computeIfAbsent(structureIn, structure -> {
			for(Biome biome : this.biomes)
			{
				if(biome.hasStructure(structure))
				{
					return true;
				}
			}
			return false;
		});
	}

	@Override
	@Nonnull
	public Set<BlockState> getSurfaceBlocks()
	{
		if(this.topBlocksCache.isEmpty())
		{
			for(Biome biome : this.biomes)
			{
				this.topBlocksCache.add(biome.getSurfaceBuilderConfig().getTop());
			}
		}
		return this.topBlocksCache;
	}

}
