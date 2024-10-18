/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.integration;

import blusunrize.immersiveengineering.api.crafting.StackWithChance;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.compat.jei.JEIHelper;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GravitySeparatorRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.IndustrialSluiceRecipe;
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

import java.util.Arrays;
import java.util.List;

public class IGSluiceCategory extends IGRecipeCategory<IndustrialSluiceRecipe>
{
	public IGSluiceCategory(IGuiHelper helper)
	{
		super(helper, JEIRecipeTypes.SLUICE, "block.immersivegeology.industrialsluice");
		ResourceLocation background = new ResourceLocation(IGLib.MODID, "textures/gui/jei/temp_sluice_jei.png");
		IDrawableStatic back = guiHelper.drawableBuilder(background, 0, 0, 128, 128).setTextureSize(128,128).build();
		setBackground(back);
		setIcon(IGMultiblockProvider.INDUSTRIAL_SLUICE.iconStack());
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, IndustrialSluiceRecipe recipe, IFocusGroup focuses)
	{
		builder.addSlot(RecipeIngredientRole.INPUT, 16, 18)
				.addItemStacks(Arrays.asList(recipe.itemIn.getItems()))
				.setBackground(JEIHelper.slotDrawable, -1,-1);

		builder.addSlot(RecipeIngredientRole.OUTPUT, 78, 18)
				.addItemStack(recipe.itemOutput.get())
				.setBackground(JEIHelper.slotDrawable, -1,-1);

		List<StackWithChance> byproducts = recipe.getByproducts();
		int i = 0;
		for(StackWithChance byproduct : byproducts)
		{
			builder.addSlot(RecipeIngredientRole.OUTPUT, 78, 38 + (17 * i))
					.addItemStack(byproduct.stack().get())
					.setBackground(JEIHelper.slotDrawable, -1,-1);
			i = i + 1;
		}
	}

	@Override
	public void draw(IndustrialSluiceRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY)
	{
		List<StackWithChance> validSecondaries = recipe.getByproducts();
		int yBase = 38;
		for(int i = 0; i < validSecondaries.size(); i++)
		{
			int x = 78;
			int y = yBase+i*17;
			graphics.drawString(
					ClientUtils.font(),
					Utils.formatDouble(validSecondaries.get(i).chance()*100, "0.##")+"%",
					x+21,
					y+6,
					0xaaaaaa,
					false
			);
		}

		graphics.pose().pushPose();
		graphics.pose().scale(3f, 3f, 1);
		graphics.pose().translate(-2,23,0);
		this.getIcon().draw(graphics, 8, 0);
		graphics.pose().popPose();
	}
}
