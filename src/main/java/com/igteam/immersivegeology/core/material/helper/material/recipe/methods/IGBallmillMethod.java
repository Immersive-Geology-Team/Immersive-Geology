/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.methods;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.RotaryKilnRecipeBuilder;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod.RecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class IGBallmillMethod extends IGRecipeMethod
{
	private int energy, time;

	private IngredientWithSize input;
	private ItemStack output;
	private String name;

	public IGBallmillMethod(MaterialHelper parent, IGStageDesignation stage)
	{
		super(new IGRecipeStage(parent, stage){});
	}

	public void create(IFlagType<?> input_form, IFlagType<?> output_form) {
		this.name = create_advanced_method_name(input_form, output_form);
		this.output = parentMaterial.getStack(input_form, 1);
		this.input = new IngredientWithSize( parentMaterial.getItemTag(input_form), 1);
		this.time = 800;
		this.energy = 64000;
	}

	public void create(IFlagType<?> input_form, IFlagType<?> output_form, int time, int energy) {
		this.name = create_advanced_method_name(input_form, output_form);
		this.output = parentMaterial.getStack(input_form, 1);
		this.input = new IngredientWithSize( parentMaterial.getItemTag(input_form), 1);
		this.time = time;
		this.energy = energy;
	}

	public void create(IFlagType<?> input_form, int input_amount, IFlagType<?> output_form, int output_amount, int time, int energy){
		this.name = create_advanced_method_name(input_form, output_form);
		this.output = parentMaterial.getStack(input_form, input_amount);
		this.input = new IngredientWithSize(parentMaterial.getItemTag(input_form), output_amount);
		this.time = time;
		this.energy = energy;
	}

	public void create(MaterialHelper input_mat, IFlagType<?> input_form, int input_amount, MaterialHelper output_mat, IFlagType<?> output_form, int output_amount, int time, int energy){
		this.name = create_advanced_method_name(input_form, output_form);
		this.output = input_mat.getStack(input_form, input_amount);
		this.input = new IngredientWithSize(output_mat.getItemTag(input_form), output_amount);
		this.time = time;
		this.energy = energy;
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
		return toRL("ballmill/refine_" + getName());
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
			RotaryKilnRecipeBuilder builder = RotaryKilnRecipeBuilder.builder(output);
			builder.addInput(input);
			builder.setEnergy(energy);
			builder.setTime(time);
			builder.build(consumer, getLocation());
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
}
