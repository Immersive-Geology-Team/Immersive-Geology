/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.google.common.base.Preconditions;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CrystallizerRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GeothermalExchangerRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class GeothermalExchangerRecipeBuilder extends IGMultiblockRecipeBuilder<GeothermalExchangerRecipeBuilder>
{
	private SizedFluidIngredient input;
	private final FluidStack output;
	private Integer energy;
	private Integer time;

	private GeothermalExchangerRecipeBuilder(FluidStack output)
	{
		this.output = output;
	}

	public static GeothermalExchangerRecipeBuilder builder(FluidStack fluid_out)
	{
		return new GeothermalExchangerRecipeBuilder(fluid_out);
	}

	public GeothermalExchangerRecipeBuilder addInput(SizedFluidIngredient fluidTag)
	{
		Preconditions.checkArgument(input==null, "Recipe can only have 1 input");
		this.input = fluidTag;
		return this;
	}

	public GeothermalExchangerRecipeBuilder addInput(TagKey<Fluid> fluidTag, int amount)
	{
		return addInput(SizedFluidIngredient.of(fluidTag, amount));
	}

	public GeothermalExchangerRecipeBuilder setEnergy(int energy)
	{
		this.energy = energy;
		return this;
	}

	public GeothermalExchangerRecipeBuilder setTime(int time)
	{
		this.time = time;
		return this;
	}

	public void build(RecipeOutput out, ResourceLocation name)
	{
		Preconditions.checkArgument(input!=null&&energy!=null&&time!=null, "This recipe is incomplete");
		GeothermalExchangerRecipe recipe = new GeothermalExchangerRecipe(input, Lazy.of(() -> output), energy, time);
		out.accept(name, recipe, null, getConditions());
	}

}
