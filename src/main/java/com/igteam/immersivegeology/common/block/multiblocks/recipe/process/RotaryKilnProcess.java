/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.process;

import blusunrize.immersiveengineering.api.crafting.ArcFurnaceRecipe;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.arcfurnace.ArcFurnaceLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInMachine;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext.ProcessContextInMachine;
import com.igteam.immersivegeology.common.block.multiblocks.logic.RotaryKilnLogic;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RotaryKilnRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.BiFunction;

public class RotaryKilnProcess extends MultiblockProcessInMachine<RotaryKilnRecipe>
{
	int slot = 1;
    public RotaryKilnProcess(BiFunction< Level, ResourceLocation, RotaryKilnRecipe > getRecipe, CompoundTag data) {
		super(getRecipe, data);
		this.slot = data.getInt("slot_index");
	}

    public RotaryKilnProcess(RotaryKilnRecipe recipe, int slot) {
		super(recipe);
		this.slot = slot;
	}

	public void writeExtraDataToNBT(CompoundTag nbt) {
		super.writeExtraDataToNBT(nbt);
		nbt.putInt("slot_index", slot);
	}

	protected List<ItemStack> getRecipeItemOutputs(Level level, ProcessContext.ProcessContextInMachine<RotaryKilnRecipe> context) {
		RotaryKilnRecipe recipe = this.getRecipe(level);
		if (recipe == null) {
			return NonNullList.create();
		} else {
			ItemStack input = context.getInventory().getStackInSlot(slot);
			recipe = RotaryKilnRecipe.findRecipe(level, input);
			if(recipe == null) return NonNullList.create();
			return recipe.getItemOutputs();
		}
	}

	@Override
	public void doProcessTick(ProcessContextInMachine<RotaryKilnRecipe> context, IMultiblockLevel level)
	{
		RotaryKilnRecipe recipe = getRecipe(level.getRawLevel());
		if(context instanceof RotaryKilnLogic.State state && recipe != null)
		{
			int recipeHeat = recipe.getHeatRequired();
			boolean canProcess = state.hasRequiredHeat(recipeHeat);
			if(canProcess)
			{
				super.doProcessTick(context, level);
			}
		}
	}

	public int getSlot()
	{
		return slot;
	}

	protected void processFinish(ProcessContext.ProcessContextInMachine<RotaryKilnRecipe> context, IMultiblockLevel level) {
		super.processFinish(context, level);
	}
}
