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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GravitySeparatorRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;

import java.util.Calendar;

public class GravitySeparatorRecipeBuilder extends IEFinishedRecipe<GravitySeparatorRecipeBuilder>
{
	protected GravitySeparatorRecipeBuilder()
	{
		super(GravitySeparatorRecipe.SERIALIZER.get());
	}

	public static GravitySeparatorRecipeBuilder builder(Item result)
	{
		return new GravitySeparatorRecipeBuilder().addResult(result);
	}

	public static GravitySeparatorRecipeBuilder builder(ItemStack result)
	{
		return new GravitySeparatorRecipeBuilder().addResult(result);
	}

	public static GravitySeparatorRecipeBuilder builder(TagKey<Item> result)
	{
		return new GravitySeparatorRecipeBuilder().addResult(Ingredient.of(result));
	}

	public GravitySeparatorRecipeBuilder addInput(Ingredient ingredient)
	{
		return addIngredient(generateSafeInputKey(), ingredient);
	}

	public GravitySeparatorRecipeBuilder addInput(TagKey<Item> item)
	{
		return addIngredient(generateSafeInputKey(), Ingredient.of(item));
	}

	public GravitySeparatorRecipeBuilder setWater(int water)
	{
		return this.addWriter((jsonObject) -> {
			jsonObject.addProperty("water", water);
		});
	}

	public GravitySeparatorRecipeBuilder setChance(float chance)
	{
		return this.addWriter((jsonObject) -> {
			jsonObject.addProperty("byproduct_chance", chance);
		});
	}


	public GravitySeparatorRecipeBuilder setByproduct(ItemStack byproduct)
	{
		return this.addItem("byproduct", byproduct);
	}

	public GravitySeparatorRecipeBuilder setByproduct(Item byproduct)
	{
		return this.addItem("byproduct", new ItemStack(byproduct));
	}
}
