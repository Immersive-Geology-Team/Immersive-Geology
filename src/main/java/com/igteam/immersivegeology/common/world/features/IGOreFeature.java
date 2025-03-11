/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world.features;
import com.igteam.immersivegeology.common.block.IGWeatheringOreBlock;
import com.igteam.immersivegeology.common.block.helper.MineralWeathering;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.common.config.IGServerConfig.Ores.OreConfig;
import com.igteam.immersivegeology.common.world.IWorldGenConfig;
import com.igteam.immersivegeology.common.world.features.IGOreFeature.IGOreFeatureConfig;
import com.igteam.immersivegeology.common.world.features.helper.IGGenerationType;
import com.igteam.immersivegeology.common.world.noise.INoise3D;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class IGOreFeature extends Feature<IGOreFeatureConfig>
{
	public IGOreFeature(){
		super(IGOreFeature.IGOreFeatureConfig.CODEC.codec());
	}

	private static final float THRESHOLD = 0.3f;

	@Override
	public boolean place(FeaturePlaceContext<IGOreFeatureConfig> ctx)
	{
		IGOreFeatureConfig config = ctx.config();
		if(!config.canSpawn()) return false;
		ChunkGenerator chunkGenerator = ctx.chunkGenerator();
		BiomeSource biomeSource = chunkGenerator.getBiomeSource();

		WorldGenLevel level = ctx.level();
		BlockPos pos = ctx.origin();
		ChunkPos chunkPos = new ChunkPos(pos);
		Objects.requireNonNull(level);
		OreConfig rConfig = IGServerConfig.ORES.ores.get(config.entry);
		if(rConfig.veinSize.get() <= 0) return false;

		int chance_max = 50000;
		if(config.entry.instance().equals(MineralEnum.Unobtania.instance()))
		{
			chance_max = 1000000;
		}
		RandomSource random = new XoroshiroRandomSource(level.getSeed() ^ (long)chunkPos.x * 61728364132L, config.seed ^ (long)chunkPos.z * 16298364123L);

		if((random.nextInt(chance_max) < rConfig.generationChance.get()))
		{
			boolean noValidChunks = true;
			if(!biomeSource.possibleBiomes().isEmpty())
			{
				if(config.canSpawnAt(pos, (p) -> biomeSource.getNoiseBiome(p.getX(), p.getY(), p.getZ(), Climate.empty())))
				{
					noValidChunks = false;
				}
			}
			if(noValidChunks) return false;
			IGOreFeature.placeVein(level, random, chunkPos, createVein(random, rConfig, config.seed()), config);
			return true;
		}

		return false;
	}

	private static Vein createVein(RandomSource random, OreConfig config, long seed)
	{
		int FEATURE_SIZE = config.veinSize.get();
		INoise3D noise = config.generationPattern.get().getPattern().getiNoise3D(FEATURE_SIZE, seed);
		return new Vein(defaultPosRespectingHeight(random, config), random, noise);
	}

	private static BlockPos defaultPosRespectingHeight(RandomSource random, OreConfig config) {
		return new BlockPos(random.nextInt(16), defaultYPos(config.veinSize.get(), random, config), random.nextInt(16));
	}

	protected static int defaultYPos(int verticalShrinkRange, RandomSource random, OreConfig config) {
		int actualRange = config.maxY.get() - config.minY.get() - 2 * verticalShrinkRange;
		return actualRange > 0 ? config.minY.get() + verticalShrinkRange + random.nextInt(actualRange) : (config.minY.get() + config.maxY.get()) / 2;
	}

	private static MaterialHelper getFriendMaterial(RandomSource random, int height, Set<Pair<Function<Integer, MaterialHelper>, Integer>> friends)
	{
		float totalWeight = 0;
		// Calculate total weight
		for (Pair<Function<Integer, MaterialHelper>, Integer> entry : friends) {
			totalWeight += entry.getSecond();
		}

		// Get a random value in range [0, totalWeight)
		float randomValue = random.nextFloat() * totalWeight;

		// Iterate and select the weighted material
		for (Pair<Function<Integer, MaterialHelper>, Integer> entry : friends) {
			randomValue -= entry.getSecond();
			if (randomValue <= 0) {
				return entry.getFirst().apply(height);
			}
		}

		return null;
	}

	public static void placeVein(LevelAccessor level, RandomSource random, ChunkPos chunk, Vein vein, IGOreFeatureConfig config)
	{
		// Get the noise generator from the vein
		INoise3D noiseGenerator = vein.noise;

		// Get the chunk's world position (bottom-left corner)
		BlockPos chunkOrigin = chunk.getWorldPosition();

		// Get the vein's center position
		BlockPos veinCenter = chunkOrigin.offset(vein.pos);
		// MutableBlockPos for iterating over positions
		MutableBlockPos cursor = new MutableBlockPos();
		int veinMinY = config.entry().getMinY();
		int veinMaxY = config.entry().getMaxY();
		double associateChance = config.getConfig().associateChance.get();
		boolean useFriendMaterials = associateChance > random.nextDouble();
		MaterialInterface<?> parentMaterial = (MaterialInterface<?>) config.entry;
		Set<Pair<Function<Integer, MaterialHelper>, Integer>> friends = parentMaterial.instance().getAssociateMaterialSet();

		boolean isTube = config.getConfig().generationPattern.get().equals(IGGenerationType.TUBE);

		// Iterate over every block in the chunk
		for (int y = veinMinY; y < veinMaxY; y++) {
			for (int x = -16; x < 32; x++) { // Chunk local x with 1 chunk radius
				for (int z = -16; z < 32; z++) { // Chunk local z with 1 chunk radius

					// Calculate world coordinates
					int worldX = chunkOrigin.getX() + x;
					int worldZ = chunkOrigin.getZ() + z;

					// Set the position for the cursor
					cursor.set(worldX, y, worldZ);

					// Generate noise for the current block position
					double noiseValue = noiseGenerator.noise(worldX, y, worldZ);

					// Determine proximity to chunk boundaries we can't iterate in
					for(int boundary = 1; boundary <= 3; boundary++)
					{
						if(isNearNonIterableBoundary(x, z, boundary))
						{
							// Reduce the noise value if near boundary
							noiseValue *= 0.9;
						}
					}
					MaterialHelper useMaterial = parentMaterial.instance();

					float distance_from_centre_as_percentage = (float) (cursor.distToCenterSqr(veinCenter.getX(), isTube ? 0 : veinCenter.getY(), veinCenter.getZ()) / chunkOrigin.distSqr(chunkOrigin.offset(32,isTube ? 0 : vein.pos.getY(),32)));
					float passRate = (float) (config.getDensity() * (1 - distance_from_centre_as_percentage));
					// Custom logic for ore placement
					if (shouldPlaceOre(noiseValue, vein, config) && random.nextFloat() > passRate) {
						if(useFriendMaterials)
						{
							// TODO see if we can remove this from the internal loop for x and y...
							useMaterial = getFriendMaterial(random, y, friends);
							if(useMaterial == null){
								useMaterial = parentMaterial.instance();
							}
						}
						BlockState stoneState = level.getBlockState(cursor);

						if(stoneState.is(Blocks.AIR)) continue;
						BlockState oreState = config.getStateToGenerate(stoneState, random, noiseValue, useMaterial);
						if (oreState != null) {
							if(oreState.getBlock() instanceof IGWeatheringOreBlock) oreState = oxidizeExposed(level, cursor, oreState);
							level.setBlock(cursor, oreState, 3);
						}
					}
				}
			}
		}
	}

	//Checks if the current position is near a chunk boundary we can't iterate in.
	private static boolean isNearNonIterableBoundary(int x, int z, int threshold) {
		// Boundaries for this iteration (-16 to 32 inclusive)
		int minBoundary = -16;
		int maxBoundary = 32;

		// Check proximity to boundaries
		return (x < minBoundary + threshold || x > maxBoundary - threshold ||
				z < minBoundary + threshold || z > maxBoundary - threshold);
	}

	private static boolean shouldPlaceOre(double noiseValue, Vein vein, IGOreFeatureConfig config) {
		return noiseValue > THRESHOLD;
	}

	private static final Direction[] DIRECTIONS = Direction.values();

	private static BlockState oxidizeExposed(LevelAccessor level, BlockPos.MutableBlockPos cursor, BlockState oreState)
	{
		// Check if the ore block is randomly ticking
		if(oreState.getBlock().isRandomlyTicking(oreState))
		{
			// Iterate over directions and corresponding oxidation properties
			for(int i = 0; i < DIRECTIONS.length; i++)
			{
				Direction direction = DIRECTIONS[i];
				EnumProperty<MineralWeathering> oxidationProperty = IGWeatheringOreBlock.OXIDATION_PROPERTIES.get(i);
				BlockPos adjacentPos = cursor.offset(direction.getNormal());

				// Set the exposed side to OXIDIZED based on the direction
				oreState = handleOxidation(oreState, level, adjacentPos, oxidationProperty);
			}
		}

		return oreState;
	}

	private static BlockState handleOxidation(BlockState state, LevelAccessor level, BlockPos adjacentPos, EnumProperty<MineralWeathering> oxidationProperty)
	{
		if (level.getBlockState(adjacentPos).isAir())
		{
			return state.setValue(oxidationProperty, MineralWeathering.CORRODED);
		}
		if (level.getBlockState(adjacentPos).is(Blocks.WATER))
		{
			return state.setValue(oxidationProperty, MineralWeathering.TARNISHED);
		}
		return state;
	}

	private boolean isNearLava(WorldGenLevel level, MutableBlockPos cursor, int x, int z)
	{
		for(int lavaX = x - 4; lavaX <= x + 4; ++lavaX) {
			for(int lavaZ = z - 4; lavaZ <= z + 4; ++lavaZ) {
				cursor.set(lavaX, -55, lavaZ);
				if (level.getFluidState(cursor).getType() == Fluids.LAVA) {
					return true;
				}
			}
		}
		return false;
	}


	public record IGOreFeatureConfig(IWorldGenConfig entry, long seed, double temp_range_min, double temp_range_max, double downfall_min, double downfall_max) implements FeatureConfiguration
	{
		public static final MapCodec<IGOreFeatureConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
			return instance.group(
					IWorldGenConfig.CODEC.fieldOf("entry").forGetter((c) -> c.entry),
					Codec.either(Codec.STRING, Codec.LONG)
							.xmap((e) -> e.map(IGOreFeatureConfig::hash, (l) -> l), Either::right)
							.fieldOf("random_name").forGetter((c) -> c.seed),
					Codec.DOUBLE.fieldOf("temp_range_min").forGetter((c) -> c.temp_range_min),
					Codec.DOUBLE.fieldOf("temp_range_max").forGetter((c) -> c.temp_range_max),
					Codec.DOUBLE.fieldOf("downfall_min").forGetter((c) -> c.downfall_min),
					Codec.DOUBLE.fieldOf("downfall_max").forGetter((c) -> c.downfall_max)
			).apply(instance, IGOreFeatureConfig::new);
		});

		public int getSize() {
			return getConfig().veinSize.get();
		}

		public static long hash(String name) {
			RandomSupport.Seed128bit seed128 = RandomSupport.seedFromHashOf(name);
			return seed128.seedLo() ^ seed128.seedHi();
		}

		public boolean canSpawnAt(BlockPos pos, Function<BlockPos, Holder<Biome>> biomeQuery) {
			Holder<Biome> biome = biomeQuery.apply(pos);
			// Get biome temperature and downfall
			float biomeTemp = biome.value().getBaseTemperature();
			float biomeDownfall = biome.value().getModifiedClimateSettings().downfall();
			OreConfig config = IGServerConfig.ORES.ores.get(entry);

			// Check if the biome's temperature and downfall are within the configured ranges
			return biomeTemp >= config.min_downfall.get() &&
					biomeTemp <= config.max_downfall.get() &&
					biomeDownfall >= config.min_downfall.get() &&
					biomeDownfall <= config.max_downfall.get();
		}

		public int getRarity() {
			return getConfig().rarity.get();
		}

		private OreConfig getConfig()
		{
			return IGServerConfig.ORES.ores.get(entry);
		}

		public IWorldGenConfig type() {
			return this.entry;
		}

		public BlockState getStateToGenerate(BlockState stoneState, RandomSource random, double noiseValue, MaterialHelper mineral)
		{
			TagMatchTest validStone = new TagMatchTest(Tags.Blocks.STONE);

			if(stoneState.is(Blocks.DRIPSTONE_BLOCK) &! stoneState.is(Blocks.SANDSTONE)) return null;

			StoneEnum stone = null;

			if(stoneState.is(Blocks.NETHERRACK))
			{
				stone = StoneEnum.Netherrack;
			}
			if(stoneState.is(Blocks.BASALT))
			{
				stone = StoneEnum.MCBasalt;
			}
			if(stone == null)
			{
				stone = StoneEnum.selectWorldState(stoneState);
			}

			if(stone == null) {
				return null;
			}

			if(!mineral.acceptableStoneType(stone.instance())) {
				return null;
			}

			// Checks if the stone is a MOD only type, and if so, is it available?
			if(!stone.isStoneTypeValid())
			{
				return null;
			}

			List<BlockState> blocks;

			// List of blocks for each ore richness
			try
			{
				blocks = List.of(
						mineral.getOreBlock(stone, OreRichness.POOR).getDefaultBlockState(),
						mineral.getOreBlock(stone, OreRichness.NORMAL).getDefaultBlockState(),
						mineral.getOreBlock(stone, OreRichness.RICH).getDefaultBlockState()
				);
			}
			catch(Exception e)
			{
				return null;
			}

			int selectedBlock = noiseValue > (THRESHOLD+0.2) ? 2 : (noiseValue > (THRESHOLD+0.1) ? 1 : 0);

			return blocks.get(selectedBlock);
		}

		public int getChanceToGenerate(IWorldGenConfig entry)
		{
			IGServerConfig.Ores.OreConfig config = IGServerConfig.ORES.ores.get(entry);
			return config.generationChance.get();
		}

		public double getDensity()
		{
			IGServerConfig.Ores.OreConfig config = IGServerConfig.ORES.ores.get(entry);
			return config.density.get();
		}

		public boolean canSpawn()
		{
			IGServerConfig.Ores.OreConfig config = IGServerConfig.ORES.ores.get(entry);
			return config.canSpawn.get();
		}
	}

	protected record Vein(BlockPos pos, RandomSource random, INoise3D noise) {
		protected Vein(BlockPos pos, RandomSource random, INoise3D noise) {
			this.pos = pos;
			this.random = random;
			this.noise = noise;
		}

		public INoise3D getNoise()
		{
			return this.noise;
		}

		public BlockPos pos() {
			return this.pos;
		}
	}
}
