/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.methods;

import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.GravitySeparatorRecipeBuilder;
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

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class IGSeparatorMethod extends IGRecipeMethod
{
	public IGSeparatorMethod(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		super(new IGRecipeStage(parentMaterial, stage) {});
	}

	@NotNull
	@Override
	public RecipeMethod getMethod()
	{
		return RecipeMethod.SEPARATOR;
	}

	private ItemStack result;
	private TagKey<Item> input;
	private ItemStack waste;
	private String name;
	private float waste_chance;
	private int time, water_amount;
	public void create(TagKey<Item> input, ItemStack output, ItemStack waste, float waste_chance, int time, int water){
		this.name = input.toString().substring(input.toString().lastIndexOf("/")+1, input.toString().indexOf("]"));
		this.result = output;
		this.input = input;
		this.waste = waste;
		this.waste_chance = waste_chance;
		this.time = time;
		this.water_amount = water;
	}

	@Override
	public ResourceLocation getLocation() {
		return toRL("wash/wash_" + getName());
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
			GravitySeparatorRecipeBuilder builder = GravitySeparatorRecipeBuilder.builder(result).addInput(input).setWater(water_amount).setByproduct(waste).setChance(waste_chance).setTime(time);
			builder.build(consumer, getLocation());
			return true;
		} catch(Exception e)
		{
			IGLib.IG_LOGGER.error("Error Building Gravity Separator Recipe: {}", e.getMessage());
			return false;
		}
	}
}
