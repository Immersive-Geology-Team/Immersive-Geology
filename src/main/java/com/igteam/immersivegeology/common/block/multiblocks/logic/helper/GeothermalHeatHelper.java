/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.logic.helper;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import com.igteam.immersivegeology.common.block.multiblocks.logic.GeothermalExchangerLogic;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GeothermalConversionRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GeothermalExchangerRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class GeothermalHeatHelper
{
	// Heat exchange constants
	private static final int HEAT_EXCHANGE_PER_TICK = 5;
	private static final double LAYER_SWITCH_THRESHOLD = 0.75;
	private static final int HEAT_CONVERSION_THRESHOLD = 100;
	private static final double UPPER_LAYER_CHANCE = 0.1;

	public static class HeatData {
		public int machineHeat;
		public int accumulatedHeatExchange;
		public int currentY;

		public HeatData(int machineHeat, int accumulatedHeatExchange, int currentY) {
			this.machineHeat = machineHeat;
			this.accumulatedHeatExchange = accumulatedHeatExchange;
			this.currentY = currentY;
		}
	}

	public static HeatData updateMachineTemperature(
			Map<Integer, List<BlockPos>> transitionPlanes,
			IMultiblockLevel multiblockLevel,
			HeatData currentData, boolean isCooling) {

		Level level = multiblockLevel.getRawLevel();
		if (transitionPlanes.isEmpty()) {
			int newHeat = Math.round(Mth.lerp(0.1f, currentData.machineHeat, 300));
			return new HeatData(newHeat, currentData.accumulatedHeatExchange, currentData.currentY);
		}

		int targetTemp = getTargetTemperature(transitionPlanes, level, multiblockLevel, isCooling);

		int tempDifference = targetTemp - currentData.machineHeat;
		int newHeat = currentData.machineHeat;
		if (Math.abs(tempDifference) > 1) {
			newHeat += Integer.signum(tempDifference) * Math.min(Math.abs(tempDifference), 10);
		}

		return new HeatData(newHeat, currentData.accumulatedHeatExchange, currentData.currentY);
	}

	public static boolean canProcessRecipe(GeothermalExchangerRecipe recipe, int machineTemp) {
		int inputTemp = recipe.fluidIn.getRandomizedExampleStack(0).getFluid().getFluidType().getTemperature();
		int outputTemp = recipe.fluidOutput.get().getFluid().getFluidType().getTemperature();

		boolean isHeating = outputTemp > inputTemp;
		boolean isCooling = outputTemp < inputTemp;

		if (isHeating) {
			return machineTemp >= outputTemp;
		} else if (isCooling) {
			return machineTemp <= outputTemp;
		}

		return true;
	}

	public static HeatData processRecipeHeatEffects(
			GeothermalExchangerRecipe recipe,
			HeatData currentData,
			Map<Integer, List<BlockPos>> transitionPlanes,
			IMultiblockLevel multiblockLevel) {
		Level level = multiblockLevel.getRawLevel();
		int inputTemp = recipe.fluidIn.getRandomizedExampleStack(0).getFluid().getFluidType().getTemperature();
		int outputTemp = recipe.fluidOutput.get().getFluid().getFluidType().getTemperature();
		int temperatureDelta = outputTemp - inputTemp;
		int consumed = temperatureDelta / 4;

		int newMachineHeat = (int)Math.max(0, currentData.machineHeat);
		int newAccumulated = currentData.accumulatedHeatExchange - consumed;

		return new HeatData(newMachineHeat, newAccumulated, currentData.currentY);
	}

	public static HeatData processBlockConversions(
			Map<Integer, List<BlockPos>> transitionPlanes,
			IMultiblockLevel multiblockLevel,
			HeatData currentData) {
		Level level = multiblockLevel.getRawLevel();
		if (Math.abs(currentData.accumulatedHeatExchange) < HEAT_CONVERSION_THRESHOLD) {
			return currentData; // No changes needed
		}

		boolean needsHeat = currentData.accumulatedHeatExchange < 0;
		boolean hasExcessHeat = currentData.accumulatedHeatExchange > 0;

		HeatData resultData = currentData;

		if (needsHeat) {
			resultData = extractHeatFromBlocks(transitionPlanes, level, multiblockLevel, currentData);
		} else if (hasExcessHeat) {
			resultData = dumpHeatToBlocks(transitionPlanes, level, multiblockLevel, currentData);
		}

		return new HeatData(resultData.machineHeat, 0, resultData.currentY);
	}

	private static HeatData extractHeatFromBlocks(
			Map<Integer, List<BlockPos>> transitionPlanes,
			Level level,
			IMultiblockLevel multiblockLevel,
			HeatData currentData) {

		List<BlockPos> layerBelow = transitionPlanes.get(currentData.currentY-1) != null ? new ArrayList<>(transitionPlanes.get(currentData.currentY-1)) : new ArrayList<>();
		List<BlockPos> currentLayerBlocks = new ArrayList<>(transitionPlanes.get(currentData.currentY));
		Collections.shuffle(currentLayerBlocks);

		int layerConversions = countConvertibleBlocks(currentLayerBlocks, level, multiblockLevel, true);
		int lowerLayerConversions = countConvertibleBlocks(currentLayerBlocks, level, multiblockLevel, true);

		boolean isCurrentLayerComplete = layerConversions == 0;
		boolean isLowerLayerComplete = lowerLayerConversions == 0;

		if (currentLayerBlocks.isEmpty()) {
			int newY = moveToNextLayer(transitionPlanes, currentData.currentY);
			return new HeatData(currentData.machineHeat, currentData.accumulatedHeatExchange, newY);
		}

		if(isCurrentLayerComplete && lowerLayerConversions < (layerBelow.size() / 2))
		{
			int newY = moveToNextLayer(transitionPlanes, currentData.currentY);
			return new HeatData(currentData.machineHeat, currentData.accumulatedHeatExchange, newY);
		}

		if ((isLowerLayerComplete && isCurrentLayerComplete)) {
			int newY = moveToNextLayer(transitionPlanes, currentData.currentY);
			return new HeatData(currentData.machineHeat, currentData.accumulatedHeatExchange, newY);
		} else if(Math.random() < UPPER_LAYER_CHANCE &! isLowerLayerComplete)
		{
			currentLayerBlocks = layerBelow;
			Collections.shuffle(currentLayerBlocks);
		}

		for (BlockPos pos : currentLayerBlocks) {
			if (processBlockCooling(pos, level, multiblockLevel)) {
				int newHeat = currentData.machineHeat + HEAT_EXCHANGE_PER_TICK;
				return new HeatData(newHeat, currentData.accumulatedHeatExchange, currentData.currentY);
			}
		}

		return currentData;
	}

	private static HeatData dumpHeatToBlocks(
			Map<Integer, List<BlockPos>> transitionPlanes,
			Level level,
			IMultiblockLevel multiblockLevel,
			HeatData currentData) {

		List<BlockPos> layerBelow = transitionPlanes.get(currentData.currentY-1) != null ? new ArrayList<>(transitionPlanes.get(currentData.currentY-1)) : new ArrayList<>();
		List<BlockPos> currentLayerBlocks = new ArrayList<>(transitionPlanes.get(currentData.currentY));

		Collections.shuffle(currentLayerBlocks);

		int layerConversions = countConvertibleBlocks(currentLayerBlocks, level, multiblockLevel, false);
		int lowerLayerConversions = countConvertibleBlocks(currentLayerBlocks, level, multiblockLevel, false);

		boolean isCurrentLayerComplete = layerConversions == 0;
		boolean isLowerLayerComplete = lowerLayerConversions == 0;

		if (currentLayerBlocks.isEmpty()) {
			int newY = moveToNextLayer(transitionPlanes, currentData.currentY);
			return new HeatData(currentData.machineHeat, currentData.accumulatedHeatExchange, newY);
		}

		if(isCurrentLayerComplete && lowerLayerConversions < (layerBelow.size() / 2))
		{
			int newY = moveToNextLayer(transitionPlanes, currentData.currentY);
			return new HeatData(currentData.machineHeat, currentData.accumulatedHeatExchange, newY);
		}

		if ((isLowerLayerComplete && isCurrentLayerComplete)) {
			int newY = moveToNextLayer(transitionPlanes, currentData.currentY);
			return new HeatData(currentData.machineHeat, currentData.accumulatedHeatExchange, newY);
		} else if(Math.random() < UPPER_LAYER_CHANCE &! isLowerLayerComplete)
		{
			currentLayerBlocks = layerBelow;
			Collections.shuffle(currentLayerBlocks);
		}

		for (BlockPos pos : currentLayerBlocks) {
			if (processBlockHeating(pos, level, multiblockLevel)) {
				int newHeat = currentData.machineHeat - HEAT_EXCHANGE_PER_TICK;
				return new HeatData(newHeat, currentData.accumulatedHeatExchange, currentData.currentY);
			}
		}

		return currentData;
	}

	private static boolean processBlockCooling(BlockPos pos, Level level, IMultiblockLevel multiblockLevel) {
		GeothermalConversionRecipe recipe = getRecipeForBlock(pos, level, multiblockLevel);
		if (recipe == null || recipe.lowerTransition == null) {
			return false;
		}

		BlockState newState = recipe.lowerTransition.defaultBlockState();
		level.setBlockAndUpdate(multiblockLevel.toAbsolute(pos), newState);
		return true;
	}

	private static boolean processBlockHeating(BlockPos pos, Level level, IMultiblockLevel multiblockLevel) {
		GeothermalConversionRecipe recipe = getRecipeForBlock(pos, level, multiblockLevel);
		if (recipe == null || recipe.upperTransition == null) {
			return false;
		}

		BlockState newState = recipe.upperTransition.defaultBlockState();
		level.setBlockAndUpdate(multiblockLevel.toAbsolute(pos), newState);
		return true;
	}

	private static int countConvertibleBlocks(
			List<BlockPos> blocks,
			Level level,
			IMultiblockLevel multiblockLevel,
			boolean forCooling) {

		int count = 0;
		for (BlockPos pos : blocks) {
			GeothermalConversionRecipe recipe = getRecipeForBlock(pos, level, multiblockLevel);
			if (recipe != null) {
				if (forCooling && recipe.lowerTransition != null) {
					count++;
				} else if (!forCooling && recipe.upperTransition != null) {
					count++;
				}
			}
		}
		return count;
	}

	private static int moveToNextLayer(Map<Integer, List<BlockPos>> transitionPlanes, int currentY) {
		List<Integer> yLevels = new ArrayList<>(transitionPlanes.keySet());

		int currentIndex = yLevels.indexOf(currentY);

		if (currentIndex < yLevels.size() - 1) {
			return yLevels.get(currentIndex + 1);
		}

		return currentY;
	}

	private static int getTargetTemperature(
			Map<Integer, List<BlockPos>> transitionPlanes,
			Level level,
			IMultiblockLevel multiblockLevel, boolean isCooling) {

		int maxTemp = isCooling ? 300 : 1;
		for(List<BlockPos> layerBlocks : transitionPlanes.values())
		{
			if(layerBlocks==null||layerBlocks.isEmpty()) continue;
			for(BlockPos pos : layerBlocks)
			{
				GeothermalConversionRecipe recipe = getRecipeForBlock(pos, level, multiblockLevel);
				if(recipe!=null&& (isCooling ? recipe.blockHeat < maxTemp : recipe.blockHeat > maxTemp))
				{
					maxTemp = recipe.blockHeat;
				}
			}
		}

		return maxTemp;
	}

	private static GeothermalConversionRecipe getRecipeForBlock(
			BlockPos pos,
			Level level,
			IMultiblockLevel multiblockLevel) {

		BlockState state = level.getBlockState(multiblockLevel.toAbsolute(pos));
		Block block = state.getBlock();

		return GeothermalConversionRecipe.findRecipe(level, block);
	}

	public static HeatData syncToMachineState(HeatData heatData, GeothermalExchangerLogic.State machineState) {
		machineState.setHeat(heatData.machineHeat);
		machineState.setAccumulatedHeatExchange(heatData.accumulatedHeatExchange);
		return heatData;
	}

	public static HeatData fromMachineState(GeothermalExchangerLogic.State machineState, int currentY) {
		return new HeatData(
				machineState.getCurrentHeat(),
				machineState.getAccumulatedHeatExchange(),
				currentY
		);
	}
}
