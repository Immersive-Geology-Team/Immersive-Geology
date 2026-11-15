/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.integration;

import blusunrize.immersiveengineering.common.util.compat.jei.JEIHelper;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CentrifugeRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.Arrays;

public class IGCentrifugeCategory extends IGRecipeCategory<CentrifugeRecipe>
{
	public IGCentrifugeCategory(IGuiHelper helper)
	{
		super(helper, JEIRecipeTypes.CENTRIFUGE, "block.immersivegeology.centrifuge");
		ResourceLocation background = ResourceLocation.fromNamespaceAndPath(IGLib.MODID, "textures/gui/jei/centrifuge.png");
		IDrawableStatic back = guiHelper.drawableBuilder(background, 0, 0, 101, 101).setTextureSize(101,101).build();
		setBackground(back);
		setIcon(IGMultiblockProvider.CENTRIFUGE.iconStack());
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, CentrifugeRecipe recipe, IFocusGroup focuses)
	{
		int tankSize = Math.max(2*FluidType.BUCKET_VOLUME,  Math.max(recipe.fluidIn.amount(),recipe.fluidIn.amount()));
		builder.addSlot(RecipeIngredientRole.INPUT, 43, 9)
				.setFluidRenderer(tankSize, false, 15, 51)
				.addIngredients(NeoForgeTypes.FLUID_STACK, Arrays.asList(recipe.fluidIn.getFluids()))
				.addRichTooltipCallback(JEIHelper.fluidTooltipCallback);

		if(!recipe.primaryFluidOutput.get().isEmpty())
		{
			builder.addSlot(RecipeIngredientRole.OUTPUT, 10, 37)
					.setFluidRenderer(432, false, 15, 55)
					.addFluidStack(recipe.primaryFluidOutput.get().getFluid(), recipe.primaryFluidOutput.get().getAmount())
					.addRichTooltipCallback(JEIHelper.fluidTooltipCallback);
		}

		if(!recipe.secondaryFluidOutput.get().isEmpty())
		{
			builder.addSlot(RecipeIngredientRole.OUTPUT, 76, 37)
					.setFluidRenderer(432, false, 15, 55)
					.addFluidStack(recipe.secondaryFluidOutput.get().getFluid(), recipe.secondaryFluidOutput.get().getAmount())
					.addRichTooltipCallback(JEIHelper.fluidTooltipCallback);
		}

		builder.addSlot(RecipeIngredientRole.OUTPUT, 42, 76)
				.addItemStack(recipe.itemOutput.get());
	}

	@Override
	public void draw(CentrifugeRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY)
	{
		super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
		int time = recipe.getTotalProcessTime();
		int timeInSeconds = time / 20;
		int energyPt = recipe.getTotalProcessEnergy() / time;
		guiGraphics.drawString(this.font, timeInSeconds + " Seconds", 4, 4, 0xffffffff);
		guiGraphics.drawString(this.font, energyPt + " FE/t", 4, 14, 0xffffffff);
	}
}
