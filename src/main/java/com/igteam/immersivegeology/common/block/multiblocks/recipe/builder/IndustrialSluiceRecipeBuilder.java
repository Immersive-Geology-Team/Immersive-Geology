/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GravitySeparatorRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.IndustrialSluiceRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class IndustrialSluiceRecipeBuilder extends IEFinishedRecipe<IndustrialSluiceRecipeBuilder>
{
	protected IndustrialSluiceRecipeBuilder()
	{
		super(IndustrialSluiceRecipe.SERIALIZER.get());
	}

	public static IndustrialSluiceRecipeBuilder builder(Item result)
	{
		return new IndustrialSluiceRecipeBuilder().addResult(result);
	}

	public static IndustrialSluiceRecipeBuilder builder(ItemStack result)
	{
		return new IndustrialSluiceRecipeBuilder().addResult(result);
	}

	public static IndustrialSluiceRecipeBuilder builder(TagKey<Item> result)
	{
		return new IndustrialSluiceRecipeBuilder().addResult(Ingredient.of(result));
	}

	public IndustrialSluiceRecipeBuilder addInput(Ingredient ingredient)
	{
		return addIngredient(generateSafeInputKey(), ingredient);
	}

	public IndustrialSluiceRecipeBuilder addInput(TagKey<Item> item)
	{
		return addIngredient(generateSafeInputKey(), Ingredient.of(item));
	}

	public IndustrialSluiceRecipeBuilder setWater(int water)
	{
		return this.addWriter((jsonObject) -> {
			jsonObject.addProperty("water", water);
		});
	}

	public IndustrialSluiceRecipeBuilder setChance(float chance)
	{
		return this.addWriter((jsonObject) -> {
			jsonObject.addProperty("byproduct_chance", chance);
		});
	}


	public IndustrialSluiceRecipeBuilder setByproduct(ItemStack byproduct)
	{
		return this.addItem("byproduct", byproduct);
	}

	public IndustrialSluiceRecipeBuilder setByproduct(Item byproduct)
	{
		return this.addItem("byproduct", new ItemStack(byproduct));
	}
}
