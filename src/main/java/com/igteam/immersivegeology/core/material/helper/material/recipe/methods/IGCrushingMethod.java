/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.methods;

import blusunrize.immersiveengineering.api.crafting.CrusherRecipe;
import blusunrize.immersiveengineering.api.crafting.builders.CrusherRecipeBuilder;
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
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

public class IGCrushingMethod extends IGRecipeMethod
{
	public IGCrushingMethod(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		super(new IGRecipeStage(parentMaterial, stage) {});
	}
	private ItemStack output;
	private TagKey<Item> input, secondary;
	private float chance = 0;
	private int energy, time;
	private String name;

	public void create(String method_name, TagKey<Item> input,ItemStack output, int energy, int time){
		this.input = input;
		this.output = output;
		this.name = method_name;
		this.energy = energy;
		this.time = time;
	}

	public void create(String method_name, TagKey<Item> input, ItemStack output, TagKey<Item> secondary, int energy, int time, float chance){
		this.input = input;
		this.output = output;
		this.name = method_name;
		this.energy = energy;
		this.time = time;
		this.secondary = secondary;
		this.chance = chance;
	}

	@NotNull
	@Override
	public RecipeMethod getMethod()
	{
		return RecipeMethod.CRUSHING;
	}

	@Override
	public ResourceLocation getLocation()
	{
		return toRL("crushing/crush_" + Objects.requireNonNull(getName()));
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
			CrusherRecipeBuilder builder = CrusherRecipeBuilder.builder(output).addInput(input).setTime(time).setEnergy(energy);
			if(secondary != null) builder.addSecondary(secondary, chance);
			builder.build(consumer, getLocation());
			return true;
		}catch(Exception e)
		{
			IGLib.IG_LOGGER.error("Exception Building IG Crushing Recipe: {}", e.getMessage());
			return false;
		}
	}
}
