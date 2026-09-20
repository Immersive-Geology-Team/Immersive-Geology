/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.integration;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.common.util.compat.jei.JEIHelper;
import com.igteam.immersivegeology.common.block.multiblocks.logic.BulkBlastFurnaceLogic;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BulkBlastFurnaceRecipe;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

import blusunrize.immersiveengineering.api.crafting.BlastFurnaceFuel;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BulkBlastFluxRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class IGBulkBlastFurnaceCategory extends IGRecipeCategory<BulkBlastFurnaceRecipe>
{
	private static final int ORE_X = 7;
	private static final int COKE_X = 31;
	private static final int FLUX_X = 55;
	private static final int ARROW_X = 79;
	private static final int MELT_X = 107;
	private static final int RICH_X = 131;
	private static final int SLOT_Y = 9;
	private static final int ARROW_Y = 12;
	private static final int ARROW_WIDTH = 18;
	private static final int ARROW_HEIGHT = 10;

	private final IDrawableAnimated arrow;

	public IGBulkBlastFurnaceCategory(IGuiHelper helper)
	{
		super(helper, JEIRecipeTypes.BULK_BLAST_FURNACE, "block.immersivegeology.bulk_blast_furnace");
		setBackground(helper.createBlankDrawable(156, 54));
		setIcon(IGMultiblockProvider.BULK_BLAST_FURNACE.iconStack());
		IDrawableStatic full = helper.drawableBuilder(new ResourceLocation(IGLib.MODID, "textures/gui/jei/arrow.png"), 0, 0, ARROW_WIDTH, ARROW_HEIGHT)
				.setTextureSize(32, 32).build();
		this.arrow = helper.createAnimatedDrawable(full, 60, IDrawableAnimated.StartDirection.LEFT, false);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, BulkBlastFurnaceRecipe recipe, IFocusGroup focuses)
	{
		if(recipe.oreInput!=null)
			builder.addSlot(RecipeIngredientRole.INPUT, ORE_X, SLOT_Y)
					.addItemStacks(Arrays.asList(recipe.oreInput.getMatchingStacks()))
					.setBackground(JEIHelper.slotDrawable, -1, -1);
		else if(recipe.meltInput!=null)
			builder.addSlot(RecipeIngredientRole.INPUT, ORE_X, SLOT_Y)
					.setFluidRenderer(recipe.meltInput.getAmount(), false, 16, 16)
					.addIngredients(ForgeTypes.FLUID_STACK, recipe.meltInput.getMatchingFluidStacks())
					.setBackground(JEIHelper.slotDrawable, -1, -1)
					.addTooltipCallback(JEIHelper.fluidTooltipCallback);

		builder.addSlot(RecipeIngredientRole.INPUT, COKE_X, SLOT_Y)
				.addItemStacks(fuelStacks())
				.setBackground(JEIHelper.slotDrawable, -1, -1);

		builder.addSlot(RecipeIngredientRole.INPUT, FLUX_X, SLOT_Y)
				.addItemStacks(fluxStacks())
				.setBackground(JEIHelper.slotDrawable, -1, -1);

		addMelt(builder, MELT_X, recipe.meltPerUnit);
		if(recipe.hasRichRegime()) addMelt(builder, RICH_X, recipe.richMelt);
	}

	private static List<ItemStack> fuelStacks()
	{
		Level level = Minecraft.getInstance().level;
		List<ItemStack> stacks = new ArrayList<>();
		for(BlastFurnaceFuel fuel : BlastFurnaceFuel.RECIPES.getRecipes(level))
			stacks.addAll(Arrays.asList(fuel.input.getItems()));
		return stacks;
	}

	private static List<ItemStack> fluxStacks()
	{
		Level level = Minecraft.getInstance().level;
		List<ItemStack> stacks = new ArrayList<>();
		for(BulkBlastFluxRecipe flux : BulkBlastFluxRecipe.RECIPES.getRecipes(level))
			stacks.addAll(Arrays.asList(flux.input.getItems()));
		return stacks;
	}

	private void addMelt(IRecipeLayoutBuilder builder, int x, FluidStack melt)
	{
		builder.addSlot(RecipeIngredientRole.OUTPUT, x, SLOT_Y)
				.setFluidRenderer(melt.getAmount(), false, 16, 16)
				.addFluidStack(melt.getFluid(), melt.getAmount())
				.setBackground(JEIHelper.slotDrawable, -1, -1)
				.addTooltipCallback(JEIHelper.fluidTooltipCallback);
	}

	@Override
	public void draw(BulkBlastFurnaceRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY)
	{
		super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
		arrow.draw(guiGraphics, ARROW_X, ARROW_Y);

		guiGraphics.drawString(font, "Coke x"+trim(recipe.cokeRatio)+"   Flux x"+trim(recipe.fluxRatio), ORE_X, 32, 0xff808080, false);
		guiGraphics.drawString(font, "Heat "+recipe.getHeatRequired()+"   Yield "+percent(recipe.minYield)+"-"+percent(recipe.maxYield), ORE_X, 42, 0xff808080, false);
	}

	private static String trim(float value)
	{
		return value==Math.round(value)?String.valueOf(Math.round(value)): String.format("%.2f", value);
	}

	private static String percent(float value)
	{
		return Math.round(value*100)+"%";
	}
}
