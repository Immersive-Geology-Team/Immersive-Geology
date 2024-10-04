/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import com.igteam.immersivegeology.core.registration.IGRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryObject;

public class RotaryKilnRecipe extends MultiblockRecipe
{
	public static RegistryObject<IERecipeSerializer<RotaryKilnRecipe>> SERIALIZER;
	public static final CachedRecipeList<RotaryKilnRecipe> RECIPES = new CachedRecipeList<>(IGRecipeTypes.ROTARYKILN);
	public final Lazy<ItemStack> itemOutput;
	public final IngredientWithSize itemIn;
	Lazy<Integer> totalProcessEnergy;
	Lazy<Integer> totalProcessTime;

	public <T extends Recipe<?>> RotaryKilnRecipe(ResourceLocation id, IngredientWithSize input, Lazy<ItemStack> output, int energy, int time)
	{
		super(LAZY_EMPTY, IGRecipeTypes.ROTARYKILN, id);
		this.itemOutput = output;
		this.itemIn = input;
		totalProcessEnergy = Lazy.of(() -> energy);
		totalProcessTime = Lazy.of(() -> time);
		this.outputList = Lazy.of(() -> NonNullList.of(ItemStack.EMPTY, this.itemOutput.get()));
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

	public static RotaryKilnRecipe findRecipe(Level level, ItemStack input)
	{
		for(RotaryKilnRecipe recipe : RECIPES.getRecipes(level))
			if(recipe.itemIn.test(input))
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
