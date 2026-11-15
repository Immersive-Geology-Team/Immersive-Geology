/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import com.igteam.immersivegeology.common.block.multiblocks.recipe.BloomeryFuel;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class BloomeryFuelBuilder extends IGMultiblockRecipeBuilder<BloomeryFuelBuilder> {
	private final Ingredient input;
	private int time = 1200;

	private BloomeryFuelBuilder(Ingredient input)
	{
		this.input = input;
	}

	public static BloomeryFuelBuilder builder(ItemLike input)
	{
		return new BloomeryFuelBuilder(Ingredient.of(input));
	}

	public static BloomeryFuelBuilder builder(ItemStack input)
	{
		return new BloomeryFuelBuilder(Ingredient.of(input));
	}

	public static BloomeryFuelBuilder builder(TagKey<Item> input)
	{
		return new BloomeryFuelBuilder(Ingredient.of(input));
	}

	public BloomeryFuelBuilder setTime(int time)
	{
		this.time = time;
		return this;
	}

	public void build(RecipeOutput out, ResourceLocation name)
	{
		BloomeryFuel recipe = new BloomeryFuel(input, time);
		out.accept(name, recipe, null, getConditions());
	}
}
