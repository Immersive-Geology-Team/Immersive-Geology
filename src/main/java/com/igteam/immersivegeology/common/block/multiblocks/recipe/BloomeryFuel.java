/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import com.igteam.immersivegeology.core.registration.IGRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Iterator;

public class BloomeryFuel extends IESerializableRecipe
{
	public static DeferredHolder<RecipeSerializer<?>, IERecipeSerializer<BloomeryFuel>> SERIALIZER;
	public static final CachedRecipeList<BloomeryFuel> RECIPES;
	public final Ingredient input;
	public final int burnTime;

    public BloomeryFuel(Ingredient input, int burnTime) {
		super(TagOutput.EMPTY, IGRecipeTypes.BLOOMERY_FUEL);
		this.input = input;
		this.burnTime = burnTime;
	}

	public static int getBloomeryFuelTime(Level level, ItemStack stack) {
		Iterator<RecipeHolder<BloomeryFuel>> var2 = RECIPES.getRecipes(level).iterator();

		BloomeryFuel e;
		do {
			if (!var2.hasNext()) {
				return 0;
			}

			e = var2.next().value();
		} while(!e.input.test(stack));

		return e.burnTime;
	}

	public static boolean isValidBloomeryFuel(Level level, ItemStack stack) {
		return getBloomeryFuelTime(level, stack) > 0;
	}

	protected IERecipeSerializer<BloomeryFuel> getIESerializer() {
		return (IERecipeSerializer)SERIALIZER.get();
	}

	public ItemStack getResultItem(HolderLookup.Provider access) {
		return ItemStack.EMPTY;
	}

	static {
		RECIPES = new CachedRecipeList(IGRecipeTypes.BLOOMERY_FUEL);
	}
}
