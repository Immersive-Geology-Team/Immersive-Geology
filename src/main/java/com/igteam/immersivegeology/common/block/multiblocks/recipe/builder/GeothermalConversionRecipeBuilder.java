/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GeothermalConversionRecipe;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class GeothermalConversionRecipeBuilder  extends IGMultiblockRecipeBuilder<GeothermalConversionRecipeBuilder>
{
	private final Block transitionBlock;
	private final int blockHeat;
	@Nullable
	private final Pair<Block, Integer> upperBound;
	@Nullable
	private final Pair<Block, Integer> lowerBound;

	private GeothermalConversionRecipeBuilder(Block transitionBlock, int blockHeat, @Nullable Pair<Block, Integer> upperBound, @Nullable Pair<Block, Integer> lowerBound)
	{
		this.transitionBlock = transitionBlock;
		this.blockHeat = blockHeat;
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
	}

	public static GeothermalConversionRecipeBuilder builder(Block transitionBlock, int blockHeat)
	{
		return new GeothermalConversionRecipeBuilder(transitionBlock, blockHeat, null, null);
	}

	public static GeothermalConversionRecipeBuilder builder(Block transitionBlock, int blockHeat, @Nullable Pair<Block, Integer> upperBound, @Nullable Pair<Block, Integer> lowerBound)
	{
		return new GeothermalConversionRecipeBuilder(transitionBlock, blockHeat, upperBound, lowerBound);
	}

	public void build(RecipeOutput out, ResourceLocation name)
	{
		GeothermalConversionRecipe recipe = new GeothermalConversionRecipe(() -> transitionBlock, blockHeat, upperBound, lowerBound);
		out.accept(name, recipe, null, getConditions());
	}
}
