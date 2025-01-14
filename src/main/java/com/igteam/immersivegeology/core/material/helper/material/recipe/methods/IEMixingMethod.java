/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.methods;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.MixerRecipeBuilder;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.BallmillRecipeBuilder;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class IEMixingMethod extends IGRecipeMethod
{
	private int energy, input_fluid_amount;
	private FluidStack fluid_result;
	private TagKey<Fluid> input_fluid;
	private String name;
	private ItemStack[] input_list;

	public IEMixingMethod(MaterialHelper parent, IGStageDesignation stage)
	{
		super(new IGRecipeStage(parent, stage){});
	}

	public void create(Item input, TagKey<Fluid> input_fluid, int input_fluid_amount, int fluid_out_amount) {
		this.fluid_result = new FluidStack(parentMaterial.getFluid(BlockCategoryFlags.FLUID), fluid_out_amount);
		this.input_fluid = input_fluid;
		this.name = create_basic_method_name(BlockCategoryFlags.FLUID);
		this.input_list = new ItemStack[]{new ItemStack(input)};
		this.input_fluid_amount = input_fluid_amount;
		this.energy = 3200;
	}

	public void create(ItemStack input, TagKey<Fluid> input_fluid, int input_fluid_amount, int fluid_out_amount) {
		this.name = create_basic_method_name(BlockCategoryFlags.FLUID);
		this.fluid_result = new FluidStack(parentMaterial.getFluid(BlockCategoryFlags.FLUID), fluid_out_amount);
		this.input_fluid = input_fluid;
		this.input_list = new ItemStack[]{input};
		this.input_fluid_amount = input_fluid_amount;
		this.energy = 3200;
	}

	@NotNull
	@Override
	public IGRecipeMethod.RecipeMethod getMethod()
	{
		return RecipeMethod.REFINING;
	}

	@Override
	public ResourceLocation getLocation()
	{
		return toRL("mixer/mix_" + getName());
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public boolean build(Consumer<FinishedRecipe> consumer)
	{
		try
		{
			MixerRecipeBuilder builder = MixerRecipeBuilder.builder(fluid_result);
			builder.addFluidTag(input_fluid, input_fluid_amount);
			builder.addInput(input_list);
			builder.setEnergy(energy);
			builder.build(consumer, getLocation());
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
}
