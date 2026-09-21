package com.igteam.immersivegeology.common.block.multiblocks.shim.process;

import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shim.env.IMultiblockLevel;
import com.igteam.immersivegeology.common.block.multiblocks.shim.process.ProcessContext.ProcessContextInWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class MultiblockProcessInWorld<R extends MultiblockRecipe> extends MultiblockProcess<R, ProcessContextInWorld<R>>
{
	private ItemStack inputItem;

	public MultiblockProcessInWorld(R recipe, ItemStack inputItem)
	{
		super(recipe);
		this.inputItem = inputItem==null?ItemStack.EMPTY: inputItem.copy();
	}

	public ItemStack getInputItem()
	{
		return inputItem;
	}

	@Override
	protected void processFinish(ProcessContextInWorld<R> context, IMultiblockLevel level)
	{
		for(ItemStack output : recipe.getItemOutputs())
			if(!output.isEmpty()) context.doProcessOutput(output.copy(), level);
		clearProcess = true;
	}

	@Override
	protected void writeExtraDataToNBT(NBTTagCompound nbt)
	{
		if(!inputItem.isEmpty()) nbt.setTag("inputItem", inputItem.writeToNBT(new NBTTagCompound()));
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.inputItem = nbt.hasKey("inputItem")
				?new ItemStack(nbt.getCompoundTag("inputItem"))
				: ItemStack.EMPTY;
	}
}
