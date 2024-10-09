/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.methods;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.ChemicalRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.ChemicalRecipeBuilder;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.RotaryKilnRecipeBuilder;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class IGChemicalMethod extends IGRecipeMethod
{
	private ItemStack itemOutput;
	private FluidStack fluidOutput;
	private IngredientWithSize itemIn;
	private FluidTagInput fluidInA, fluidInB, fluidInC;
	private int energy, time;
	private String name;

	public IGChemicalMethod(IGRecipeStage stage)
	{
		super(stage);
	}

	@NotNull
	@Override
	public RecipeMethod getMethod()
	{
		return RecipeMethod.CHEMICAL;
	}


	public void create(String name, ItemStack itemOutput, FluidStack fluidOutput, IngredientWithSize itemIn, FluidTagInput fluidInA, FluidTagInput fluidInB, FluidTagInput fluidInC, int time, int energy){
		this.itemOutput = itemOutput;
		this.fluidOutput = fluidOutput;
		this.itemIn = itemIn;
		this.fluidInA = fluidInA;
		this.fluidInB = fluidInB;
		this.fluidInC = fluidInC;
		this.energy = energy;
		this.time = time;
		this.name = name;
	}

	@Override
	public ResourceLocation getLocation()
	{
		return toRL("chemical_reactor/leach_" + getName());
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public boolean build(Consumer<FinishedRecipe> consumer)
	{
		try
		{
			ChemicalRecipeBuilder builder = ChemicalRecipeBuilder.builder(itemOutput, fluidOutput, itemIn, fluidInA, fluidInB, fluidInC);
			builder.setEnergy(energy);
			builder.setTime(time);
			builder.build(consumer, getLocation());
			return true;
		}
		catch(Exception e)
		{
			IGLib.IG_LOGGER.error("Error: {}", e.getMessage());
			return false;
		}
	}
}
