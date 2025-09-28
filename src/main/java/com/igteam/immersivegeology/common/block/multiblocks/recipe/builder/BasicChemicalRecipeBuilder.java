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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BasicChemicalRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.ChemicalRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class BasicChemicalRecipeBuilder extends IEFinishedRecipe<BasicChemicalRecipeBuilder>
{
	protected BasicChemicalRecipeBuilder()
	{
		super(BasicChemicalRecipe.SERIALIZER.get());
	}

	public static BasicChemicalRecipeBuilder builder(ItemStack result, FluidStack fluidStack, IngredientWithSize itemInput, FluidTagInput fluidInputA, FluidTagInput fluidInputB, int damage_per_second)
	{
		BasicChemicalRecipeBuilder builder = new BasicChemicalRecipeBuilder().addFluid("fluidResult", fluidStack).addFluid("fluidResult", fluidStack)
				.addIngredient("itemInput",itemInput);

		if(!result.isEmpty())
		{
			builder.addResult(result);
		}
		else
		{
			builder.addResult(IngredientWithSize.of(ItemStack.EMPTY));
		}
		if(fluidInputA != null)
		{
			builder.addFluidTag("fluidInputA", fluidInputA);
		}
		if(fluidInputB != null)
		{
			builder.addFluidTag("fluidInputB", fluidInputB);
		}

		builder.addWriter((jsonObject) -> {
			jsonObject.addProperty("damage_per_second", damage_per_second);
		});

		return builder;
	}

	public static BasicChemicalRecipeBuilder builder(ItemStack result, FluidStack fluidStack, IngredientWithSize itemInput, FluidTagInput fluidInputA, FluidTagInput fluidInputB)
	{
		BasicChemicalRecipeBuilder builder = new BasicChemicalRecipeBuilder().addFluid("fluidResult", fluidStack).addFluid("fluidResult", fluidStack)
				.addIngredient("itemInput",itemInput);

		if(!result.isEmpty())
		{
			builder.addResult(result);
		}
		else
		{
			builder.addResult(IngredientWithSize.of(ItemStack.EMPTY));
		}
		if(fluidInputA != null)
		{
			builder.addFluidTag("fluidInputA", fluidInputA);
		}
		if(fluidInputB != null)
		{
			builder.addFluidTag("fluidInputB", fluidInputB);
		}
		return builder;
	}
}
