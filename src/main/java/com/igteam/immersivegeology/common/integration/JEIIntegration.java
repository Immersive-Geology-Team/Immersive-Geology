/*
 * BluSunrize
 * Copyright (c) 2024
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.integration;

import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GravitySeparatorRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@JeiPlugin
public class JEIIntegration implements IModPlugin
{
	private static final ResourceLocation ID = new ResourceLocation(IGLib.MODID, "main");
	@Override
	public ResourceLocation getPluginUid()
	{
		return ID;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration)
	{
		IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
		registration.addRecipeCategories(new IGSeparatorCategory(guiHelper));
	}


	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		registration.addRecipes(JEIRecipeTypes.SEPARATOR, getRecipes(GravitySeparatorRecipe.RECIPES));
	}


	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(IGMultiblockProvider.GRAVITY_SEPARATOR.iconStack(), JEIRecipeTypes.SEPARATOR);
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		IModPlugin.super.registerGuiHandlers(registration);
	}

	private <T extends Recipe<?>> List<T> getRecipes(CachedRecipeList<T> cachedList)
	{
		return getFiltered(cachedList, $ -> true);
	}

	private <T extends Recipe<?>> List<T> getFiltered(CachedRecipeList<T> cachedList, Predicate<T> include)
	{
		return cachedList.getRecipes(Minecraft.getInstance().level).stream()
				.filter(include)
				.toList();
	}

}
