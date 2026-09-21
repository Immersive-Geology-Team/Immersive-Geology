package com.igteam.immersivegeology.common.block.multiblocks.shim.process;

import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shim.component.AveragingEnergyStorage;
import com.igteam.immersivegeology.common.block.multiblocks.shim.env.IMultiblockLevel;
import net.minecraft.item.ItemStack;

public interface ProcessContext<R extends MultiblockRecipe>
{
	AveragingEnergyStorage getEnergy();

	interface ProcessContextInWorld<R extends MultiblockRecipe> extends ProcessContext<R>
	{
		void doProcessOutput(ItemStack result, IMultiblockLevel level);
	}
}
