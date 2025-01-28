/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.menu;

import blusunrize.immersiveengineering.api.client.TextUtils;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import com.igteam.immersivegeology.common.item.blueprint.IGBlueprintSettings;
import com.igteam.immersivegeology.common.menu.SchematicsContainerMenu.SchematicSlot;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MiscEnum;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;

import java.util.ArrayList;
import java.util.List;

public class SchematicOutputArea extends InfoArea
{
	private final SchematicSlot slot;
	public SchematicOutputArea(SchematicSlot slot, int guiLeft, int guiTop)
	{
		super(new Rect2i(guiLeft+slot.x, guiTop+slot.y, 16, 16));
		this.slot = slot;
	}

	@Override
	protected void fillTooltipOverArea(int mouseX, int mouseY, List<Component> tooltip)
	{
		TemplateMultiblock template = slot.template;
		if(template == null) return;
		if(slot.hasItem()) return;

		ItemStack blueprint = new ItemStack(MiscEnum.Blueprint.getItem(ItemCategoryFlags.BLUEPRINT));
		IGBlueprintSettings settings = new IGBlueprintSettings(blueprint);
		settings.setMultiblock(template);
		settings.setMirror(false);
		settings.setRotation(Rotation.NONE);
		settings.setPlaced(false);
		settings.applyTo(blueprint);

		tooltip.add(TextUtils.applyFormat(blueprint.getHoverName().copy(), ChatFormatting.AQUA));

	}

	@Override
	public void draw(GuiGraphics graphics)
	{
		TemplateMultiblock template = slot.template;
		if(template == null) return;
		ItemStack blueprint = new ItemStack(MiscEnum.Blueprint.getItem(ItemCategoryFlags.BLUEPRINT));
		IGBlueprintSettings settings = new IGBlueprintSettings(blueprint);
		settings.setMultiblock(template);
		settings.setMirror(false);
		settings.setRotation(Rotation.NONE);
		settings.setPlaced(false);
		settings.applyTo(blueprint);
		if(slot.hasItem())
		{
			return;
		}

		graphics.renderItem(blueprint, area.getX(), area.getY());
		graphics.fill(RenderType.guiGhostRecipeOverlay(), area.getX(), area.getY(), area.getX()+area.getWidth(), area.getY()+area.getHeight(), 0xbb333333);
	}
}
