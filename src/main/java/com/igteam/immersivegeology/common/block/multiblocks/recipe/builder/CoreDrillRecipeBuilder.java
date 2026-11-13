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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CoreDrillRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CrystallizerRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class CoreDrillRecipeBuilder extends IGMultiblockRecipeBuilder<CoreDrillRecipeBuilder>
{
	private SizedFluidIngredient input;
	private final Fluid output;

	private CoreDrillRecipeBuilder(Fluid output)
	{
		this.output = output;
	}

	public static CoreDrillRecipeBuilder builder(Fluid fluid_out)
	{
		return new CoreDrillRecipeBuilder(fluid_out);
	}

	public CoreDrillRecipeBuilder addInput(SizedFluidIngredient fluidTag)
	{
		Preconditions.checkArgument(input==null, "Recipe can only have 1 input");
		this.input = fluidTag;
		return this;
	}

	public CoreDrillRecipeBuilder addInput(TagKey<Fluid> fluidTag, int amount)
	{
		return addInput(SizedFluidIngredient.of(fluidTag, amount));
	}

	public void build(RecipeOutput out, ResourceLocation name)
	{
		Preconditions.checkArgument(input!=null, "This recipe is incomplete");
		CoreDrillRecipe recipe = new CoreDrillRecipe(input, output);
		out.accept(name, recipe, null, getConditions());
	}

}
