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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.ChemicalRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class ChemicalRecipeBuilder extends IGMultiblockRecipeBuilder<ChemicalRecipeBuilder>
{
	private final IngredientWithSize itemInput;
	private final Set<SizedFluidIngredient> fluidInputs = Collections.newSetFromMap(new IdentityHashMap<>());
	private final TagOutput itemOutput;
	private final FluidStack fluidOutput;
	private Integer energy;
	private Integer time;

	private ChemicalRecipeBuilder(TagOutput result, FluidStack fluidStack, IngredientWithSize itemInput, SizedFluidIngredient fluidInputA, SizedFluidIngredient fluidInputB, SizedFluidIngredient fluidInputC)
	{
		this.itemOutput = result;
		this.fluidOutput = fluidStack;
		this.itemInput = itemInput;
		for(SizedFluidIngredient input : new SizedFluidIngredient[]{fluidInputA, fluidInputB, fluidInputC})
			if(input!=null)
				// Each old JSON slot decoded into a distinct object, even if callers reused one input.
				fluidInputs.add(new SizedFluidIngredient(input.ingredient(), input.amount()));
	}

	public static ChemicalRecipeBuilder builder(ItemStack result, FluidStack fluidStack, IngredientWithSize itemInput, SizedFluidIngredient fluidInputA, SizedFluidIngredient fluidInputB, SizedFluidIngredient fluidInputC)
	{
		return new ChemicalRecipeBuilder(result.isEmpty()?TagOutput.EMPTY:new TagOutput(result), fluidStack, itemInput, fluidInputA, fluidInputB, fluidInputC);
	}

	public static ChemicalRecipeBuilder builder(IngredientWithSize result, FluidStack fluidStack, IngredientWithSize itemInput, SizedFluidIngredient fluidInputA, SizedFluidIngredient fluidInputB, SizedFluidIngredient fluidInputC)
	{
		return new ChemicalRecipeBuilder(new TagOutput(result), fluidStack, itemInput, fluidInputA, fluidInputB, fluidInputC);
	}

	public ChemicalRecipeBuilder setEnergy(int energy)
	{
		this.energy = energy;
		return this;
	}

	public ChemicalRecipeBuilder setTime(int time)
	{
		this.time = time;
		return this;
	}

	public void build(RecipeOutput out, ResourceLocation name)
	{
		Preconditions.checkArgument(itemInput!=null&&energy!=null&&time!=null, "This recipe is incomplete");
		ChemicalRecipe recipe = new ChemicalRecipe(itemInput, fluidInputs, itemOutput, fluidOutput, energy, time);
		out.accept(name, recipe, null, getConditions());
	}

}
