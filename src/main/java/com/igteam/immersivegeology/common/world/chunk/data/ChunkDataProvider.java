package com.igteam.immersivegeology.common.world.chunk.data;

import com.igteam.immersivegeology.common.util.FiniteLinkedHashMap;
import com.igteam.immersivegeology.common.world.chunk.ChunkGeneratorImmersiveOverworld;
import com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings;
import com.igteam.immersivegeology.common.world.gen.config.ImmersiveNetherGenSettings;
import com.igteam.immersivegeology.common.world.noise.INoise2D;
import com.igteam.immersivegeology.common.world.noise.SimplexNoise2D;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

public class ChunkDataProvider
{
	@Nullable
	@SuppressWarnings("ConstantConditions")
	public static ChunkDataProvider get(World world)
	{
		AbstractChunkProvider chunkProvider = world.getChunkProvider();
		// Chunk provider can be null during the attach capabilities event
		if(chunkProvider!=null)
		{
			ChunkGenerator<?> chunkGenerator = chunkProvider.getChunkGenerator();
			if(chunkGenerator!=null)
			{
				if(chunkGenerator instanceof ChunkGeneratorImmersiveOverworld)
				{
					return ((ChunkGeneratorImmersiveOverworld)chunkGenerator).getChunkDataProvider();
				}
			}
			else
			{
				Random random = new Random();
				random.setSeed(world.getSeed());
				return new ChunkDataProvider(world, new ImmersiveGenerationSettings(), random);
			}
		}

		Random random = new Random();
		random.setSeed(world.getSeed());
		return new ChunkDataProvider(world, new ImmersiveGenerationSettings(), random);
	}

	private final Map<ChunkPos, ChunkData> cachedChunkData;
	private final IWorld world;
	private final int bottomLayerBaseHeight, middleLayerBaseHeight;

	private final INoise2D regionalTempNoise;
	private final INoise2D rainfallNoise;

	public ChunkDataProvider(IWorld world, ImmersiveGenerationSettings settings, Random seedGenerator)
	{
		this.cachedChunkData = new FiniteLinkedHashMap<>(256);
		this.world = world;

		//TODO should include in config
		this.bottomLayerBaseHeight = 30;
		this.middleLayerBaseHeight = 30;

		// Climate
		this.regionalTempNoise = new SimplexNoise2D(seedGenerator.nextLong()).octaves(4).scaled(-5.5f, 5.5f).flattened(-5, 5).spread(0.002f);
		this.rainfallNoise = new SimplexNoise2D(seedGenerator.nextLong()).octaves(4).scaled(-25, 525).flattened(0, 500).spread(0.002f);

		final INoise2D warpX = new SimplexNoise2D(seedGenerator.nextLong()).octaves(4).spread(0.1f).scaled(-30, 30);
		final INoise2D warpZ = new SimplexNoise2D(seedGenerator.nextLong()+1).octaves(4).spread(0.1f).scaled(-30, 30);
	}


	public ChunkDataProvider(IWorld world, ImmersiveNetherGenSettings settings, Random seedGenerator)
	{
		this.cachedChunkData = new FiniteLinkedHashMap<>(256);
		this.world = world;

		//TODO should include in config
		this.bottomLayerBaseHeight = 30;
		this.middleLayerBaseHeight = 30;

		// Climate
		this.regionalTempNoise = new SimplexNoise2D(seedGenerator.nextLong()).octaves(4).scaled(-5.5f, 5.5f).flattened(-5, 5).spread(0.002f);
		this.rainfallNoise = new SimplexNoise2D(seedGenerator.nextLong()).octaves(4).scaled(-25, 525).flattened(0, 500).spread(0.002f);

		final INoise2D warpX = new SimplexNoise2D(seedGenerator.nextLong()).octaves(4).spread(0.1f).scaled(-30, 30);
		final INoise2D warpZ = new SimplexNoise2D(seedGenerator.nextLong()+1).octaves(4).spread(0.1f).scaled(-30, 30);
	}

	@Nonnull
	public ChunkData get(ChunkPos pos)
	{
		if(world.chunkExists(pos.x, pos.z))
		{
			return get(world.getChunk(pos.x, pos.z));
		}
		return getOrCreate(pos);
	}

	@Nonnull
	public ChunkData get(IChunk chunkIn)
	{
		if(chunkIn instanceof Chunk)
		{
			LazyOptional<ChunkData> capability = ((Chunk)chunkIn).getCapability(ChunkDataCapability.CAPABILITY);
			return capability.orElseGet(() -> getOrCreate(chunkIn.getPos()));
		}
		return getOrCreate(chunkIn.getPos());
	}

	@Nonnull
	public ChunkData getOrCreate(ChunkPos pos)
	{
		if(cachedChunkData.containsKey(pos))
		{
			return cachedChunkData.get(pos);
		}
		return createData(pos);
	}

	@Nonnull
	private ChunkData createData(ChunkPos pos)
	{
		ChunkData data = new ChunkData();
		int chunkX = pos.getXStart(), chunkZ = pos.getZStart();
		cachedChunkData.put(pos, data);

		// Temperature / Rainfall
		data.setRainfall(rainfallNoise.noise(chunkX, chunkZ));
		data.setRegionalTemp(regionalTempNoise.noise(chunkX, chunkZ));

		return data;
	}
}
