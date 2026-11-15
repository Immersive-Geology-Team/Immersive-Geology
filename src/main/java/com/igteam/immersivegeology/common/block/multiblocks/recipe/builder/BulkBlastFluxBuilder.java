/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import com.igteam.immersivegeology.common.block.multiblocks.recipe.BulkBlastFluxRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class BulkBlastFluxBuilder extends IGMultiblockRecipeBuilder<BulkBlastFluxBuilder>
{
	private final Ingredient input;
	private int fluxValue = 1;

	private BulkBlastFluxBuilder(Ingredient input)
	{
		this.input = input;
	}

	public static BulkBlastFluxBuilder builder(ItemLike input)
	{
		return new BulkBlastFluxBuilder(Ingredient.of(input));
	}

	public static BulkBlastFluxBuilder builder(ItemStack input)
	{
		return new BulkBlastFluxBuilder(Ingredient.of(input));
	}

	public static BulkBlastFluxBuilder builder(TagKey<Item> input)
	{
		return new BulkBlastFluxBuilder(Ingredient.of(input));
	}

	public BulkBlastFluxBuilder setFluxValue(int fluxValue)
	{
		this.fluxValue = fluxValue;
		return this;
	}

	public void build(RecipeOutput out, ResourceLocation name)
	{
		BulkBlastFluxRecipe recipe = new BulkBlastFluxRecipe(input, fluxValue);
		out.accept(name, recipe, null, getConditions());
	}
}
