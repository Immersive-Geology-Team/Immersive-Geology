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
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class CrystallizerRecipeBuilder extends IEFinishedRecipe<CrystallizerRecipeBuilder>
{
	protected CrystallizerRecipeBuilder()
	{
		super(CrystallizerRecipe.SERIALIZER.get());
	}

	public static CrystallizerRecipeBuilder builder(Item result, FluidStack fluid_out)
	{
		return new CrystallizerRecipeBuilder().addResult(result).addFluid("fluidResult", fluid_out);
	}

	public static CrystallizerRecipeBuilder builder(ItemStack result, FluidStack fluid_out)
	{
		return new CrystallizerRecipeBuilder().addResult(result).addFluid("fluidResult", fluid_out);
	}

	public static CrystallizerRecipeBuilder builder(TagKey<Item> result, int count, FluidStack fluid_out)
	{
		return new CrystallizerRecipeBuilder().addResult(new IngredientWithSize(result, count)).addFluid("fluidResult", fluid_out);
	}

	public CrystallizerRecipeBuilder addInput(FluidTagInput fluidTag)
	{
		return addFluidTag(generateSafeInputKey(), fluidTag);
	}

	public CrystallizerRecipeBuilder addInput(TagKey<Fluid> fluidTag, int amount)
	{
		return addFluidTag(generateSafeInputKey(), fluidTag, amount);
	}

}
