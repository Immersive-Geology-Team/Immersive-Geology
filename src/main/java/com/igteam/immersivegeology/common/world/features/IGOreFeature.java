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
import com.igteam.immersivegeology.core.lib.IGLib;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class IGOreFeature extends Feature<IGOreFeatureConfig>
{
	public IGOreFeature(){
		super(IGOreFeature.IGOreFeatureConfig.CODEC.codec());
	}

	private static final float THRESHOLD = 0.4f;
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
		RandomSource random = new XoroshiroRandomSource(level.getSeed() ^ (long)chunkPos.x * 61728364132L, config.seed ^ (long)chunkPos.z * 16298364123L);
		if(config.canPlaceVein(chunkPos, level.getSeed(), biomeSource.getNoiseBiome(pos.getX(), pos.getY(), pos.getZ(), Climate.empty())))
		{
			IGOreFeature.placeVein(level, random, chunkPos, createVein(random, rConfig, config.seed()), config);
		}

		return false;
	}

	public static String formatTime(long timeInNanoSeconds) {
		long seconds = timeInNanoSeconds / 1_000_000_000;
		long milliseconds = (timeInNanoSeconds % 1_000_000_000) / 1_000_000;
		long microseconds = (timeInNanoSeconds % 1_000_000) / 1_000;

		return String.format("%d seconds, %d milliseconds, %d microseconds", seconds, milliseconds, microseconds);
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

	public static void placeVein(LevelAccessor level, RandomSource random, ChunkPos centerChunk, Vein vein, IGOreFeatureConfig config)
	{
		// Get the noise generator from the vein
		INoise3D noiseGenerator = vein.noise;

		// Get config values once
		int veinMinY = config.entry().getMinY();
		int veinMaxY = config.entry().getMaxY();
		double associateChance = config.getConfig().associateChance.get();
		boolean useFriendMaterials = associateChance > random.nextDouble();
		MaterialInterface<?> parentMaterial = (MaterialInterface<?>) config.entry;
		Set<Pair<Function<Integer, MaterialHelper>, Integer>> friends = parentMaterial.instance().getAssociateMaterialSet();
		boolean isTube = config.getConfig().generationPattern.get().equals(IGGenerationType.TUBE);

		// Get the vein's center position
		BlockPos chunkOrigin = centerChunk.getWorldPosition();
		BlockPos veinCenter = chunkOrigin.offset(vein.pos);

		// Pre-calculate squared max distance for percentage calculation
		double maxDistSq = chunkOrigin.distSqr(chunkOrigin.offset(32, isTube ? 0 : vein.pos.getY(), 32));

		// Cache frequently accessed coordinates
		int centerX = chunkOrigin.getX();
		int centerY = isTube ? 0 : veinCenter.getY();
		int centerZ = chunkOrigin.getZ();
		double density = config.getDensity();

		// Iterate over the 3x3 chunk area
		for (int chunkDX = -1; chunkDX <= 1; chunkDX++) {
			for (int chunkDZ = -1; chunkDZ <= 1; chunkDZ++) {
				ChunkPos currentChunkPos = new ChunkPos(centerChunk.x + chunkDX, centerChunk.z + chunkDZ);
				ChunkAccess currentChunk = level.getChunk(currentChunkPos.x, currentChunkPos.z);

				// Calculate world position of this chunk
				int chunkWorldX = currentChunkPos.getMinBlockX();
				int chunkWorldZ = currentChunkPos.getMinBlockZ();

				// Iterate through relevant chunk sections
				int minSection = level.getSectionIndex(veinMinY);
				int maxSection = level.getSectionIndex(veinMaxY - 1);

				for (int sectionY = minSection; sectionY <= maxSection; sectionY++) {
					LevelChunkSection section = currentChunk.getSection(sectionY);

					// Skip if section is empty or doesn't have replaceable blocks
					if (section.hasOnlyAir() || !section.maybeHas(config::canStateGenerate)) {
						continue;
					}

					// Calculate Y bounds for this section
					int sectionMinY = Math.max(sectionY * 16, veinMinY);
					int sectionMaxY = Math.min((sectionY + 1) * 16, veinMaxY);

					// Process this section
					processChunkSection(
							level, random, currentChunk, section,
							chunkWorldX, chunkWorldZ, sectionMinY, sectionMaxY,
							noiseGenerator, vein, config, veinCenter, centerX, centerY, centerZ,
							maxDistSq, density, useFriendMaterials, parentMaterial, friends
					);
				}
			}
		}
	}
	private static void processChunkSection(
			LevelAccessor level, RandomSource random, ChunkAccess chunk, LevelChunkSection section,
			int chunkX, int chunkZ, int minY, int maxY,
			INoise3D noiseGenerator, Vein vein, IGOreFeatureConfig config,
			BlockPos veinCenter, int centerX, int centerY, int centerZ,
			double maxDistSq, double density, boolean useFriendMaterials,
			MaterialInterface<?> parentMaterial, Set<Pair<Function<Integer, MaterialHelper>, Integer>> friends) {

		MutableBlockPos cursor = new MutableBlockPos();

		// Calculate boundary multipliers for this chunk (relative to center chunk)
		double[] xBoundaryMultipliers = new double[16];
		double[] zBoundaryMultipliers = new double[16];

		for (int x = 0; x < 16; x++) {
			int relativeX = (chunkX - centerX + 16) + x;
			xBoundaryMultipliers[x] = calculateBoundaryMultiplier(relativeX);
		}

		for (int z = 0; z < 16; z++) {
			int relativeZ = (chunkZ - centerZ + 16) + z;
			zBoundaryMultipliers[z] = calculateBoundaryMultiplier(relativeZ);
		}

		// Iterate through each block in the section
		for (int y = minY; y < maxY; y++) {
			// Cache a potentially reusable material per y-level
			MaterialHelper reusableFriendMaterial = useFriendMaterials ? getFriendMaterial(random, y, friends) : null;

			for (int x = 0; x < 16; x++) {
				int worldX = chunkX + x;
				double xMultiplier = xBoundaryMultipliers[x];
				if (xMultiplier == 0.0) continue;

				for (int z = 0; z < 16; z++) {
					// Density Check
					if(random.nextFloat() <= density) continue;
					int worldZ = chunkZ + z;
					double zMultiplier = zBoundaryMultipliers[z];
					if (zMultiplier == 0.0) continue;

					// Final boundary multiplier
					double boundaryMultiplier = Math.min(xMultiplier, zMultiplier);

					// Set cursor position
					cursor.set(worldX, y, worldZ);
					// Generate noise with boundary adjustment
					// This prevents harsh edges on chunk boundaries when generating our ours.
					double noiseValue = noiseGenerator.noise(worldX, y, worldZ) * boundaryMultiplier;
					if (shouldPlaceOre(noiseValue, vein, config)) {
						BlockState stoneState = chunk.getBlockState(cursor);
						if (stoneState.isAir()) continue;

						// Determine material to use
						MaterialHelper useMaterial = parentMaterial.instance();
						if (useFriendMaterials && reusableFriendMaterial != null) {
							useMaterial = reusableFriendMaterial;
						}

						BlockState oreState = config.getStateToGenerate(stoneState, noiseValue, useMaterial);
						if (oreState != null) {
							if (oreState.getBlock() instanceof IGWeatheringOreBlock) {
								oreState = oxidizeExposed(level, cursor, oreState);
							}
							chunk.setBlockState(cursor, oreState, false);
						}
					}
				}
			}
		}
	}

	// Calculate multiplier for a single axis
	private static double calculateBoundaryMultiplier(int relativePos) {
		// Boundaries for iteration (-16 to 32 inclusive from center chunk)
		if (relativePos < 0 || relativePos >= 48) return 0.0;

		// Distance from nearest boundary
		int distToBoundary = Math.min(relativePos, 48 - relativePos - 1);

		// Apply graduated reduction if within 8 blocks of boundary
		if (distToBoundary <= 8) {
			return Math.max(0.0, 0.9 * (distToBoundary / 8.0));
		}

		return 1.0;
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
		BlockState adjState = level.getBlockState(adjacentPos);
		if (!adjState.isCollisionShapeFullBlock(level, adjacentPos))
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
			return biomeTemp >= config.min_temp.get() &&
					biomeTemp <= config.max_temp.get() &&
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

		public BlockState getStateToGenerate(BlockState stoneState, double noiseValue, MaterialHelper mineral)
		{
			StoneEnum stone = null;
			if(stoneState.is(Blocks.NETHERRACK))
			{
				stone = StoneEnum.MCNetherrack;
			}
			if(stoneState.is(Blocks.BASALT))
			{
				stone = StoneEnum.MCBasalt;
			}
			if(stoneState.is(Blocks.END_STONE))
			{
				stone = StoneEnum.MCEndStone;
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

			int selectedBlock = noiseValue > (0.99) ? 2 : (noiseValue > (0.7) ? 1 : 0);

			return blocks.get(selectedBlock);
		}

		public boolean canStateGenerate(BlockState stoneState) {
			MaterialHelper mineral = entry().instance();
			StoneEnum stone;

			// Directly check for specific blocks first for faster matching
			if (stoneState.is(Blocks.STONE)) {
				return mineral.acceptableStoneType(StoneEnum.MCStone);
			}
			if (stoneState.is(Blocks.NETHERRACK)) {
				return mineral.acceptableStoneType(StoneEnum.MCNetherrack);
			}
			if (stoneState.is(Blocks.BASALT)) {
				return mineral.acceptableStoneType(StoneEnum.MCBasalt);
			}
			if (stoneState.is(Blocks.END_STONE)) {
				return mineral.acceptableStoneType(StoneEnum.MCEndStone);
			}

			// If we're not looking for some vanilla stone, let's try and handle a match for the name.
			stone = StoneEnum.selectWorldState(stoneState);

			// Return false if no stone is selected or if it's not acceptable
			if (stone == null || !mineral.acceptableStoneType(stone.instance())) {
				return false;
			}

			// Return whether the stone is a valid type
			return stone.isStoneTypeValid();
		}

		public int getChanceToGenerate()
		{
			IGServerConfig.Ores.OreConfig config = IGServerConfig.ORES.ores.get(this.entry);
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

		public boolean canPlaceVein(ChunkPos pos, long level_seed, Holder<Biome> biome)
		{
			OreConfig rConfig = IGServerConfig.ORES.ores.get(entry);
			if(rConfig.veinSize.get() <= 0) return false;
			int chance_max = 2_000_000;
			RandomSource random = new XoroshiroRandomSource(level_seed ^ (long)pos.x * 61728364132L, seed ^ (long)pos.z * 16298364123L);

			if((random.nextInt(chance_max) < getChanceToGenerate()))
			{
				boolean noValidChunks = !canSpawnAt(pos.getWorldPosition(), (p) -> biome);

				return !noValidChunks;
			}
			return false;
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
