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
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.List;

public class SmallChemicalReactorScreen extends IEContainerScreen<SmallChemicalReactorMenu>
{
	private static final ResourceLocation TEXTURE = IGLib.makeTextureLocation("multiblocks/small_chemical_reactor");

	public SmallChemicalReactorScreen(SmallChemicalReactorMenu inventorySlotsIn, Inventory inv, Component title)
	{
		super(inventorySlotsIn, inv, title, TEXTURE);
		this.imageHeight = 204;
		this.imageWidth = 218;
	}

	@Override
	protected void init()
	{
		super.init();
		this.inventoryLabelY = this.imageHeight - 93;
		this.inventoryLabelX = 49;
	}

	@Override
	protected void drawBackgroundTexture(GuiGraphics graphics)
	{
		PoseStack pose = graphics.pose();
		graphics.blit(TEXTURE, leftPos+5, topPos+67, 225, 0, 31, 62);

		pose.pushPose();
		{
			pose.translate(leftPos+39, topPos+99, 0);
			pose.mulPose(new Quaternionf().rotateAxis(250*Mth.DEG_TO_RAD, new Vector3f(0, 0, 1)));
			pose.pushPose();
			{
				pose.translate(0, 3, 0);
				int damage = Math.round((getMenu().damage.get() / 100f) * 140);
				pose.mulPose(new Quaternionf().rotateAxis(-(damage)*Mth.DEG_TO_RAD, new Vector3f(0, 0, 1)));
				pose.translate(0, -3, 0);
				graphics.blit(TEXTURE, 0, 0, 224, 64, 32, 7);
			}
			pose.popPose();
		}
		pose.popPose();

		graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
	{
		super.renderLabels(graphics, mouseX, mouseY);
		String status = "immersivegeology.gui.basic_chemical_reactor.nominal_status";
		float damage = getMenu().damage.get();
		if(damage > 25) status = "immersivegeology.gui.basic_chemical_reactor.worn_status";
		if(damage > 50) status = "immersivegeology.gui.basic_chemical_reactor.damaged_status";
		if(damage > 99) status = "immersivegeology.gui.basic_chemical_reactor.failed_status";

		if(getMenu().tanks.output().getSpace() == 0) status = "immersivegeology.gui.basic_chemical_reactor.output_blocked";

		graphics.drawString(this.font, Component.translatable(status), 108, 94, -557004, true);
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
				new FluidInfoArea(this.menu.tanks.rightInput(), new Rect2i(this.leftPos + 196, this.topPos + 28, 6, 14), 0, 0, 0, 0, TEXTURE),
				new FluidInfoArea(this.menu.tanks.leftInput(), new Rect2i(this.leftPos + 196, this.topPos + 45, 6, 15), 0, 0, 0, 0, TEXTURE),
				new FluidInfoArea(this.menu.tanks.output(), new Rect2i(this.leftPos + 196, this.topPos + 76, 6, 11), 0, 0, 0, 0, TEXTURE),
				new EnergyInfoArea(this.leftPos + 174,this.topPos + 40, this.menu.energy));
	}
}