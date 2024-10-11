/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.integration;

import blusunrize.immersiveengineering.api.crafting.StackWithChance;
import blusunrize.immersiveengineering.common.register.IEMultiblockLogic;
import blusunrize.immersiveengineering.common.util.compat.jei.JEIHelper;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GravitySeparatorRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IGSeparatorCategory extends IGRecipeCategory<GravitySeparatorRecipe>
{
	public IGSeparatorCategory(IGuiHelper helper)
	{
		super(helper, JEIRecipeTypes.SEPARATOR, "block.immersivegeology.gravityseparator");
		ResourceLocation background = new ResourceLocation(IGLib.MODID, "textures/gui/jei/gravity_separator.png");
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

		String chance = (recipe.getChance() * 100) + "%" + " Output Chance";
		builder.addSlot(RecipeIngredientRole.OUTPUT, 67, 62)
				.addItemStack(recipe.itemByproduct.get())
				.addTooltipCallback((a,b) -> b.add(Component.literal(chance)));
	}
}
