/*
 * BluSunrize
 * Copyright (c) 2024
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.integration;

import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GravitySeparatorRecipe;
import com.igteam.immersivegeology.core.registration.IGRecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.world.item.crafting.Recipe;

public class JEIRecipeTypes
{
	public static final RecipeType<GravitySeparatorRecipe> SEPARATOR = create(IGRecipeTypes.GRAVITYSEPARATOR);

	private static <T extends Recipe<?>>
	RecipeType<T> create(IERecipeTypes.TypeWithClass<T> type)
	{
		return new RecipeType<>(type.type().getId(), type.recipeClass());
	}
}
