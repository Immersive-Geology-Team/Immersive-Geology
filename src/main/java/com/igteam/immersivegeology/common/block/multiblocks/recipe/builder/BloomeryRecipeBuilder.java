/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BloomeryRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RevFurnaceRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BloomeryRecipeBuilder extends IEFinishedRecipe<BloomeryRecipeBuilder>
{
	protected BloomeryRecipeBuilder()
	{
		super(BloomeryRecipe.SERIALIZER.get());
	}

	public static BloomeryRecipeBuilder builder(Item result)
	{
		return new BloomeryRecipeBuilder().addResult(result);
	}

	public static BloomeryRecipeBuilder builder(ItemStack result)
	{
		return new BloomeryRecipeBuilder().addResult(result);
	}

	public static BloomeryRecipeBuilder builder(TagKey<Item> result, int count)
	{
		return new BloomeryRecipeBuilder().addResult(new IngredientWithSize(result, count));
	}
}
