/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import blusunrize.immersiveengineering.api.crafting.TagOutput;
import com.google.common.base.Preconditions;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CrystallizerRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class CrystallizerRecipeBuilder extends IGMultiblockRecipeBuilder<CrystallizerRecipeBuilder>
{
	private SizedFluidIngredient input;
	private final TagOutput output;
	private final FluidStack fluidOutput;
	private Integer energy;
	private Integer time;

	private CrystallizerRecipeBuilder(TagOutput output, FluidStack fluidOutput)
	{
		this.output = output;
		this.fluidOutput = fluidOutput;
	}

	public static CrystallizerRecipeBuilder builder(Item result, FluidStack fluid_out)
	{
		return builder(new ItemStack(result), fluid_out);
	}

	public static CrystallizerRecipeBuilder builder(ItemStack result, FluidStack fluid_out)
	{
		return new CrystallizerRecipeBuilder(new TagOutput(result), fluid_out);
	}

	public static CrystallizerRecipeBuilder builder(TagKey<Item> result, int count, FluidStack fluid_out)
	{
		return new CrystallizerRecipeBuilder(new TagOutput(result, count), fluid_out);
	}

	public CrystallizerRecipeBuilder addInput(SizedFluidIngredient fluidTag)
	{
		Preconditions.checkArgument(input==null, "Recipe can only have 1 input");
		this.input = fluidTag;
		return this;
	}

	public CrystallizerRecipeBuilder addInput(TagKey<Fluid> fluidTag, int amount)
	{
		return addInput(SizedFluidIngredient.of(fluidTag, amount));
	}

	public CrystallizerRecipeBuilder setEnergy(int energy)
	{
		this.energy = energy;
		return this;
	}

	public CrystallizerRecipeBuilder setTime(int time)
	{
		this.time = time;
		return this;
	}

	public void build(RecipeOutput out, ResourceLocation name)
	{
		Preconditions.checkArgument(input!=null&&energy!=null&&time!=null, "This recipe is incomplete");
		CrystallizerRecipe recipe = new CrystallizerRecipe(input, output, Lazy.of(() -> fluidOutput), energy, time);
		out.accept(name, recipe, null, getConditions());
	}

}
