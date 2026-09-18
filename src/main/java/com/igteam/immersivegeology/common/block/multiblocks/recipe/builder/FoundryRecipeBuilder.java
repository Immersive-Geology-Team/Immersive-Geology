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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.FoundryRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

public class FoundryRecipeBuilder extends IEFinishedRecipe<FoundryRecipeBuilder>
{
	protected FoundryRecipeBuilder()
	{
		super(FoundryRecipe.SERIALIZER.get());
	}

	public static FoundryRecipeBuilder builder(ItemLike result)
	{
		return new FoundryRecipeBuilder().addResult(result);
	}

	public static FoundryRecipeBuilder builder(ItemStack result)
	{
		return new FoundryRecipeBuilder().addResult(result);
	}

	public static FoundryRecipeBuilder builder(TagKey<Item> result, int count)
	{
		return new FoundryRecipeBuilder().addResult(new IngredientWithSize(result, count));
	}

	public FoundryRecipeBuilder addInput(FluidTagInput fluidTag)
	{
		return addFluidTag(generateSafeInputKey(), fluidTag);
	}

	public FoundryRecipeBuilder addInput(TagKey<Fluid> fluidTag, int amount)
	{
		return addFluidTag(generateSafeInputKey(), fluidTag, amount);
	}

	public FoundryRecipeBuilder setMold(ItemLike mold)
	{
		return addWriter(json -> json.addProperty("mold", ForgeRegistries.ITEMS.getKey(mold.asItem()).toString()));
	}
}
