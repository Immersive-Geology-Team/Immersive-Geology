package com.igteam.immersivegeology.common.block.multiblocks.shim.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.List;
import java.util.function.Predicate;

public class SlotwiseItemHandler implements IItemHandlerModifiable
{
	public static class IOConstraint
	{
		public static final IOConstraint OUTPUT = new IOConstraint(false, stack -> false);

		private final boolean allowInsert;
		private final Predicate<ItemStack> filter;

		public IOConstraint(boolean allowInsert, Predicate<ItemStack> filter)
		{
			this.allowInsert = allowInsert;
			this.filter = filter;
		}

		public boolean accepts(ItemStack stack)
		{
			return allowInsert&&filter.test(stack);
		}
	}

	private final List<IOConstraint> constraints;
	private final ItemStack[] stacks;
	private final Runnable setChanged;

	public SlotwiseItemHandler(List<IOConstraint> constraints, Runnable setChanged)
	{
		this.constraints = constraints;
		this.stacks = new ItemStack[constraints.size()];
		java.util.Arrays.fill(this.stacks, ItemStack.EMPTY);
		this.setChanged = setChanged;
	}

	@Override
	public int getSlots()
	{
		return stacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return stacks[slot];
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack)
	{
		stacks[slot] = stack==null?ItemStack.EMPTY: stack;
		setChanged.run();
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		if(stack.isEmpty()||!constraints.get(slot).accepts(stack)) return stack;

		ItemStack existing = stacks[slot];
		int limit = getSlotLimit(slot);
		if(!existing.isEmpty())
		{
			if(!canStack(existing, stack)) return stack;
			limit -= existing.getCount();
		}
		if(limit <= 0) return stack;

		int moved = Math.min(limit, stack.getCount());
		if(!simulate)
		{
			if(existing.isEmpty())
			{
				ItemStack placed = stack.copy();
				placed.setCount(moved);
				stacks[slot] = placed;
			}
			else existing.grow(moved);
			setChanged.run();
		}

		if(moved >= stack.getCount()) return ItemStack.EMPTY;
		ItemStack remainder = stack.copy();
		remainder.shrink(moved);
		return remainder;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		ItemStack existing = stacks[slot];
		if(existing.isEmpty()||amount <= 0) return ItemStack.EMPTY;

		int taken = Math.min(amount, existing.getCount());
		ItemStack result = existing.copy();
		result.setCount(taken);
		if(!simulate)
		{
			if(taken >= existing.getCount()) stacks[slot] = ItemStack.EMPTY;
			else existing.shrink(taken);
			setChanged.run();
		}
		return result;
	}

	@Override
	public int getSlotLimit(int slot)
	{
		return 64;
	}

	private static boolean canStack(ItemStack a, ItemStack b)
	{
		return a.getItem()==b.getItem()&&a.getMetadata()==b.getMetadata()
				&&ItemStack.areItemStackTagsEqual(a, b);
	}

	public NBTTagList toNBT()
	{
		NBTTagList list = new NBTTagList();
		for(int slot = 0; slot < stacks.length; slot++)
		{
			if(stacks[slot].isEmpty()) continue;
			NBTTagCompound tag = stacks[slot].writeToNBT(new NBTTagCompound());
			tag.setInteger("Slot", slot);
			list.appendTag(tag);
		}
		return list;
	}

	public void fromNBT(NBTTagList list)
	{
		java.util.Arrays.fill(stacks, ItemStack.EMPTY);
		for(int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound tag = list.getCompoundTagAt(i);
			int slot = tag.getInteger("Slot");
			if(slot >= 0&&slot < stacks.length) stacks[slot] = new ItemStack(tag);
		}
	}
}
