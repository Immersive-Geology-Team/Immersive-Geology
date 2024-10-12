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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.ChemicalRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CrystallizerRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.List;
import java.util.Set;

public class IGChemicalCategory extends IGRecipeCategory<ChemicalRecipe>
{
	public IGChemicalCategory(IGuiHelper helper)
	{
		super(helper, JEIRecipeTypes.CHEMICAL, "block.immersivegeology.chemical_reactor");
		ResourceLocation background = new ResourceLocation(IGLib.MODID, "textures/gui/jei/vat.png");
		IDrawableStatic back = guiHelper.drawableBuilder(background, 0, 0, 128, 192).setTextureSize(128,192).build();
		setBackground(back);
		setIcon(IGMultiblockProvider.CHEMICAL_REACTOR.iconStack());
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, ChemicalRecipe recipe, IFocusGroup focuses)
	{

		builder.addSlot(RecipeIngredientRole.INPUT, 56, 89)
				.addIngredients(recipe.itemInput.getBaseIngredient());

		List<Integer> tank_pos_list = List.of(17, 18, 56, 18, 95, 18);
		int i = 0;
		for(FluidTagInput fluid_tag : recipe.fluidIn)
		{
			builder.addSlot(RecipeIngredientRole.INPUT, tank_pos_list.get(i), tank_pos_list.get(i+1))
					.setFluidRenderer(FluidType.BUCKET_VOLUME * 2, false, 16, 44)
					.addIngredients(ForgeTypes.FLUID_STACK, fluid_tag.getMatchingFluidStacks())
					.addTooltipCallback(JEIHelper.fluidTooltipCallback);
			i = i + 2;
		}

		if(!recipe.fluidOutput.isEmpty())
		{
			builder.addSlot(RecipeIngredientRole.OUTPUT, 79, 137)
					.setFluidRenderer(FluidType.BUCKET_VOLUME*2, false, 16, 44)
					.addFluidStack(recipe.fluidOutput.getFluid(), recipe.fluidOutput.getAmount())
					.addTooltipCallback(JEIHelper.fluidTooltipCallback);
		}
		builder.addSlot(RecipeIngredientRole.OUTPUT, 35, 163)
				.addItemStack(recipe.itemOutput);
	}
}
