package com.igteam.immersivegeology.common.block.multiblocks.recipe;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.IngredientStack;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BallmillRecipe extends MultiblockRecipe
{
	public static final List<BallmillRecipe> recipeList = new ArrayList<>();

	public final IngredientStack input;
	public final ItemStack output;

	private final int processEnergy;
	private final int processTime;

	public BallmillRecipe(ItemStack output, Object input, int energy, int time)
	{
		this.output = output;
		this.input = ApiUtils.createIngredientStack(input);

		this.inputList = Collections.singletonList(this.input);
		this.outputList = NonNullList.from(ItemStack.EMPTY, output);
		this.processEnergy = energy;
		this.processTime = time;
	}

	public static BallmillRecipe addRecipe(ItemStack output, Object input, int energy, int time)
	{
		BallmillRecipe recipe = new BallmillRecipe(output, input, energy, time);
		recipeList.add(recipe);
		return recipe;
	}

	public static BallmillRecipe findRecipe(ItemStack stack)
	{
		if(stack.isEmpty()) return null;
		for(BallmillRecipe recipe : recipeList)
			if(recipe.input!=null&&recipe.input.matchesItemStackIgnoringSize(stack)) return recipe;
		return null;
	}

	public static BallmillRecipe loadFromNBT(NBTTagCompound nbt)
	{
		IngredientStack stored = IngredientStack.readFromNBT(nbt.getCompoundTag("input"));
		for(BallmillRecipe recipe : recipeList)
			if(recipe.input.equals(stored)) return recipe;
		return null;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		nbt.setTag("input", input.writeToNBT(new NBTTagCompound()));
		return nbt;
	}

	@Override
	public int getTotalProcessEnergy()
	{
		return processEnergy;
	}

	@Override
	public int getTotalProcessTime()
	{
		return processTime;
	}

	@Override
	public int getMultipleProcessTicks()
	{
		return 0;
	}
}
