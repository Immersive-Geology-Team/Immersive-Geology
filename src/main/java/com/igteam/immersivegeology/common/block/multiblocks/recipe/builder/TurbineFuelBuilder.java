/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.energy.GeneratorFuel;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.TurbineFuel;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public class TurbineFuelBuilder  extends IGMultiblockRecipeBuilder<TurbineFuelBuilder>
{
	public static final String FLUID_TAG_KEY = "fluidTag";
	public static final String BURN_TIME_KEY = "burnTime";
	public static final String CONSUME_AMOUNT_KEY = "consume_amount";
	public static final String OUTPUT_RATIO = "outputRatio";

	private final TagKey<Fluid> fluid;
	private final float outputRatio;
	private final int burnTime;
	private final int consume_amount;

	private TurbineFuelBuilder(TagKey<Fluid> fluid, float outputRatio, int burnTime, int consume_amount)
	{
		this.fluid = fluid;
		this.outputRatio = outputRatio;
		this.burnTime = burnTime;
		this.consume_amount = consume_amount;
	}

	public static TurbineFuelBuilder builder(TagKey<Fluid> fluid, float outputRatio, int burnTime, int consume_amount)
	{
		return new TurbineFuelBuilder(fluid, outputRatio, burnTime, consume_amount);
	}

	public void build(RecipeOutput out, ResourceLocation name)
	{
		TurbineFuel recipe = new TurbineFuel(fluid, outputRatio, consume_amount, burnTime);
		out.accept(name, recipe, null, getConditions());
	}
}
