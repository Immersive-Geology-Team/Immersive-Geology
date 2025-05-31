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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

public class GeothermalConversionRecipeBuilder  extends IEFinishedRecipe<GeothermalConversionRecipeBuilder>
{

	private GeothermalConversionRecipeBuilder(Block startingBlock, Block transitionBlock, Block finalBlock, int sIndex, int tIndex, int fIndex)
	{
		super(GeothermalConversionRecipe.SERIALIZER.get());
		this.addWriter((obj) -> {
			obj.addProperty("startingBlock", BuiltInRegistries.BLOCK.getKey(startingBlock).toString());
		});
		this.addWriter((obj) -> {
			obj.addProperty("transitionBlock", BuiltInRegistries.BLOCK.getKey(transitionBlock).toString());
		});
		this.addWriter((obj) -> {
			obj.addProperty("finalBlock", BuiltInRegistries.BLOCK.getKey(finalBlock).toString());
		});

		this.addWriter((obj) -> {
			obj.addProperty("startingBlockIndex", sIndex);
		});
		this.addWriter((obj) -> {
			obj.addProperty("transitionBlockIndex", tIndex);
		});
		this.addWriter((obj) -> {
			obj.addProperty("finalBlockIndex", fIndex);
		});
	}

	public static GeothermalConversionRecipeBuilder builder(Block[] blockData, int[] index) {
		return new GeothermalConversionRecipeBuilder(blockData[0], blockData[1], blockData[2], index[0], index[1], index[2]);
	}
}
