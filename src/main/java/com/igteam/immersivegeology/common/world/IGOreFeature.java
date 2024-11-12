/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world;
import com.igteam.immersivegeology.common.block.IGOreBlock;
import com.igteam.immersivegeology.common.block.IGOreBlock.MineralOxidation;
import com.igteam.immersivegeology.common.block.IGOreBlock.OreRichness;
import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.common.config.IGServerConfig.Ores.OreConfig;
import com.igteam.immersivegeology.common.world.IGOreFeature.IGOreFeatureConfig;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

public class IGOreFeature extends Feature<IGOreFeatureConfig>
{
	public IGOreFeature(){
		super(IGOreFeature.IGOreFeatureConfig.CODEC.codec());
	}

	@Override
	public boolean place(FeaturePlaceContext<IGOreFeatureConfig> ctx)
	{
		IGOreFeatureConfig config = ctx.config();
		WorldGenLevel level = ctx.level();
		BlockPos pos = ctx.origin();
		RandomSource random = ctx.random();
		ChunkPos chunkPos = new ChunkPos(pos);
		Objects.requireNonNull(level);
		List<Vein> veins = this.getNearbyVeins(level, chunkPos, config.getSize(), config, level::getBiome);
		if (veins.isEmpty()) {
			return false;
		} else {
			Iterator<Vein> iter = veins.iterator();

			while(iter.hasNext()) {
				Vein vein = iter.next();
				this.place(level, random, chunkPos.getMinBlockX(), chunkPos.getMinBlockZ(), vein, config);
			}

			return true;
		}
	}


	public final List<Vein> getNearbyVeins(WorldGenLevel level, ChunkPos pos, int radius, IGOreFeatureConfig config, Function<BlockPos, Holder<Biome>> biomeQuery) {
		List<Vein> veins = new ArrayList();

		for(int x = pos.x - radius; x <= pos.x + radius; ++x) {
			for(int z = pos.z - radius; z <= pos.z + radius; ++z) {
				this.getVeinsAtChunk(level, x, z, veins, config, biomeQuery);
			}
		}

		return veins;
	}

	public final void getVeinsAtChunk(WorldGenLevel level, int chunkPosX, int chunkPosZ, List<Vein> veins, IGOreFeatureConfig config, Function<BlockPos, Holder<Biome>> biomeQuery) {
		RandomSource random = new XoroshiroRandomSource(level.getSeed() ^ (long)chunkPosX * 61728364132L, config.seed ^ (long)chunkPosZ * 16298364123L);
		OreConfig rConfig = IGServerConfig.ORES.ores.get(config.entry);
		Vein vein = this.createVein(chunkPosX<<4, chunkPosZ<<4, random, rConfig);

		if(config.getChanceToGenerate(config.entry) > random.nextInt(10000))
		{
			if(config.canSpawnAt(vein.pos(), biomeQuery))
			{
				veins.add(vein);
			}
		}
	}

	private Vein createVein(int chunkX, int chunkZ, RandomSource random, OreConfig config)
	{
		return new Vein(this.defaultPosRespectingHeight(chunkX, chunkZ, random, config));
	}

	private BlockPos defaultPosRespectingHeight(int chunkX, int chunkZ, RandomSource random, OreConfig config) {
		return new BlockPos(chunkX + random.nextInt(16), this.defaultYPos(config.veinSize.get(), random, config), chunkZ + random.nextInt(16));
	}

	protected final int defaultYPos(int verticalShrinkRange, RandomSource random, OreConfig config) {
		int actualRange = config.maxY.get() - config.minY.get() - 2 * verticalShrinkRange;
		return actualRange > 0 ? config.minY.get() + verticalShrinkRange + random.nextInt(actualRange) : (config.minY.get() + config.maxY.get()) / 2;
	}

	protected void place(WorldGenLevel level, RandomSource random, int blockX, int blockZ, Vein vein, IGOreFeatureConfig config)
	{
		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
		IWorldGenConfig mineral = config.entry;
		OreConfig oreConfig = IGServerConfig.ORES.ores.get(mineral);
		BlockPos pos = vein.pos();
		BoundingBox box = new BoundingBox(pos).inflatedBy(random.nextInt(Math.floorDiv((Math.max(4,oreConfig.veinSize.get())), 4)) + (Math.max(2,oreConfig.veinSize.get()) / 2));
		int offsetX;
		int offsetZ;
		offsetX = random.nextInt(16) - random.nextInt(16);
		offsetZ = random.nextInt(16) - random.nextInt(16);

		int minX = Math.max(blockX, box.minX());
		int maxX = Math.min(blockX + 15, box.maxX());
		int minY = Math.max(oreConfig.minY.get(), box.minY());
		int maxY = Math.min(oreConfig.maxY.get(), box.maxY());
		int minZ = Math.max(blockZ, box.minZ());
		int maxZ = Math.min(blockZ + 15, box.maxZ());
		for(int x = minX; x <= maxX; ++x) {
			for(int z = minZ; z <= maxZ; ++z) {
				int projectedY = (int) Math.min(10,Math.round(level.getHeight(Types.OCEAN_FLOOR_WG, offsetX + x, offsetZ + z) * 0.25));
				for(int y = minY; y <= maxY; ++y) {
					cursor.set(x, y + projectedY, z);
					BlockState stoneState = level.getBlockState(cursor);
					BlockState oreState = config.getStateToGenerate(stoneState, random, config, x - pos.getX(), y - pos.getY(), z - pos.getZ());
					if (oreState != null) {
						oreState = oxidizeExposed(level, cursor, oreState);
						level.setBlock(cursor, oreState, 3);
					}
				}
			}
		}

	}

	private BlockState oxidizeExposed(WorldGenLevel level, BlockPos.MutableBlockPos cursor, BlockState oreState)
	{
		if(((IGOreBlock)oreState.getBlock()).isRandomlyTicking(oreState))
		{
			if(level.getBlockState(cursor.above()).isAir()||
				level.getBlockState(cursor.below()).isAir()||
				level.getBlockState(cursor.east()).isAir()||
				level.getBlockState(cursor.west()).isAir()||
				level.getBlockState(cursor.north()).isAir()||
				level.getBlockState(cursor.south()).isAir())
			{
				return oreState.setValue(IGOreBlock.OXIDATION, MineralOxidation.OXIDIZED);
			}
		}
		return oreState;
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


	public record IGOreFeatureConfig(IWorldGenConfig entry, long seed, Optional<TagKey<Biome>> biomes) implements FeatureConfiguration
	{
		public static final MapCodec<IGOreFeatureConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
			return instance.group(IWorldGenConfig.CODEC.fieldOf("entry").forGetter((c) -> c.entry),
					Codec.either(Codec.STRING, Codec.LONG).xmap((e) -> e.map(IGOreFeatureConfig::hash, (l) -> l), Either::right).fieldOf("random_name").forGetter((c) -> c.seed),
					CodecHelper.optionalFieldOf(TagKey.hashedCodec(Registries.BIOME), "biomes").forGetter((c) -> c.biomes)).apply(instance, IGOreFeatureConfig::new);
		});

		public int getSize() {
			return getConfig().veinSize.get();
		}

		public static long hash(String name) {
			RandomSupport.Seed128bit seed128 = RandomSupport.seedFromHashOf(name);
			return seed128.seedLo() ^ seed128.seedHi();
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

		public BlockState getStateToGenerate(BlockState stoneState, RandomSource random, IGOreFeatureConfig config, int xFromCenter, int yFromCenter, int zFromCenter)
		{
			IWorldGenConfig mineral = config.entry;
			TagMatchTest validStone = new TagMatchTest(Tags.Blocks.STONE);
			if(!validStone.test(stoneState, random)) return null;
			StoneEnum stone = StoneEnum.selectWorldState(stoneState);
			if(stone == null) {
				return null;
			}
			if(!mineral.instance().acceptableStoneType(stone.instance())) {
				return null;
			}

			// List of blocks for each ore richness
			List<BlockState> blocks = List.of(
					mineral.getOreBlock(stone, OreRichness.POOR).defaultBlockState(),
					mineral.getOreBlock(stone, OreRichness.NORMAL).defaultBlockState(),
					mineral.getOreBlock(stone, OreRichness.RICH).defaultBlockState()
			);

			// Get the size of the ore deposit (it's assumed to be a cubic or spherical region)
			int size = config.getSize();

			// Rarity 0 ~ 100, higher the rarity the more often POOR ore will be in the distribution, the lower the rarity more RICH ore will be in the distribution.
			int rarity = config.getRarity();

			// Calculate the distance from the center (0, 0, 0)
			double distance = Math.sqrt(xFromCenter * xFromCenter + yFromCenter * yFromCenter + zFromCenter * zFromCenter);

			// Normalize the distance based on the size of the deposit
			double normalizedDistance = distance / (size / 2.0);  // Assuming the size is the diameter

			// You can tweak the Gaussian standard deviation factor to control the spread
			double standardDeviation = 0.5; // Lower values give more concentration near the center

			// Calculate the Gaussian probability based on the normalized distance
			double gaussianProbability = Math.exp(-0.5 * (normalizedDistance * normalizedDistance) / (standardDeviation * standardDeviation));

			// Normalize the probability to ensure it's between 0 and 1
			gaussianProbability = Math.max(0.0, Math.min(1.0, gaussianProbability));

			double rarityAdjustment = 0.5 - (rarity / 100.0);

			// Adjust probability based on rarity (scale it toward poorer ore as rarity increases)
			gaussianProbability = Math.max(0.0, Math.min(1.0, gaussianProbability + rarityAdjustment));

			// Now decide which ore block to return based on the probability
			// Use weighted probability based on the Gaussian distribution

			if(gaussianProbability < 0.11)
			{
				return random.nextInt(6) < 5 ? null : blocks.get(0);
			} else if (gaussianProbability < 0.33) {
				return blocks.get(0); // Poor Ore
			} else if (gaussianProbability < 0.66) {
				return blocks.get(1); // Normal Ore
			} else {
				return blocks.get(2); // Rich Ore
			}
		}

		public int getChanceToGenerate(IWorldGenConfig entry)
		{
			IGServerConfig.Ores.OreConfig config = IGServerConfig.ORES.ores.get(entry);
			return config.generationChance.get();
		}

		boolean canSpawnAt(BlockPos pos, Function<BlockPos, Holder<Biome>> biomeQuery) {
			return true;
		}
	}

	protected record Vein(BlockPos pos) {
		protected Vein(BlockPos pos) {
			this.pos = pos;
		}

		public BlockPos pos() {
			return this.pos;
		}
	}
}
