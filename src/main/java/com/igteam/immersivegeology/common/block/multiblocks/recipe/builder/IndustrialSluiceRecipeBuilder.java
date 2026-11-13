/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.StackWithChance;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.IndustrialSluiceRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class IndustrialSluiceRecipeBuilder extends IGMultiblockRecipeBuilder<IndustrialSluiceRecipeBuilder>
{
	private Ingredient input;
	private final TagOutput output;
	private NonNullList<StackWithChance> byproducts;
	private Integer water;
	private Integer time;
	private Integer energy;

	private IndustrialSluiceRecipeBuilder(TagOutput output)
	{
		this.output = output;
	}

	public static IndustrialSluiceRecipeBuilder builder(Item result)
	{
		return builder(new ItemStack(result));
	}

	public static IndustrialSluiceRecipeBuilder builder(ItemStack result)
	{
		return new IndustrialSluiceRecipeBuilder(result.isEmpty()?TagOutput.EMPTY:new TagOutput(result));
	}

	public static IndustrialSluiceRecipeBuilder builder(TagKey<Item> result)
	{
		return new IndustrialSluiceRecipeBuilder(new TagOutput(result));
	}

	public IndustrialSluiceRecipeBuilder addInput(Ingredient input)
	{
		Preconditions.checkArgument(this.input==null, "Recipe can only have one input");
		this.input = input;
		return this;
	}

	public IndustrialSluiceRecipeBuilder addInput(IngredientWithSize input)
	{
		return addInput(input.getBaseIngredient());
	}

	public IndustrialSluiceRecipeBuilder addInput(TagKey<Item> input)
	{
		return addInput(Ingredient.of(input));
	}

	public IndustrialSluiceRecipeBuilder setWater(int water)
	{
		this.water = water;
		return this;
	}

	public IndustrialSluiceRecipeBuilder setTime(int time)
	{
		this.time = time;
		return this;
	}

	public IndustrialSluiceRecipeBuilder setEnergy(int energy)
	{
		this.energy = energy;
		return this;
	}

	public IndustrialSluiceRecipeBuilder setByproducts(NonNullList<StackWithChance> byproducts)
	{
		this.byproducts = NonNullList.createWithCapacity(byproducts.size());
		this.byproducts.addAll(byproducts);
		return this;
	}

	public void build(RecipeOutput out, ResourceLocation name)
	{
		Preconditions.checkNotNull(input, "Missing input");
		Preconditions.checkNotNull(byproducts, "Missing byproducts");
		Preconditions.checkNotNull(water, "Missing water");
		Preconditions.checkNotNull(time, "Missing time");
		Preconditions.checkNotNull(energy, "Missing energy");
		IndustrialSluiceRecipe recipe = new IndustrialSluiceRecipe(input, output, byproducts, water, time, energy);
		out.accept(name, recipe, null, getConditions());
	}
}
