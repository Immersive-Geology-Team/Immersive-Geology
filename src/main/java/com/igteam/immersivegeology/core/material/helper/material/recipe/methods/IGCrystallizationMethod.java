/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.methods;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.CrystallizerRecipeBuilder;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class IGCrystallizationMethod extends IGRecipeMethod
{
	private ItemStack itemResult;
	private FluidTagInput fluidInput;
	private int time;
	private int energy;
	private String name;

	public IGCrystallizationMethod(MaterialHelper material, IGStageDesignation stage)
	{
		super(new IGRecipeStage(material, stage){});
	}

	public void create(String name, ItemStack output, TagKey<Fluid> fluidInput, int fluidAmount, int time, int energy)
	{
		this.name = name;

		this.itemResult = output;
		IGLib.IG_LOGGER.debug("Fluid Tag? {}", fluidInput.toString());
		this.fluidInput = new FluidTagInput(fluidInput, fluidAmount);
		this.time = time;
		this.energy = energy;
	}

	@NotNull
	@Override
	public RecipeMethod getMethod()
	{
		return RecipeMethod.CRYSTALLIZATION;
	}

	@Override
	public ResourceLocation getLocation()
	{
		return toRL("crystallizer/crystallize_" + getName());
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
			CrystallizerRecipeBuilder builder = CrystallizerRecipeBuilder.builder(this.itemResult).addInput(this.fluidInput).setEnergy(energy).setTime(time);
			builder.build(consumer, getLocation());
			return true;
		} catch(Exception e)
		{
			IGLib.IG_LOGGER.error("Exception in Crystallizer Recipe Builder: {}", e.getMessage());
			return false;
		}
	}
}
