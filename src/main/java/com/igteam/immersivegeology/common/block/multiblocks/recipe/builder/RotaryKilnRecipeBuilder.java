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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RotaryKilnRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RotaryKilnRecipeBuilder extends IGMultiblockRecipeBuilder<RotaryKilnRecipeBuilder>
{
	private IngredientWithSize input;
	private final TagOutput output;
	private Integer time;
	private Integer heat;

	private RotaryKilnRecipeBuilder(TagOutput output)
	{
		this.output = output;
	}

	public static RotaryKilnRecipeBuilder builder(Item result)
	{
		return new RotaryKilnRecipeBuilder(new TagOutput(result));
	}

	public static RotaryKilnRecipeBuilder builder(ItemStack result)
	{
		return new RotaryKilnRecipeBuilder(new TagOutput(result));
	}

	public static RotaryKilnRecipeBuilder builder(TagKey<Item> result, int count)
	{
		return new RotaryKilnRecipeBuilder(new TagOutput(result, count));
	}

	public RotaryKilnRecipeBuilder addInput(IngredientWithSize ingredient)
	{
		Preconditions.checkArgument(input==null, "Recipe can only have 1 input");
		this.input = ingredient;
		return this;
	}

	public RotaryKilnRecipeBuilder addInput(TagKey<Item> itemTag)
	{
		return addInput(new IngredientWithSize(itemTag));
	}

	public RotaryKilnRecipeBuilder setTime(int time)
	{
		this.time = time;
		return this;
	}

	public RotaryKilnRecipeBuilder setHeat(int heat)
	{
		this.heat = heat;
		return this;
	}

	public void build(RecipeOutput out, ResourceLocation name)
	{
		Preconditions.checkArgument(input!=null&&time!=null&&heat!=null, "This recipe is incomplete");
		RotaryKilnRecipe recipe = new RotaryKilnRecipe(input, output, time, heat);
		out.accept(name, recipe, null, getConditions());
	}

}
