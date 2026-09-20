/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BulkBlastFluxRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class BulkBlastFluxBuilder extends IEFinishedRecipe<BulkBlastFluxBuilder>
{
	private BulkBlastFluxBuilder()
	{
		super(BulkBlastFluxRecipe.SERIALIZER.get());
		this.maxResultCount = 0;
	}

	public static BulkBlastFluxBuilder builder(ItemLike input)
	{
		return new BulkBlastFluxBuilder().addInput(input);
	}

	public static BulkBlastFluxBuilder builder(ItemStack input)
	{
		return new BulkBlastFluxBuilder().addInput(input);
	}

	public static BulkBlastFluxBuilder builder(TagKey<Item> input)
	{
		return new BulkBlastFluxBuilder().addInput(Ingredient.of(input));
	}

	public BulkBlastFluxBuilder setFluxValue(int fluxValue)
	{
		return addWriter(json -> json.addProperty("flux_value", fluxValue));
	}
}
