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
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor.InMachineProcessor;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext.ProcessContextInMachine;
import com.igteam.immersivegeology.common.block.multiblocks.logic.SmallChemicalReactorLogic;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BasicChemicalRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class BasicChemicalProcessor extends InMachineProcessor<BasicChemicalRecipe>
{
	public BasicChemicalProcessor(int maxQueueLength, float minDelayAfter, int maxProcessPerTick, Runnable markDirty, BiFunction<Level, ResourceLocation, @Nullable BasicChemicalRecipe> getRecipeFromID)
	{
		super(maxQueueLength, minDelayAfter, maxProcessPerTick, markDirty, getRecipeFromID);
	}

	@Override
	public boolean tickServer(ProcessContextInMachine<BasicChemicalRecipe> context, IMultiblockLevel level, boolean canWork)
	{
		Level rawLevel = level.getRawLevel();
		if(context instanceof SmallChemicalReactorLogic.State state && rawLevel.getGameTime() % 20 == 0)
		{
			List<MultiblockProcess<BasicChemicalRecipe, ProcessContextInMachine<BasicChemicalRecipe>>> queue = getQueue();
			if (queue == null || queue.isEmpty()) {
				return false;
			}

			Optional<MultiblockProcess<BasicChemicalRecipe, ProcessContextInMachine<BasicChemicalRecipe>>> optionalElement = queue.stream().findFirst();
			MultiblockProcess<BasicChemicalRecipe, ProcessContextInMachine<BasicChemicalRecipe>> element = optionalElement.get();
			BasicChemicalRecipe current_recipe = element.getRecipe(rawLevel);
			if(current_recipe == null) return false;
			int damage_per_second = current_recipe.getDamagePerTick();
			state.damage += damage_per_second;
		}
		return super.tickServer(context, level, canWork);
	}
}
