/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BloomeryFuel;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.PelletizerFuel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class PelletizerFuelBuilder extends IEFinishedRecipe<PelletizerFuelBuilder> {
	private PelletizerFuelBuilder() {
		super(PelletizerFuel.SERIALIZER.get());
		this.maxResultCount = 0;
	}

	public static PelletizerFuelBuilder builder(ItemLike input) {
		return new PelletizerFuelBuilder().addInput(new ItemLike[]{input});
	}

	public static PelletizerFuelBuilder builder(ItemStack input) {
		return new PelletizerFuelBuilder().addInput(new ItemStack[]{input});
	}

	public static PelletizerFuelBuilder builder(TagKey<Item> input) {
		return new PelletizerFuelBuilder().addInput(Ingredient.of(input));
	}
}
