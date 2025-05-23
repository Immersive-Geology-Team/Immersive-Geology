/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.integration;

import blusunrize.immersiveengineering.common.util.compat.jei.JEIHelper;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GeothermalExchangerRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidType;

public class IGGeothermalExchangerCategory extends IGRecipeCategory<GeothermalExchangerRecipe>
{
	public IGGeothermalExchangerCategory(IGuiHelper helper)
	{
		super(helper, JEIRecipeTypes.GEOTHERMAL_EXCHANGER, "block.immersivegeology.geothermal_exchanger");
		ResourceLocation background = new ResourceLocation(IGLib.MODID, "textures/gui/jei/geothermal_exchanger.png");
		IDrawableStatic back = guiHelper.drawableBuilder(background, 0, 0, 101, 101).setTextureSize(101,101).build();
		setBackground(back);
		setIcon(IGMultiblockProvider.GEOTHERMAL_EXCHANGER.iconStack());
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, GeothermalExchangerRecipe recipe, IFocusGroup focuses)
	{
		int tankSize = Math.max(2*FluidType.BUCKET_VOLUME,  Math.max(recipe.fluidIn.getAmount(),recipe.fluidIn.getAmount()));
		builder.addSlot(RecipeIngredientRole.INPUT, 15, 27)
				.setFluidRenderer(tankSize, false, 16, 47)
				.addIngredients(ForgeTypes.FLUID_STACK, recipe.fluidIn.getMatchingFluidStacks())
				.addTooltipCallback(JEIHelper.fluidTooltipCallback);

		if(!recipe.fluidOutput.get().isEmpty())
		{
			builder.addSlot(RecipeIngredientRole.OUTPUT, 41, 71)
					.setFluidRenderer(432, false, 16, 20)
					.addFluidStack(recipe.fluidOutput.get().getFluid(), recipe.fluidOutput.get().getAmount())
					.addTooltipCallback(JEIHelper.fluidTooltipCallback);
		}
	}
}
