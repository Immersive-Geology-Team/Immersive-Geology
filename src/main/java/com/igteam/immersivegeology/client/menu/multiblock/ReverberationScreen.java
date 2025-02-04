/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.menu.multiblock;

import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.gui.info.EnergyInfoArea;
import blusunrize.immersiveengineering.client.gui.info.FluidInfoArea;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import blusunrize.immersiveengineering.common.gui.SqueezerMenu;
import com.google.common.collect.ImmutableList;
import com.igteam.immersivegeology.common.block.multiblocks.gui.BloomeryMenu;
import com.igteam.immersivegeology.common.block.multiblocks.gui.ReverberationFurnaceMenu;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGFurnaceHandler.StateView;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGRevFurnaceHandler.RevStateView;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

public class ReverberationScreen extends IEContainerScreen<ReverberationFurnaceMenu>
{
	private static final ResourceLocation TEXTURE = IGLib.makeTextureLocation("reverberation_furnace");

	public ReverberationScreen(ReverberationFurnaceMenu inventorySlotsIn, Inventory inv, Component title)
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

	static int leftArrowX = 42;
	static int rightArrowX = 122;
	static int leftFlameX = 32;
	static int rightFlameX = 112;

	public static void drawFlameAndArrowLeft(ContainerData state, GuiGraphics graphics, int leftPos, int topPos)
	{
		if(RevStateView.getLastBurnTimeLeft(state) > 0)
		{
			int h = (int)(12*(RevStateView.getBurnTimeLeft(state)/(float)RevStateView.getLastBurnTimeLeft(state)));
			graphics.blit(TEXTURE, leftPos+leftFlameX, topPos+49-h, 179, 1+12-h, 9, h);
		}
		if(RevStateView.getMaxProcessLeft(state) > 0)
		{
			int w = (int)(22*(1-RevStateView.getProcessLeft(state)/(float)RevStateView.getMaxProcessLeft(state)));
			graphics.blit(TEXTURE, leftPos+leftArrowX, topPos+34, 176, 15, w, 16);
		}
	}

	public static void drawFlameAndArrowRight(ContainerData state, GuiGraphics graphics, int leftPos, int topPos)
	{
		if(RevStateView.getLastBurnTimeRight(state) > 0)
		{
			int h = (int)(12*(RevStateView.getBurnTimeRight(state)/(float)RevStateView.getLastBurnTimeRight(state)));
			graphics.blit(TEXTURE, leftPos+rightFlameX, topPos+37+12-h, 179, 1+12-h, 9, h);
		}
		if(RevStateView.getMaxProcessRight(state) > 0)
		{
			int w = (int)(22*(1-RevStateView.getProcessRight(state)/(float)RevStateView.getMaxProcessRight(state)));
			graphics.blit(TEXTURE, leftPos+rightArrowX, topPos+34, 176, 15, w, 16);
		}
	}

	@Override
	protected void drawBackgroundTexture(GuiGraphics graphics)
	{
		graphics.blit(TEXTURE, leftPos, topPos, 0, 0, 176, imageHeight);
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
	{
		super.renderLabels(graphics, mouseX, mouseY);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		super.render(graphics, mouseX, mouseY, partialTicks);
		graphics.blit(TEXTURE, leftPos+11, topPos+16, 180, 126, 20, 51);
	}

	@Override
	protected void drawContainerBackgroundPre(@Nonnull GuiGraphics graphics, float f, int mx, int my)
	{
		drawFlameAndArrowLeft(menu.state, graphics, leftPos, topPos);
		drawFlameAndArrowRight(menu.state, graphics, leftPos, topPos);
	}

	@NotNull
	@Override
	protected List<InfoArea> makeInfoAreas()
	{
		return ImmutableList.of(new FluidInfoArea(this.menu.tank, new Rect2i(this.leftPos + 13, this.topPos + 18, 16, 47), 177, 31, 20, 51, TEXTURE));
	}
}
