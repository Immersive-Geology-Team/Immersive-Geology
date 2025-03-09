/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.integration;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.common.util.compat.jei.JEIHelper;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GravitySeparatorRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RevFurnaceRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import mezz.jei.api.forge.ForgeTypes;
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
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;

import java.util.Arrays;

public class IGReverberationCategory extends IGRecipeCategory<RevFurnaceRecipe>
{
	public IGReverberationCategory(IGuiHelper helper)
	{
		super(helper, JEIRecipeTypes.REVERBERATION, "block.immersivegeology.reverberation_furnace");
		ResourceLocation background = new ResourceLocation(IGLib.MODID, "textures/gui/jei/reverberation_furnace.png");
		IDrawableStatic back = guiHelper.drawableBuilder(background, 0, 0, 101, 101).setTextureSize(101,101).build();
		setBackground(back);
		setIcon(IGMultiblockProvider.REVERBERATION_FURNACE.iconStack());
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RevFurnaceRecipe recipe, IFocusGroup focuses)
	{
		builder.addSlot(RecipeIngredientRole.INPUT, 4, 41)
				.addItemStacks(recipe.input.getMatchingStackList());

		builder.addSlot(RecipeIngredientRole.OUTPUT, 54, 41)
				.addItemStack(recipe.result.get());

		TagKey<Fluid> fluid = ChemicalEnum.SulfurDioxde.getFluidTag(BlockCategoryFlags.FLUID);
		FluidTagInput taggedFluid = new FluidTagInput(fluid, recipe.getWasteAmount());
		int tankSize = Math.max(FluidType.BUCKET_VOLUME,  recipe.getWasteAmount());
		builder.addSlot(RecipeIngredientRole.OUTPUT, 81, 27)
				.setFluidRenderer(tankSize, false, 16, 47)
				.addIngredients(ForgeTypes.FLUID_STACK, taggedFluid.getMatchingFluidStacks())
				.addTooltipCallback(JEIHelper.fluidTooltipCallback);
	}

	@Override
	public void draw(RevFurnaceRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY)
	{
		super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);

		int time = recipe.getTotalProcessTime();
		int timeInSeconds = time / 20;
		guiGraphics.drawString(this.font, timeInSeconds + " Seconds", 8, 8, 0xffffffff);
	}
}
