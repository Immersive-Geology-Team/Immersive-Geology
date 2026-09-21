package com.igteam.immersivegeology.common.block.multiblocks.shim.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public abstract class InsertOnlyInventory implements IItemHandler
{
	protected abstract ItemStack insert(ItemStack toInsert, boolean simulate);

	@Override
	public int getSlots()
	{
		return 1;
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return ItemStack.EMPTY;
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
	{
		return stack.isEmpty()?ItemStack.EMPTY: insert(stack, simulate);
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot)
	{
		return 64;
	}
}
