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
import com.igteam.immersivegeology.common.block.multiblocks.gui.SmallChemicalReactorMenu;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

public class SmallChemicalReactorScreen extends IEContainerScreen<SmallChemicalReactorMenu>
{
	private static final ResourceLocation TEXTURE = IGLib.makeTextureLocation("multiblocks/small_chemical_reactor");

	public SmallChemicalReactorScreen(SmallChemicalReactorMenu inventorySlotsIn, Inventory inv, Component title)
	{
		super(inventorySlotsIn, inv, title, TEXTURE);
		this.imageHeight = 208;
		this.imageWidth = 179;
	}

	@Override
	protected void init()
	{
		super.init();
		this.inventoryLabelY = this.imageHeight - 93;
		this.inventoryLabelX = 10;
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
		graphics.drawString(this.font, Component.literal("Working"), 69, 98, -557004, true);
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
				new FluidInfoArea(this.menu.tanks.rightInput(), new Rect2i(this.leftPos + 158, this.topPos + 33, 6, 14), 0, 0, 0, 0, TEXTURE),
				new FluidInfoArea(this.menu.tanks.leftInput(), new Rect2i(this.leftPos + 158, this.topPos + 50, 6, 15), 0, 0, 0, 0, TEXTURE),
				new FluidInfoArea(this.menu.tanks.output(), new Rect2i(this.leftPos + 158, this.topPos + 81, 6, 11), 0, 0, 0, 0, TEXTURE),
				new EnergyInfoArea(this.leftPos + 136,this.topPos + 45, this.menu.energy));
	}
}