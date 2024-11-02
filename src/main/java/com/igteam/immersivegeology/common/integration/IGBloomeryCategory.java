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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BloomeryRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RevFurnaceRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;

public class IGBloomeryCategory extends IGRecipeCategory<BloomeryRecipe>
{
	public IGBloomeryCategory(IGuiHelper helper)
	{
		super(helper, JEIRecipeTypes.BLOOMERY, "block.immersivegeology.bloomery");
		ResourceLocation background = new ResourceLocation(IGLib.MODID, "textures/gui/jei/bloomery.png");
		IDrawableStatic back = guiHelper.drawableBuilder(background, 0, 0, 101, 101).setTextureSize(101,101).build();
		setBackground(back);
		setIcon(IGMultiblockProvider.BLOOMERY.iconStack());
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, BloomeryRecipe recipe, IFocusGroup focuses)
	{
		builder.addSlot(RecipeIngredientRole.INPUT, 17, 41)
				.addItemStacks(recipe.input.getMatchingStackList());

		builder.addSlot(RecipeIngredientRole.OUTPUT, 68, 41)
				.addItemStack(recipe.result.get());
	}
}
