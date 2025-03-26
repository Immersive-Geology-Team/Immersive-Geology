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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class FoundryRecipe extends MultiblockRecipe
{
	public static RegistryObject<IERecipeSerializer<FoundryRecipe>> SERIALIZER;
	public static final CachedRecipeList<FoundryRecipe> RECIPES = new CachedRecipeList<>(IGRecipeTypes.FOUNDRY);
	public final Lazy<ItemStack> itemOutput;
	public final FluidTagInput fluidIn;
	Lazy<Integer> totalProcessEnergy;
	Lazy<Integer> totalProcessTime;
	public final Item mold;

	public <T extends Recipe<?>> FoundryRecipe(ResourceLocation id, FluidTagInput fluidInput, Lazy<ItemStack> output, Item mold, int energy, int time)
	{
		super(LAZY_EMPTY, IGRecipeTypes.PELLETIZER, id);
		this.itemOutput = output;
		this.fluidIn = fluidInput;
		this.mold = mold;
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

	public static FoundryRecipe findRecipe(Level level, FluidStack input)
	{
		for(FoundryRecipe recipe : RECIPES.getRecipes(level))
			if(recipe.fluidIn.test(input))
				return recipe;
		return null;
	}

	@Override
	public ItemStack getDisplayStack(ItemStack input)
	{
		return new ItemStack(input.getItem(), input.getCount());
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
