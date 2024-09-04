/*
 * BluSunrize
 * Copyright (c) 2024
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CrystallizerRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

public class CrystallizerRecipeBuilder extends IEFinishedRecipe<CrystallizerRecipeBuilder>
{
	protected CrystallizerRecipeBuilder()
	{
		super(CrystallizerRecipe.SERIALIZER.get());
	}

	public static CrystallizerRecipeBuilder builder(Item result)
	{
		return new CrystallizerRecipeBuilder().addResult(result);
	}

	public static CrystallizerRecipeBuilder builder(ItemStack result)
	{
		return new CrystallizerRecipeBuilder().addResult(result);
	}

	public static CrystallizerRecipeBuilder builder(TagKey<Item> result, int count)
	{
		return new CrystallizerRecipeBuilder().addResult(new IngredientWithSize(result, count));
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
