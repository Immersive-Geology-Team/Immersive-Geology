/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.helper;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.igteam.immersivegeology.client.helper.IGRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;

public class TFCCollapseRecipeBuilder extends IGRecipeBuilder<TFCCollapseRecipeBuilder>
{
	protected TFCCollapseRecipeBuilder()
	{
		super(TFCDatagenCompat.invokeCollapseRecipe());
	}

	public static TFCCollapseRecipeBuilder builder(Ingredient result)
	{
		return (TFCCollapseRecipeBuilder) new TFCCollapseRecipeBuilder().addWriter((jsonObject) -> {
			// Create the root object for the "forge:conditional" type
			jsonObject.addProperty("type", "forge:conditional");
			// Create the recipes array
			JsonArray recipesArray = new JsonArray();

			JsonObject tfcLoadedCondition = conditionalRecipe(result);
			JsonObject emptyCondition = emptyRecipe(result);

			// Add the condition object to the recipes array
			recipesArray.add(tfcLoadedCondition);
			recipesArray.add(emptyCondition);

			// Add the recipes array to the conditionalJson
			jsonObject.add("recipes", recipesArray);

			// Add the conditionalJson object to the main jsonObject (output)
		});
	}

	public static JsonObject emptyRecipe(Ingredient result)
	{
		// Create the recipe object
		JsonObject recipeObject = new JsonObject();
		recipeObject.addProperty("type", "immersivegeology:empty");

		// Add the "recipe" object into the recipes array
		JsonObject conditionObject = new JsonObject();

		// Add conditions array and mod_loaded condition
		JsonArray conditionsArray = new JsonArray();
		JsonObject modLoadedCondition = new JsonObject();

		modLoadedCondition.addProperty("type", "forge:true");

		// Add the condition to conditions array
		conditionsArray.add(modLoadedCondition);

		// Add the conditions array to the recipe object
		conditionObject.add("conditions", conditionsArray);

		// Add the recipe object to the recipe list in conditions
		conditionObject.add("recipe", recipeObject);
		return conditionObject;
	}

	public static JsonObject conditionalRecipe(Ingredient result)
	{
		// Create the recipe object
		JsonObject recipeObject = new JsonObject();
		recipeObject.addProperty("type", "tfc:collapse");
		recipeObject.addProperty("copy_input", true);

		// Add the ingredient property (from the builder method)
		recipeObject.addProperty("ingredient", result.toJson().getAsJsonObject().get("item").getAsString());

		// Add the "recipe" object into the recipes array
		JsonObject conditionObject = new JsonObject();

		// Add conditions array and mod_loaded condition
		JsonArray conditionsArray = new JsonArray();
		JsonObject modLoadedCondition = new JsonObject();
		modLoadedCondition.addProperty("type", "forge:mod_loaded");
		modLoadedCondition.addProperty("modid", "tfc");

		// Add the condition to conditions array
		conditionsArray.add(modLoadedCondition);

		// Add the conditions array to the recipe object
		conditionObject.add("conditions", conditionsArray);

		// Add the recipe object to the recipe list in conditions
		conditionObject.add("recipe", recipeObject);
		return conditionObject;
	}
}
