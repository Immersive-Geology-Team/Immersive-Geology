/*
 * BluSunrize
 * Copyright (c) 2024
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.logic.helper;

import com.igteam.immersivegeology.common.block.multiblocks.recipe.GravitySeparatorRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.Random;

public class SeparatorProcess
{
	private final ItemStack input;
	private RecipeDependentData recipeDependentData;
	private int processTick;
	private boolean processFinished = false;

	public SeparatorProcess(ItemStack input)
	{
		this.input = input;
	}

	private RecipeDependentData getRecipeDependentData(Level level)
	{
		if(this.recipeDependentData == null)
		{
			GravitySeparatorRecipe recipe = GravitySeparatorRecipe.findRecipe(level, input);
			if(recipe!= null)
			{
				this.recipeDependentData = new RecipeDependentData(recipe, recipe.getTotalProcessTime(), recipe.getTotalProcessWater() / recipe.getTotalProcessTime());
			}
		} else if(this.recipeDependentData.recipe == null) this.recipeDependentData = null;
		return this.recipeDependentData;
	}

	public boolean processStep(Level level, FluidTank tank)
	{
		RecipeDependentData data = getRecipeDependentData(level);
		if(data == null) return false;
		if(tank.drain(data.waterPerTick, FluidAction.SIMULATE).getAmount() < data.waterPerTick) return false;
		if(recipeDependentData.recipe == null) return false;
		tank.drain(data.waterPerTick, FluidAction.EXECUTE);
		this.processTick++;
		float relative = getRelativeProcessStep(level);
		if(relative >= 1) this.processFinished = true;
		return true;
	}

	public ItemStack getCurrentOutput()
	{
		if(recipeDependentData.recipe != null) return recipeDependentData.recipe.itemOutput.get();
		return ItemStack.EMPTY;
	}

	public ItemStack getCurrentByproduct()
	{
		if(recipeDependentData.recipe != null) return recipeDependentData.recipe.itemByproduct.get();
		return ItemStack.EMPTY;
	}

	public CompoundTag writeToNBT()
	{
		CompoundTag nbt = new CompoundTag();
		nbt.put("input", this.input.save(new CompoundTag()));
		nbt.putInt("processTick", this.processTick);
		return nbt;
	}

	public static SeparatorProcess readFromNBT(CompoundTag nbt)
	{
		ItemStack input = ItemStack.of(nbt.getCompound("input"));
		SeparatorProcess process = new SeparatorProcess(input);
		process.processTick = nbt.getInt("processTick");
		return process;
	}

	public boolean isProcessFinished()
	{
		return processFinished;
	}

	public ItemStack getInput()
	{
		return input;
	}

	public void incrementProcessOnClient()
	{
		++this.processTick;
	}

	public float getRelativeProcessStep(Level level)
	{
		return this.processTick/getRecipeDependentData(level).maxProcessTicks;
	}

	public boolean outputByproduct()
	{
		Random rand = new Random();
		return this.recipeDependentData.recipe!=null && (this.recipeDependentData.recipe.getChance() > rand.nextFloat());
	}

	private record RecipeDependentData(GravitySeparatorRecipe recipe, float maxProcessTicks, int waterPerTick)
	{
	}
}
