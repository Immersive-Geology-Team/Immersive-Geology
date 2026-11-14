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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BasicChemicalRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class BasicChemicalRecipeBuilder extends IGMultiblockRecipeBuilder<BasicChemicalRecipeBuilder>
{
	private final IngredientWithSize itemInput;
	private final Set<SizedFluidIngredient> fluidInputs = Collections.newSetFromMap(new IdentityHashMap<>());
	private final TagOutput itemOutput;
	private final FluidStack fluidOutput;
	private final int damage_per_second;
	private Integer energy;
	private Integer time;

	private BasicChemicalRecipeBuilder(TagOutput result, FluidStack fluidStack, IngredientWithSize itemInput, SizedFluidIngredient fluidInputA, SizedFluidIngredient fluidInputB, int damage_per_second)
	{
		this.itemOutput = result;
		this.fluidOutput = fluidStack;
		this.itemInput = itemInput;
		this.damage_per_second = damage_per_second;
		for(SizedFluidIngredient input : new SizedFluidIngredient[]{fluidInputA, fluidInputB})
			if(input!=null)
				// Each old JSON slot decoded into a distinct object, even if callers reused one input.
				fluidInputs.add(new SizedFluidIngredient(input.ingredient(), input.amount()));
	}

	public static BasicChemicalRecipeBuilder builder(ItemStack result, FluidStack fluidStack, IngredientWithSize itemInput, SizedFluidIngredient fluidInputA, SizedFluidIngredient fluidInputB, int damage_per_second)
	{
		return new BasicChemicalRecipeBuilder(result.isEmpty()?TagOutput.EMPTY:new TagOutput(result), fluidStack, itemInput, fluidInputA, fluidInputB, damage_per_second);
	}

	public static BasicChemicalRecipeBuilder builder(IngredientWithSize result, FluidStack fluidStack, IngredientWithSize itemInput, SizedFluidIngredient fluidInputA, SizedFluidIngredient fluidInputB, int damage_per_second)
	{
		return new BasicChemicalRecipeBuilder(new TagOutput(result), fluidStack, itemInput, fluidInputA, fluidInputB, damage_per_second);
	}

	public BasicChemicalRecipeBuilder setEnergy(int energy)
	{
		this.energy = energy;
		return this;
	}

	public BasicChemicalRecipeBuilder setTime(int time)
	{
		this.time = time;
		return this;
	}

	public void build(RecipeOutput out, ResourceLocation name)
	{
		Preconditions.checkArgument(itemInput!=null&&energy!=null&&time!=null, "This recipe is incomplete");
		BasicChemicalRecipe recipe = new BasicChemicalRecipe(itemInput, fluidInputs, itemOutput, fluidOutput, damage_per_second, energy, time);
		out.accept(name, recipe, null, getConditions());
	}
}
