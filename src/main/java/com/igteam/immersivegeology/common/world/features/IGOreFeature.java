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
import com.igteam.immersivegeology.common.world.features.helper.IGOreGenUtils;
import com.igteam.immersivegeology.common.world.noise.INoise3D;
import com.igteam.immersivegeology.core.lib.IGLib;
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
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class IGOreFeature extends Feature<IGOreFeatureConfig>
{
	public IGOreFeature(){
		super(IGOreFeature.IGOreFeatureConfig.CODEC.codec());
	}

	public static final float THRESHOLD = 0.4f;
	@Override
	public boolean place(FeaturePlaceContext<IGOreFeatureConfig> ctx)
	{
		IGOreFeatureConfig config = ctx.config();
		WorldGenLevel level = ctx.level();
		BlockPos pos = ctx.origin();
		ChunkPos chunkPos = new ChunkPos(pos);
		Objects.requireNonNull(level);
		OreConfig rConfig = IGServerConfig.ORES.ores.get(config.entry());
		RandomSource random = IGOreGenUtils.getReuseRandom(config.entry, level.getSeed(), chunkPos);
		Vein vein = createVein(random, rConfig, config.entry);
		IGOreFeature.placeVein(level, random, chunkPos, vein, config);
		return true;
	}

	public static String formatTime(long timeInNanoSeconds) {
		long seconds = timeInNanoSeconds / 1_000_000_000;
		long milliseconds = (timeInNanoSeconds % 1_000_000_000) / 1_000_000;
		long microseconds = (timeInNanoSeconds % 1_000_000) / 1_000;

		return String.format("%d seconds, %d milliseconds, %d microseconds", seconds, milliseconds, microseconds);
	}

	public static Vein createVein(RandomSource random, OreConfig config, IWorldGenConfig material)
	{
		int FEATURE_SIZE = config.veinSize.get();
		INoise3D noise = config.generationPattern.get().getPattern().getiNoise3D(FEATURE_SIZE, material.seed());
		return new Vein(defaultPosRespectingHeight(random, config), noise, material);
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
		// Get config values once
		int veinMinY = config.entry().getMinY();
		int veinMaxY = config.entry().getMaxY();
		double associateChance = config.getConfig().associateChance.get();
		boolean useFriendMaterials = associateChance > random.nextDouble();
		MaterialInterface<?> parentMaterial = (MaterialInterface<?>) config.entry;
		Set<Pair<Function<Integer, MaterialHelper>, Integer>> friends = parentMaterial.instance().getAssociateMaterialSet();

		int sectionMin = level.getSectionIndex(Math.max(veinMinY, level.getMinBuildHeight()));
		int sectionMax = level.getSectionIndex(Math.min(veinMaxY, level.getMaxBuildHeight()));
		// Iterate over the 3x3 chunk area
		for (int chunkDX = -1; chunkDX <= 1; chunkDX++) {
			for (int chunkDZ = -1; chunkDZ <= 1; chunkDZ++) {
				ChunkPos currentChunkPos = new ChunkPos(centerChunk.x + chunkDX, centerChunk.z + chunkDZ);
				ChunkAccess currentChunk = level.getChunk(currentChunkPos.x, currentChunkPos.z);
				for (int sectionIndex = sectionMin; sectionIndex <= sectionMax; sectionIndex++) {
					LevelChunkSection section = currentChunk.getSection(sectionIndex);

					// Skip if section is empty or doesn't have replaceable blocks
					if (section.hasOnlyAir() || !section.maybeHas((b) -> IGOreGenUtils.canStateGenerate(b, parentMaterial.instance()))) {
						continue;
					}

					// Calculate Y bounds for this section
					int sectionMinY = SectionPos.sectionToBlockCoord(sectionIndex);
					int sectionMaxY = sectionMinY + 15;

					// Process this section
					processChunkSection(
							level, random, currentChunk, sectionMinY, sectionMaxY,
							vein, useFriendMaterials, parentMaterial, friends, centerChunk
					);
				}
			}
		}
	}

	private static void processChunkSection(
			LevelAccessor level, RandomSource random, ChunkAccess chunk, int minY, int maxY, Vein vein, boolean useFriendMaterials,
			MaterialInterface<?> parentMaterial, Set<Pair<Function<Integer, MaterialHelper>, Integer>> friends, ChunkPos centerChunkPos) {
		ChunkPos chunkPos = chunk.getPos();

		for (int y = minY; y < maxY; y++) {
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					MaterialHelper reusableFriendMaterial = useFriendMaterials ? getFriendMaterial(random, y, friends) : null;
					BlockPos pos = chunkPos.getBlockAt(x,y,z);
					double noiseValue = IGOreGenUtils.noise(chunkPos, x,y,z, vein, centerChunkPos);
					if (noiseValue > THRESHOLD) {
						BlockState stoneState = chunk.getBlockState(new BlockPos(x,y,z));
						if (stoneState.isAir()) continue;

						// Determine material to use
						MaterialHelper useMaterial = parentMaterial.instance();
						if (useFriendMaterials && reusableFriendMaterial != null) {
							useMaterial = reusableFriendMaterial;
						}

						BlockState oreState = IGOreGenUtils.getStateToGenerate(stoneState, noiseValue, useMaterial);
						if (oreState != null) {
							if (oreState.getBlock() instanceof IGWeatheringOreBlock) {
								oreState = IGOreGenUtils.oxidizeExposed(level, chunkPos.getBlockAt(x,y,z), oreState);
							}
							chunk.setBlockState(pos, oreState, false);
						}
					}
				}
			}
		}
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

		private static final Map<String, Long> HASH_CACHE = new ConcurrentHashMap<>();

		public static long hash(String name) {
			return HASH_CACHE.computeIfAbsent(name, k -> {
				RandomSupport.Seed128bit seed128 = RandomSupport.seedFromHashOf(k);
				return seed128.seedLo() ^ seed128.seedHi();
			});
		}

		public int getRarity() {
			return getConfig().rarity.get();
		}

		public OreConfig getConfig()
		{
			return IGServerConfig.ORES.ores.get(entry);
		}

		public IWorldGenConfig type() {
			return this.entry;
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
	}

	public record Vein(BlockPos pos, INoise3D noise, IWorldGenConfig material) {
		public INoise3D getNoise()
		{
			return this.noise;
		}
		public IWorldGenConfig getMaterial() {return this.material;}
		public BlockPos pos() {return this.pos;}
	}
}