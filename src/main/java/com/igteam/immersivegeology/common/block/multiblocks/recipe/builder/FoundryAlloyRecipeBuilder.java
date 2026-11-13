/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.FoundryAlloyRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.ArrayList;
import java.util.List;

public class FoundryAlloyRecipeBuilder extends IGMultiblockRecipeBuilder<FoundryAlloyRecipeBuilder>
{
	private final List<SizedFluidIngredient> inputs = new ArrayList<>();
	private final FluidStack output;
	private Integer energy;
	private Integer time;

	private FoundryAlloyRecipeBuilder(FluidStack output)
	{
		this.output = output;
	}

	public static FoundryAlloyRecipeBuilder builder(FluidStack result)
	{
		return new FoundryAlloyRecipeBuilder(result);
	}

	public FoundryAlloyRecipeBuilder addPart(TagKey<Fluid> fluidTag, int parts)
	{
		return addPart(SizedFluidIngredient.of(fluidTag, parts));
	}

	public FoundryAlloyRecipeBuilder addPart(SizedFluidIngredient part)
	{
		inputs.add(part);
		return this;
	}

	public FoundryAlloyRecipeBuilder setEnergy(int energy)
	{
		this.energy = energy;
		return this;
	}

	public FoundryAlloyRecipeBuilder setTime(int time)
	{
		this.time = time;
		return this;
	}

	public void build(RecipeOutput out, ResourceLocation name)
	{
		Preconditions.checkArgument(energy!=null&&time!=null, "This recipe is incomplete");
		FoundryAlloyRecipe recipe = new FoundryAlloyRecipe(inputs, output, energy, time);
		out.accept(name, recipe, null, getConditions());
	}
}
