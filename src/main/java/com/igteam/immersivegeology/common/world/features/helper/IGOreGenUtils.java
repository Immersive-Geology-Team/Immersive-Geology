/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world.features.helper;

import com.igteam.immersivegeology.common.block.IGOreBlock;
import com.igteam.immersivegeology.common.block.IGWeatheringOreBlock;
import com.igteam.immersivegeology.common.block.helper.MineralWeathering;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.common.world.IWorldGenConfig;
import com.igteam.immersivegeology.common.world.features.IGOreFeature;
import com.igteam.immersivegeology.common.world.features.IGOreFeature.Vein;
import com.igteam.immersivegeology.common.world.noise.INoise3D;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

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
			return mineral.getOreBlock(stone, richness).getDefaultBlockState();
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

		// Try to match other stone types
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


	public static float getWorthwhileCount(LevelAccessor level, ChunkPos centerChunk, int maxY, int minY, Vein vein, @Nullable Graphics2D g2d) {
		int totalViableLocations = 0;
		// Use the same 3x3 chunk area approach
		for (int chunkDX = -1; chunkDX <= 1; chunkDX++) {
			for (int chunkDZ = -1; chunkDZ <= 1; chunkDZ++) {
				ChunkPos currentChunkPos = new ChunkPos(centerChunk.x + chunkDX, centerChunk.z + chunkDZ);
				ChunkAccess currentChunk = level.getChunk(currentChunkPos.x, currentChunkPos.z);

				// Calculate section indices
				int minSection = level.getSectionIndex(minY);
				int maxSection = level.getSectionIndex(maxY - 1);

				for (int sectionY = minSection; sectionY <= maxSection; sectionY++) {
					LevelChunkSection section = currentChunk.getSection(sectionY);

					// Skip if section is empty or doesn't have potential viable blocks
					if (section.hasOnlyAir()) {
						continue;
					}

					// Calculate Y bounds for this section
					int sectionMinY = Math.max(sectionY * 16, minY);
					int sectionMaxY = Math.min((sectionY + 1) * 16, maxY);

					// Process this section to count viable locations
					totalViableLocations += countViableLocationsInSection(section,
							currentChunk, sectionMinY, sectionMaxY, vein, g2d
					);
				}
			}
		}

		// Draw the overall area if graphics are provided
		if (g2d != null) {
			BlockPos centerPos = centerChunk.getMiddleBlockPosition(0);
			centerPos = centerPos.offset(-24, 0, -24);
			int mapX = (centerPos.getX() + (64 * 16) / 2) % (64 * 16);
			int mapZ = (centerPos.getZ() + (64 * 16) / 2) % (64 * 16);
			g2d.setColor(Color.DARK_GRAY);
			g2d.setStroke(new BasicStroke(1));
			g2d.drawRect(mapX, mapZ, 48, 48);
			g2d.drawString(vein.material().name(), mapX, mapZ+12);
		}
		// Calculate the total volume of blocks in the 3x3 chunk area within the Y range
		int totalBlocks = 48 * 48 * (Math.abs(maxY - minY));
		return (float) totalViableLocations / totalBlocks;
	}

	private static int countViableLocationsInSection(LevelChunkSection section, ChunkAccess chunk,
			int minY, int maxY, Vein vein, @Nullable Graphics2D g2d) {

		int viablePositions = 0;
		ChunkPos chunkPos = chunk.getPos();

		for (int y = minY; y < maxY; y++) {
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					BlockPos pos = chunkPos.getBlockAt(x,y,z);
					double noiseValue = IGOreGenUtils.noise(chunkPos, x,y,z, vein);
					if (noiseValue > IGOreFeature.THRESHOLD) {
						BlockState state = chunk.getBlockState(new BlockPos(x,y,z));
						boolean viable = state.is(Tags.Blocks.STONE) || state.is(Tags.Blocks.END_STONES) || state.is(Tags.Blocks.NETHERRACK) || (state.getBlock() instanceof IGOreBlock);
						if (viable) {
							viablePositions += 1;
							// Visualization if g2d is provided
							if (g2d != null) {
								// Calculate map position (assuming 2D top-down view at this Y level)
								// MAP Must be 64x64 chunks.
								int mapX = (pos.getX() + 512) % 1024;
								int mapZ = (pos.getZ() + 512) % 1024;
								g2d.setColor(new Color(0, 0, 200, 30));
								g2d.fillRect(mapX, mapZ, 1, 1);
								g2d.setColor(Color.DARK_GRAY);
							}
						}
					}
				}
			}
		}


		return viablePositions;
	}

	public static double noise(ChunkPos pos, int x, int y, int z, @NotNull Vein vein) {
		INoise3D noiseGen = vein.noise();
		BlockPos b = pos.getBlockAt(x,y,z);
		return noiseGen.noise(b);
	}

	public static boolean isVeinWorthwhile(LevelAccessor level, ChunkPos chunk, int maxY, int minY, Vein vein, @Nullable Graphics2D g2d)
	{
		float totalViableLocations = getWorthwhileCount(level, chunk, maxY, minY, vein, g2d);
		return Math.floor(totalViableLocations * 100) > 0f;
	}

	public static RandomSource getReuseRandom(IWorldGenConfig material, long level_seed, ChunkPos position) {
		return new XoroshiroRandomSource(
				(level_seed ^ position.x) * 61728364132L,
				(material.seed() ^ position.z) * 16298364123L
		);
	}
}
