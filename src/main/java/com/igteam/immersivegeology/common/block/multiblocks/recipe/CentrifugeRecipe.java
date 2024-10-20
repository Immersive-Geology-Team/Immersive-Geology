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
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import com.igteam.immersivegeology.core.registration.IGRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryObject;

public class CentrifugeRecipe extends MultiblockRecipe
{
	public static RegistryObject<IERecipeSerializer<CentrifugeRecipe>> SERIALIZER;
	public static final CachedRecipeList<CentrifugeRecipe> RECIPES = new CachedRecipeList<>(IGRecipeTypes.CENTRIFUGE);
	public final Lazy<ItemStack> itemOutput;
	public final FluidTagInput fluidIn;
	Lazy<Integer> totalProcessEnergy;
	Lazy<Integer> totalProcessTime;

	public <T extends Recipe<?>> CentrifugeRecipe(ResourceLocation id, FluidTagInput fluidInput, Lazy<ItemStack> output, int energy, int time)
	{
		super(LAZY_EMPTY, IGRecipeTypes.CENTRIFUGE, id);
		this.itemOutput = output;
		this.fluidIn = fluidInput;
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

	public static CentrifugeRecipe findRecipe(Level level, FluidStack input)
	{
		for(CentrifugeRecipe recipe : RECIPES.getRecipes(level))
			if(recipe.fluidIn.test(input))
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
