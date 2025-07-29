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

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class IGGeologyCategory extends IGRecipeCategory<IGGeoRecipe>
{
	public IGGeologyCategory(IGuiHelper helper)
	{
		super(helper, JEIRecipeTypes.GEOHINT, "block.immersivegeology.geohint");
		ResourceLocation background = new ResourceLocation("minecraft", "textures/gui/light_dirt_background.png");
		IDrawableStatic back = guiHelper.drawableBuilder(background, 0, 0, 180, 192).setTextureSize(16,16).build();
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

		builder.addSlot(RecipeIngredientRole.OUTPUT, 4, 18)
				.addItemStacks(triggerInputs)
				.setBackground(JEIHelper.slotDrawable, -1,-1);
	}

	int tick = 0;

	@Override
	public void draw(IGGeoRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
		tick++;
		GeologyMaterial material = recipe.material;
		String materialName = formatMaterialName(material.getName());
		OreConfig config = getOreConfig(material, materialName);

		drawMaterialName(guiGraphics, materialName);
		drawOreInformation(guiGraphics, config, material);
		drawFoundInLocations(guiGraphics, config, material);
		drawManualReference(guiGraphics);
	}

	private String formatMaterialName(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	private OreConfig getOreConfig(GeologyMaterial material, String materialName) {
		boolean isMineral = material instanceof MaterialMineral;
		MaterialInterface<?> materialInterface = isMineral
				? MineralEnum.valueOf(materialName)
				: MetalEnum.valueOf(materialName);

		return IGServerConfig.ORES.ores.get(materialInterface.getConfig());
	}

	private void drawMaterialName(GuiGraphics guiGraphics, String materialName) {
		guiGraphics.drawString(Minecraft.getInstance().font, materialName, 4, 4, 0xffffffff);
	}

	private void drawOreInformation(GuiGraphics guiGraphics, OreConfig config, GeologyMaterial material) {
		final int INFO_X = 4;
		final int INFO_Y_BASE = 60;
		final int LINE_HEIGHT = 12;
		final int TEXT_COLOR = 0xffffffff;

		var font = Minecraft.getInstance().font;
		int currentY = INFO_Y_BASE;

		double noise_probability = material.getNoiseProbability();
		double chunk_probability = (double) config.generationChance.get()/2_000_000;
		double finalProb = noise_probability*chunk_probability;
		DecimalFormat format = new DecimalFormat("0.####");

		String[] infoLines = {
				"Enabled: " + config.canSpawn.get(),
				"Y Range: " + config.minY.get() + " ~ " + config.maxY.get(),
				"Temp Range: " + config.min_temp.get() + " ~ " + config.max_temp.get(),
				"Downfall Range: " + config.min_downfall.get() + " ~ " + config.max_downfall.get(),
				"Chunk Spawn probability: " + format.format(finalProb * 100) + "%"
		};

		for (String line : infoLines) {
			currentY += LINE_HEIGHT;
			guiGraphics.drawString(font, line, INFO_X, currentY, TEXT_COLOR);
		}
	}

	private void drawFoundInLocations(GuiGraphics guiGraphics, OreConfig config, GeologyMaterial material) {
		final int LOCATION_X = 4;
		final int LOCATION_Y_BASE = 48;
		final int LINE_HEIGHT = 12;
		final int TEXT_COLOR = 0xffffffff;

		var font = Minecraft.getInstance().font;

		guiGraphics.drawString(font, "Found in:", LOCATION_X, LOCATION_Y_BASE, TEXT_COLOR);

		// Collect available dimensions
		String[] availableDimensions = getAvailableDimensions(config, material);

		// Only show a dimension if there are any available
		if (availableDimensions.length > 0) {
			int dimensionIndex = (tick / 60) % availableDimensions.length; // Change every second (20 ticks)
			if(dimensionIndex == 0 && tick > 256) tick = 0;
			int yPos = LOCATION_Y_BASE + LINE_HEIGHT;
			guiGraphics.drawString(font, availableDimensions[dimensionIndex], LOCATION_X, yPos, TEXT_COLOR);
		}
	}

	private final Map<String, String> dimensionNameCache = new HashMap<>();

	private String[] getAvailableDimensions(OreConfig config, GeologyMaterial material) {
		List<String> dimensions = new ArrayList<>();
		List<? extends String> dimension_id = config.dimension_whitelist.get();

		for (String id : dimension_id) {
			dimensions.add(formatDimensionID(id));
		}

		return dimensions.toArray(new String[0]);
	}

	private String formatDimensionID(String dimensionId) {
		// Check cache first
		return dimensionNameCache.computeIfAbsent(dimensionId, this::computeDimensionName);
	}

	private String computeDimensionName(String dimensionId) {
		String name = dimensionId.contains(":") ? dimensionId.substring(dimensionId.indexOf(":") + 1) : dimensionId;
		return Arrays.stream(name.split("_"))
				.map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
				.collect(Collectors.joining(" "));
	}

	private void drawManualReference(GuiGraphics guiGraphics) {
		final float SCALE = 0.75f;
		final int TEXT_COLOR = 0xffffffff;

		var font = Minecraft.getInstance().font;

		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(32, 18, 0);

		// First line
		guiGraphics.pose().pushPose();
		guiGraphics.pose().scale(SCALE, SCALE, SCALE);
		guiGraphics.drawString(font, Component.literal("See more in the Geology"), 0, 0, TEXT_COLOR);
		guiGraphics.pose().popPose();

		// Second line
		guiGraphics.pose().translate(0, 8, 0);
		guiGraphics.pose().pushPose();
		guiGraphics.pose().scale(SCALE, SCALE, SCALE);
		guiGraphics.drawString(font, Component.literal("Section of the IE Manual"), 0, 0, TEXT_COLOR);
		guiGraphics.pose().popPose();

		guiGraphics.pose().popPose();
	}
}
