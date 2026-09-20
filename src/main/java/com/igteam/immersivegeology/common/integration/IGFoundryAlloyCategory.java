/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.integration;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.common.util.compat.jei.JEIHelper;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.FoundryAlloyRecipe;
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

import java.util.List;

public class IGFoundryAlloyCategory extends IGRecipeCategory<FoundryAlloyRecipe>
{
	private static final int INPUT_X = 7;
	private static final int INPUT_Y = 5;
	private static final int INPUT_PITCH = 20;
	private static final int INPUT_COLUMNS = 3;
	private static final int SLOT_SIZE = 16;
	private static final int ARROW_X = 74;
	private static final int OUTPUT_X = 102;
	private static final int ARROW_WIDTH = 18;
	private static final int ARROW_HEIGHT = 10;

	private final IDrawableAnimated arrow;

	public IGFoundryAlloyCategory(IGuiHelper helper)
	{
		super(helper, JEIRecipeTypes.FOUNDRY_ALLOYING, "gui.immersivegeology.foundry.alloying");
		setBackground(helper.createBlankDrawable(128, 46));
		setIcon(IGMultiblockProvider.FOUNDRY.iconStack());
		IDrawableStatic full = helper.drawableBuilder(new ResourceLocation(IGLib.MODID, "textures/gui/jei/arrow.png"), 0, 0, ARROW_WIDTH, ARROW_HEIGHT)
				.setTextureSize(32, 32).build();
		this.arrow = helper.createAnimatedDrawable(full, 40, IDrawableAnimated.StartDirection.LEFT, false);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, FoundryAlloyRecipe recipe, IFocusGroup focuses)
	{
		List<FluidTagInput> inputs = recipe.alloyInputs;
		for(int i = 0; i < inputs.size(); i++)
		{
			FluidTagInput input = inputs.get(i);
			builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X+(i%INPUT_COLUMNS)*INPUT_PITCH, INPUT_Y+(i/INPUT_COLUMNS)*INPUT_PITCH)
					.setFluidRenderer(input.getAmount(), false, 16, 16)
					.addIngredients(ForgeTypes.FLUID_STACK, input.getMatchingFluidStacks())
					.setBackground(JEIHelper.slotDrawable, -1, -1)
					.addTooltipCallback(JEIHelper.fluidTooltipCallback);
		}

		builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, centreOf(inputs.size(), SLOT_SIZE))
				.setFluidRenderer(recipe.alloyOutput.getAmount(), false, 16, 16)
				.addIngredient(ForgeTypes.FLUID_STACK, recipe.alloyOutput)
				.setBackground(JEIHelper.slotDrawable, -1, -1)
				.addTooltipCallback(JEIHelper.fluidTooltipCallback);
	}

	@Override
	public void draw(FoundryAlloyRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY)
	{
		super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
		int arrowY = centreOf(recipe.alloyInputs.size(), ARROW_HEIGHT);
		arrow.draw(guiGraphics, ARROW_X, arrowY);

		String time = Math.max(1, recipe.getTotalProcessTime())+" ticks";
		guiGraphics.drawString(font, time, ARROW_X+(ARROW_WIDTH-font.width(time))/2, arrowY+ARROW_HEIGHT+2, 0xff808080, false);
	}

	private static int centreOf(int inputs, int height)
	{
		int rows = Math.max(1, (inputs+INPUT_COLUMNS-1)/INPUT_COLUMNS);
		return INPUT_Y+((rows-1)*INPUT_PITCH+SLOT_SIZE-height)/2;
	}
}
