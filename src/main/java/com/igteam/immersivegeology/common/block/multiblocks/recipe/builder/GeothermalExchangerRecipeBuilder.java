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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CrystallizerRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GeothermalExchangerRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class GeothermalExchangerRecipeBuilder extends IEFinishedRecipe<GeothermalExchangerRecipeBuilder>
{
	protected GeothermalExchangerRecipeBuilder()
	{
		super(GeothermalExchangerRecipe.SERIALIZER.get());
	}

	public static GeothermalExchangerRecipeBuilder builder(FluidStack fluid_out)
	{
		return new GeothermalExchangerRecipeBuilder().addFluid("fluidResult", fluid_out);
	}

	public GeothermalExchangerRecipeBuilder addInput(FluidTagInput fluidTag)
	{
		return addFluidTag(generateSafeInputKey(), fluidTag);
	}

	public GeothermalExchangerRecipeBuilder addInput(TagKey<Fluid> fluidTag, int amount)
	{
		return addFluidTag(generateSafeInputKey(), fluidTag, amount);
	}

}
