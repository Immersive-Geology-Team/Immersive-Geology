package com.igteam.immersivegeology.common.block.multiblocks.recipe;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.IngredientStack;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BloomeryRecipe
{
	public static final List<BloomeryRecipe> recipeList = new ArrayList<>();

	public final IngredientStack input;
	public final ItemStack result;
	public final int time;

	public BloomeryRecipe(ItemStack result, Object input, int time)
	{
		this.result = result;
		this.input = ApiUtils.createIngredientStack(input);
		this.time = time;
	}

	public static BloomeryRecipe addRecipe(ItemStack result, Object input, int time)
	{
		BloomeryRecipe recipe = new BloomeryRecipe(result, input, time);
		recipeList.add(recipe);
		return recipe;
	}

	public static BloomeryRecipe findRecipe(ItemStack input)
	{
		if(input.isEmpty()) return null;
		for(BloomeryRecipe recipe : recipeList)
			if(recipe.input!=null&&recipe.input.matchesItemStackIgnoringSize(input)) return recipe;
		return null;
	}
}
