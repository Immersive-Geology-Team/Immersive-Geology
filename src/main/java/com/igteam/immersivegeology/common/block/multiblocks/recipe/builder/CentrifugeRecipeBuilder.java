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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CentrifugeRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.ChemicalRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class CentrifugeRecipeBuilder extends IEFinishedRecipe<CentrifugeRecipeBuilder>
{
	protected CentrifugeRecipeBuilder()
	{
		super(CentrifugeRecipe.SERIALIZER.get());
	}

	public static CentrifugeRecipeBuilder builder(ItemStack itemOutput, FluidTagInput fluidIn, int energy, int time, FluidStack primaryFluidOutput, FluidStack secondaryFluidOutput) {
		CentrifugeRecipeBuilder builder = new CentrifugeRecipeBuilder()
				.addIngredient("item_output", itemOutput)
				.addFluidTag("fluid_input", fluidIn)
				.setEnergy(energy)
				.setTime(time)
				.addFluid("primary_fluid_out", primaryFluidOutput)
				.addFluid("secondary_fluid_out", secondaryFluidOutput);
		return builder;
	}


}
