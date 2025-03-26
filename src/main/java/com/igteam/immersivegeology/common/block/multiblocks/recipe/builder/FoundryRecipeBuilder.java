/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import blusunrize.immersiveengineering.api.crafting.FermenterRecipe;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CrystallizerRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FoundryRecipeBuilder extends IEFinishedRecipe<FoundryRecipeBuilder>
{
	protected FoundryRecipeBuilder()
	{
		super(FermenterRecipe.SERIALIZER.get());
	}

	public static FoundryRecipeBuilder builder(Item result)
	{
		return new FoundryRecipeBuilder().addResult(result);
	}

	public static FoundryRecipeBuilder builder(ItemStack result)
	{
		return new FoundryRecipeBuilder().addResult(result);
	}

	public static FoundryRecipeBuilder builder(TagKey<Item> result, int count)
	{
		return new FoundryRecipeBuilder().addResult(new IngredientWithSize(result, count));
	}

	public FoundryRecipeBuilder addInput(FluidTagInput fluidTag)
	{
		return addFluidTag(generateSafeInputKey(), fluidTag);
	}

	public FoundryRecipeBuilder addInput(TagKey<Fluid> fluidTag, int amount)
	{
		return addFluidTag(generateSafeInputKey(), fluidTag, amount);
	}

}
