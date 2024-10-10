/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.integration;

import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.*;
import com.igteam.immersivegeology.core.registration.IGRecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.world.item.crafting.Recipe;

public class JEIRecipeTypes
{
	public static final RecipeType<GravitySeparatorRecipe> SEPARATOR = create(IGRecipeTypes.GRAVITYSEPARATOR);
	public static final RecipeType<RevFurnaceRecipe> REVERBERATION = create(IGRecipeTypes.REVFURNACE);
	public static final RecipeType<CrystallizerRecipe> CRYSTALLIZER = create(IGRecipeTypes.CRYSTALLIZER);
	public static final RecipeType<RotaryKilnRecipe> ROTARYKILN = create(IGRecipeTypes.ROTARYKILN);
	public static final RecipeType<ChemicalRecipe> CHEMICAL = create(IGRecipeTypes.CHEMICAL_REACTOR);

	private static <T extends Recipe<?>>
	RecipeType<T> create(IERecipeTypes.TypeWithClass<T> type)
	{
		return new RecipeType<>(type.type().getId(), type.recipeClass());
	}
}
