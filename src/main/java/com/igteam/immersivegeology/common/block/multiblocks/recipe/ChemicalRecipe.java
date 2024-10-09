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
import com.igteam.immersivegeology.common.block.multiblocks.logic.ChemicalReactorLogic;
import com.igteam.immersivegeology.core.lib.IGLib;
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

import java.util.List;
import java.util.Set;

public class ChemicalRecipe extends MultiblockRecipe
{
	public static RegistryObject<IERecipeSerializer<ChemicalRecipe>> SERIALIZER;
	public static final CachedRecipeList<ChemicalRecipe> RECIPES = new CachedRecipeList<>(IGRecipeTypes.CHEMICAL_REACTOR);
	public final ItemStack itemOutput;
	public final FluidStack fluidOutput;
	public final Set<FluidTagInput> fluidIn;
	public final IngredientWithSize itemInput;
	Lazy<Integer> totalProcessEnergy;
	Lazy<Integer> totalProcessTime;

	public <T extends Recipe<?>> ChemicalRecipe(ResourceLocation id, IngredientWithSize inputItem, Set<FluidTagInput> fluidInputSet, ItemStack itemOutput, FluidStack fluidOutput, int energy, int time)
	{
		super(LAZY_EMPTY, IGRecipeTypes.CHEMICAL_REACTOR, id);
		this.itemOutput = itemOutput;
		this.fluidOutput = fluidOutput;
		this.fluidIn = fluidInputSet;
		this.itemInput = inputItem;
		totalProcessEnergy = Lazy.of(() -> energy);
		totalProcessTime = Lazy.of(() -> time);
		this.outputList = Lazy.of(() -> NonNullList.of(this.itemOutput));
		this.fluidOutputList = List.of(fluidOutput);
		this.fluidInputList = fluidIn.stream().toList();
		this.setInputList(List.of(itemInput.getBaseIngredient()));
		if(this.fluidIn.isEmpty() || this.fluidIn.size() > 3) IGLib.IG_LOGGER.error("Chemical Recipe {} has either NO or more than 3 Fluid Tag inputs in the set.", id);
	}

	public static boolean acceptableCatalyst(Level level, ItemStack stack)
	{
		for(ChemicalRecipe recipe : RECIPES.getRecipes(level))
		{
			if(recipe.itemInput.test(stack)){
				return true;
			}
		}
		return false;
	}

	@Override
	public NonNullList<ItemStack> getActualItemOutputs()
	{
		NonNullList<ItemStack> list = NonNullList.create();
		list.add(this.itemOutput);
		return list;
	}

	// Required for normal IE Multiblock Processor to access recipe info. Used in Server Tick.
	@Override
	public List<FluidStack> getActualFluidOutputs()
	{
		return List.of(fluidOutput);
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

	public static ChemicalRecipe findRecipe(Level level, FluidStack inputA, FluidStack inputB, FluidStack inputC, ItemStack itemInput)
	{
		// TODO: Ensure this is tested a LOT.
		List<FluidStack> inputFluids = List.of(inputA, inputB, inputC);
		for(ChemicalRecipe recipe : RECIPES.getRecipes(level))
		{
			for(FluidStack inputF : inputFluids)
			{
				if(inputFluids.size() > recipe.fluidIn.size() && inputF.isEmpty()) continue;
				if(recipe.fluidIn.stream().noneMatch((f) -> f.test(inputF))) return null;
			}
			if(recipe.itemInput.test(itemInput)) return recipe;
		}
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
