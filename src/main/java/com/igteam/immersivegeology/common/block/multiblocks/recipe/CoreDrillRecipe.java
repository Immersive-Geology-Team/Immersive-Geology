/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IERecipeTypes.TypeWithClass;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import com.igteam.immersivegeology.core.registration.IGRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class CoreDrillRecipe extends MultiblockRecipe
{
	public static DeferredHolder<RecipeSerializer<?>, IERecipeSerializer<CoreDrillRecipe>> SERIALIZER;
	public static final CachedRecipeList<CoreDrillRecipe> RECIPES = new CachedRecipeList<>(IGRecipeTypes.COREDRILL);

	SizedFluidIngredient input;
	Fluid output;

	public <T extends Recipe<?>> CoreDrillRecipe(SizedFluidIngredient input, Fluid output)
	{
		super(TagOutput.EMPTY, IGRecipeTypes.COREDRILL, 1, 0, () -> new RecipeMultiplier(() -> 1, () -> 1));
		this.input = input;
		this.output = output;
	}

	@Nullable
	public static CoreDrillRecipe get(Level level, FluidStack fluid)
	{
		for(RecipeHolder<CoreDrillRecipe> r : RECIPES.getRecipes(level))
		{
			if(r.value().input.test(fluid))
			{
				return r.value();
			}
		}
		return null;
	}

	public SizedFluidIngredient getInput()
	{
		return input;
	}

	public Fluid getOutput()
	{
		return output;
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
