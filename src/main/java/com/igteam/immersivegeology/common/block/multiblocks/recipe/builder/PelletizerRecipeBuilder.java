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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.PelletizerRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RotaryKilnRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PelletizerRecipeBuilder extends IEFinishedRecipe<PelletizerRecipeBuilder>
{
	protected PelletizerRecipeBuilder()
	{
		super(PelletizerRecipe.SERIALIZER.get());
	}

	public static PelletizerRecipeBuilder builder(Item result)
	{
		return new PelletizerRecipeBuilder().addResult(result);
	}

	public static PelletizerRecipeBuilder builder(ItemStack result)
	{
		return new PelletizerRecipeBuilder().addResult(result);
	}

	public static PelletizerRecipeBuilder builder(TagKey<Item> result, int count)
	{
		return new PelletizerRecipeBuilder().addResult(new IngredientWithSize(result, count));
	}

	public PelletizerRecipeBuilder addInput(IngredientWithSize ingredient)
	{
		return addIngredient(generateSafeInputKey(), ingredient);
	}

	public PelletizerRecipeBuilder addInput(TagKey<Item> itemTag)
	{
		return addIngredient(generateSafeInputKey(), itemTag);
	}

}
