/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import com.google.gson.JsonArray;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.FoundryAlloyRecipe;
import net.minecraft.tags.TagKey;
import net.minecraftforge.fluids.FluidStack;
import net.minecraft.world.level.material.Fluid;

public class FoundryAlloyRecipeBuilder extends IEFinishedRecipe<FoundryAlloyRecipeBuilder>
{
	private final JsonArray inputs = new JsonArray();

	protected FoundryAlloyRecipeBuilder()
	{
		super(FoundryAlloyRecipe.SERIALIZER.get());
		addWriter(json -> json.add("inputs", inputs));
	}

	public static FoundryAlloyRecipeBuilder builder(FluidStack result)
	{
		return new FoundryAlloyRecipeBuilder().addFluid("result", result);
	}

	public FoundryAlloyRecipeBuilder addPart(TagKey<Fluid> fluidTag, int parts)
	{
		return addPart(new FluidTagInput(fluidTag, parts));
	}

	public FoundryAlloyRecipeBuilder addPart(FluidTagInput part)
	{
		inputs.add(part.serialize());
		return this;
	}
}
