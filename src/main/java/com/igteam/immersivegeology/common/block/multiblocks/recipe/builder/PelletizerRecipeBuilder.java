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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.PelletizerRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RotaryKilnRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class PelletizerRecipeBuilder extends IGMultiblockRecipeBuilder<PelletizerRecipeBuilder>
{
	private IngredientWithSize input;
	private final TagOutput output;
	private Integer energy;
	private Integer time;

	private PelletizerRecipeBuilder(TagOutput output)
	{
		this.output = output;
	}

	public static PelletizerRecipeBuilder builder(Item result)
	{
		return builder(new ItemStack(result));
	}

	public static PelletizerRecipeBuilder builder(ItemStack result)
	{
		return new PelletizerRecipeBuilder(new TagOutput(result));
	}

	public static PelletizerRecipeBuilder builder(TagKey<Item> result, int count)
	{
		return new PelletizerRecipeBuilder(new TagOutput(result, count));
	}

	public PelletizerRecipeBuilder addInput(IngredientWithSize ingredient)
	{
		Preconditions.checkArgument(input==null, "Recipe can only have 1 input");
		this.input = ingredient;
		return this;
	}

	public PelletizerRecipeBuilder addInput(Ingredient ingredient)
	{
		return addInput(new IngredientWithSize(ingredient));
	}

	public PelletizerRecipeBuilder addInput(TagKey<Item> itemTag)
	{
		return addInput(new IngredientWithSize(itemTag));
	}

	public PelletizerRecipeBuilder addInput(ItemLike... items)
	{
		return addInput(Ingredient.of(items));
	}

	public PelletizerRecipeBuilder addInput(ItemStack... stacks)
	{
		return addInput(Ingredient.of(stacks));
	}

	public PelletizerRecipeBuilder setEnergy(int energy)
	{
		this.energy = energy;
		return this;
	}

	public PelletizerRecipeBuilder setTime(int time)
	{
		this.time = time;
		return this;
	}

	public void build(RecipeOutput out, ResourceLocation name)
	{
		Preconditions.checkArgument(input!=null&&energy!=null&&time!=null, "This recipe is incomplete");
		PelletizerRecipe recipe = new PelletizerRecipe(input, output, energy, time);
		out.accept(name, recipe, null, getConditions());
	}

}
