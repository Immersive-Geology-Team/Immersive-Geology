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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class IGRoastingMethod extends IGRecipeMethod
{
	public IGRoastingMethod(IGRecipeStage stage)
	{
		super(stage);
	}

	@Override
	public @NotNull RecipeMethod getMethod()
	{
		return RecipeMethod.ROASTING;
	}

	@Override
	public ResourceLocation getLocation()
	{
		return null;
	}

	@Override
	public ItemStack getGenericOutput()
	{
		return null;
	}

	@Override
	public String getName()
	{
		return "";
	}
}
