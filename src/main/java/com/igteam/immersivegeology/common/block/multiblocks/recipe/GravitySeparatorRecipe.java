/*
 * BluSunrize
 * Copyright (c) 2024
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
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

public class GravitySeparatorRecipe extends MultiblockRecipe
{
	public static RegistryObject<IERecipeSerializer<GravitySeparatorRecipe>> SERIALIZER;
	public static final CachedRecipeList<GravitySeparatorRecipe> RECIPES = new CachedRecipeList<>(IGRecipeTypes.GRAVITYSEPARATOR);
	public final Lazy<ItemStack> itemOutput;
	public final Lazy<ItemStack> itemByproduct;
	public final Ingredient itemIn;
	Lazy<Integer> totalProcessWater;
	Lazy<Integer> totalProcessTime;
	Lazy<Float> byproductChance;

	public <T extends Recipe<?>> GravitySeparatorRecipe(ResourceLocation id, Ingredient itemIn, Lazy<ItemStack> output, Lazy<ItemStack> byproduct, float chance, int water, int time)
	{
		super(LAZY_EMPTY, IGRecipeTypes.GRAVITYSEPARATOR, id);
		this.itemOutput = output;
		this.itemByproduct = byproduct;
		this.itemIn = itemIn;
		byproductChance = Lazy.of(() -> chance);
		totalProcessWater = Lazy.of(() -> water);
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
		return 0;
	}

	@Override
	public int getTotalProcessTime()
	{
		return totalProcessTime.get();
	}

	public static GravitySeparatorRecipe findRecipe(Level level, ItemStack item)
	{
		for(GravitySeparatorRecipe recipe : RECIPES.getRecipes(level))
			if(recipe.itemIn.test(item))
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

	public int getTotalProcessWater()
	{
		return totalProcessWater.get();
	}

	public float getChance()
	{
		return byproductChance.get();
	}
}
