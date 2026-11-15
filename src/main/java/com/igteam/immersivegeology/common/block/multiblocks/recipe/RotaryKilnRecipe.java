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
import com.igteam.immersivegeology.core.registration.IGRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public class RotaryKilnRecipe extends MultiblockRecipe
{
	public static DeferredHolder<RecipeSerializer<?>, IERecipeSerializer<RotaryKilnRecipe>> SERIALIZER;
	public static final CachedRecipeList<RotaryKilnRecipe> RECIPES = new CachedRecipeList<>(IGRecipeTypes.ROTARYKILN);
	public final TagOutput itemOutput;
	public final IngredientWithSize itemIn;
	Lazy<Integer> totalProcessEnergy;
	Lazy<Integer> heatRequired;
	Lazy<Integer> totalProcessTime;

	public <T extends Recipe<?>> RotaryKilnRecipe(IngredientWithSize input, TagOutput output, int time, int heat)
	{
		super(TagOutput.EMPTY, IGRecipeTypes.ROTARYKILN, time, time, () -> new RecipeMultiplier(() -> 1, () -> 1));
		this.itemOutput = output;
		this.itemIn = input;
		// Basic upkeep
		totalProcessEnergy = Lazy.of(() -> time);
		heatRequired = Lazy.of(() -> heat);
		totalProcessTime = Lazy.of(() -> time);
		this.outputList = new TagOutputList(this.itemOutput);
	}

	@Override
	public @NotNull RecipeSerializer<?> getSerializer()
	{
		return SERIALIZER.get();
	}

	@Override
	public int getTotalProcessEnergy()
	{
		return totalProcessEnergy.get();
	}

	public int getHeatRequired()
	{
		return heatRequired.get();
	}

	@Override
	public int getTotalProcessTime()
	{
		return totalProcessTime.get();
	}

	public static RecipeHolder<RotaryKilnRecipe> findRecipe(Level level, ItemStack input)
	{
		for(RecipeHolder<RotaryKilnRecipe> recipe : RECIPES.getRecipes(level))
			if(recipe.value().itemIn.testIgnoringSize(input))
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
		// No surplus-power overclock: the kiln is paced by heat, not by how hard it is fed
		return 0;
	}
}
