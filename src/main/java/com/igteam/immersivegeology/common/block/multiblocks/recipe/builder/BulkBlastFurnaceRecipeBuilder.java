/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BulkBlastFurnaceRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.fluids.FluidStack;

public class BulkBlastFurnaceRecipeBuilder extends IEFinishedRecipe<BulkBlastFurnaceRecipeBuilder>
{
	protected BulkBlastFurnaceRecipeBuilder()
	{
		super(BulkBlastFurnaceRecipe.SERIALIZER.get());
	}

	public static BulkBlastFurnaceRecipeBuilder builder(FluidStack melt)
	{
		return new BulkBlastFurnaceRecipeBuilder().addFluid("melt", melt);
	}

	public BulkBlastFurnaceRecipeBuilder setOre(TagKey<Item> ore, int count)
	{
		return addIngredient("ore", new IngredientWithSize(ore, count));
	}

	public BulkBlastFurnaceRecipeBuilder setOre(IngredientWithSize ore)
	{
		return addIngredient("ore", ore);
	}

	public BulkBlastFurnaceRecipeBuilder setMeltInput(FluidTagInput meltInput)
	{
		return addFluidTag("melt_input", meltInput);
	}

	public BulkBlastFurnaceRecipeBuilder setRichRegime(FluidStack richMelt, float richCokeRatio)
	{
		return addFluid("rich_melt", richMelt).addWriter(json -> json.addProperty("rich_coke_ratio", richCokeRatio));
	}

	public BulkBlastFurnaceRecipeBuilder setRatios(float cokeRatio, float fluxRatio)
	{
		return addWriter(json -> {
			json.addProperty("coke_ratio", cokeRatio);
			json.addProperty("flux_ratio", fluxRatio);
		});
	}

	public BulkBlastFurnaceRecipeBuilder setYield(float minYield, float maxYield)
	{
		return addWriter(json -> {
			json.addProperty("min_yield", minYield);
			json.addProperty("max_yield", maxYield);
		});
	}

	public BulkBlastFurnaceRecipeBuilder setHeat(int heat)
	{
		return addWriter(json -> json.addProperty("heat", heat));
	}
}
