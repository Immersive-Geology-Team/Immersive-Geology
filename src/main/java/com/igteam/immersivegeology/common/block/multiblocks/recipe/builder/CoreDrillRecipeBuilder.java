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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CoreDrillRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CrystallizerRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class CoreDrillRecipeBuilder extends IEFinishedRecipe<CoreDrillRecipeBuilder>
{
	protected CoreDrillRecipeBuilder()
	{
		super(CoreDrillRecipe.SERIALIZER.get());
	}

	public static CoreDrillRecipeBuilder builder(Fluid fluid_out)
	{
		return new CoreDrillRecipeBuilder().addFluid("fluidResult", new FluidStack(fluid_out, 1));
	}

	public CoreDrillRecipeBuilder addInput(FluidTagInput fluidTag)
	{
		return addFluidTag(generateSafeInputKey(), fluidTag);
	}

	public CoreDrillRecipeBuilder addInput(TagKey<Fluid> fluidTag, int amount)
	{
		return addFluidTag(generateSafeInputKey(), fluidTag, amount);
	}

}
