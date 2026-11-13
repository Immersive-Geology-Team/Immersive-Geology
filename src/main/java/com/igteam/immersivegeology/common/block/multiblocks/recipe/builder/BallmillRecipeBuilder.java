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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BallmillRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RotaryKilnRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer.BallmillRecipeSerializer;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class BallmillRecipeBuilder extends IGMultiblockRecipeBuilder<BallmillRecipeBuilder>
{
	private IngredientWithSize input;
	private final TagOutput output;
	private Integer energy;
	private Integer time;

	private BallmillRecipeBuilder(TagOutput output)
	{
		this.output = output;
	}

	public static BallmillRecipeBuilder builder(Item result)
	{
		return builder(new ItemStack(result));
	}

	public static BallmillRecipeBuilder builder(ItemStack result)
	{
		return new BallmillRecipeBuilder(new TagOutput(IngredientWithSize.of(result)));
	}

	public static BallmillRecipeBuilder builder(TagKey<Item> result, int count)
	{
		return new BallmillRecipeBuilder(new TagOutput(result, count));
	}

	public BallmillRecipeBuilder addInput(IngredientWithSize ingredient)
	{
		Preconditions.checkArgument(input==null, "Recipe can only have 1 input");
		this.input = ingredient;
		return this;
	}

	public BallmillRecipeBuilder addInput(Ingredient ingredient)
	{
		return addInput(new IngredientWithSize(ingredient));
	}

	public BallmillRecipeBuilder addInput(TagKey<Item> itemTag)
	{
		return addInput(new IngredientWithSize(itemTag));
	}

	public BallmillRecipeBuilder addInput(ItemLike... items)
	{
		return addInput(Ingredient.of(items));
	}

	public BallmillRecipeBuilder addInput(ItemStack... stacks)
	{
		return addInput(Ingredient.of(stacks));
	}

	public BallmillRecipeBuilder setEnergy(int energy)
	{
		this.energy = energy;
		return this;
	}

	public BallmillRecipeBuilder setTime(int time)
	{
		this.time = time;
		return this;
	}

	public void build(RecipeOutput out, ResourceLocation name)
	{
		Preconditions.checkArgument(input!=null&&energy!=null&&time!=null, "This recipe is incomplete");
		BallmillRecipe recipe = new BallmillRecipe(input, output, energy, time);
		out.accept(name, recipe, null, getConditions());
	}

}
