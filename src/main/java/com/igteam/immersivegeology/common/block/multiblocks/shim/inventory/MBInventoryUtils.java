package com.igteam.immersivegeology.common.block.multiblocks.shim.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.function.Consumer;

public final class MBInventoryUtils
{
	private MBInventoryUtils()
	{
	}

	public static void dropItems(IItemHandler inventory, Consumer<ItemStack> drop)
	{
		if(inventory==null) return;
		for(int slot = 0; slot < inventory.getSlots(); slot++)
		{
			ItemStack stack = inventory.getStackInSlot(slot);
			if(!stack.isEmpty()) drop.accept(stack.copy());
		}
	}
}
