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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BallmillRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RotaryKilnRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer.BallmillRecipeSerializer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BallmillRecipeBuilder extends IEFinishedRecipe<BallmillRecipeBuilder>
{
	protected BallmillRecipeBuilder()
	{
		super(BallmillRecipe.SERIALIZER.get());
	}

	public static BallmillRecipeBuilder builder(Item result)
	{
		return new BallmillRecipeBuilder().addResult(result);
	}

	public static BallmillRecipeBuilder builder(ItemStack result)
	{
		return new BallmillRecipeBuilder().addResult(result);
	}

	public static BallmillRecipeBuilder builder(TagKey<Item> result, int count)
	{
		return new BallmillRecipeBuilder().addResult(new IngredientWithSize(result, count));
	}

	public BallmillRecipeBuilder addInput(IngredientWithSize ingredient)
	{
		return addIngredient(generateSafeInputKey(), ingredient);
	}

	public BallmillRecipeBuilder addInput(TagKey<Item> itemTag)
	{
		return addIngredient(generateSafeInputKey(), itemTag);
	}

}
