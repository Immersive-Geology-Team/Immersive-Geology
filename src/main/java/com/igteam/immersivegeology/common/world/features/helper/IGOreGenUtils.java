/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world.features.helper;

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
		StoneEnum stone = null;
		if (stoneState.is(Blocks.NETHERRACK)) stone = StoneEnum.MCNetherrack;
		else if (stoneState.is(Blocks.BASALT)) stone = StoneEnum.MCBasalt;
		else if (stoneState.is(Blocks.END_STONE)) stone = StoneEnum.MCEndStone;
		else stone = StoneEnum.selectWorldState(stoneState);

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

		StoneEnum stone = StoneEnum.selectWorldState(stoneState);
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


	static TagKey<Block> stoneTag = Tags.Blocks.STONE;
	static TagKey<Block> endStoneTag = Tags.Blocks.END_STONES;
	static TagKey<Block> netherackTag = Tags.Blocks.NETHERRACK;
	public static float getWorthwhileCount(LevelAccessor level, ChunkPos centerChunk, int maxY, int minY, Vein vein) {
		int totalViableLocations = 0;
		// Use the same 3x3 chunk area approach
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
						"Please report to Immerisve Geology Github or Discord. \n" +
						"Include the Dimension and Biome you're in when triggering this message"));
			}
			for(int chunkDX = -1; chunkDX <= 1; chunkDX++)
			{
				for(int chunkDZ = -1; chunkDZ <= 1; chunkDZ++)
				{
					ChunkPos currentChunkPos = new ChunkPos(centerChunk.x+chunkDX, centerChunk.z+chunkDZ);
					ChunkAccess currentChunk = level.getChunk(currentChunkPos.x, currentChunkPos.z);
					for(int sectionIndex = sectionMin; sectionIndex <= sectionMax; sectionIndex++)
					{
						LevelChunkSection section = currentChunk.getSection(sectionIndex);
						if(section.hasOnlyAir()||!section.maybeHas(b -> b.is(stoneTag)||b.is(netherackTag)||b.is(endStoneTag)))
						{
							continue;
						}

						int sectionMinY = SectionPos.sectionToBlockCoord(sectionIndex);
						int fromY = Math.max(sectionMinY, lowestY);
						int toY = Math.min(sectionMinY+16, highestY+1);

						totalViableLocations += countViableLocationsInSection(currentChunk, fromY, toY, vein, centerChunk);
					}
				}
			}

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
	private static int countViableLocationsInSection(ChunkAccess chunk,int minY, int maxY, Vein vein, ChunkPos centreChunk) {
		// Kept local: world generation calls this from several worker threads at once.
		BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
		int viablePositions = 0;
		ChunkPos chunkPos = chunk.getPos();
		MaterialHelper mineral = vein.material().instance();
		for (int y = minY; y < maxY; y++) {
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					double noiseValue = IGOreGenUtils.noise(chunkPos, x,y,z, vein, centreChunk);
					if (noiseValue > IGOreFeature.THRESHOLD) {
						mutablePos.set(x,y,z);
						BlockState state = chunk.getBlockState(mutablePos);
						boolean viable = canStateGenerate(state, mineral) || state.getBlock() instanceof IGOreBlock;
						if (viable) {
							viablePositions++;
						}
					}
				}
			}
		}
		return viablePositions;
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
		BlockPos middleBlockPosition = centerChunkPos.getMiddleBlockPosition(0);

		double dx = worldX - middleBlockPosition.getX();
		double dz = worldZ - middleBlockPosition.getZ();
		double horizontalDistance = Math.hypot(dx, dz);

		double radius = 24.0;
		double outerThreshold = 16.0;
		double boundaryMultiplication = getBoundaryMultiplication(horizontalDistance, outerThreshold, radius);

		return noiseGen.noise(worldX, worldY, worldZ) * boundaryMultiplication;
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
		float totalViableLocations = getWorthwhileCount(level, chunk, maxY, minY, vein);
		return Math.floor(totalViableLocations * 100) > 0f;
	}

	public static RandomSource getReuseRandom(IWorldGenConfig material, long level_seed, ChunkPos position) {
		return new XoroshiroRandomSource(
				(level_seed ^ position.x) * 61728364132L,
				(material.seed() ^ position.z) * 16298364123L
		);
	}
}
