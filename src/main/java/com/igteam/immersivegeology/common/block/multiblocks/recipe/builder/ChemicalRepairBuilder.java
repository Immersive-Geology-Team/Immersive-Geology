/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import com.igteam.immersivegeology.common.block.multiblocks.recipe.ChemicalRepairRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class ChemicalRepairBuilder extends IGMultiblockRecipeBuilder<ChemicalRepairBuilder> {
	private final Ingredient input;
	private int time = 10;

	private ChemicalRepairBuilder(Ingredient input)
	{
		this.input = input;
	}

	public static ChemicalRepairBuilder builder(ItemLike input)
	{
		return new ChemicalRepairBuilder(Ingredient.of(input));
	}

	public static ChemicalRepairBuilder builder(ItemStack input)
	{
		return new ChemicalRepairBuilder(Ingredient.of(input));
	}

	public static ChemicalRepairBuilder builder(TagKey<Item> input)
	{
		return new ChemicalRepairBuilder(Ingredient.of(input));
	}

	public ChemicalRepairBuilder setTime(int time)
	{
		this.time = time;
		return this;
	}

	public void build(RecipeOutput out, ResourceLocation name)
	{
		ChemicalRepairRecipe recipe = new ChemicalRepairRecipe(input, time);
		out.accept(name, recipe, null, getConditions());
	}
}
