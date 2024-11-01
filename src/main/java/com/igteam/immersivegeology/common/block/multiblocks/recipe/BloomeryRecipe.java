/*
 * ${USER}
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IERecipeTypes.TypeWithClass;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import com.igteam.immersivegeology.core.registration.IGRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;

public class BloomeryRecipe extends MultiblockRecipe
{
	public static RegistryObject<IERecipeSerializer<BloomeryRecipe>> SERIALIZER;
	public static final CachedRecipeList<BloomeryRecipe> RECIPES = new CachedRecipeList<>(IGRecipeTypes.BLOOMERY);
	public int time;
	public IngredientWithSize input;
	public Lazy<ItemStack> result;
	Lazy<Integer> totalProcessTime;

	public <T extends Recipe<?>> BloomeryRecipe(ResourceLocation id, IngredientWithSize input, Lazy<ItemStack> result, int time)
	{
		super(LAZY_EMPTY, IGRecipeTypes.BLOOMERY, id);
		this.input = input;
		this.result = result;
		this.time = time;
		totalProcessTime = Lazy.of(() -> time);
	}

	public static BloomeryRecipe findRecipe(Level level, ItemStack input)
	{
		for(BloomeryRecipe recipe : RECIPES.getRecipes(level))
			if(recipe.input.test(input))
				return recipe;
		return null;
	}

	public static BloomeryRecipe findRecipe(Level level, ItemStack input, @Nullable BloomeryRecipe hint)
	{
		if (input.isEmpty())
			return null;
		if (hint != null && hint.matches(input))
			return hint;
		for(BloomeryRecipe recipe : RECIPES.getRecipes(level))
			if(recipe.input.test(input))
				return recipe;
		return null;
	}

	private boolean matches(ItemStack input)
{
	return this.input.test(input);
}

	@Override
	public int getTotalProcessTime()
	{
		return totalProcessTime.get();
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
}
