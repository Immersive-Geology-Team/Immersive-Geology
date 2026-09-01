/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.integration;

import blusunrize.immersiveengineering.common.util.compat.jei.JEIHelper;
import com.igteam.immersivegeology.common.block.helper.IOreBlock;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.common.config.IGServerConfig.Ores.OreConfig;
import com.igteam.immersivegeology.common.recipe.IGGeoRecipe;
import com.igteam.immersivegeology.common.world.IWorldGenConfig;
import com.igteam.immersivegeology.common.world.compat.IGTFCWorld;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.mojang.datafixers.util.Pair;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;

public class IGGeologyCategory extends IGRecipeCategory<IGGeoRecipe>
{
	private static final int PAGE_WIDTH = 180;
	private static final int PAGE_HEIGHT = 192;

	private static final int PANEL_LEFT = 3;
	private static final int PANEL_TOP = 2;
	private static final int PANEL_RIGHT = 177;
	private static final int PANEL_BOTTOM = 190;

	private static final int CONTENT_LEFT = 8;
	private static final int CONTENT_RIGHT = 172;

	private static final int HEADER_Y = 6;
	private static final int HEADER_RULE_Y = 18;

	private static final int GRADE_SLOT_X = 8;
	private static final int GRADE_SLOT_Y = 22;
	private static final int GRADE_PITCH = 19;
	private static final int GRADE_TEXT_X = 70;
	private static final int GRADE_RULE_Y = 44;

	private static final int STAT_VALUE_RIGHT = 126;

	private static final int COLUMN_SPLIT = 128;
	private static final int COLUMN_TOP = 46;
	private static final int COLUMN_BOTTOM = 166;
	private static final int ROW_HEIGHT = 11;
	private static final int ROW_CHANCE = 49;
	private static final int ROW_VEIN = ROW_CHANCE+ROW_HEIGHT;
	private static final int ROW_DENSITY = ROW_VEIN+ROW_HEIGHT;
	private static final int ROW_TEMPERATURE = ROW_DENSITY+ROW_HEIGHT;
	private static final int ROW_RAINFALL = ROW_TEMPERATURE+ROW_HEIGHT;
	private static final int HOST_LABEL_Y = 106;
	private static final int HOST_VALUE_Y = 117;
	private static final int HOST_BOTTOM = 132;

	private static final int METER_WIDTH = 40;
	private static final int METER_HEIGHT = 5;

	private static final int ASSOCIATE_RULE_Y = 132;
	private static final int ASSOCIATE_LABEL_Y = 136;
	private static final int ASSOCIATE_SLOT_Y = 147;
	private static final int ASSOCIATE_MAX = 6;

	private static final int MANUAL_Y = 170;

	private static final int CHART_LEFT = 130;
	private static final int CHART_LABEL_Y = 49;
	private static final int CHART_NUMBER_RIGHT = 154;
	private static final int BAR_LEFT = 156;
	private static final int BAR_RIGHT = 170;
	private static final int BAR_TOP = 60;
	private static final int BAR_BOTTOM = 164;

	private static final float SMALL = 0.75f;

	private static final int COLOUR_TINT = 0xA8000000;
	private static final int COLOUR_BORDER = 0xFF6E5B44;
	private static final int COLOUR_RULE = 0x40FFFFFF;
	private static final int COLOUR_LABEL = 0xFFE4D7BE;
	private static final int COLOUR_VALUE = 0xFFFFFFFF;
	private static final int COLOUR_MUTED = 0xFFC3B49A;
	private static final int COLOUR_WARN = 0xFFFF8A63;
	private static final int COLOUR_CHIP = 0x66000000;
	private static final int COLOUR_TRACK = 0xFF2A2118;
	private static final int COLOUR_METER = 0xFF6FA8C4;
	private static final int READABLE_PEAK = 220;

	private static final int ROLL_RANGE = 1_000_000;
	private static final int ROLL_RANGE_END = 750_000;
	private static final ResourceLocation OVERWORLD = new ResourceLocation("minecraft", "overworld");
	private static final ResourceLocation NETHER = new ResourceLocation("minecraft", "the_nether");
	private static final ResourceLocation THE_END = new ResourceLocation("minecraft", "the_end");

	private static final FormattedCharSequence ELLIPSIS = FormattedCharSequence.forward("...", Style.EMPTY);

	private static final DecimalFormat PERCENT = new DecimalFormat("0.##");
	private static final DecimalFormat CLIMATE = new DecimalFormat("0.#");
	private static final DecimalFormat COMPACT = new DecimalFormat("0.#");

	private static final String KEY = "jei.immersivegeology.geology.";

	private static final Map<GeologyMaterial, Facts> FACTS = new HashMap<>();

	public IGGeologyCategory(IGuiHelper helper)
	{
		super(helper, JEIRecipeTypes.GEOHINT, "block.immersivegeology.geohint");
		ResourceLocation background = new ResourceLocation("minecraft", "textures/block/dirt.png");
		IDrawableStatic back = guiHelper.drawableBuilder(background, 0, 0, PAGE_WIDTH, PAGE_HEIGHT)
				.setTextureSize(16, 16).build();
		setBackground(back);
		setIcon(new ItemStack(MineralEnum.Unobtania.getOreBlock(StoneEnum.MCStone, OreRichness.NORMAL).asIGItem()));
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, IGGeoRecipe recipe, IFocusGroup focuses)
	{
		GeologyMaterial material = recipe.material;
		Facts facts = facts(material);

		OreRichness[] grades = OreRichness.values();
		for(int i = 0; i < grades.length; i++)
		{
			List<ItemStack> stacks = oresOf(material, grades[i]);
			if(stacks.isEmpty()) continue;

			Component note = Component.translatable(KEY+"grade."+grades[i].getSanitizedName());
			builder.addSlot(RecipeIngredientRole.OUTPUT, GRADE_SLOT_X+i*GRADE_PITCH, GRADE_SLOT_Y)
					.addItemStacks(stacks)
					.setBackground(JEIHelper.slotDrawable, -1, -1)
					.addTooltipCallback((view, tooltip) -> tooltip.add(note.copy().withStyle(ChatFormatting.GRAY)));
		}

		List<Associate> associates = facts.associates();
		for(int i = 0; i < associates.size()&&i < ASSOCIATE_MAX; i++)
		{
			Associate associate = associates.get(i);
			builder.addSlot(RecipeIngredientRole.OUTPUT, GRADE_SLOT_X+i*GRADE_PITCH, ASSOCIATE_SLOT_Y)
					.addItemStack(associate.icon())
					.setBackground(JEIHelper.slotDrawable, -1, -1)
					.addTooltipCallback((view, tooltip) -> tooltip.add(
							Component.translatable(KEY+"associate_share",
											percent(associate.share()*associateChance(facts)))
									.withStyle(ChatFormatting.GRAY)));
		}
	}

	private static List<ItemStack> oresOf(GeologyMaterial material, OreRichness richness)
	{
		List<ItemStack> stacks = new ArrayList<>();
		ItemCategoryFlags flag = richness.toCategory();
		if(material.hasFlag(flag)) stacks.add(material.getStack(flag, 1));

		for(StoneEnum stone : StoneEnum.values())
		{
			if(!stone.isStoneTypeValid()||!material.acceptableStoneType(stone)) continue;
			IOreBlock ore = material.getOreBlock(stone, richness);
			if(ore!=null) stacks.add(new ItemStack(ore.asIGBlock()));
		}
		return stacks;
	}

	@Override
	public void draw(IGGeoRecipe recipe, IRecipeSlotsView slotsView, GuiGraphics graphics, double mouseX, double mouseY)
	{
		Facts facts = facts(recipe.material);
		OreConfig config = facts.config();

		drawFrame(graphics);
		drawHeader(graphics, facts, config);
		drawGradeCaption(graphics);

		if(config==null)
		{
			drawString(graphics, Component.translatable(KEY+"no_data"), CONTENT_LEFT, ROW_CHANCE, COLOUR_WARN);
			return;
		}

		boolean enabled = generationEnabled(config);
		drawStats(graphics, facts, config, enabled);
		drawDepthChart(graphics, facts, config, enabled);
		drawAssociates(graphics, facts);
		drawManualNote(graphics);
	}

	private void drawFrame(GuiGraphics graphics)
	{
		graphics.fill(0, 0, PAGE_WIDTH, PAGE_HEIGHT, COLOUR_TINT);
		graphics.fill(PANEL_LEFT, PANEL_TOP, PANEL_RIGHT, PANEL_TOP+1, COLOUR_BORDER);
		graphics.fill(PANEL_LEFT, PANEL_BOTTOM-1, PANEL_RIGHT, PANEL_BOTTOM, COLOUR_BORDER);
		graphics.fill(PANEL_LEFT, PANEL_TOP, PANEL_LEFT+1, PANEL_BOTTOM, COLOUR_BORDER);
		graphics.fill(PANEL_RIGHT-1, PANEL_TOP, PANEL_RIGHT, PANEL_BOTTOM, COLOUR_BORDER);

		rule(graphics, HEADER_RULE_Y);
		rule(graphics, GRADE_RULE_Y);
		graphics.fill(COLUMN_SPLIT, COLUMN_TOP, COLUMN_SPLIT+1, COLUMN_BOTTOM, COLOUR_RULE);
	}

	private void rule(GuiGraphics graphics, int y)
	{
		graphics.fill(CONTENT_LEFT-2, y, CONTENT_RIGHT+2, y+1, COLOUR_RULE);
	}

	private void columnRule(GuiGraphics graphics, int y)
	{
		graphics.fill(CONTENT_LEFT-2, y, COLUMN_SPLIT, y+1, COLOUR_RULE);
	}

	private void drawHeader(GuiGraphics graphics, Facts facts, @Nullable OreConfig config)
	{
		graphics.drawString(font, facts.name(), CONTENT_LEFT, HEADER_Y, facts.colour(), true);

		Component chip;
		int colour;
		if(config==null||!generationEnabled(config))
		{
			chip = Component.translatable(KEY+"disabled");
			colour = COLOUR_WARN;
		}
		else
		{
			List<Destination> hosting = destinations(facts, config).stream().filter(Destination::hosts).toList();
			chip = hosting.isEmpty()?Component.translatable(KEY+"nowhere")
					: hosting.size()==1?dimensionName(hosting.get(0).id())
					: Component.translatable(KEY+"dimension_many", hosting.size());
			colour = hosting.isEmpty()?COLOUR_WARN: COLOUR_LABEL;
		}

		int width = font.width(chip);
		graphics.fill(CONTENT_RIGHT-width-3, HEADER_Y-2, CONTENT_RIGHT+1, HEADER_Y+9, COLOUR_CHIP);
		graphics.drawString(font, chip, CONTENT_RIGHT-width, HEADER_Y, colour, true);
	}

	private void drawGradeCaption(GuiGraphics graphics)
	{
		List<FormattedCharSequence> lines = font.split(Component.translatable(KEY+"grades_hint"),
				(int)((CONTENT_RIGHT-GRADE_TEXT_X)/SMALL));
		int y = GRADE_SLOT_Y+1;
		for(int i = 0; i < lines.size()&&y+7 <= GRADE_RULE_Y; i++)
		{
			drawSmall(graphics, lines.get(i), GRADE_TEXT_X, y, COLOUR_LABEL);
			y += 7;
		}
	}

	private void drawStats(GuiGraphics graphics, Facts facts, OreConfig config, boolean enabled)
	{
		int value = enabled?COLOUR_VALUE: COLOUR_MUTED;

		double chance = chunkChance(facts, config);
		statRow(graphics, ROW_CHANCE, Component.translatable(KEY+"chance"),
				chance <= 0?Component.translatable(KEY+"never")
						: Component.translatable(KEY+"chance_ratio", compact(1.0/chance)), value);
		statRow(graphics, ROW_VEIN, Component.translatable(KEY+"vein_size"),
				Component.translatable(KEY+"blocks", IGServerConfig.getOrDefault(config.veinSize)), value);
		statRow(graphics, ROW_DENSITY, Component.translatable(KEY+"density"),
				Component.literal(percent(IGServerConfig.getOrDefault(config.density))), value);

		climateRow(graphics, ROW_TEMPERATURE, Component.translatable(KEY+"temperature"),
				IGServerConfig.getOrDefault(config.min_temp), IGServerConfig.getOrDefault(config.max_temp),
				-2.0, 2.0, enabled);
		climateRow(graphics, ROW_RAINFALL, Component.translatable(KEY+"rainfall"),
				IGServerConfig.getOrDefault(config.min_downfall), IGServerConfig.getOrDefault(config.max_downfall),
				0.0, 1.0, enabled);

		graphics.drawString(font, Component.translatable(KEY+"host_rock"), CONTENT_LEFT, HOST_LABEL_Y,
				COLOUR_LABEL, true);
		List<FormattedCharSequence> host = font.split(facts.hostRock(), (int)((STAT_VALUE_RIGHT-CONTENT_LEFT)/SMALL));
		int y = HOST_VALUE_Y;
		int drawn = 0;
		while(drawn < host.size()&&y+7 <= HOST_BOTTOM)
		{
			drawSmall(graphics, host.get(drawn), CONTENT_LEFT, y, value);
			y += 7;
			drawn++;
		}

		if(drawn < host.size())
		{
			int width = Math.round(font.width(host.get(drawn-1))*SMALL);
			drawSmall(graphics, ELLIPSIS, Math.min(CONTENT_LEFT+width, STAT_VALUE_RIGHT-4), y-7, value);
		}
	}

	private void statRow(GuiGraphics graphics, int y, Component label, Component value, int colour)
	{
		graphics.drawString(font, label, CONTENT_LEFT, y, COLOUR_LABEL, true);
		graphics.drawString(font, value, STAT_VALUE_RIGHT-font.width(value), y, colour, true);
	}

	private void climateRow(GuiGraphics graphics, int y, Component label, double min, double max,
							double floor, double ceiling, boolean enabled)
	{
		graphics.drawString(font, label, CONTENT_LEFT, y, COLOUR_LABEL, true);

		if(min <= floor&&max >= ceiling)
		{
			Component any = Component.translatable(KEY+"any");
			graphics.drawString(font, any, STAT_VALUE_RIGHT-font.width(any), y, COLOUR_MUTED, true);
			return;
		}

		int left = STAT_VALUE_RIGHT-METER_WIDTH;
		int top = y+1;
		graphics.fill(left, top, STAT_VALUE_RIGHT, top+METER_HEIGHT, COLOUR_TRACK);

		double span = ceiling-floor;
		int from = left+(int)Math.round(METER_WIDTH*clamp((min-floor)/span));
		int to = left+(int)Math.round(METER_WIDTH*clamp((max-floor)/span));
		if(to-from < 1) to = from+1;
		graphics.fill(from, top, Math.min(to, STAT_VALUE_RIGHT), top+METER_HEIGHT,
				enabled?COLOUR_METER: COLOUR_MUTED);
	}

	private void drawDepthChart(GuiGraphics graphics, Facts facts, OreConfig config, boolean enabled)
	{
		graphics.drawString(font, Component.translatable(KEY+"depth"), CHART_LEFT, CHART_LABEL_Y, COLOUR_LABEL, true);

		int bandMin = IGServerConfig.getOrDefault(config.minY);
		int bandMax = IGServerConfig.getOrDefault(config.maxY);
		int[] world = chartRange(facts, config);
		int chartMin = Math.min(world[0], Math.min(bandMin, bandMax));
		int chartMax = Math.max(world[1], Math.max(bandMin, bandMax));
		if(chartMax <= chartMin) return;

		graphics.fill(BAR_LEFT, BAR_TOP, BAR_RIGHT, BAR_BOTTOM, COLOUR_TRACK);

		int seaLevel = seaLevel();

		if(chartsPlayerDimension(facts, config)&&seaLevel > chartMin&&seaLevel < chartMax)
		{
			int seaY = chartY(seaLevel, chartMin, chartMax);
			graphics.fill(BAR_LEFT, seaY, BAR_RIGHT, seaY+1, 0x553F7FB2);
		}

		int top = chartY(Math.max(bandMin, bandMax), chartMin, chartMax);
		int bottom = chartY(Math.min(bandMin, bandMax), chartMin, chartMax);
		if(bottom-top < 2) bottom = top+2;
		graphics.fill(BAR_LEFT, top, BAR_RIGHT, bottom, enabled?facts.colour(): COLOUR_MUTED);
		graphics.fill(BAR_LEFT, top, BAR_RIGHT, top+1, 0x55FFFFFF);

		int maxLabelY = Math.min(bottom-8, Math.max(BAR_TOP-1, top-1));
		int minLabelY = Math.max(maxLabelY+8, Math.min(BAR_BOTTOM-6, bottom-6));
		drawSmallRight(graphics, Component.literal(String.valueOf(Math.max(bandMin, bandMax))),
				CHART_NUMBER_RIGHT, maxLabelY, COLOUR_LABEL);
		drawSmallRight(graphics, Component.literal(String.valueOf(Math.min(bandMin, bandMax))),
				CHART_NUMBER_RIGHT, minLabelY, COLOUR_LABEL);
	}

	private void drawAssociates(GuiGraphics graphics, Facts facts)
	{
		if(facts.associates().isEmpty()) return;
		columnRule(graphics, ASSOCIATE_RULE_Y);
		graphics.drawString(font, Component.translatable(KEY+"found_with"), CONTENT_LEFT, ASSOCIATE_LABEL_Y,
				COLOUR_LABEL, true);
	}

	private void drawManualNote(GuiGraphics graphics)
	{
		List<FormattedCharSequence> lines = font.split(Component.translatable(KEY+"manual"),
				(int)((CONTENT_RIGHT-CONTENT_LEFT)/SMALL));
		int y = MANUAL_Y;
		for(int i = 0; i < lines.size()&&i < 2; i++)
		{
			drawSmall(graphics, lines.get(i), CONTENT_LEFT, y, COLOUR_MUTED);
			y += 7;
		}
	}

	@Override
	public List<Component> getTooltipStrings(IGGeoRecipe recipe, IRecipeSlotsView slotsView, double mouseX, double mouseY)
	{
		Facts facts = facts(recipe.material);
		OreConfig config = facts.config();
		if(config==null) return List.of();

		if(over(mouseX, mouseY, CONTENT_RIGHT-70, HEADER_Y-2, CONTENT_RIGHT+1, HEADER_Y+9))
		{
			List<Component> lines = new ArrayList<>();
			if(!generationEnabled(config)) lines.add(Component.translatable(KEY+"disabled_tip"));

			boolean anywhere = false;
			for(Destination destination : destinations(facts, config))
			{
				Component name = dimensionName(destination.id());
				if(!destination.hosts())
				{
					lines.add(Component.translatable(KEY+"dimension_blocked", name)
							.withStyle(ChatFormatting.DARK_GRAY));
					continue;
				}
				anywhere = true;
				lines.add(destination.viaTFC()?Component.translatable(KEY+"dimension_tfc", name)
						.withStyle(ChatFormatting.GRAY): name);
			}
			if(!anywhere) lines.add(Component.translatable(KEY+"nowhere_tip").withStyle(ChatFormatting.GRAY));
			return lines;
		}
		if(overRow(mouseX, mouseY, ROW_CHANCE))
		{
			return List.of(
					Component.translatable(KEY+"chance_tip", percent(chunkChance(facts, config))),
					Component.translatable(KEY+"chance_roll", percent(seedRoll(config)))
							.withStyle(ChatFormatting.GRAY),
					Component.translatable(KEY+"chance_ground", percent(facts.viability()))
							.withStyle(ChatFormatting.GRAY),
					Component.translatable(KEY+"chance_climate").withStyle(ChatFormatting.DARK_GRAY));
		}
		if(overRow(mouseX, mouseY, ROW_VEIN)) return List.of(Component.translatable(KEY+"vein_size_tip"));
		if(overRow(mouseX, mouseY, ROW_DENSITY)) return List.of(Component.translatable(KEY+"density_tip"));
		if(overRow(mouseX, mouseY, ROW_TEMPERATURE))
			return List.of(range(IGServerConfig.getOrDefault(config.min_temp),
					IGServerConfig.getOrDefault(config.max_temp)));
		if(overRow(mouseX, mouseY, ROW_RAINFALL))
			return List.of(range(IGServerConfig.getOrDefault(config.min_downfall),
					IGServerConfig.getOrDefault(config.max_downfall)));
		if(over(mouseX, mouseY, CONTENT_LEFT, HOST_LABEL_Y, STAT_VALUE_RIGHT, HOST_BOTTOM))
		{
			List<Component> lines = new ArrayList<>();
			lines.add(Component.translatable(KEY+"host_rock_tip"));
			lines.addAll(facts.stones());
			return lines;
		}
		if(over(mouseX, mouseY, CONTENT_LEFT, ASSOCIATE_LABEL_Y, STAT_VALUE_RIGHT, ASSOCIATE_SLOT_Y)
				&&!facts.associates().isEmpty())
			return List.of(Component.translatable(KEY+"found_with_tip"));
		if(over(mouseX, mouseY, CHART_LEFT, CHART_LABEL_Y, BAR_RIGHT+2, BAR_BOTTOM))
		{
			int[] world = chartRange(facts, config);
			List<Component> lines = new ArrayList<>();
			lines.add(Component.translatable(KEY+"depth_band", IGServerConfig.getOrDefault(config.minY),
					IGServerConfig.getOrDefault(config.maxY)));
			if(chartsPlayerDimension(facts, config))
			{
				lines.add(Component.translatable(KEY+"depth_world", world[0], world[1])
						.withStyle(ChatFormatting.GRAY));
				lines.add(Component.translatable(KEY+"sea_level", seaLevel()).withStyle(ChatFormatting.GRAY));
			}
			else lines.add(Component.translatable(KEY+"depth_world_named", dimensionName(chartDimension(facts, config)),
					world[0], world[1]).withStyle(ChatFormatting.GRAY));
			return lines;
		}
		return List.of();
	}

	private static boolean overRow(double mouseX, double mouseY, int row)
	{
		return over(mouseX, mouseY, CONTENT_LEFT, row-1, STAT_VALUE_RIGHT, row+ROW_HEIGHT-2);
	}

	private static boolean over(double mouseX, double mouseY, int left, int top, int right, int bottom)
	{
		return mouseX >= left&&mouseX < right&&mouseY >= top&&mouseY < bottom;
	}

	private static boolean generationEnabled(OreConfig config)
	{
		if(IGServerConfig.getOrDefault(IGServerConfig.disable_mineral_generation)) return false;
		return IGServerConfig.getOrDefault(config.canSpawn)&&IGServerConfig.getOrDefault(config.veinSize) > 0;
	}

	private static double seedRoll(OreConfig config)
	{
		if(!generationEnabled(config)) return 0;
		List<? extends String> dimensions = dimensionsOf(config);
		boolean endOnly = dimensions.size()==1&&THE_END.equals(new ResourceLocation(dimensions.get(0)));
		return Math.min(1.0, (double)IGServerConfig.getOrDefault(config.generationChance)/(endOnly?ROLL_RANGE_END
				: ROLL_RANGE));
	}

	private static double chunkChance(Facts facts, OreConfig config)
	{
		return seedRoll(config)*facts.viability();
	}

	private static double associateChance(Facts facts)
	{
		OreConfig config = facts.config();
		return config==null?1.0: IGServerConfig.getOrDefault(config.associateChance);
	}

	private static double clamp(double value)
	{
		return Math.max(0.0, Math.min(1.0, value));
	}

	private static String compact(double count)
	{
		if(count >= 10_000) return Math.round(count/1000)+"k";
		if(count >= 1_000) return COMPACT.format(count/1000)+"k";
		return String.valueOf(Math.round(count));
	}

	private static String percent(double fraction)
	{
		double value = fraction*100.0;
		if(value > 0&&value < 0.01) return "<0.01%";
		return PERCENT.format(value)+"%";
	}

	private static Component range(double min, double max)
	{
		return Component.translatable(KEY+"range", CLIMATE.format(min), CLIMATE.format(max));
	}

	private static int chartY(int y, int chartMin, int chartMax)
	{
		double portion = (double)(y-chartMin)/(chartMax-chartMin);
		return BAR_BOTTOM-(int)Math.round(portion*(BAR_BOTTOM-BAR_TOP));
	}

	private static int opaque(int colour)
	{
		return 0xFF000000|(colour&0xFFFFFF);
	}

	private static int readable(int colour)
	{
		int red = (colour >> 16)&0xFF;
		int green = (colour >> 8)&0xFF;
		int blue = colour&0xFF;
		int peak = Math.max(red, Math.max(green, blue));
		if(peak >= READABLE_PEAK) return opaque(colour);
		if(peak==0) return 0xFFD8D8D8;

		float boost = (float)READABLE_PEAK/peak;
		return 0xFF000000
				|(Math.min(255, Math.round(red*boost)) << 16)
				|(Math.min(255, Math.round(green*boost)) << 8)
				|Math.min(255, Math.round(blue*boost));
	}

	private static int worldMin()
	{
		Level level = Minecraft.getInstance().level;
		return level!=null?level.getMinBuildHeight(): -64;
	}

	private static int worldMax()
	{
		Level level = Minecraft.getInstance().level;
		return level!=null?level.getMaxBuildHeight()-1: 319;
	}

	private static int seaLevel()
	{
		Level level = Minecraft.getInstance().level;
		return level!=null?level.getSeaLevel(): 63;
	}

	private static ResourceLocation chartDimension(Facts facts, OreConfig config)
	{
		Level level = Minecraft.getInstance().level;
		ResourceLocation here = level!=null?level.dimension().location(): OVERWORLD;

		ResourceLocation first = null;
		for(Destination destination : destinations(facts, config))
		{
			if(!destination.hosts()) continue;
			if(destination.id().equals(here)) return here;
			if(first==null) first = destination.id();
		}
		return first!=null?first: here;
	}

	private static boolean chartsPlayerDimension(Facts facts, OreConfig config)
	{
		Level level = Minecraft.getInstance().level;
		return level!=null&&chartDimension(facts, config).equals(level.dimension().location());
	}

	private static int[] chartRange(Facts facts, OreConfig config)
	{
		Level level = Minecraft.getInstance().level;
		int[] here = {worldMin(), worldMax()};
		if(level==null) return here;

		ResourceLocation dimension = chartDimension(facts, config);
		if(dimension.equals(level.dimension().location())) return here;

		// Vanilla names a dimension's type after the dimension itself; anything else falls back to where we are.
		DimensionType type = level.registryAccess().registry(Registries.DIMENSION_TYPE)
				.map(registry -> registry.get(dimension)).orElse(null);
		if(type==null) return here;
		return new int[]{type.minY(), type.minY()+type.height()-1};
	}

	private static List<? extends String> dimensionsOf(OreConfig config)
	{
		return IGServerConfig.getOrDefault(config.dimension_whitelist);
	}

	private record Destination(ResourceLocation id, boolean hosts, boolean viaTFC)
	{
	}

	private static List<Destination> destinations(Facts facts, OreConfig config)
	{
		GeologyMaterial material = facts.material();
		boolean vanillaStone = material.acceptableStoneType(StoneEnum.MCStone);
		boolean tfcOverworld = tfcOverworldOverride()&&IGTFCWorld.canHostInOverworld(material);

		List<Destination> destinations = new ArrayList<>();
		boolean overworldListed = false;
		for(String id : dimensionsOf(config))
		{
			if(!ResourceLocation.isValidResourceLocation(id)) continue;
			ResourceLocation dimension = new ResourceLocation(id);

			boolean hosts;
			boolean viaTFC = false;
			if(OVERWORLD.equals(dimension))
			{
				overworldListed = true;
				hosts = vanillaStone||tfcOverworld;
				viaTFC = hosts&&!vanillaStone;
			}
			else if(NETHER.equals(dimension)) hosts = material.acceptableStoneType(StoneEnum.MCNetherrack);
			else if(THE_END.equals(dimension)) hosts = material.acceptableStoneType(StoneEnum.MCEndStone);
			else hosts = true;
			destinations.add(new Destination(dimension, hosts, viaTFC));
		}

		if(tfcOverworld&&!overworldListed) destinations.add(0, new Destination(OVERWORLD, true, true));
		return destinations;
	}

	private static Level checkedLevel;
	private static boolean checkedIsTFC;

	private static boolean tfcOverworldOverride()
	{
		if(!ModFlags.TFC.isStrictlyLoaded()) return false;
		if(!IGServerConfig.getOrDefault(IGServerConfig.TFC.allow_all_in_overworld)) return false;

		Minecraft minecraft = Minecraft.getInstance();
		Level level = minecraft.level;
		if(level==null) return false;
		if(level!=checkedLevel)
		{
			checkedLevel = level;
			checkedIsTFC = isTFCWorld(minecraft);
		}
		return checkedIsTFC;
	}

	private static boolean isTFCWorld(Minecraft minecraft)
	{
		IntegratedServer server = minecraft.getSingleplayerServer();
		if(server==null) return true;
		ServerLevel overworld = server.getLevel(Level.OVERWORLD);
		return overworld==null||IGTFCWorld.isTFCWorld(overworld);
	}

	private void drawString(GuiGraphics graphics, Component text, int x, int y, int colour)
	{
		graphics.drawString(font, text, x, y, colour, true);
	}

	private void drawSmall(GuiGraphics graphics, FormattedCharSequence text, int x, int y, int colour)
	{
		graphics.pose().pushPose();
		graphics.pose().translate(x, y, 0);
		graphics.pose().scale(SMALL, SMALL, 1f);
		graphics.drawString(font, text, 0, 0, colour, true);
		graphics.pose().popPose();
	}

	private void drawSmallRight(GuiGraphics graphics, Component text, int right, int y, int colour)
	{
		graphics.pose().pushPose();
		graphics.pose().translate(right-font.width(text)*SMALL, y, 0);
		graphics.pose().scale(SMALL, SMALL, 1f);
		graphics.drawString(font, text, 0, 0, colour, true);
		graphics.pose().popPose();
	}

	private static Component dimensionName(ResourceLocation id)
	{
		String path = id.getPath();
		StringBuilder name = new StringBuilder();
		for(String word : path.split("_"))
		{
			if(word.isEmpty()) continue;
			if(name.length() > 0) name.append(' ');
			name.append(word.substring(0, 1).toUpperCase(Locale.ROOT)).append(word.substring(1).toLowerCase(Locale.ROOT));
		}
		return Component.literal(name.toString());
	}

	private record Facts(@Nullable IWorldGenConfig entry, GeologyMaterial material, Component name, int colour,
						 double viability, Component hostRock, List<Component> stones, List<Associate> associates,
						 int bandMin, int bandMax)
	{
		@Nullable
		OreConfig config()
		{
			return entry==null?null: IGServerConfig.ORES.ores.get(entry);
		}
	}

	private record Associate(ItemStack icon, double share)
	{
	}

	private static Facts facts(GeologyMaterial material)
	{
		Facts cached = FACTS.get(material);
		IWorldGenConfig entry = cached!=null?cached.entry(): entryFor(material);
		OreConfig config = entry==null?null: IGServerConfig.ORES.ores.get(entry);
		int bandMin = config==null?worldMin(): IGServerConfig.getOrDefault(config.minY);
		int bandMax = config==null?worldMax(): IGServerConfig.getOrDefault(config.maxY);
		if(cached!=null&&cached.bandMin()==bandMin&&cached.bandMax()==bandMax) return cached;

		Facts facts = new Facts(
				entry,
				material,
				Component.translatable("material.immersivegeology."+material.getName().toLowerCase(Locale.ROOT)),
				readable(material.getColor(BlockCategoryFlags.ORE_BLOCK, 0)),
				material.getNoiseProbability(),
				formationList(material),
				stoneList(material),
				associatesOf(material, bandMin, bandMax),
				bandMin, bandMax);
		FACTS.put(material, facts);
		return facts;
	}

	@Nullable
	private static IWorldGenConfig entryFor(GeologyMaterial material)
	{
		for(IWorldGenConfig entry : IGServerConfig.ORES.ores.keySet())
		{
			if(entry.instance()==material) return entry;
		}
		return null;
	}

	private static Component formationList(GeologyMaterial material)
	{
		List<StoneFormation> formations = new ArrayList<>(material.getValidStoneFormations());
		formations.sort(Comparator.comparingInt(StoneFormation::ordinal));
		if(formations.isEmpty()) return Component.translatable(KEY+"formation.none");

		Component joined = null;
		for(StoneFormation formation : formations)
		{
			Component name = Component.translatable(KEY+"formation."+formation.name().toLowerCase(Locale.ROOT));
			joined = joined==null?name: joined.copy().append(", ").append(name);
		}
		return joined;
	}

	private static List<Component> stoneList(GeologyMaterial material)
	{
		Map<ModFlags, Component> bySource = new EnumMap<>(ModFlags.class);
		for(StoneEnum stone : StoneEnum.values())
		{
			if(!stone.isStoneTypeValid()||!material.acceptableStoneType(stone)) continue;
			bySource.merge(sourceOf(stone), stone.getTranslation().copy(),
					(joined, name) -> joined.copy().append(", ").append(name));
		}

		if(bySource.isEmpty()) return List.of(Component.translatable(KEY+"formation.none"));

		List<Component> lines = new ArrayList<>();
		bySource.forEach((source, stones) -> lines.add(Component.translatable(KEY+"host_row",
				Component.translatable(KEY+"source."+source.name().toLowerCase(Locale.ROOT)), stones)
				.withStyle(ChatFormatting.GRAY)));
		return List.copyOf(lines);
	}

	private static ModFlags sourceOf(StoneEnum stone)
	{
		for(IFlagType<?> flag : stone.getFlags())
		{
			if(flag instanceof ModFlags mod) return mod;
		}
		return ModFlags.MINECRAFT;
	}

	private static List<Associate> associatesOf(GeologyMaterial material, int bandMin, int bandMax)
	{
		var friends = material.getAssociateMaterialSet();
		double totalWeight = 0;
		for(Pair<Function<Integer, MaterialHelper>, Integer> friend : friends) totalWeight += friend.getSecond();
		if(totalWeight <= 0) return List.of();

		int low = Math.min(bandMin, bandMax);
		int high = Math.max(bandMin, bandMax);
		int samples = 33;
		Map<MaterialHelper, Double> shares = new HashMap<>();
		for(int i = 0; i < samples; i++)
		{
			int height = high==low?low: low+(high-low)*i/(samples-1);
			for(Pair<Function<Integer, MaterialHelper>, Integer> friend : friends)
			{
				MaterialHelper picked = friend.getFirst().apply(height);
				if(picked==null||picked==material) continue;
				shares.merge(picked, friend.getSecond()/totalWeight/samples, Double::sum);
			}
		}

		List<Associate> associates = new ArrayList<>();
		shares.entrySet().stream()
				.sorted(Map.Entry.<MaterialHelper, Double>comparingByValue().reversed())
				.limit(ASSOCIATE_MAX)
				.forEach(entry -> {
					ItemStack icon = entry.getKey() instanceof GeologyMaterial geology?geology.getOreIcon()
							: ItemStack.EMPTY;
					if(!icon.isEmpty()) associates.add(new Associate(icon, entry.getValue()));
				});
		return associates;
	}
}
