/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.logic.helper;


import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import com.igteam.immersivegeology.common.block.multiblocks.logic.GeothermalExchangerLogic;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GeothermalConversionRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class GeothermalHeatHelper
{
	private static final int LAYER_COUNT = 5;
	private static final int GRID_WIDTH = 5;
	private static final int GRID_LENGTH = 3;
	private static final int EMPTY_VALUE = 0;

	private final boolean[][][] markedCells;
	private byte[][][] data;
	private final Supplier<Level> level;
	private List<GeothermalConversionRecipe> cachedRecipes;
	private final Random random;

	public GeothermalHeatHelper(Supplier<Level> level)
	{
		this.data = new byte[LAYER_COUNT][GRID_WIDTH][GRID_LENGTH];
		this.markedCells = new boolean[LAYER_COUNT][GRID_WIDTH][GRID_LENGTH];
		this.level = level;
		this.random = new Random();
		clear();
		clearMarks();
	}

	private void ensureRecipesLoaded() {
		if (cachedRecipes == null) {
			Level currentLevel = level.get();
			if (currentLevel != null) {
				cachedRecipes = new ArrayList<>(GeothermalConversionRecipe.RECIPES.getRecipes(currentLevel));
			}
		}
	}

	public List<GeothermalConversionRecipe> getRecipes() {
		ensureRecipesLoaded();
		return cachedRecipes != null ? cachedRecipes : Collections.emptyList();
	}

	public void clear() {
		for (int layer = 0; layer < LAYER_COUNT; layer++) {
			for (int x = 0; x < GRID_WIDTH; x++) {
				for (int y = 0; y < GRID_LENGTH; y++) {
					data[layer][x][y] = EMPTY_VALUE;
				}
			}
		}
	}

	public void clearMarks() {
		for (int layer = 0; layer < LAYER_COUNT; layer++) {
			for (int x = 0; x < GRID_WIDTH; x++) {
				for (int y = 0; y < GRID_LENGTH; y++) {
					markedCells[layer][x][y] = false;
				}
			}
		}
	}

	public void clearLayer(int layer) {
		if (layer < 0 || layer >= LAYER_COUNT) {
			return;
		}

		for (int x = 0; x < GRID_WIDTH; x++) {
			for (int y = 0; y < GRID_LENGTH; y++) {
				data[layer][x][y] = EMPTY_VALUE;
			}
		}
	}

	public byte[] getLayerCopy(int layer) {
		if (layer < 0 || layer >= LAYER_COUNT) {
			return null;
		}

		byte[] copy = new byte[GRID_WIDTH * GRID_LENGTH];
		int index = 0;
		for (int x = 0; x < GRID_WIDTH; x++) {
			for (int y = 0; y < GRID_LENGTH; y++) {
				copy[index++] = data[layer][x][y];
			}
		}
		return copy;
	}

	public byte accessDataAtLayer(int layer, int x, int z) {
		if (layer < 0 || layer >= LAYER_COUNT ||
				x < 0 || x >= GRID_WIDTH ||
				z < 0 || z >= GRID_LENGTH) {
			return EMPTY_VALUE;
		}

		return data[layer][x][z];
	}
	public void setDataAtLayer(int layer, int x, int z, byte value) {
		if (layer < 0 || layer >= LAYER_COUNT ||
				x < 0 || x >= GRID_WIDTH ||
				z < 0 || z >= GRID_LENGTH) {
			return;
		}

		data[layer][x][z] = value;
	}

	public byte getFastPseudoRandomCell(int layer, long seed) {
		if (layer < 0 || layer >= 5) return 0;

		// Simple hash-based coordinate generation
		long hash = seed * 31 + layer;
		int x = (int)((hash >>> 16) % 3);
		int y = (int)((hash >>> 8) % 5);

		return accessDataAtLayer(layer, Math.abs(x), Math.abs(y));
	}

	public byte getRandomNonEmptyCellCoords(int layer, int[] outCoords) {
		if (layer < 0 || layer >= LAYER_COUNT || outCoords.length < 2) return -1;

		// Count non-empty AND non-marked cells first
		int availableCount = 0;
		for (int x = 0; x < GRID_WIDTH; x++) {
			for (int z = 0; z < GRID_LENGTH; z++) {
				if (accessDataAtLayer(layer, x, z) != EMPTY_VALUE && !markedCells[layer][x][z]) {
					availableCount++;
				}
			}
		}

		// If no available cells, return -1
		if (availableCount == 0) return -1;

		// Pick random index among available cells
		int targetIndex = random.nextInt(availableCount);
		int currentIndex = 0;

		// Find the cell at that index
		for (int x = 0; x < GRID_WIDTH; x++) {
			for (int z = 0; z < GRID_LENGTH; z++) {
				byte value = accessDataAtLayer(layer, x, z);
				if (value != EMPTY_VALUE && !markedCells[layer][x][z]) {
					if (currentIndex == targetIndex) {
						markedCells[layer][x][z] = true;
						outCoords[0] = x;
						outCoords[1] = z;
						return value;
					}
					currentIndex++;
				}
			}
		}

		return -1; // Should never reach here, but safety fallback
	}

	public @Nullable GeothermalConversionRecipe getRandomCellPosition(GeothermalExchangerLogic.State state, MutableBlockPos localPos)
	{
		int[] xz = {0,0};
		int id = getRandomNonEmptyCellCoords(localPos.getY(), xz);
		if(id == -1)
		{
			state.currentY = state.currentY-1;
			localPos.setY(state.currentY);
		}
		localPos.setX(xz[0]);
		localPos.setZ(xz[1]);

		id = id - 1;
		return id < 0 ? null : getRecipes().get(id);
	}

	public @Nullable GeothermalConversionRecipe getRecipeFromCell(BlockPos localPosition) {
		return getRecipeFromCell(localPosition.getY(), localPosition.getX(), localPosition.getZ());
	}

	public @Nullable GeothermalConversionRecipe getRecipeFromCell(int layer, int x, int z) {
		int id = accessDataAtLayer(layer, x, z)-1;
		return id < 0 ? null : getRecipes().get(id);
	}

	public GeothermalConversionRecipe updateRecipeCell(IMultiblockLevel multiblockLevel, BlockPos pos) {
		return updateRecipeCell(multiblockLevel, pos.getY(), pos.getX(), pos.getZ());
	}

	public GeothermalConversionRecipe updateRecipeCell(IMultiblockLevel multiblockLevel, int layer, int x, int z) {
		Level rawLevel = multiblockLevel.getRawLevel();
		MutableBlockPos localPos = new MutableBlockPos(x, layer, z);
		BlockPos worldPos = multiblockLevel.toAbsolute(localPos);
		BlockState block = rawLevel.getBlockState(worldPos);
		GeothermalConversionRecipe recipe = GeothermalConversionRecipe.findRecipe(rawLevel, block.getBlock());
		int recipeIndex = 1 + getRecipes().indexOf(recipe);
		if(recipeIndex > 255) IGLib.IG_LOGGER.error("Could not set Recipe Marker Correctly, recipe ID exceeds 255 (Undefined Behaviour is likely to occur)");
		setDataAtLayer(layer, x, z, (byte) recipeIndex);
		return recipe;
	}

	public void setupRecipeData(IMultiblockLevel multiblockLevel)
	{
		Level rawLevel = multiblockLevel.getRawLevel();
		MutableBlockPos localPos = new MutableBlockPos(0,0,0);
		BlockState block;
		for (int layer = 0; layer < LAYER_COUNT; layer++) {
			for (int x = 0; x < GRID_WIDTH; x++) {
				for (int z = 0; z < GRID_LENGTH; z++) {
					localPos.set(x, layer, z);
					block = rawLevel.getBlockState(multiblockLevel.toAbsolute(localPos).below());
					GeothermalConversionRecipe recipe = findRecipe(block.getBlock());
					int recipeIndex = 1 + getRecipes().indexOf(recipe);
					data[layer][x][z] = (byte) recipeIndex;
				}
			}
		}
	}

	public GeothermalConversionRecipe findRecipe(Block block)
	{
		for(GeothermalConversionRecipe recipe : getRecipes())
			if(recipe.transitionBlock.get().equals(block))
				return recipe;
		return null;
	}

	public @Nullable GeothermalConversionRecipe findCellWithHeat(boolean cooling, int outputTemp)
	{
		GeothermalConversionRecipe returnRecipe;
		int temperature = 1;
		for (int layer = 0; layer < LAYER_COUNT; layer++)
		{
			for(int x = 0; x < GRID_WIDTH; x++)
			{
				for(int y = 0; y < GRID_LENGTH; y++)
				{
					int id = data[layer][x][y]-1;
					if(id < 0) continue;
					returnRecipe = getRecipes().get(id);
					if(returnRecipe==null) continue;
					int blockHeat = returnRecipe.blockHeat;
					if(cooling&&blockHeat <= outputTemp)
					{
						return returnRecipe;
					}
					if(!cooling&&blockHeat >= outputTemp)
					{
						return returnRecipe;
					}
				}
			}
		}

		return null;
	}

	public void fromNBT(CompoundTag tag)
	{
		CompoundTag nbt = tag.getCompound("helper");
		byte[] flatData = nbt.getByteArray("data");
		data = reconstruct3DArray(flatData, LAYER_COUNT, GRID_WIDTH, GRID_LENGTH);
		clearMarks();
		cachedRecipes = null;
	}

	public Tag toNBT()
	{
		CompoundTag tag_data = new CompoundTag();
		byte[] flatData = flatten3DArray(data);
		tag_data.putByteArray("data", flatData);

		return tag_data;
	}

	public static byte[] flatten3DArray(byte[][][] array3D) {
		if (array3D == null || array3D.length == 0) {
			return new byte[0];
		}

		int dim1 = array3D.length;
		int dim2 = array3D[0].length;
		int dim3 = array3D[0][0].length;

		int totalDataSize = dim1 * dim2 * dim3;
		byte[] result = new byte[totalDataSize];

		int index = 0;
		for (int i = 0; i < dim1; i++) {
			for (int j = 0; j < dim2; j++) {
				for (int k = 0; k < dim3; k++) {
					result[index++] = array3D[i][j][k];
				}
			}
		}

		return result;
	}

	/**
	 * Reconstructs a 3D byte array from a flattened 1D byte array
	 */
	public static byte[][][] reconstruct3DArray(byte[] flatArray, int dim1, int dim2, int dim3) {
		if (flatArray == null || dim1 == 0 || dim2 == 0 || dim3 == 0) {
			return new byte[0][0][0];
		}

		// Create the 3D array
		byte[][][] result = new byte[dim1][dim2][dim3];

		// Fill the array
		int index = 0;
		for (int i = 0; i < dim1; i++) {
			for (int j = 0; j < dim2; j++) {
				for (int k = 0; k < dim3; k++) {
					result[i][j][k] = flatArray[index++];
				}
			}
		}

		return result;
	}
}
