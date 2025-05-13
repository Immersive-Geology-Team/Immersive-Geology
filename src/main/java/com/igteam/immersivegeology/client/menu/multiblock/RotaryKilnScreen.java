/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.menu.multiblock;

import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.gui.info.EnergyInfoArea;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import blusunrize.immersiveengineering.common.gui.sync.GetterAndSetter;
import com.google.common.collect.ImmutableList;
import com.igteam.immersivegeology.common.block.multiblocks.gui.RotaryKilnMenu;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Stream;

public class RotaryKilnScreen extends IEContainerScreen<RotaryKilnMenu>
{
	private static final ResourceLocation TEXTURE = IGLib.makeTextureLocation("multiblocks/rotarykiln");

	public RotaryKilnScreen(RotaryKilnMenu menu, Inventory inv, Component title)
	{
		super(menu, inv, title, TEXTURE);
		this.imageHeight = 190;
		this.imageWidth = 254;
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
		PoseStack pose = graphics.pose();

		graphics.blit(TEXTURE, leftPos+218, topPos+34, 44, 192, 31, 62);

		pose.pushPose();
		pose.translate(leftPos+218, topPos+61,0);
		pose.mulPose(new Quaternionf().rotateAxis(75 * Mth.DEG_TO_RAD, new Vector3f(0,0,1)));
		pose.pushPose();
		pose.mulPose(new Quaternionf().rotateAxis(-(this.menu.heat.get()) * Mth.DEG_TO_RAD, new Vector3f(0,0,1)));
		graphics.blit(TEXTURE, 0,0, 6, 193, 32, 7);
		pose.popPose();
		pose.popPose();

		graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
	{
		super.renderLabels(graphics, mouseX, mouseY);
	}

	private static int getProcessStepFromPacked(int packed, int position) {
		if (position < 0 || position > 6)
			throw new IllegalArgumentException("Position must be between 0 and 6");
		return (packed >> ((position) * 4)) & 0xF;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		super.render(graphics, mouseX, mouseY, partialTicks);
		int packedData = menu.packed_process_data.get();

		for(int slotID = 0; slotID < 7; slotID++)
		{
			int process = getProcessStepFromPacked(packedData, slotID);
			graphics.blit(TEXTURE, leftPos+16+(slotID * 18), topPos+50, 6, 202, 11, process);
		}

		int fe = this.menu.energyAverage.get();
		PoseStack pose = graphics.pose();
		pose.pushPose();
		pose.translate(leftPos + 197,topPos + 103.5f,0);
			pose.pushPose();
			pose.scale(0.75f,0.75f,0.75f);
			graphics.drawString(ClientUtils.font(), Component.literal(fe+"FE/t"), 0,0, Lib.COLOUR_I_ImmersiveOrange);
			pose.popPose();
		pose.popPose();
	}

	@Override
	protected void drawContainerBackgroundPre(@Nonnull GuiGraphics graphics, float f, int mx, int my)
	{

	}

	@NotNull
	@Override
	protected List<InfoArea> makeInfoAreas()
	{
		IEnergyStorage lv = this.menu.energy_lv;
		IEnergyStorage mv = this.menu.energy_mv;
		IEnergyStorage hv = this.menu.energy_hv;
		return ImmutableList.of(
				new EnergyInfoArea(this.leftPos + 157,this.topPos + 39, lv),
				new EnergyInfoArea(this.leftPos + 174,this.topPos + 39, mv),
				new EnergyInfoArea(this.leftPos + 191,this.topPos + 39, hv)
		);
	}
}
