/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.methods;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.RotaryKilnRecipeBuilder;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class IGCalcinationMethod extends IGRecipeMethod
{
	private int energy, time;

	private IngredientWithSize input;
	private ItemStack output;
	private String name;

	public IGCalcinationMethod(IGRecipeStage stage)
	{
		super(stage);
	}

	public void create(String name, ItemStack output, TagKey<Item> inputTag, int itemAmount, int time, int energy){
		this.name = name;
		this.output = output;
		this.input = new IngredientWithSize(inputTag, itemAmount);
		this.time = time;
		this.energy = energy;
	}

	@NotNull
	@Override
	public RecipeMethod getMethod()
	{
		return RecipeMethod.CALCINATION;
	}

	@Override
	public ResourceLocation getLocation()
	{
		return toRL("calcination/decompose_" + getName());
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
