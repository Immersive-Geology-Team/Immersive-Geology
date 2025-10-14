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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.ChemicalRepairRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class ChemicalRepairBuilder extends IEFinishedRecipe<ChemicalRepairBuilder> {
	private ChemicalRepairBuilder() {
		super(ChemicalRepairRecipe.SERIALIZER.get());
		this.maxResultCount = 0;
	}

	public static ChemicalRepairBuilder builder(ItemLike input) {
		return new ChemicalRepairBuilder().addInput(new ItemLike[]{input});
	}

	public static ChemicalRepairBuilder builder(ItemStack input) {
		return new ChemicalRepairBuilder().addInput(new ItemStack[]{input});
	}

	public static ChemicalRepairBuilder builder(TagKey<Item> input) {
		return new ChemicalRepairBuilder().addInput(Ingredient.of(input));
	}
}
