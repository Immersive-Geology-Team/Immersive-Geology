package com.igteam.immersivegeology.common.block.multiblocks.shim.process;

import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shim.env.IMultiblockLevel;
import net.minecraft.nbt.NBTTagCompound;

public abstract class MultiblockProcess<R extends MultiblockRecipe, CTX extends ProcessContext<R>>
{
	protected final R recipe;

	public int processTick;
	public boolean clearProcess;

	protected MultiblockProcess(R recipe)
	{
		this.recipe = recipe;
	}

	public R getRecipe()
	{
		return recipe;
	}

	public int getMaxTicks()
	{
		return Math.max(1, recipe.getTotalProcessTime());
	}

	public int getEnergyPerTick()
	{
		return Math.max(0, recipe.getTotalProcessEnergy()/getMaxTicks());
	}

	public boolean canProcess(CTX context)
	{
		return context.getEnergy().getEnergyStored() >= getEnergyPerTick();
	}

	public void doProcessTick(CTX context, IMultiblockLevel level)
	{
		int required = getEnergyPerTick();
		if(required > 0&&context.getEnergy().consumeEnergy(required) < required) return;

		processTick++;
		if(processTick >= getMaxTicks()) processFinish(context, level);
	}

	protected abstract void processFinish(CTX context, IMultiblockLevel level);

	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("processTick", processTick);
		writeExtraDataToNBT(nbt);
	}

	public void readFromNBT(NBTTagCompound nbt)
	{
		this.processTick = nbt.getInteger("processTick");
	}

	protected void writeExtraDataToNBT(NBTTagCompound nbt)
	{
	}
}
