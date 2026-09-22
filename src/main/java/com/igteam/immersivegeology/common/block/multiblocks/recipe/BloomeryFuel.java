package com.igteam.immersivegeology.common.block.multiblocks.recipe;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.IngredientStack;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BloomeryFuel
{
	public static final List<BloomeryFuel> fuelList = new ArrayList<>();

	public final IngredientStack input;
	public final int burnTime;

	public BloomeryFuel(Object input, int burnTime)
	{
		this.input = ApiUtils.createIngredientStack(input);
		this.burnTime = burnTime;
	}

	public static BloomeryFuel addFuel(Object input, int burnTime)
	{
		BloomeryFuel fuel = new BloomeryFuel(input, burnTime);
		fuelList.add(fuel);
		return fuel;
	}

	public static int getBloomeryFuelTime(ItemStack stack)
	{
		if(stack.isEmpty()) return 0;
		for(BloomeryFuel fuel : fuelList)
			if(fuel.input!=null&&fuel.input.matchesItemStackIgnoringSize(stack)) return fuel.burnTime;
		return 0;
	}

	public static boolean isValidBloomeryFuel(ItemStack stack)
	{
		return getBloomeryFuelTime(stack) > 0;
	}
}
