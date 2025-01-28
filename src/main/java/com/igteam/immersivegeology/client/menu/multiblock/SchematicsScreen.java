/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.menu.multiblock;

import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import com.igteam.immersivegeology.common.menu.SchematicOutputArea;
import com.igteam.immersivegeology.common.menu.SchematicsContainerMenu;
import com.igteam.immersivegeology.common.menu.SchematicsContainerMenu.SchematicSlot;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SchematicsScreen extends IEContainerScreen<SchematicsContainerMenu>
{
	private static final ResourceLocation TEXTURE = IGLib.makeTextureLocation("bloomery");

	public SchematicsScreen(SchematicsContainerMenu inventorySlotsIn, Inventory inv, Component title)
	{
		super(inventorySlotsIn, inv, title, TEXTURE);
	}

	@Override
	protected void init()
	{
		super.init();
		this.inventoryLabelY = this.imageHeight - 95;
		this.inventoryLabelX = 9;
	}

	@Override
	protected void drawBackgroundTexture(GuiGraphics graphics)
	{
		graphics.blit(TEXTURE, leftPos, topPos, 0, 0, 176, imageHeight);
	}

	@Override
	protected void drawContainerBackgroundPre(@Nonnull GuiGraphics graphics, float f, int mx, int my)
	{

	}

	@NotNull
	@Override
	protected List<InfoArea> makeInfoAreas()
	{
		List<InfoArea> areas = new ArrayList<>();
		for(int i = 0; i < menu.ownSlotCount; i++)
		{
			Slot s = menu.getSlot(i);
			if(s instanceof SchematicSlot schematicSlot)
				areas.add(new SchematicOutputArea(schematicSlot, leftPos, topPos));
		}
		return areas;
	}
}
