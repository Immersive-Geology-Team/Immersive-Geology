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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.FoundryRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class FoundryRecipeBuilder extends IGMultiblockRecipeBuilder<FoundryRecipeBuilder>
{
	private SizedFluidIngredient input;
	private final TagOutput output;
	private Item mold;
	private Integer energy;
	private Integer time;

	private FoundryRecipeBuilder(TagOutput output)
	{
		this.output = output;
	}

	public static FoundryRecipeBuilder builder(ItemLike result)
	{
		return new FoundryRecipeBuilder(new TagOutput(result));
	}

	public static FoundryRecipeBuilder builder(ItemStack result)
	{
		return new FoundryRecipeBuilder(new TagOutput(result));
	}

	public static FoundryRecipeBuilder builder(TagKey<Item> result, int count)
	{
		return new FoundryRecipeBuilder(new TagOutput(result, count));
	}

	public FoundryRecipeBuilder addInput(SizedFluidIngredient fluidTag)
	{
		Preconditions.checkArgument(input==null, "Recipe can only have 1 input");
		this.input = fluidTag;
		return this;
	}

	public FoundryRecipeBuilder addInput(TagKey<Fluid> fluidTag, int amount)
	{
		return addInput(SizedFluidIngredient.of(fluidTag, amount));
	}

	public FoundryRecipeBuilder setMold(ItemLike mold)
	{
		this.mold = mold.asItem();
		return this;
	}

	public FoundryRecipeBuilder setEnergy(int energy)
	{
		this.energy = energy;
		return this;
	}

	public FoundryRecipeBuilder setTime(int time)
	{
		this.time = time;
		return this;
	}

	public void build(RecipeOutput out, ResourceLocation name)
	{
		Preconditions.checkArgument(input!=null&&mold!=null&&energy!=null&&time!=null, "This recipe is incomplete");
		FoundryRecipe recipe = new FoundryRecipe(input, output, mold, energy, time);
		out.accept(name, recipe, null, getConditions());
	}
}
