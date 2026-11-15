/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import blusunrize.immersiveengineering.api.crafting.TagOutput;
import com.google.common.base.Preconditions;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GravitySeparatorRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class GravitySeparatorRecipeBuilder extends IGMultiblockRecipeBuilder<GravitySeparatorRecipeBuilder>
{
	private Ingredient input;
	private final TagOutput output;
	private TagOutput byproduct;
	private Float chance;
	private Integer water;
	private Integer time;

	private GravitySeparatorRecipeBuilder(TagOutput output)
	{
		this.output = output;
	}

	public static GravitySeparatorRecipeBuilder builder(Item result)
	{
		return builder(new ItemStack(result));
	}

	public static GravitySeparatorRecipeBuilder builder(ItemStack result)
	{
		return new GravitySeparatorRecipeBuilder(new TagOutput(result));
	}

	public static GravitySeparatorRecipeBuilder builder(TagKey<Item> result)
	{
		return new GravitySeparatorRecipeBuilder(new TagOutput(result));
	}

	public GravitySeparatorRecipeBuilder addInput(Ingredient ingredient)
	{
		Preconditions.checkArgument(input==null, "Recipe can only have 1 input");
		this.input = ingredient;
		return this;
	}

	public GravitySeparatorRecipeBuilder addInput(TagKey<Item> item)
	{
		return addInput(Ingredient.of(item));
	}

	public GravitySeparatorRecipeBuilder setWater(int water)
	{
		this.water = water;
		return this;
	}

	public GravitySeparatorRecipeBuilder setTime(int time)
	{
		this.time = time;
		return this;
	}

	public GravitySeparatorRecipeBuilder setChance(float chance)
	{
		this.chance = chance;
		return this;
	}

	public GravitySeparatorRecipeBuilder setByproduct(ItemStack byproduct)
	{
		this.byproduct = new TagOutput(byproduct);
		return this;
	}

	public GravitySeparatorRecipeBuilder setByproduct(Item byproduct)
	{
		return setByproduct(new ItemStack(byproduct));
	}

	public void build(RecipeOutput out, ResourceLocation name)
	{
		Preconditions.checkArgument(input!=null&&byproduct!=null&&chance!=null&&water!=null&&time!=null, "This recipe is incomplete");
		GravitySeparatorRecipe recipe = new GravitySeparatorRecipe(input, output, byproduct, chance, water, time);
		out.accept(name, recipe, null, getConditions());
	}
}
