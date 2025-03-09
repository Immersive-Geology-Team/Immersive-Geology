/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.integration;

import blusunrize.lib.manual.ManualUtils;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BallmillRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RotaryKilnRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;

public class IGBallmillCategory extends IGRecipeCategory<BallmillRecipe>
{
	public IGBallmillCategory(IGuiHelper helper)
	{
		super(helper, JEIRecipeTypes.BALLMILL, "block.immersivegeology.ballmill");
		ResourceLocation background = new ResourceLocation(IGLib.MODID, "textures/gui/jei/ballmill.png");
		IDrawableStatic back = guiHelper.drawableBuilder(background, 0, 0, 101, 101).setTextureSize(101,101).build();
		setBackground(back);
		setIcon(IGMultiblockProvider.BALLMILL.iconStack());
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, BallmillRecipe recipe, IFocusGroup focuses)
	{
		builder.addSlot(RecipeIngredientRole.INPUT, 16, 33)
				.addItemStacks(Arrays.asList(recipe.itemIn.getMatchingStacks()));

		builder.addSlot(RecipeIngredientRole.OUTPUT, 67, 42)
				.addItemStack(recipe.itemOutput.get());
	}

	@Override
	public void draw(BallmillRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY)
	{
		super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
		int time = recipe.getTotalProcessTime();
		int timeInSeconds = time / 20;
		int energyPt = recipe.getTotalProcessEnergy() / time;
		guiGraphics.drawString(this.font, timeInSeconds + " Seconds", 12, 72, 0xffffffff);
		guiGraphics.drawString(this.font, energyPt + " FE/t", 12, 84, 0xffffffff);
	}
}
