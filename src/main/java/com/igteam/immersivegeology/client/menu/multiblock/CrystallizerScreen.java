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
import com.google.common.collect.ImmutableList;
import com.igteam.immersivegeology.common.block.multiblocks.gui.CrystallizerMenu;
import com.igteam.immersivegeology.common.block.multiblocks.gui.ReverberationFurnaceMenu;
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

public class CrystallizerScreen extends IEContainerScreen<CrystallizerMenu>
{
	private static final ResourceLocation TEXTURE = IGLib.makeTextureLocation("multiblocks/crystallizer");

	public CrystallizerScreen(CrystallizerMenu inventorySlotsIn, Inventory inv, Component title)
	{
		super(inventorySlotsIn, inv, title, TEXTURE);
		this.imageHeight = 200;
	}

	@Override
	protected void init()
	{
		super.init();
		this.inventoryLabelY = this.imageHeight - 94;
		this.inventoryLabelX = 8;
	}

	@Override
	protected void drawBackgroundTexture(GuiGraphics graphics)
	{
		graphics.blit(TEXTURE, leftPos, topPos, 0, 0, 174, imageHeight);
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
		int w = (int)(this.menu.guiProgress.get() * 47);
		graphics.blit(TEXTURE, leftPos+76, topPos+19, 181, 0, w, 27);
	}

	@NotNull
	@Override
	protected List<InfoArea> makeInfoAreas()
	{
		return ImmutableList.of(
				new FluidInfoArea(this.menu.tanks[0], new Rect2i(this.leftPos + 49, this.topPos + 14, 16, 47), 174, 47, 20, 51, TEXTURE),
				new FluidInfoArea(this.menu.tanks[1], new Rect2i(this.leftPos + 89, this.topPos + 52, 34, 9), 0, 0, 0, 0, TEXTURE),
				new EnergyInfoArea(this.leftPos + 22,this.topPos + 15, this.menu.energy));
	}
}
