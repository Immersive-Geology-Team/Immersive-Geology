/*
 * BluSunrize
 * Copyright (c) 2024
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.integration;

import blusunrize.immersiveengineering.common.util.compat.jei.IEFluidTooltipCallback;
import blusunrize.immersiveengineering.common.util.compat.jei.JEIHelper;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CrystallizerRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GravitySeparatorRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidType;

import java.util.Arrays;

public class IGCrystallizerCategory extends IGRecipeCategory<CrystallizerRecipe>
{
	public IGCrystallizerCategory(IGuiHelper helper)
	{
		super(helper, JEIRecipeTypes.CRYSTALLIZER, "block.immersivegeology.crystallizer");
		ResourceLocation background = new ResourceLocation(IGLib.MODID, "textures/gui/jei/crystalizer.png");
		IDrawableStatic back = guiHelper.drawableBuilder(background, 0, 0, 101, 101).setTextureSize(101,101).build();
		setBackground(back);
		setIcon(IGMultiblockProvider.CRYSTALLIZER.iconStack());
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, CrystallizerRecipe recipe, IFocusGroup focuses)
	{
		int tankSize = Math.max(2*FluidType.BUCKET_VOLUME,  Math.max(recipe.fluidIn.getAmount(),recipe.fluidIn.getAmount()));
		builder.addSlot(RecipeIngredientRole.INPUT, 15, 27)
				.setFluidRenderer(tankSize, false, 16, 47)
				.addIngredients(ForgeTypes.FLUID_STACK, recipe.fluidIn.getMatchingFluidStacks())
				.addTooltipCallback(JEIHelper.fluidTooltipCallback);

		builder.addSlot(RecipeIngredientRole.OUTPUT, 66, 49)
				.addItemStack(recipe.itemOutput.get());
	}
}
