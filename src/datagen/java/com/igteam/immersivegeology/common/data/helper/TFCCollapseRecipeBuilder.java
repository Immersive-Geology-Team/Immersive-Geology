/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.helper;

import com.google.gson.JsonArray;
import com.igteam.immersivegeology.client.helper.IGRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

public class TFCCollapseRecipeBuilder extends IGRecipeBuilder<TFCCollapseRecipeBuilder>
{
	protected TFCCollapseRecipeBuilder(HolderLookup.Provider provider)
	{
		super(TFCDatagenCompat.invokeCollapseRecipe(), provider);
	}

	public static TFCCollapseRecipeBuilder builder(Block block, HolderLookup.Provider provider)
	{
		TFCCollapseRecipeBuilder builder = new TFCCollapseRecipeBuilder(provider);
		builder.addWriter(json -> {
			JsonArray ingredient = new JsonArray();
			ingredient.add(BuiltInRegistries.BLOCK.getKey(block).toString());
			json.add("ingredient", ingredient);
		});
		builder.addCondition(new ModLoadedCondition("tfc"));
		return builder;
	}
}
