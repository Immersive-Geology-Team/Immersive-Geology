/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world.features.helper;

import com.igteam.immersivegeology.core.material.data.stone.IGStoneTypes;
import com.igteam.immersivegeology.core.material.helper.material.IStoneType;
import com.igteam.immersivegeology.common.block.ore.IGOreBlock;
import com.igteam.immersivegeology.common.block.ore.IGWeatheringOreBlock;
import com.igteam.immersivegeology.common.block.helper.MineralWeathering;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.common.world.IWorldGenConfig;
import com.igteam.immersivegeology.common.world.features.IGOreFeature;
import com.igteam.immersivegeology.common.world.features.IGOreFeature.Vein;
import com.igteam.immersivegeology.common.world.noise.INoise3D;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public class IGOreGenUtils
{
	private static final Direction[] DIRECTIONS = Direction.values();

	public static BlockState getStateToGenerate(BlockState stoneState, double noiseValue, MaterialHelper mineral) {
		IStoneType stone = null;
		if (stoneState.is(Blocks.NETHERRACK)) stone = StoneEnum.MCNetherrack;
		else if (stoneState.is(Blocks.BASALT)) stone = StoneEnum.MCBasalt;
		else if (stoneState.is(Blocks.END_STONE)) stone = StoneEnum.MCEndStone;
		else stone = IGStoneTypes.fromWorldState(stoneState);

		if (stone == null || !stone.isStoneTypeValid() || !mineral.acceptableStoneType(stone.instance())) {
			return null;
		}

		try {
			OreRichness richness = noiseValue > 0.99 ? OreRichness.RICH :
					(noiseValue > 0.7 ? OreRichness.NORMAL : OreRichness.POOR);
			return mineral.getOreBlock(stone, richness).getIGDefaultBlockState();
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean canStateGenerate(BlockState stoneState, MaterialHelper mineral) {
		// Fast path for common blocks
		if (stoneState.is(Blocks.STONE)) return mineral.acceptableStoneType(StoneEnum.MCStone);
		if (stoneState.is(Blocks.NETHERRACK)) return mineral.acceptableStoneType(StoneEnum.MCNetherrack);
		if (stoneState.is(Blocks.BASALT)) return mineral.acceptableStoneType(StoneEnum.MCBasalt);
		if (stoneState.is(Blocks.END_STONE)) return mineral.acceptableStoneType(StoneEnum.MCEndStone);

		IStoneType stone = IGStoneTypes.fromWorldState(stoneState);
		return stone != null && stone.isStoneTypeValid() && mineral.acceptableStoneType(stone.instance());
	}
	public static BlockState oxidizeExposed(LevelAccessor level, BlockPos cursor, BlockState oreState)
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

	public static BlockState handleOxidation(BlockState state, LevelAccessor level, BlockPos adjacentPos, EnumProperty<MineralWeathering> oxidationProperty)
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


	/**
	 * Neighbour offsets for the 3x3 chunk area, centre first and then outwards. The centre chunk sits inside the
	 * vein's full-strength radius, so it should yield viable positions the fastest. Which is what lets
	 * {@link #isVeinWorthwhile} stop scanning early on a vein that is going to pass.
	 */
	private static final int[] NEIGHBOUR_DX = {0, -1, 0, 1, 0, -1, 1, -1, 1};
	private static final int[] NEIGHBOUR_DZ = {0, 0, -1, 0, 1, -1, -1, 1, 1};

	public static float getWorthwhileCount(LevelAccessor level, ChunkPos centerChunk, int maxY, int minY, Vein vein) {
		return worthwhileFraction(level, centerChunk, maxY, minY, vein, Integer.MAX_VALUE);
	}

	/**
	 * The fraction of the 3x3 chunk area, over the vein's Y band, that the vein could actually turn into ore.
	 *
	 * @param stopAt stop counting once this many viable positions have been found and report the fraction reached
	 *               so far. Callers that only need to know whether the count clears a threshold pass that
	 *               threshold; callers that need the true fraction pass {@link Integer#MAX_VALUE}.
	 */
	private static float worthwhileFraction(LevelAccessor level, ChunkPos centerChunk, int maxY, int minY, Vein vein, int stopAt) {
		try
		{
			int lowestY = Math.max(minY, level.getMinBuildHeight());
			int highestY = Math.min(maxY, level.getMaxBuildHeight()-1);
			if(lowestY > highestY)
			{
				return 0;
			}

			int sectionMin = level.getSectionIndex(lowestY);
			int sectionMax = level.getSectionIndex(highestY);
			if(sectionMin < 0)
			{
				throw(new IllegalArgumentException("Section Min is Negative, this should be impossible. \n" +
						"Please report to Immerisve Geology Github. \n" +
						"Include the Dimension and Biome you're in when triggering this message"));
			}

			int totalViableLocations = countViableLocations(level, centerChunk, sectionMin, sectionMax, lowestY, highestY, vein, stopAt);
			int totalBlocks = 48*48*(highestY-lowestY+1);
			return (float)totalViableLocations/totalBlocks;
		} catch(Exception ex)
		{
			if(ex.getMessage() != null)
			{
				IGLib.IG_LOGGER.info("Error in vein estimation: {}", ex.getMessage());
			}
		}
		return 0;
	}

	private static int countViableLocations(LevelAccessor level, ChunkPos centerChunk, int sectionMin, int sectionMax,
											int lowestY, int highestY, Vein vein, int stopAt)
	{
		MaterialHelper veinMaterial = vein.material().instance();
		INoise3D noiseGen = vein.noise();
		int centerX = centerChunk.getMiddleBlockX();
		int centerZ = centerChunk.getMiddleBlockZ();

		// The vein's boundary falloff depends only on the horizontal distance from the centre chunk, so it is the
		// same for every Y in a column. Computing it once per column, instead of once per block position. As a
		// square root each time also lets the sampling skip the corners of the 3x3 area outright: roughly a
		// fifth of the columns sit outside the vein radius, where the falloff is zero and no noise value can ever
		// clear the threshold.
		double[] columnMultiplier = new double[256];
		int totalViableLocations = 0;

		for(int neighbour = 0; neighbour < NEIGHBOUR_DX.length; neighbour++)
		{
			ChunkPos currentChunkPos = new ChunkPos(centerChunk.x+NEIGHBOUR_DX[neighbour], centerChunk.z+NEIGHBOUR_DZ[neighbour]);
			int baseX = currentChunkPos.getMinBlockX();
			int baseZ = currentChunkPos.getMinBlockZ();
			for(int x = 0; x < 16; x++)
			{
				for(int z = 0; z < 16; z++)
				{
					double horizontalDistance = distanceFrom(baseX+x, baseZ+z, centerX, centerZ);
					columnMultiplier[(x<<4)|z] = getBoundaryMultiplication(horizontalDistance, 16.0, 24.0);
				}
			}

			ChunkAccess currentChunk = level.getChunk(currentChunkPos.x, currentChunkPos.z);
			for(int sectionIndex = sectionMin; sectionIndex <= sectionMax; sectionIndex++)
			{
				LevelChunkSection section = currentChunk.getSection(sectionIndex);
				// Ask whether this material can generate in anything the section holds, rather than
				// testing three hardcoded rock tags. A rock type declared in the configuration is in
				// none of them... so the old check would throw away every section it appeared in and the vein
				// was never judged worthwhile, so now we do this to ensure pack developers can have specific
				// stones generate IG ores.
				if(section.hasOnlyAir()||!section.maybeHas(b -> canStateGenerate(b, veinMaterial)))
				{
					continue;
				}

				// Section indices are relative to the world floor; only the height accessor can turn one back
				// into a block coordinate. Sampling the shifted band made this count the stone 64 blocks
				// above where the vein would land, so isVeinWorthwhile rejected veins that were fine.
				int sectionMinY = SectionPos.sectionToBlockCoord(level.getSectionYFromSectionIndex(sectionIndex));
				int fromY = Math.max(sectionMinY, lowestY);
				int toY = Math.min(sectionMinY+16, highestY+1);

				for(int y = fromY; y < toY; y++)
				{
					for(int x = 0; x < 16; x++)
					{
						for(int z = 0; z < 16; z++)
						{
							double multiplier = columnMultiplier[(x<<4)|z];
							if(multiplier <= 0)
							{
								continue;
							}
							double noiseValue = noiseGen.noise(baseX+x, y, baseZ+z)*multiplier;
							if(noiseValue <= IGOreFeature.THRESHOLD)
							{
								continue;
							}
							// The section is already in hand, so read through it rather than making the chunk
							// resolve the section again for every position.
							BlockState state = section.getBlockState(x, y&15, z);
							if(canStateGenerate(state, veinMaterial)||state.getBlock() instanceof IGOreBlock)
							{
								if(++totalViableLocations >= stopAt)
								{
									return totalViableLocations;
								}
							}
						}
					}
				}
			}
		}
		return totalViableLocations;
	}

	public static double noise(ChunkPos pos, int x, int y, int z, @NotNull Vein vein, ChunkPos centerChunkPos) {
		return noise(pos.getMinBlockX()+x, y, pos.getMinBlockZ()+z, vein, centerChunkPos);
	}

	/**
	 * Vein density at an absolute world position. Depends only on the world position, the vein's noise and the
	 * chunk the vein is centred on, so it can be evaluated without touching (or generating) any chunk.
	 */
	public static double noise(int worldX, int worldY, int worldZ, @NotNull Vein vein, ChunkPos centerChunkPos) {
		INoise3D noiseGen = vein.noise();

		double horizontalDistance = distanceFrom(worldX, worldZ, centerChunkPos.getMiddleBlockX(), centerChunkPos.getMiddleBlockZ());

		double radius = 24.0;
		double outerThreshold = 16.0;
		double boundaryMultiplication = getBoundaryMultiplication(horizontalDistance, outerThreshold, radius);

		return noiseGen.noise(worldX, worldY, worldZ) * boundaryMultiplication;
	}

	/**
	 * Horizontal distance from a vein centre. Math.sqrt rather than Math.hypot: the offsets here are whole blocks,
	 * where the two agree exactly, hypot did some extra stuff that isn't needed anymore.
	 */
	private static double distanceFrom(int worldX, int worldZ, int centerX, int centerZ)
	{
		double dx = worldX-centerX;
		double dz = worldZ-centerZ;
		return Math.sqrt(dx*dx+dz*dz);
	}

	private static double getBoundaryMultiplication(double horizontalDistance, double outerThreshold, double radius)
	{
		double middleThreshold = 20.0; // 4 blocks from edge (24-4=20)

		double boundaryMultiplication = 1.0;

		if (horizontalDistance > outerThreshold) {
			if (horizontalDistance > middleThreshold) {
				double t = (horizontalDistance- middleThreshold) / (radius- middleThreshold);
				boundaryMultiplication = 0.5 * (1.0 - t);
			} else {
				double t = (horizontalDistance-outerThreshold) / (middleThreshold -outerThreshold);
				boundaryMultiplication = 0.75 - 0.25 * t;
			}
		}

		boundaryMultiplication = Math.max(0.0, boundaryMultiplication);
		return boundaryMultiplication;
	}

	public static boolean isVeinWorthwhile(LevelAccessor level, ChunkPos chunk, int maxY, int minY, Vein vein)
	{
		// The gate is one percent of the 3x3 area, so there is no reason to keep sampling once the count is clear
		// of it. The center chunk is scanned first, where the vein is at full strength and reaches that one
		// percent faster than other areas.
		int lowestY = Math.max(minY, level.getMinBuildHeight());
		int highestY = Math.min(maxY, level.getMaxBuildHeight()-1);
		int totalBlocks = 48*48*(highestY-lowestY+1);
		int stopAt = lowestY > highestY?Integer.MAX_VALUE: (int)Math.ceil(totalBlocks/100.0)+1;

		float totalViableLocations = worthwhileFraction(level, chunk, maxY, minY, vein, stopAt);
		return Math.floor(totalViableLocations * 100) > 0f;
	}

	public static RandomSource getReuseRandom(IWorldGenConfig material, long level_seed, ChunkPos position) {
		return new XoroshiroRandomSource(
				(level_seed ^ position.x) * 61728364132L,
				(material.seed() ^ position.z) * 16298364123L
		);
	}
}
