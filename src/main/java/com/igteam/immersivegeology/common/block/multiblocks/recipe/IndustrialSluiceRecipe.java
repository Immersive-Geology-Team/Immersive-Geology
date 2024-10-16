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
import net.minecraftforge.registries.RegistryObject;

public class IndustrialSluiceRecipe extends MultiblockRecipe
{
	public static RegistryObject<IERecipeSerializer<IndustrialSluiceRecipe>> SERIALIZER;
	public static final CachedRecipeList<IndustrialSluiceRecipe> RECIPES = new CachedRecipeList<>(IGRecipeTypes.SLUICE);
	public final Lazy<ItemStack> itemOutput;
	public final Ingredient itemIn;
	Lazy<Integer> totalProcessWater;
	Lazy<Integer> totalProcessTime;
	Lazy<NonNullList<Float>> byproductChance;
	Lazy<NonNullList<ItemStack>> byproducts;

	public <T extends Recipe<?>> IndustrialSluiceRecipe(ResourceLocation id, Ingredient itemIn, Lazy<ItemStack> output, NonNullList<ItemStack> byproducts, NonNullList<Float> chances, int water, int time)
	{
		super(LAZY_EMPTY, IGRecipeTypes.SLUICE, id);
		this.itemOutput = output;
		this.itemIn = itemIn;
		this.byproducts = Lazy.of(() -> byproducts);
		this.byproductChance = Lazy.of(() -> chances);
		this.totalProcessWater = Lazy.of(() -> water);
		this.totalProcessTime = Lazy.of(() -> time);

		NonNullList<ItemStack> outputs = NonNullList.createWithCapacity(byproducts.size() + 1);
		outputs.add(output.get());
		outputs.addAll(byproducts);

		this.outputList = Lazy.of(() -> outputs);
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

	public static IndustrialSluiceRecipe findRecipe(Level level, ItemStack item)
	{
		for(IndustrialSluiceRecipe recipe : RECIPES.getRecipes(level))
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
		return 64;
	}

	public NonNullList<ItemStack> getByproducts()
	{
		return byproducts.get();
	}

	public NonNullList<Float> getByproductChance()
	{
		return byproductChance.get();
	}

	public int getTotalProcessWater()
	{
		return totalProcessWater.get();
	}
}
