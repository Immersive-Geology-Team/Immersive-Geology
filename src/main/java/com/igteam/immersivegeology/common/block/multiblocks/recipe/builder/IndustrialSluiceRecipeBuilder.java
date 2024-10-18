/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.StackWithChance;
import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.IndustrialSluiceRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.core.NonNullList;
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

	public IndustrialSluiceRecipeBuilder setByproducts(NonNullList<StackWithChance> byproducts)
	{
		IGLib.IG_LOGGER.info("Stack checker ====== {}", byproducts.size());
		return this.addWriter(obj -> writeByproductsToJson(obj, byproducts));
	}

	private static void writeByproductsToJson(JsonObject obj, NonNullList<StackWithChance> byproducts) {
		// Store the number of byproducts
		obj.addProperty("amount_of_byproducts", byproducts.size());

		// Create a JsonArray to store all byproduct items
		JsonArray jsonArray = new JsonArray();

		// Iterate through each byproduct and add it to the JsonArray
		for(StackWithChance byproduct : byproducts)
		{
			JsonObject itemObject;
			ItemStack stack = byproduct.stack().get();
			float chance = byproduct.chance();
			itemObject = IEFinishedRecipe.serializeStackWithChance(IngredientWithSize.of(stack), chance);
			jsonArray.add(itemObject);
		}

		// Add the JsonArray to the main object
		obj.add("byproducts", jsonArray);
	}

	public IndustrialSluiceRecipeBuilder setByproduct(Item byproduct)
	{
		return this.addItem("byproduct", new ItemStack(byproduct));
	}

	public IndustrialSluiceRecipeBuilder setChances(NonNullList<Float> chances)
	{return this.addWriter((jsonObject -> {
		for(Float byproduct : chances) jsonObject.addProperty("chance_" + chances.indexOf(byproduct), byproduct);
	}));
	}
}
