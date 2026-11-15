/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import blusunrize.immersiveengineering.api.crafting.TagOutputList;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import com.igteam.immersivegeology.core.registration.IGRecipeTypes;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.registries.DeferredHolder;

public class CentrifugeRecipe extends MultiblockRecipe
{
	public static DeferredHolder<RecipeSerializer<?>, IERecipeSerializer<CentrifugeRecipe>> SERIALIZER;
	public static final CachedRecipeList<CentrifugeRecipe> RECIPES = new CachedRecipeList<>(IGRecipeTypes.CENTRIFUGE);
	public final TagOutput itemOutput;
	public final SizedFluidIngredient fluidIn;

	public final Lazy<FluidStack> primaryFluidOutput;
	public final Lazy<FluidStack> secondaryFluidOutput;

	Lazy<Integer> totalProcessEnergy;
	Lazy<Integer> totalProcessTime;

	public <T extends Recipe<?>> CentrifugeRecipe(SizedFluidIngredient fluidInput, TagOutput output, Lazy<FluidStack> primaryFluidOutput, Lazy<FluidStack> secondaryFluidOutput, int energy, int time)
	{
		super(TagOutput.EMPTY, IGRecipeTypes.CENTRIFUGE, time, energy, () -> new RecipeMultiplier(() -> 1, () -> 1));
		this.itemOutput = output;
		this.fluidIn = fluidInput;
		totalProcessEnergy = Lazy.of(() -> energy);
		totalProcessTime = Lazy.of(() -> time);
		this.primaryFluidOutput = primaryFluidOutput;
		this.secondaryFluidOutput = secondaryFluidOutput;

		this.outputList = new TagOutputList(this.itemOutput);
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

	public static RecipeHolder<CentrifugeRecipe> findRecipe(Level level, FluidStack input)
	{
		for(RecipeHolder<CentrifugeRecipe> recipe : RECIPES.getRecipes(level))
			if(recipe.value().fluidIn.test(input))
				return recipe;
		return null;
	}

	@Override
	protected IERecipeSerializer<?> getIESerializer()
	{
		return SERIALIZER.get();
	}

	@Override
	public int getMultipleProcessTicks()
	{
		return 4;
	}
}
