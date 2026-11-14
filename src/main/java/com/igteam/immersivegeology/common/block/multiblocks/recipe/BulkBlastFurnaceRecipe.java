/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import com.igteam.immersivegeology.core.registration.IGRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BulkBlastFurnaceRecipe extends MultiblockRecipe
{
	public static DeferredHolder<RecipeSerializer<?>, IERecipeSerializer<BulkBlastFurnaceRecipe>> SERIALIZER;
	public static final CachedRecipeList<BulkBlastFurnaceRecipe> RECIPES = new CachedRecipeList<>(IGRecipeTypes.BULK_BLAST_FURNACE);

	@Nullable
	public final IngredientWithSize oreInput;
	@Nullable
	public final SizedFluidIngredient meltInput;
	public final FluidStack meltPerUnit;
	public final FluidStack richMelt;
	public final float cokeRatio;
	public final float richCokeRatio;
	public final float fluxRatio;
	public final float minYield;
	public final float maxYield;
	private final int heatRequired;
	private final int totalProcessTime;

	public BulkBlastFurnaceRecipe(
			@Nullable IngredientWithSize oreInput, @Nullable SizedFluidIngredient meltInput,
			FluidStack meltPerUnit, FluidStack richMelt, float cokeRatio, float richCokeRatio, float fluxRatio,
			float minYield, float maxYield, int heatRequired, int time
	)
	{
		super(TagOutput.EMPTY, IGRecipeTypes.BULK_BLAST_FURNACE, time, 0, () -> new RecipeMultiplier(() -> 1, () -> 1));
		this.oreInput = oreInput;
		this.meltInput = meltInput;
		this.meltPerUnit = meltPerUnit;
		this.richMelt = richMelt;
		this.cokeRatio = cokeRatio;
		this.richCokeRatio = richCokeRatio;
		this.fluxRatio = fluxRatio;
		this.minYield = minYield;
		this.maxYield = maxYield;
		this.heatRequired = heatRequired;
		this.totalProcessTime = time;
	}

	public boolean hasRichRegime()
	{
		return !richMelt.isEmpty()&&richCokeRatio > 0;
	}

	public Regime regimeFor(float actualCokeRatio)
	{
		if(!hasRichRegime()) return new Regime(meltPerUnit, cokeRatio);
		boolean rich = Math.abs(actualCokeRatio-richCokeRatio) < Math.abs(actualCokeRatio-cokeRatio);
		return rich?new Regime(richMelt, richCokeRatio): new Regime(meltPerUnit, cokeRatio);
	}

	public int getHeatRequired()
	{
		return heatRequired;
	}

	@Override
	public int getTotalProcessTime()
	{
		return totalProcessTime;
	}

	@Override
	public int getTotalProcessEnergy()
	{
		return 0;
	}

	public float metalFraction(float quality)
	{
		return minYield+(maxYield-minYield)*Math.max(0, Math.min(1, quality));
	}

	@Nullable
	public static BulkBlastFurnaceRecipe findRecipe(@Nullable Level level, ItemStack ore)
	{
		if(level==null||ore.isEmpty()) return null;
		for(RecipeHolder<BulkBlastFurnaceRecipe> recipe : RECIPES.getRecipes(level))
			if(recipe.value().oreInput!=null&&recipe.value().oreInput.testIgnoringSize(ore))
				return recipe.value();
		return null;
	}

	@Nullable
	public static BulkBlastFurnaceRecipe findRefiningRecipe(@Nullable Level level, FluidStack melt)
	{
		if(level==null||melt.isEmpty()) return null;
		for(RecipeHolder<BulkBlastFurnaceRecipe> recipe : RECIPES.getRecipes(level))
			if(recipe.value().meltInput!=null&&recipe.value().meltInput.ingredient().test(melt))
				return recipe.value();
		return null;
	}

	@Override
	public @NotNull RecipeSerializer<?> getSerializer()
	{
		return SERIALIZER.get();
	}

	@Override
	protected IERecipeSerializer<?> getIESerializer()
	{
		return SERIALIZER.get();
	}

	@Override
	public int getMultipleProcessTicks()
	{
		return 0;
	}

	public record Regime(FluidStack melt, float cokeIdeal)
	{
	}
}
