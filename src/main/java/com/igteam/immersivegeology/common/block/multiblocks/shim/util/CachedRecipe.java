package com.igteam.immersivegeology.common.block.multiblocks.shim.util;

import net.minecraft.item.ItemStack;

import java.util.function.Function;
import java.util.function.Supplier;

public class CachedRecipe<R> implements Supplier<R>
{
	private final Function<ItemStack, R> lookup;
	private final Supplier<ItemStack> input;

	private ItemStack lastInput = ItemStack.EMPTY;
	private R cached;
	private boolean valid;

	private CachedRecipe(Function<ItemStack, R> lookup, Supplier<ItemStack> input)
	{
		this.lookup = lookup;
		this.input = input;
	}

	public static <R> CachedRecipe<R> cached(Function<ItemStack, R> lookup, Supplier<ItemStack> input)
	{
		return new CachedRecipe<>(lookup, input);
	}

	@Override
	public R get()
	{
		ItemStack current = input.get();
		if(valid&&ItemStack.areItemStacksEqual(current, lastInput)) return cached;

		lastInput = current.isEmpty()?ItemStack.EMPTY: current.copy();
		cached = current.isEmpty()?null: lookup.apply(current);
		valid = true;
		return cached;
	}

	public void invalidate()
	{
		valid = false;
	}
}
