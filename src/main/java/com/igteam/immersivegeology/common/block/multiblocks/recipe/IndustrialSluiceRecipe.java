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
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.stream.Collectors;

public class IndustrialSluiceRecipe extends MultiblockRecipe
{
	public static DeferredHolder<RecipeSerializer<?>, IERecipeSerializer<IndustrialSluiceRecipe>> SERIALIZER;
	public static final CachedRecipeList<IndustrialSluiceRecipe> RECIPES = new CachedRecipeList<>(IGRecipeTypes.SLUICE);
	public final TagOutput itemOutput;
	public final Ingredient itemIn;
	Lazy<Integer> totalProcessWater;
	Lazy<Integer> totalProcessTime;
	Lazy<Integer> totalProcessEnergy;
	Lazy<NonNullList<StackWithChance>> byproducts;

	public <T extends Recipe<?>> IndustrialSluiceRecipe(Ingredient itemIn, TagOutput output, NonNullList<StackWithChance> byproducts, int water, int time, int energy)
	{
		super(TagOutput.EMPTY, IGRecipeTypes.SLUICE, time, 0, () -> new RecipeMultiplier(() -> 1, () -> 1));
		this.itemOutput = output;
		this.itemIn = itemIn;
		this.byproducts = Lazy.of(() -> byproducts);
		this.totalProcessWater = Lazy.of(() -> water);
		this.totalProcessTime = Lazy.of(() -> time);;
		this.totalProcessEnergy = Lazy.of(() -> energy);

		NonNullList<TagOutput> outputs = NonNullList.createWithCapacity(byproducts.size() + 1);
		List<TagOutput> stacks = byproducts.stream().map((s) -> s.stack()).toList();
		outputs.add(output);
		outputs.addAll(stacks);

		this.outputList = new TagOutputList(outputs);
	}

	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return SERIALIZER.get();
	}

	public int getConfiguredEnergy()
	{
		return totalProcessEnergy.get();
	}

	@Override
	public int getTotalProcessEnergy()
	{
		return 0;
	}

	@Override
	public int getTotalProcessTime()
	{
		return totalProcessTime.get();
	}

	public static IndustrialSluiceRecipe findRecipe(Level level, ItemStack item)
	{
		for(RecipeHolder<IndustrialSluiceRecipe> recipe : RECIPES.getRecipes(level))
			if(recipe.value().itemIn.test(item))
				return recipe.value();
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
		return 1;
	}

	public NonNullList<StackWithChance> getByproducts()
	{
		return byproducts.get();
	}

	public int getTotalProcessWater()
	{
		return totalProcessWater.get();
	}
}
