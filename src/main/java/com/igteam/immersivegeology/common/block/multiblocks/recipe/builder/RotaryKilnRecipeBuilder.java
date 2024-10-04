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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RotaryKilnRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

public class RotaryKilnRecipeBuilder extends IEFinishedRecipe<RotaryKilnRecipeBuilder>
{
	protected RotaryKilnRecipeBuilder()
	{
		super(RotaryKilnRecipe.SERIALIZER.get());
	}

	public static RotaryKilnRecipeBuilder builder(Item result)
	{
		return new RotaryKilnRecipeBuilder().addResult(result);
	}

	public static RotaryKilnRecipeBuilder builder(ItemStack result)
	{
		return new RotaryKilnRecipeBuilder().addResult(result);
	}

	public static RotaryKilnRecipeBuilder builder(TagKey<Item> result, int count)
	{
		return new RotaryKilnRecipeBuilder().addResult(new IngredientWithSize(result, count));
	}

	public RotaryKilnRecipeBuilder addInput(IngredientWithSize ingredient)
	{
		return addIngredient(generateSafeInputKey(), ingredient);
	}

	public RotaryKilnRecipeBuilder addInput(TagKey<Item> itemTag)
	{
		return addIngredient(generateSafeInputKey(), itemTag);
	}

}
