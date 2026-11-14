/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe;

import blusunrize.immersiveengineering.api.crafting.*;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.Set;

public class ChemicalRecipe extends MultiblockRecipe
{
	public static DeferredHolder<RecipeSerializer<?>, IERecipeSerializer<ChemicalRecipe>> SERIALIZER;
	public static final CachedRecipeList<ChemicalRecipe> RECIPES = new CachedRecipeList<>(IGRecipeTypes.CHEMICAL_REACTOR);
	public final TagOutput itemOutput;
	public final FluidStack fluidOutput;
	public final Set<SizedFluidIngredient> fluidIn;
	public final IngredientWithSize itemInput;
	Lazy<Integer> totalProcessEnergy;
	Lazy<Integer> totalProcessTime;

	public <T extends Recipe<?>> ChemicalRecipe(IngredientWithSize inputItem, Set<SizedFluidIngredient> fluidInputSet, TagOutput itemOutput, FluidStack fluidOutput, int energy, int time)
	{
		super(TagOutput.EMPTY, IGRecipeTypes.CHEMICAL_REACTOR, time, energy, () -> new RecipeMultiplier(() -> 1, () -> 1));
		this.itemOutput = itemOutput;
		this.fluidOutput = fluidOutput;
		this.fluidIn = fluidInputSet;
		this.itemInput = inputItem;
		totalProcessEnergy = Lazy.of(() -> energy);
		totalProcessTime = Lazy.of(() -> time);
		this.outputList = new TagOutputList(this.itemOutput);
		this.fluidOutputList = List.of(fluidOutput);
		this.fluidInputList = fluidIn.stream().toList();
		this.setInputListWithSizes(List.of(itemInput));
		if(this.fluidIn.isEmpty() || this.fluidIn.size() > 3) IGLib.IG_LOGGER.error("Chemical Recipe has either NO or more than 3 Fluid Tag inputs in the set: {}", this.fluidIn);
	}

	public static boolean acceptableCatalyst(Level level, ItemStack stack)
	{
		for(RecipeHolder<ChemicalRecipe> recipe : RECIPES.getRecipes(level))
		{
			if(recipe.value().itemInput.testIgnoringSize(stack)){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean shouldCheckItemAvailability()
	{
		return !itemInput.hasNoMatchingItems();
	}

	@Override
	public NonNullList<ItemStack> getActualItemOutputs()
	{
		NonNullList<ItemStack> list = NonNullList.create();
		list.add(this.itemOutput.get());
		return list;
	}

	// Required for normal IE Multiblock Processor to access recipe info. Used in Server Tick.
	@Override
	public List<FluidStack> getActualFluidOutputs()
	{
		return List.of(fluidOutput);
	}

	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return SERIALIZER.get();
	}

	@Override
	public int getTotalProcessEnergy()
	{
		return totalProcessEnergy.get();
	}

	@Override
	public int getTotalProcessTime()
	{
		return totalProcessTime.get();
	}

	public static RecipeHolder<ChemicalRecipe> findRecipe(Level level, FluidStack inputA, FluidStack inputB, FluidStack inputC, ItemStack itemInput)
	{
		// TODO: Ensure this is tested a LOT.
		List<FluidStack> tankedFluids = List.of(inputA, inputB, inputC);
		RecipeHolder<ChemicalRecipe> bestMatch = null;
		for(RecipeHolder<ChemicalRecipe> recipe : RECIPES.getRecipes(level))
		{
			if(!recipe.value().itemInput.test(itemInput)) continue;

			Set<SizedFluidIngredient> recipeFluids = recipe.value().fluidIn;

			boolean allFluidsAvailable = recipeFluids.stream().allMatch(rf -> tankedFluids.stream().anyMatch(rf::test));

			if(allFluidsAvailable)
			{
				if(bestMatch == null || recipeFluids.size() > bestMatch.value().fluidIn.size())
				{
					bestMatch = recipe;
				}
			}
		}

		return bestMatch;
	}

	@Override
	protected IERecipeSerializer<?> getIESerializer()
	{
		return SERIALIZER.get();
	}

	@Override
	public int getMultipleProcessTicks()
	{
		return 1;
	}
}
