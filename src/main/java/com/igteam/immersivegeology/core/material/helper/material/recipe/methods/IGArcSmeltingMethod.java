/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.methods;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.ArcFurnaceRecipeBuilder;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.asList;

public class IGArcSmeltingMethod extends IGRecipeMethod
{
	public IGArcSmeltingMethod(IGRecipeStage stage)
	{
		super(stage);
	}

	public IGArcSmeltingMethod(MaterialHelper material, IGStageDesignation stage)
	{
		super(new IGRecipeStage(material, stage){});
	}

	public IGArcSmeltingMethod create(String method_name, TagKey<Item> input, int inputAmount, ItemStack output, ItemStack iSlag, float chance, IngredientWithSize... additives){
		this.input = new IngredientWithSize(input, inputAmount);
		this.output = output;
		this.slag = iSlag;
		this.additives = asList(additives);
		this.method_name = method_name;
		return this;
	}

	public IGArcSmeltingMethod create(String method_name, TagKey<Item> input, int inputAmount, ItemStack output, float chance, ItemStack iSlag){
		this.input = new IngredientWithSize(input, inputAmount);
		this.output = output;
		this.slag = iSlag;
		this.additives = List.of();
		this.method_name = method_name;
		return this;
	}

	public void setTimeAndEnergy(int time, int energy){
		this.time = time;
		this.energy = energy;
	}

	private String method_name;
	private IngredientWithSize input;

	private ItemStack slag, output;
	private List<IngredientWithSize> additives;
	int energy, time;

	@NotNull
	@Override
	public RecipeMethod getMethod()
	{
		return RecipeMethod.ARC_SMELTING;
	}

	@Override
	public ResourceLocation getLocation()
	{
		return toRL("arc_smelting/arc_" + getName());
	}

	@Override
	public String getName()
	{
		return method_name;
	}

	@Override
	public boolean build(Consumer<FinishedRecipe> consumer)
	{
		try
		{
			ArcFurnaceRecipeBuilder builder = ArcFurnaceRecipeBuilder.builder(output);
			if(!slag.isEmpty()) builder.addSlag(slag);
			builder.setEnergy(energy);
			builder.setTime(time);
			builder.addIngredient("input", input);
			additives.forEach(builder::addMultiInput);
			builder.build(consumer, getLocation());
			return true;
		} catch(Exception e)
		{
			IGLib.IG_LOGGER.error(e.getLocalizedMessage());
			return false;
		}
	}
}
