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
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class IGCraftingMethod extends IGRecipeMethod
{
	public IGCraftingMethod(IGRecipeStage stage)
	{
		super(stage);
	}

	@NotNull
	@Override
	public RecipeMethod getMethod()
	{
		return RecipeMethod.CRAFTING;
	}

	@Override
	public ResourceLocation getLocation()
	{
		return null;
	}

	@Override
	public String getName()
	{
		return "";
	}

	@Override
	public boolean build(Consumer<FinishedRecipe> consumer)
	{
		return false;
	}
}
