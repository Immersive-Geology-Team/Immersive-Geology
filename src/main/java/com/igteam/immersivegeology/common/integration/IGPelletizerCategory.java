/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.integration;

import blusunrize.immersiveengineering.common.util.compat.jei.JEIHelper;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.PelletizerRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.Arrays;

public class IGPelletizerCategory extends IGRecipeCategory<PelletizerRecipe>
{
	public IGPelletizerCategory(IGuiHelper helper)
	{
		super(helper, JEIRecipeTypes.PELLETIZER, "block.immersivegeology.pelletizer");
		ResourceLocation background = new ResourceLocation(IGLib.MODID, "textures/gui/jei/pelletizer.png");
		IDrawableStatic back = guiHelper.drawableBuilder(background, 0, 0, 101, 101).setTextureSize(101,101).build();
		setBackground(back);
		setIcon(IGMultiblockProvider.PELLETIZER.iconStack());
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, PelletizerRecipe recipe, IFocusGroup focuses)
	{
		assert Minecraft.getInstance().level!=null;

		builder.addSlot(RecipeIngredientRole.INPUT, 35, 40)
				.addItemStacks(Arrays.asList(recipe.itemIn.getMatchingStacks()));

		builder.addSlot(RecipeIngredientRole.OUTPUT, 11, 23)
				.setFluidRenderer(500, false, 16, 55)
				.addFluidStack(Fluids.WATER, 500)
				.addTooltipCallback(JEIHelper.fluidTooltipCallback);

		builder.addSlot(RecipeIngredientRole.OUTPUT, 71, 62)
				.addItemStack(recipe.itemOutput.get());
	}
}
