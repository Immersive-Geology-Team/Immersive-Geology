/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GeothermalConversionRecipe;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class GeothermalConversionRecipeBuilder  extends IEFinishedRecipe<GeothermalConversionRecipeBuilder>
{

	private GeothermalConversionRecipeBuilder(Block transitionBlock, int blockHeat, @Nullable Pair<Block, Integer> upperBound, @Nullable Pair<Block, Integer> lowerBound)
	{
		super(GeothermalConversionRecipe.SERIALIZER.get());
		this.addWriter((obj) -> {
			obj.addProperty("transitionBlock", BuiltInRegistries.BLOCK.getKey(transitionBlock).toString());
		});
		this.addWriter((obj) -> {
			obj.addProperty("blockHeat", blockHeat);
		});

		if(upperBound != null)
		{
			this.addWriter((obj) -> {
				obj.addProperty("upperBoundBlock", BuiltInRegistries.BLOCK.getKey(upperBound.getFirst()).toString());
			});
			this.addWriter((obj) -> {
				obj.addProperty("upperHeat", upperBound.getSecond());
			});
		}

		if(lowerBound != null)
		{
			this.addWriter((obj) -> {
				obj.addProperty("lowerBoundBlock", BuiltInRegistries.BLOCK.getKey(lowerBound.getFirst()).toString());
			});
			this.addWriter((obj) -> {
				obj.addProperty("lowerHeat", lowerBound.getSecond());
			});
		}
	}

	public static GeothermalConversionRecipeBuilder builder(Block transitionBlock, int blockHeat) {
		return new GeothermalConversionRecipeBuilder(transitionBlock, blockHeat, null, null);
	}

	public static GeothermalConversionRecipeBuilder builder(Block transitionBlock, int blockHeat, @Nullable Pair<Block, Integer> upperBound, @Nullable Pair<Block, Integer> lowerBound) {
		return new GeothermalConversionRecipeBuilder(transitionBlock, blockHeat, upperBound, lowerBound);
	}
}
