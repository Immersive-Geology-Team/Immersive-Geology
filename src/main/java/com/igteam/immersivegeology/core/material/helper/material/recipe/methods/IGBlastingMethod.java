/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.methods;

import blusunrize.immersiveengineering.api.crafting.builders.BlastFurnaceRecipeBuilder;
import blusunrize.immersiveengineering.common.register.IEItems;
import blusunrize.immersiveengineering.common.register.IEItems.Ingredients;
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

import java.util.function.Consumer;

public class IGBlastingMethod extends IGRecipeMethod
{
	private TagKey<Item> input;
	private ItemStack output;
	private String name;
	private ItemStack slag;

	public IGBlastingMethod(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		super(new IGRecipeStage(parentMaterial, stage){});
	}

	public void create(String method_name, TagKey<Item> input, ItemStack output){
		this.input = input;
		this.output = output;
		this.name = method_name;
		this.slag = new ItemStack(Ingredients.SLAG);
	}

	public void create(String method_name, TagKey<Item> input, ItemStack output, ItemStack slag){
		this.input = input;
		this.output = output;
		this.name = method_name;
		this.slag = slag;
	}

	@NotNull
	@Override
	public RecipeMethod getMethod()
	{
		return RecipeMethod.BLOOMERY;
	}

	@Override
	public ResourceLocation getLocation()
	{
		return toRL("blasting/blast_" + getName());
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public boolean build(Consumer<FinishedRecipe> consumer)
	{
		try {
			BlastFurnaceRecipeBuilder builder = BlastFurnaceRecipeBuilder.builder(output);
			builder.addInput(input);
			builder.addSlag(slag);
			builder.build(consumer, getLocation());
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
}
