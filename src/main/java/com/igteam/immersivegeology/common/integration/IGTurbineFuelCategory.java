/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.integration;

import blusunrize.immersiveengineering.common.util.compat.jei.JEIHelper;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.TurbineFuel;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class IGTurbineFuelCategory extends IGRecipeCategory<TurbineFuel>
{
	public IGTurbineFuelCategory(IGuiHelper helper)
	{
		super(helper, JEIRecipeTypes.TURBINE_FUEL, "block.immersivegeology.turbine_fuel");
		ResourceLocation background = new ResourceLocation(IGLib.MODID, "textures/gui/jei/turbine_fuel.png");
		IDrawableStatic back = guiHelper.drawableBuilder(background, 0, 0, 101, 101).setTextureSize(128,128).build();
		setBackground(back);
		setIcon(IGMultiblockProvider.STEAM_TURBINE.iconStack());
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, TurbineFuel recipe, IFocusGroup focuses)
	{
		builder.addSlot(RecipeIngredientRole.INPUT, 16, 24)
				.addFluidStack(recipe.getFluids().get(0), recipe.getConsumed())
				.setBackground(JEIHelper.slotDrawable, -1,-1);
	}

	@Override
	public void draw(TurbineFuel recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);

		guiGraphics.drawString(font, Component.translatable("immersivegeology.steam_turbine.jei_fuel_time").append(Component.literal(" " + recipe.getBurnTime() + " smiticks")), 10,10, 0xffffff);

	}
}
