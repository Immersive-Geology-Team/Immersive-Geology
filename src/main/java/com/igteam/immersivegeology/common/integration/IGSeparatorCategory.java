/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.integration;

import com.igteam.immersivegeology.common.block.multiblocks.recipe.GravitySeparatorRecipe;
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

public class IGSeparatorCategory extends IGRecipeCategory<GravitySeparatorRecipe>
{
	public IGSeparatorCategory(IGuiHelper helper)
	{
		super(helper, JEIRecipeTypes.SEPARATOR, "block.immersivegeology.gravity_separator");
		ResourceLocation background = ResourceLocation.fromNamespaceAndPath(IGLib.MODID, "textures/gui/jei/gravity_separator.png");
		IDrawableStatic back = guiHelper.drawableBuilder(background, 0, 0, 101, 101).setTextureSize(101,101).build();
		setBackground(back);
		setIcon(IGMultiblockProvider.GRAVITY_SEPARATOR.iconStack());
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, GravitySeparatorRecipe recipe, IFocusGroup focuses)
	{
		builder.addSlot(RecipeIngredientRole.INPUT, 16, 33)
				.addItemStacks(Arrays.asList(recipe.itemIn.getItems()));

		builder.addSlot(RecipeIngredientRole.OUTPUT, 67, 42)
				.addItemStack(recipe.itemOutput.get());

		String chance = (Math.round(recipe.getChance() * 100)) + "%" + " Output Chance";
		builder.addSlot(RecipeIngredientRole.OUTPUT, 67, 62)
				.addItemStack(recipe.itemByproduct.get())
				.addRichTooltipCallback((a,b) -> b.add(Component.literal(chance)));
	}

	@Override
	public void draw(GravitySeparatorRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY)
	{
		super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
		float c = recipe.getChance() * 100;
		int time = recipe.getTotalProcessTime();
		int timeInSeconds = time / 20;
		guiGraphics.drawString(this.font, timeInSeconds + " Seconds", 7, 58, 0xffffffff);

		String chance = (Math.round(c)) + "%";
		guiGraphics.drawString(this.font, chance, 85, 67, 0xffffffff);
	}

}
