/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import com.google.common.base.Preconditions;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BloomeryRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class BloomeryRecipeBuilder extends IGMultiblockRecipeBuilder<BloomeryRecipeBuilder>
{
	private IngredientWithSize input;
	private final TagOutput output;
	private Integer time;

	private BloomeryRecipeBuilder(TagOutput output)
	{
		this.output = output;
	}

	public static BloomeryRecipeBuilder builder(Item result)
	{
		return builder(new ItemStack(result));
	}

	public static BloomeryRecipeBuilder builder(ItemStack result)
	{
		return new BloomeryRecipeBuilder(new TagOutput(result));
	}

	public static BloomeryRecipeBuilder builder(TagKey<Item> result, int count)
	{
		return new BloomeryRecipeBuilder(new TagOutput(result, count));
	}

	public BloomeryRecipeBuilder addInput(IngredientWithSize ingredient)
	{
		Preconditions.checkArgument(input==null, "Recipe can only have 1 input");
		this.input = ingredient;
		return this;
	}

	public BloomeryRecipeBuilder addInput(Ingredient ingredient)
	{
		return addInput(new IngredientWithSize(ingredient));
	}

	public BloomeryRecipeBuilder addInput(TagKey<Item> itemTag)
	{
		return addInput(new IngredientWithSize(itemTag));
	}

	public BloomeryRecipeBuilder addInput(ItemLike... items)
	{
		return addInput(Ingredient.of(items));
	}

	public BloomeryRecipeBuilder addInput(ItemStack... stacks)
	{
		return addInput(Ingredient.of(stacks));
	}

	public BloomeryRecipeBuilder setTime(int time)
	{
		this.time = time;
		return this;
	}

	public void build(RecipeOutput out, ResourceLocation name)
	{
		Preconditions.checkArgument(input!=null&&time!=null, "This recipe is incomplete");
		BloomeryRecipe recipe = new BloomeryRecipe(input, output, time);
		out.accept(name, recipe, null, getConditions());
	}
}
