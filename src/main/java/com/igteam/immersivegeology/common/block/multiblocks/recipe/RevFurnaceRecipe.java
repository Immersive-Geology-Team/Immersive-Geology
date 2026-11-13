/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */
package com.igteam.immersivegeology.common.block.multiblocks.recipe;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import com.igteam.immersivegeology.core.registration.IGRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nullable;

public class RevFurnaceRecipe extends MultiblockRecipe
{

	public static DeferredHolder<RecipeSerializer<?>, IERecipeSerializer<RevFurnaceRecipe>> SERIALIZER;
	public static final CachedRecipeList<RevFurnaceRecipe> RECIPES = new CachedRecipeList<>(IGRecipeTypes.REVFURNACE);
	public int waste, time;
	public IngredientWithSize input;
	public TagOutput result;
	Lazy<Integer> totalProcessTime;

	public <T extends Recipe<?>> RevFurnaceRecipe(IngredientWithSize input, TagOutput result, int waste_amount, int time)
	{
		super(TagOutput.EMPTY, IGRecipeTypes.REVFURNACE, time, 0, () -> new RecipeMultiplier(() -> 1, () -> 1));
		this.input = input;
		this.result = result;
		this.waste = waste_amount;
		this.time = time;
		totalProcessTime = Lazy.of(() -> time);
	}

	public static RevFurnaceRecipe findRecipe(Level level, ItemStack input)
	{
		for(RecipeHolder<RevFurnaceRecipe> recipe : RECIPES.getRecipes(level))
			if(recipe.value().input.test(input))
				return recipe.value();
		return null;
	}

	public static RevFurnaceRecipe findRecipe(Level level, ItemStack input, @Nullable RevFurnaceRecipe hint)
	{
		if (input.isEmpty())
			return null;
		if (hint != null && hint.matches(input))
			return hint;
		for(RecipeHolder<RevFurnaceRecipe> recipe : RECIPES.getRecipes(level))
			if(recipe.value().input.test(input))
				return recipe.value();
		return null;
	}

	private boolean matches(ItemStack input)
	{
		return this.input.test(input);
	}

	@Override
	public int getTotalProcessTime()
	{
		return totalProcessTime.get();
	}

	@Override
	protected IERecipeSerializer<?> getIESerializer()
	{
		return SERIALIZER.get();
	}

	@Override
	public int getMultipleProcessTicks()
	{
		return 1;
	}

	public int getWasteAmount()
	{
		return waste;
	}
}
