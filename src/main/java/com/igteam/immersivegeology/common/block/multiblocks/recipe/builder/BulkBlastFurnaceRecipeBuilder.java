/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.google.common.base.Preconditions;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BulkBlastFurnaceRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class BulkBlastFurnaceRecipeBuilder extends IGMultiblockRecipeBuilder<BulkBlastFurnaceRecipeBuilder>
{
	private IngredientWithSize oreInput;
	private SizedFluidIngredient meltInput;
	private final FluidStack melt;
	private FluidStack richMelt = FluidStack.EMPTY;
	private Float cokeRatio;
	private float richCokeRatio;
	private Float fluxRatio;
	private Float minYield;
	private Float maxYield;
	private Integer heat;
	private Integer time;

	private BulkBlastFurnaceRecipeBuilder(FluidStack melt)
	{
		this.melt = melt;
	}

	public static BulkBlastFurnaceRecipeBuilder builder(FluidStack melt)
	{
		return new BulkBlastFurnaceRecipeBuilder(melt);
	}

	public BulkBlastFurnaceRecipeBuilder setOre(TagKey<Item> ore, int count)
	{
		return setOre(new IngredientWithSize(ore, count));
	}

	public BulkBlastFurnaceRecipeBuilder setOre(IngredientWithSize ore)
	{
		this.oreInput = ore;
		return this;
	}

	public BulkBlastFurnaceRecipeBuilder setMeltInput(SizedFluidIngredient meltInput)
	{
		this.meltInput = meltInput;
		return this;
	}

	public BulkBlastFurnaceRecipeBuilder setRichRegime(FluidStack richMelt, float richCokeRatio)
	{
		this.richMelt = richMelt;
		this.richCokeRatio = richCokeRatio;
		return this;
	}

	public BulkBlastFurnaceRecipeBuilder setRatios(float cokeRatio, float fluxRatio)
	{
		this.cokeRatio = cokeRatio;
		this.fluxRatio = fluxRatio;
		return this;
	}

	public BulkBlastFurnaceRecipeBuilder setYield(float minYield, float maxYield)
	{
		this.minYield = minYield;
		this.maxYield = maxYield;
		return this;
	}

	public BulkBlastFurnaceRecipeBuilder setHeat(int heat)
	{
		this.heat = heat;
		return this;
	}

	public BulkBlastFurnaceRecipeBuilder setTime(int time)
	{
		this.time = time;
		return this;
	}

	public void build(RecipeOutput out, ResourceLocation name)
	{
		Preconditions.checkArgument((oreInput==null)!=(meltInput==null), "Exactly one of ore or melt input is required");
		Preconditions.checkArgument(cokeRatio!=null&&fluxRatio!=null&&minYield!=null&&maxYield!=null&&heat!=null&&time!=null, "This recipe is incomplete");
		BulkBlastFurnaceRecipe recipe = new BulkBlastFurnaceRecipe(oreInput, meltInput, melt, richMelt,
				cokeRatio, richCokeRatio, fluxRatio, minYield, maxYield, heat, time);
		out.accept(name, recipe, null, getConditions());
	}
}
