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
import com.igteam.immersivegeology.common.block.multiblocks.gui.ChemicalReactorMenu;
import com.igteam.immersivegeology.common.block.multiblocks.gui.CrystallizerMenu;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

public class ChemicalReactorScreen extends IEContainerScreen<ChemicalReactorMenu>
{
	private static final ResourceLocation TEXTURE = IGLib.makeTextureLocation("multiblocks/chemical_reactor");

	public ChemicalReactorScreen(ChemicalReactorMenu inventorySlotsIn, Inventory inv, Component title)
	{
		super(inventorySlotsIn, inv, title, TEXTURE);
		this.imageHeight = 209;
		this.imageWidth = 208;
	}

	@Override
	protected void init()
	{
		super.init();
		this.inventoryLabelY = this.imageHeight - 94;
		this.inventoryLabelX = 24;
	}

	@Override
	protected void drawBackgroundTexture(GuiGraphics graphics)
	{
		graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
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
	}

	@Override
	protected void drawContainerBackgroundPre(@Nonnull GuiGraphics graphics, float f, int mx, int my)
	{
		//int w = (int)(this.menu.guiProgress.get() * 47);
		//graphics.blit(TEXTURE, leftPos+76, topPos+19, 181, 0, w, 27);
	}

	@NotNull
	@Override
	protected List<InfoArea> makeInfoAreas()
	{
		return ImmutableList.of(
				new FluidInfoArea(this.menu.tanks.leftInput(), new Rect2i(this.leftPos + 19, this.topPos + 56, 6, 32), 0, 0, 0, 0, TEXTURE),
				new FluidInfoArea(this.menu.tanks.backInput(), new Rect2i(this.leftPos + 52, this.topPos + 56, 6, 32), 0, 0, 0, 0, TEXTURE),
				new FluidInfoArea(this.menu.tanks.rightInput(), new Rect2i(this.leftPos + 85, this.topPos + 56, 6, 32), 0, 0, 0, 0, TEXTURE),
				new FluidInfoArea(this.menu.tanks.output(), new Rect2i(this.leftPos + 182, this.topPos + 56, 6, 32), 0, 0, 0, 0, TEXTURE),
				new EnergyInfoArea(this.leftPos + 108,this.topPos + 51, this.menu.energy));
	}
}