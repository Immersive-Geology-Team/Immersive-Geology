/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world;
import com.igteam.immersivegeology.common.block.IGOreBlock.OreRichness;
import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.common.config.IGServerConfig.Ores.OreConfig;
import com.igteam.immersivegeology.common.world.IGOreFeature.IGOreFeatureConfig;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.feature.vein.IVein;
import net.dries007.tfc.world.feature.vein.IVeinConfig;
import net.dries007.tfc.world.feature.vein.Indicator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.TargetBlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.Tags.Blocks;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

public class IGOreFeature extends Feature<IGOreFeatureConfig>
{
	public IGOreFeature(){
		super(IGOreFeature.IGOreFeatureConfig.CODEC);
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
		this.place(level, random, chunkPos.getMinBlockX(), chunkPos.getMinBlockZ(), pos, config);
		return false;
	}

	protected void place(WorldGenLevel level, RandomSource random, int blockX, int blockZ, BlockPos pos, IGOreFeatureConfig config)
	{
		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
		BoundingBox box = new BoundingBox(pos).inflatedBy(config.getSize());
		int offsetX;
		int offsetZ;
		offsetX = random.nextInt(16) - random.nextInt(16);
		offsetZ = random.nextInt(16) - random.nextInt(16);

		MineralEnum mineral = config.entry;

		int minX = Math.max(blockX, box.minX());
		int maxX = Math.min(blockX + 15, box.maxX());
		int minY = Math.max(mineral.getMinY(), box.minY());
		int maxY = Math.min(mineral.getMaxY(), box.maxY());
		int minZ = Math.max(blockZ, box.minZ());
		int maxZ = Math.min(blockZ + 15, box.maxZ());
		IGServerConfig.Ores.OreConfig cng = IGServerConfig.ORES.ores.get(config.entry);
		int chance = cng.generationChance.get();

		if (chance > random.nextInt(100)) {
			for(int x = minX; x <= maxX; ++x) {
				for(int z = minZ; z <= maxZ; ++z) {
					int projectedY = level.getHeight(Types.OCEAN_FLOOR_WG, offsetX + x, offsetZ + z);
					for(int y = minY; y <= maxY; ++y) {
						cursor.set(x, y + projectedY, z);
						BlockState stoneState = level.getBlockState(cursor);
						BlockState oreState = config.getStateToGenerate(stoneState, random, config, x - pos.getX(), y - pos.getY(), z - pos.getZ());
						if (oreState != null) {
							level.setBlock(cursor, oreState, 3);
						}
					}
				}
			}
		}
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


	public record IGOreFeatureConfig(MineralEnum entry) implements FeatureConfiguration
	{
		public static final Codec<IGOreFeatureConfig> CODEC = RecordCodecBuilder.create((app) -> {
			return app.group(MineralEnum.CODEC.fieldOf("entry").forGetter((cfg) -> cfg.entry)).apply(app, IGOreFeatureConfig::new);
		});

		public int getSize() {
			IGServerConfig.Ores.OreConfig config = IGServerConfig.ORES.ores.get(entry);
			return config.veinSize.get();
		}

		public double getAirExposure() {
			IGServerConfig.Ores.OreConfig config = IGServerConfig.ORES.ores.get(entry);
			return config.airExposure.get();
		}

		public MineralEnum type() {
			return this.entry;
		}

		public BlockState getStateToGenerate(BlockState stoneState, RandomSource random, IGOreFeatureConfig config, int xFromCenter, int yFromCenter, int zFromCenter)
		{

			MineralEnum mineral = config.entry;
			TagMatchTest validStone = new TagMatchTest(Blocks.STONE);
			if(!validStone.test(stoneState, random)) return null;
			StoneEnum stone = StoneEnum.selectWorldState(stoneState);
			if(stone == null) return null;
			if(!mineral.instance().acceptableStoneType(stone.instance())) return null;

			// List of blocks for each ore richness
			List<BlockState> blocks = List.of(
					mineral.getOreBlock(stone, OreRichness.POOR).defaultBlockState(),
					mineral.getOreBlock(stone, OreRichness.NORMAL).defaultBlockState(),
					mineral.getOreBlock(stone, OreRichness.RICH).defaultBlockState()
			);

			// Get the size of the ore deposit (it's assumed to be a cubic or spherical region)
			int size = config.getSize();

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

			// Now decide which ore block to return based on the probability
			// Use weighted probability based on the Gaussian distribution
			if (gaussianProbability < 0.33) {
				return blocks.get(0); // Poor Ore
			} else if (gaussianProbability < 0.66) {
				return blocks.get(1); // Normal Ore
			} else {
				return blocks.get(2); // Rich Ore
			}
		}

		public float getChanceToGenerate(int i, int i1, int i2, MineralEntry entry)
		{
			IGServerConfig.Ores.OreConfig config = IGServerConfig.ORES.ores.get(entry);
			return config.generationChance.get();
		}
	}
}
