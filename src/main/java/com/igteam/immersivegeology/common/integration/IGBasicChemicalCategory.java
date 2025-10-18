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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BasicChemicalRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.ChemicalRecipe;
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

import java.util.List;

public class IGBasicChemicalCategory extends IGRecipeCategory<BasicChemicalRecipe>
{
	public IGBasicChemicalCategory(IGuiHelper helper)
	{
		super(helper, JEIRecipeTypes.BASIC_CHEMICAL, "block.immersivegeology.basic_chemical_reactor");
		ResourceLocation background = new ResourceLocation(IGLib.MODID, "textures/gui/jei/basic_vat.png");
		IDrawableStatic back = guiHelper.drawableBuilder(background, 0, 0, 101, 101).setTextureSize(101,101).build();
		setBackground(back);
		setIcon(IGMultiblockProvider.SMALL_CHEMICAL_REACTOR.iconStack());
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, BasicChemicalRecipe recipe, IFocusGroup focuses)
	{

		builder.addSlot(RecipeIngredientRole.INPUT, 42, 40)
				.addItemStack(recipe.itemInput.getRandomizedExampleStack(0));

		List<Integer> tank_pos_list = List.of(29, 8, 61, 8);
		int i = 0;
		for(FluidTagInput fluid_tag : recipe.fluidIn)
		{
			builder.addSlot(RecipeIngredientRole.INPUT, tank_pos_list.get(i), tank_pos_list.get(i+1))
					.setFluidRenderer(FluidType.BUCKET_VOLUME * 2, false, 10, 28)
					.addIngredients(ForgeTypes.FLUID_STACK, fluid_tag.getMatchingFluidStacks())
					.addTooltipCallback(JEIHelper.fluidTooltipCallback);
			i = i + 2;
		}

		if(!recipe.fluidOutput.isEmpty())
		{
			builder.addSlot(RecipeIngredientRole.OUTPUT, 35, 66)
					.setFluidRenderer(FluidType.BUCKET_VOLUME*2, false, 10, 28)
					.addFluidStack(recipe.fluidOutput.getFluid(), recipe.fluidOutput.getAmount())
					.addTooltipCallback(JEIHelper.fluidTooltipCallback);
		}
		builder.addSlot(RecipeIngredientRole.OUTPUT, 55, 77)
				.addItemStack(recipe.itemOutput);
	}
}
