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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CrystallizerRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RevFurnaceRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;

public class RevFurnaceRecipeBuilder extends IGMultiblockRecipeBuilder<RevFurnaceRecipeBuilder>
{
	private IngredientWithSize input;
	private final TagOutput output;
	private Integer time;
	private Integer waste;

	private RevFurnaceRecipeBuilder(TagOutput output)
	{
		this.output = output;
	}

	public static RevFurnaceRecipeBuilder builder(Item result)
	{
		return builder(new ItemStack(result));
	}

	public static RevFurnaceRecipeBuilder builder(ItemStack result)
	{
		return new RevFurnaceRecipeBuilder(new TagOutput(result));
	}

	public static RevFurnaceRecipeBuilder builder(TagKey<Item> result, int count)
	{
		return new RevFurnaceRecipeBuilder(new TagOutput(result, count));
	}

	public RevFurnaceRecipeBuilder addInput(IngredientWithSize ingredient)
	{
		Preconditions.checkArgument(input==null, "Recipe can only have 1 input");
		this.input = ingredient;
		return this;
	}

	public RevFurnaceRecipeBuilder addInput(Ingredient ingredient)
	{
		return addInput(new IngredientWithSize(ingredient));
	}

	public RevFurnaceRecipeBuilder addInput(TagKey<Item> itemTag)
	{
		return addInput(new IngredientWithSize(itemTag));
	}

	public RevFurnaceRecipeBuilder addInput(ItemLike... items)
	{
		return addInput(Ingredient.of(items));
	}

	public RevFurnaceRecipeBuilder addInput(ItemStack... stacks)
	{
		return addInput(Ingredient.of(stacks));
	}

	public RevFurnaceRecipeBuilder setTime(int time)
	{
		this.time = time;
		return this;
	}

	public RevFurnaceRecipeBuilder setWasteAmount(int amount)
	{
		this.waste = amount;
		return this;
	}

	public void build(RecipeOutput out, ResourceLocation name)
	{
		Preconditions.checkArgument(input!=null&&time!=null&&waste!=null, "This recipe is incomplete");
		RevFurnaceRecipe recipe = new RevFurnaceRecipe(input, output, waste, time);
		out.accept(name, recipe, null, getConditions());
	}
}
