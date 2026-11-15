/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import com.igteam.immersivegeology.core.registration.IGRecipeTypes;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FoundryAlloyRecipe extends MultiblockRecipe
{
	public static DeferredHolder<RecipeSerializer<?>, IERecipeSerializer<FoundryAlloyRecipe>> SERIALIZER;
	public static final CachedRecipeList<FoundryAlloyRecipe> RECIPES = new CachedRecipeList<>(IGRecipeTypes.FOUNDRY_ALLOYING);

	public final List<SizedFluidIngredient> alloyInputs;
	public final FluidStack alloyOutput;
	private final int energyPerUnit;
	private final int time;

	public FoundryAlloyRecipe(List<SizedFluidIngredient> inputs, FluidStack output, int energyPerUnit, int time)
	{
		super(TagOutput.EMPTY, IGRecipeTypes.FOUNDRY_ALLOYING, time, energyPerUnit, () -> new RecipeMultiplier(() -> 1, () -> 1));
		this.alloyInputs = List.copyOf(inputs);
		this.alloyOutput = output;
		this.energyPerUnit = energyPerUnit;
		this.time = time;
		this.fluidInputList = this.alloyInputs;
		this.fluidOutputList = List.of(output);
	}

	public int getInputVolumePerUnit()
	{
		int volume = 0;
		for(SizedFluidIngredient input : alloyInputs) volume += input.amount();
		return volume;
	}

	public int getOutputVolumePerUnit()
	{
		return alloyOutput.getAmount();
	}

	public int unitsFrom(List<FluidStack> available, int limit)
	{
		int units = limit;
		for(SizedFluidIngredient input : alloyInputs)
		{
			if(input.amount() <= 0) return 0;
			int stored = 0;
			for(FluidStack fluid : available)
				if(!fluid.isEmpty()&&input.ingredient().test(fluid)) stored += fluid.getAmount();
			units = Math.min(units, stored/input.amount());
			if(units <= 0) return 0;
		}
		return units;
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
	public int getTotalProcessTime()
	{
		return time;
	}

	@Override
	public int getTotalProcessEnergy()
	{
		return energyPerUnit;
	}

	@Override
	public int getMultipleProcessTicks()
	{
		return 0;
	}
}
