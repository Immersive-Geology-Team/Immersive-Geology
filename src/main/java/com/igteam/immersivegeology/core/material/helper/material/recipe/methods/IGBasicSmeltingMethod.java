/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.methods;

import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class IGBasicSmeltingMethod extends IGRecipeMethod
{
	public IGBasicSmeltingMethod(IGRecipeStage stage)
	{
		super(stage);
	}

	private ItemLike input, output;
	private int smeltingTime;
	private float xp;

	public IGBasicSmeltingMethod create(ItemLike inputProvider, ItemLike output){
		this.input = inputProvider;
		this.output = output;
		this.smeltingTime = 100;
		this.xp = 1;
		return this;
	}

	public void setAdditionalData(int smeltingTime, float xp){
		this.smeltingTime = smeltingTime;
		this.xp = xp;
	}



	@NotNull
	@Override
	public RecipeMethod getMethod()
	{
		return RecipeMethod.BASIC_SMELTING;
	}

	@Override
	public ResourceLocation getLocation()
	{
		return toRL(output.asItem().getDescriptionId() + "_from_blasting");
	}

	@Override
	public String getName()
	{
		return output.asItem().getDescriptionId() + "_from_blasting";
	}

	@Override
	public boolean build(Consumer<FinishedRecipe> consumer)
	{
		try
		{
			SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.MISC, output, xp, smeltingTime).unlockedBy("has_"+input.asItem().getDescriptionId(), InventoryChangeTrigger.TriggerInstance.hasItems(input)).save(consumer);
			return true;
		} catch(Exception exception)
		{
			return false;
		}
	}
}
