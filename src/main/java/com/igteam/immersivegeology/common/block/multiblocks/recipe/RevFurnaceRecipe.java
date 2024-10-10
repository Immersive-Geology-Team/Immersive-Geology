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
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import com.igteam.immersivegeology.core.registration.IGRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;

public class RevFurnaceRecipe extends MultiblockRecipe
{

	public static RegistryObject<IERecipeSerializer<RevFurnaceRecipe>> SERIALIZER;
	public static final CachedRecipeList<RevFurnaceRecipe> RECIPES = new CachedRecipeList<>(IGRecipeTypes.REVFURNACE);
	public int waste, time;
	public IngredientWithSize input;
	public Lazy<ItemStack> result;
	Lazy<Integer> totalProcessTime;

	public <T extends Recipe<?>> RevFurnaceRecipe(ResourceLocation id, IngredientWithSize input, Lazy<ItemStack> result, int waste_amount, int time)
	{
		super(LAZY_EMPTY, IGRecipeTypes.REVFURNACE, id);
		this.input = input;
		this.result = result;
		this.waste = waste_amount;
		this.time = time;
		totalProcessTime = Lazy.of(() -> time);
	}

	public static RevFurnaceRecipe findRecipe(Level level, ItemStack input)
	{
		for(RevFurnaceRecipe recipe : RECIPES.getRecipes(level))
			if(recipe.input.test(input))
				return recipe;
		return null;
	}

	public static RevFurnaceRecipe findRecipe(Level level, ItemStack input, @Nullable RevFurnaceRecipe hint)
	{
		if (input.isEmpty())
			return null;
		if (hint != null && hint.matches(input))
			return hint;
		for(RevFurnaceRecipe recipe : RECIPES.getRecipes(level))
			if(recipe.input.test(input))
				return recipe;
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
		return 0;
	}

	public int getWasteAmount()
	{
		return waste;
	}
}
