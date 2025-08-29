/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.builders.GeneratorFuelBuilder;
import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import blusunrize.immersiveengineering.api.energy.GeneratorFuel;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.TurbineFuel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public class TurbineFuelBuilder  extends IEFinishedRecipe<TurbineFuelBuilder>
{
	public static final String FLUID_TAG_KEY = "fluidTag";
	public static final String BURN_TIME_KEY = "burnTime";
	public static final String CONSUME_AMOUNT_KEY = "consume_amount";
	public static final String OUTPUT_RATIO = "outputRatio";

	private TurbineFuelBuilder(TagKey<Fluid> fluid, float outputRatio, int burnTime, int consume_amount) {
		super(TurbineFuel.SERIALIZER.get());
		this.addWriter((obj) -> {
			obj.addProperty("fluidTag", fluid.location().toString());
		});
		this.addWriter((obj) -> {
			obj.addProperty("outputRatio", outputRatio);
		});
		this.addWriter((obj) -> {
			obj.addProperty("burnTime", burnTime);
		});
		this.addWriter((obj) -> {
			obj.addProperty("consume_amount", consume_amount);
		});
	}

	public static TurbineFuelBuilder builder(TagKey<Fluid> fluid, float outputRatio, int burnTime, int consume_amount) {
		return new TurbineFuelBuilder(fluid, outputRatio, burnTime, consume_amount);
	}
}
