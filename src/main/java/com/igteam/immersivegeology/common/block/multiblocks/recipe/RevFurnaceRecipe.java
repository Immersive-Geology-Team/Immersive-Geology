package com.igteam.immersivegeology.common.block.multiblocks.recipe;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.IngredientStack;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RevFurnaceRecipe
{
	public static final List<RevFurnaceRecipe> recipeList = new ArrayList<>();

	public final IngredientStack input;
	public final ItemStack result;
	public final int time;
	public final int wasteAmount;

	public RevFurnaceRecipe(ItemStack result, Object input, int time, int wasteAmount)
	{
		this.result = result;
		this.input = ApiUtils.createIngredientStack(input);
		this.time = time;
		this.wasteAmount = wasteAmount;
	}

	public static RevFurnaceRecipe addRecipe(ItemStack result, Object input, int time, int wasteAmount)
	{
		RevFurnaceRecipe recipe = new RevFurnaceRecipe(result, input, time, wasteAmount);
		recipeList.add(recipe);
		return recipe;
	}

	public static RevFurnaceRecipe findRecipe(ItemStack input)
	{
		if(input.isEmpty()) return null;
		for(RevFurnaceRecipe recipe : recipeList)
			if(recipe.input!=null&&recipe.input.matchesItemStackIgnoringSize(input)) return recipe;
		return null;
	}
}
