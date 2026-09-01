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
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidType;

import java.util.List;

public class IGBasicChemicalCategory extends IGRecipeCategory<BasicChemicalRecipe>
{
	private static final int SEIZE_THRESHOLD = 99;
	private static final int SEVERE_RATE = 2;

	private static final int BADGE_X = 4;
	private static final int BADGE_Y = 4;
	private static final int BADGE_HEIGHT = 12;
	private static final int BADGE_PADDING_LEFT = 5;
	private static final int BADGE_PADDING_RIGHT = 4;
	private static final String BADGE_LABEL = "!";

	private static final int BADGE_BACKDROP = 0xC0140A04;
	private static final int CAUTION_COLOUR = 0xFFFFAA00;
	private static final int DANGER_COLOUR = 0xFFFF5555;

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

	@Override
	public void draw(BasicChemicalRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY)
	{
		super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);

		int rate = recipe.getDamagePerTick();
		if(rate <= 0)
			return;

		int width = badgeWidth();
		int accent = rate >= SEVERE_RATE?DANGER_COLOUR: CAUTION_COLOUR;

		guiGraphics.fill(BADGE_X, BADGE_Y, BADGE_X+width, BADGE_Y+BADGE_HEIGHT, BADGE_BACKDROP);
		guiGraphics.renderOutline(BADGE_X, BADGE_Y, width, BADGE_HEIGHT, accent);
		guiGraphics.drawString(font, BADGE_LABEL, BADGE_X+BADGE_PADDING_LEFT, BADGE_Y+2, accent);
	}

	@Override
	public List<Component> getTooltipStrings(BasicChemicalRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY)
	{
		int rate = recipe.getDamagePerTick();
		if(rate <= 0)
			return List.of();

		int width = badgeWidth();
		boolean overBadge = mouseX >= BADGE_X&&mouseX < BADGE_X+width
				&&mouseY >= BADGE_Y&&mouseY < BADGE_Y+BADGE_HEIGHT;
		if(!overBadge)
			return List.of();

		ChatFormatting accent = rate >= SEVERE_RATE?ChatFormatting.RED: ChatFormatting.GOLD;
		return List.of(
				Component.translatable("jei.immersivegeology.caustic.title").withStyle(accent, ChatFormatting.BOLD),
				Component.translatable("jei.immersivegeology.caustic.rate", rate).withStyle(ChatFormatting.GRAY),
				Component.translatable("jei.immersivegeology.caustic.seize", secondsUntilSeized(rate)).withStyle(ChatFormatting.GRAY),
				Component.translatable("jei.immersivegeology.caustic.repair").withStyle(ChatFormatting.DARK_GRAY)
		);
	}

	private int badgeWidth()
	{
		return font.width(BADGE_LABEL)+BADGE_PADDING_LEFT+BADGE_PADDING_RIGHT;
	}

	private static int secondsUntilSeized(int rate)
	{
		return SEIZE_THRESHOLD/rate+1;
	}
}
