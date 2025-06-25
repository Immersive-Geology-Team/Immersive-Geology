/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.integration;

import blusunrize.immersiveengineering.api.crafting.StackWithChance;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.compat.jei.JEIHelper;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.IndustrialSluiceRecipe;
import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.common.config.IGServerConfig.Ores.OreConfig;
import com.igteam.immersivegeology.common.recipe.IGGeoRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IGGeologyCategory extends IGRecipeCategory<IGGeoRecipe>
{
	public IGGeologyCategory(IGuiHelper helper)
	{
		super(helper, JEIRecipeTypes.GEOHINT, "block.immersivegeology.geohint");
		ResourceLocation background = new ResourceLocation(IGLib.MODID, "textures/gui/jei/temp_sluice_jei.png");
		IDrawableStatic back = guiHelper.drawableBuilder(background, 0, 0, 128, 128).setTextureSize(128,128).build();
		setBackground(back);
		setIcon(new ItemStack(MineralEnum.Unobtania.getOreBlock(StoneEnum.MCStone, OreRichness.NORMAL).asIGItem()));
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, IGGeoRecipe recipe, IFocusGroup focuses)
	{
		GeologyMaterial material = recipe.material;

		List<ItemStack> triggerInputs = new ArrayList<>();
		List<ItemCategoryFlags> ore_types = List.of(ItemCategoryFlags.POOR_ORE, ItemCategoryFlags.NORMAL_ORE, ItemCategoryFlags.RICH_ORE);
		for(ItemCategoryFlags in : ore_types)
		{
			if(material.hasFlag(in)) triggerInputs.add(material.getStack(in, 1));
		}

		for(StoneEnum stone : StoneEnum.values())
		{
			if(material.acceptableStoneType(stone))
			{
				if(material.getOreBlock(stone, OreRichness.POOR) != null) triggerInputs.add(new ItemStack(material.getOreBlock(stone, OreRichness.POOR).asIGBlock()));
				if(material.getOreBlock(stone, OreRichness.NORMAL) != null) triggerInputs.add(new ItemStack(material.getOreBlock(stone, OreRichness.NORMAL).asIGBlock()));
				if(material.getOreBlock(stone, OreRichness.RICH) != null) triggerInputs.add(new ItemStack(material.getOreBlock(stone, OreRichness.RICH).asIGBlock()));
			}
		}

		builder.addSlot(RecipeIngredientRole.OUTPUT, 16, 18)
				.addItemStacks(triggerInputs)
				.setBackground(JEIHelper.slotDrawable, -1,-1);
	}


	@Override
	public void draw(IGGeoRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY)
	{
		super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
		String t = recipe.material.getName();
		t = t.substring(0,1).toUpperCase() + t.substring(1);
		guiGraphics.drawString(Minecraft.getInstance().font, t, 4,4,0xffffffff);
		GeologyMaterial material = recipe.material;
		int type = material instanceof MaterialMineral? 0 : 1;
		MaterialInterface<?> materialInterface = type == 1 ? MetalEnum.valueOf(t) : MineralEnum.valueOf(t);

		OreConfig config = IGServerConfig.ORES.ores.get(materialInterface.getConfig());
		int i = 0;
		guiGraphics.drawString(Minecraft.getInstance().font, "Found in:", 4, 48, 0xffffffff);
		i++;

		if(material.acceptableStoneType(StoneEnum.MCStone))
		{
			guiGraphics.drawString(Minecraft.getInstance().font, "Overworld", 4, 48+(i*12), 0xffffffff);
			i++;
		}

		if(material.acceptableStoneType(StoneEnum.MCNetherrack))
		{
			guiGraphics.drawString(Minecraft.getInstance().font, "Nether", 4, 48+(i*12), 0xffffffff);
			i++;
		}

		if(material.acceptableStoneType(StoneEnum.MCEndStone))
		{
			guiGraphics.drawString(Minecraft.getInstance().font, "The End", 4, 48+(i*12), 0xffffffff);
			i++;
		}

		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(4,52+(i*12),0);

			guiGraphics.pose().pushPose();
				guiGraphics.pose().scale(0.75f,0.75f,0.75f);
				guiGraphics.drawString(font, Component.literal("See more in the Geology"), 0, 0, 0xffffffff);
			guiGraphics.pose().popPose();

			guiGraphics.pose().translate(0,8,0);
			guiGraphics.pose().pushPose();
				guiGraphics.pose().scale(0.75f,0.75f,0.75f);
				guiGraphics.drawString(font, Component.literal("Section of the IE Manual"), 0, 0, 0xffffffff);
			guiGraphics.pose().popPose();
		guiGraphics.pose().popPose();
	}
}
