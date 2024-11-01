/*
 * ${USER}
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.menu.multiblock;

import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import com.igteam.immersivegeology.common.block.multiblocks.gui.BloomeryMenu;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGFurnaceHandler.StateView;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;

import javax.annotation.Nonnull;

public class BloomeryScreen extends IEContainerScreen<BloomeryMenu>
{
	private static final ResourceLocation TEXTURE = IGLib.makeTextureLocation("bloomery");

	public BloomeryScreen(BloomeryMenu inventorySlotsIn, Inventory inv, Component title)
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

	public static void drawFlameAndArrow(
			ContainerData state, GuiGraphics graphics, int leftPos, int topPos, int arrowXOffset
	)
	{
		if(StateView.getLastBurnTime(state) > 0)
		{
			int h = (int)(12*(StateView.getBurnTime(state)/(float)StateView.getLastBurnTime(state)));
			graphics.blit(TEXTURE, leftPos+54, topPos+37+12-h, 179, 1+12-h, 9, h);
		}
		if(StateView.getMaxProcess(state) > 0)
		{
			int w = (int)(22*(1-StateView.getProcess(state)/(float)StateView.getMaxProcess(state)));
			graphics.blit(TEXTURE, leftPos+arrowXOffset, topPos+13, 177, 14, w, 16);
		}
	}

	@Override
	protected void drawBackgroundTexture(GuiGraphics graphics)
	{
		graphics.blit(TEXTURE, leftPos, topPos, 0, 0, 176, imageHeight);
	}

	@Override
	protected void drawContainerBackgroundPre(@Nonnull GuiGraphics graphics, float f, int mx, int my)
	{
		drawFlameAndArrow(menu.state, graphics, leftPos, topPos, 72);
	}


}
