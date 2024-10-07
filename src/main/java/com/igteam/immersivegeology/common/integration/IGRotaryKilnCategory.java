/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.integration;

import com.igteam.immersivegeology.common.block.multiblocks.recipe.GravitySeparatorRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RotaryKilnRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;

public class IGRotaryKilnCategory extends IGRecipeCategory<RotaryKilnRecipe>
{
	public IGRotaryKilnCategory(IGuiHelper helper)
	{
		super(helper, JEIRecipeTypes.ROTARYKILN, "block.immersivegeology.rotarykiln");
		ResourceLocation background = new ResourceLocation(IGLib.MODID, "textures/gui/jei/rotary_kiln.png");
		IDrawableStatic back = guiHelper.drawableBuilder(background, 0, 0, 101, 101).setTextureSize(101,101).build();
		setBackground(back);
		setIcon(IGMultiblockProvider.ROTARYKILN.iconStack());
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RotaryKilnRecipe recipe, IFocusGroup focuses)
	{
		builder.addSlot(RecipeIngredientRole.INPUT, 16, 33)
				.addItemStacks(Arrays.asList(recipe.itemIn.getMatchingStacks()));

		builder.addSlot(RecipeIngredientRole.OUTPUT, 67, 42)
				.addItemStack(recipe.itemOutput.get());
	}
}
