/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.integration;

import blusunrize.immersiveengineering.common.util.compat.jei.JEIHelper;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.FoundryRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class IGFoundryCategory extends IGRecipeCategory<FoundryRecipe>
{
	private static final int FLUID_X = 7;
	private static final int MOLD_X = 31;
	private static final int ARROW_X = 55;
	private static final int OUTPUT_X = 85;
	private static final int SLOT_Y = 9;
	private static final int ARROW_Y = 12;
	private static final int ARROW_WIDTH = 18;
	private static final int ARROW_HEIGHT = 10;

	private final IDrawableAnimated arrow;

	public IGFoundryCategory(IGuiHelper helper)
	{
		super(helper, JEIRecipeTypes.FOUNDRY, "block.immersivegeology.foundry");
		setBackground(helper.createBlankDrawable(110, 54));
		setIcon(IGMultiblockProvider.FOUNDRY.iconStack());
		IDrawableStatic full = helper.drawableBuilder(new ResourceLocation(IGLib.MODID, "textures/gui/jei/arrow.png"), 0, 0, ARROW_WIDTH, ARROW_HEIGHT)
				.setTextureSize(32, 32).build();
		this.arrow = helper.createAnimatedDrawable(full, 40, IDrawableAnimated.StartDirection.LEFT, false);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, FoundryRecipe recipe, IFocusGroup focuses)
	{
		builder.addSlot(RecipeIngredientRole.INPUT, FLUID_X, SLOT_Y)
				.setFluidRenderer(recipe.fluidIn.getAmount(), false, 16, 16)
				.addIngredients(ForgeTypes.FLUID_STACK, recipe.fluidIn.getMatchingFluidStacks())
				.setBackground(JEIHelper.slotDrawable, -1, -1)
				.addTooltipCallback(JEIHelper.fluidTooltipCallback);

		builder.addSlot(RecipeIngredientRole.CATALYST, MOLD_X, SLOT_Y)
				.addItemStack(new ItemStack(recipe.mold))
				.setBackground(JEIHelper.slotDrawable, -1, -1);

		builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, SLOT_Y)
				.addItemStack(recipe.itemOutput.get())
				.setBackground(JEIHelper.slotDrawable, -1, -1);
	}

	@Override
	public void draw(FoundryRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY)
	{
		super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
		arrow.draw(guiGraphics, ARROW_X, ARROW_Y);

		int time = Math.max(1, recipe.getTotalProcessTime());
		guiGraphics.drawString(font, time+" ticks", FLUID_X, 32, 0xff808080, false);
		guiGraphics.drawString(font, (recipe.getTotalProcessEnergy()+time-1)/time+" FE/t", FLUID_X, 42, 0xff808080, false);
	}
}
