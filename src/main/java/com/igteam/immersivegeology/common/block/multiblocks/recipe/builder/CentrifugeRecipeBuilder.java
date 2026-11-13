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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CentrifugeRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.ChemicalRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class CentrifugeRecipeBuilder extends IGMultiblockRecipeBuilder<CentrifugeRecipeBuilder>
{
	private final TagOutput output;
	private final SizedFluidIngredient input;
	private int energy;
	private int time;
	private final FluidStack primaryFluidOutput;
	private final FluidStack secondaryFluidOutput;

	private CentrifugeRecipeBuilder(ItemStack itemOutput, SizedFluidIngredient fluidIn, int energy, int time, FluidStack primaryFluidOutput, FluidStack secondaryFluidOutput)
	{
		this.output = itemOutput.isEmpty()?TagOutput.EMPTY:new TagOutput(itemOutput);
		this.input = fluidIn;
		this.energy = energy;
		this.time = time;
		this.primaryFluidOutput = primaryFluidOutput;
		this.secondaryFluidOutput = secondaryFluidOutput;
	}

	public static CentrifugeRecipeBuilder builder(ItemStack itemOutput, SizedFluidIngredient fluidIn, int energy, int time, FluidStack primaryFluidOutput, FluidStack secondaryFluidOutput)
	{
		return new CentrifugeRecipeBuilder(itemOutput, fluidIn, energy, time, primaryFluidOutput, secondaryFluidOutput);
	}

	public CentrifugeRecipeBuilder setEnergy(int energy)
	{
		this.energy = energy;
		return this;
	}

	public CentrifugeRecipeBuilder setTime(int time)
	{
		this.time = time;
		return this;
	}

	public void build(RecipeOutput out, ResourceLocation name)
	{
		CentrifugeRecipe recipe = new CentrifugeRecipe(input, output,
				Lazy.of(() -> primaryFluidOutput), Lazy.of(() -> secondaryFluidOutput), energy, time);
		out.accept(name, recipe, null, getConditions());
	}


}
