/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.ChemicalRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class ChemicalRecipeBuilder extends IEFinishedRecipe<ChemicalRecipeBuilder>
{
	protected ChemicalRecipeBuilder()
	{
		super(ChemicalRecipe.SERIALIZER.get());
	}

	public static ChemicalRecipeBuilder builder(Item result, FluidStack fluidStack, IngredientWithSize itemInput, FluidTagInput fluidInputA, FluidTagInput fluidInputB, FluidTagInput fluidInputC)
	{
		ChemicalRecipeBuilder builder = new ChemicalRecipeBuilder().addIngredient("itemResult",result).addFluid("fluidResult", fluidStack).addFluid("fluidResult", fluidStack)
				.addIngredient("itemInput",itemInput);

		if(fluidInputA != null)
		{
			builder.addFluidTag("fluidInputA", fluidInputA);
		}
		if(fluidInputB != null)
		{
			builder.addFluidTag("fluidInputB", fluidInputB);
		}
		if(fluidInputC != null)
		{
			builder.addFluidTag("fluidInputC", fluidInputC);
		}
		return builder;
	}

	public static ChemicalRecipeBuilder builder(ItemStack result, FluidStack fluidStack, IngredientWithSize itemInput, FluidTagInput fluidInputA, FluidTagInput fluidInputB, FluidTagInput fluidInputC)
	{
		ChemicalRecipeBuilder builder = new ChemicalRecipeBuilder().addIngredient("itemResult",result).addFluid("fluidResult", fluidStack).addFluid("fluidResult", fluidStack)
				.addIngredient("itemInput",itemInput);

		if(fluidInputA != null)
		{
			builder.addFluidTag("fluidInputA", fluidInputA);
		}
		if(fluidInputB != null)
		{
			builder.addFluidTag("fluidInputB", fluidInputB);
		}
		if(fluidInputC != null)
		{
			builder.addFluidTag("fluidInputC", fluidInputC);
		}

		return builder;
	}

}
