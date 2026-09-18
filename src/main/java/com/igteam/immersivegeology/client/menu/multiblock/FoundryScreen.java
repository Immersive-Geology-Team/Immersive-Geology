/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.menu.multiblock;

import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import blusunrize.immersiveengineering.client.utils.GuiHelper;
import com.igteam.immersivegeology.common.block.multiblocks.logic.FoundryLogic;
import com.google.common.collect.ImmutableList;
import com.igteam.immersivegeology.common.block.multiblocks.gui.FoundryMenu;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class FoundryScreen extends IEContainerScreen<FoundryMenu>
{
	private static final ResourceLocation TEXTURE = IGLib.makeTextureLocation("multiblocks/foundry");

	private static final int ENERGY_X = 10;
	private static final int ENERGY_Y = 22;
	private static final int ENERGY_WIDTH = 7;
	private static final int ENERGY_HEIGHT = 47;

	private static final int TANK_X = 22;
	private static final int TANK_Y = 22;
	private static final int TANK_WIDTH = 16;
	private static final int TANK_HEIGHT = 47;

	private static final int ARROW_X = 42;
	private static final int ARROW_Y = 77;
	private static final int ARROW_U = 203;
	private static final int ARROW_V = 0;
	private static final int ARROW_WIDTH = 18;
	private static final int ARROW_HEIGHT = 10;

	private static final int OVERLAY_X = 20;
	private static final int OVERLAY_Y = 20;
	private static final int OVERLAY_U = 202;
	private static final int OVERLAY_V = 12;
	private static final int OVERLAY_WIDTH = 20;
	private static final int OVERLAY_HEIGHT = 51;

	private static final int SLICE = 32;
	private static final int SLICE_CORNER = 4;
	private static final int BORDER_U = 177;
	private static final int BORDER_V = 65;
	private static final int BACKGROUND_U = 211;
	private static final int BACKGROUND_V = 65;

	private static final int POPUP_WIDTH = 132;
	private static final int POPUP_HEIGHT = 56;

	private static final int CANCEL_U = 178;
	private static final int CANCEL_HOVER_U = 189;
	private static final int CANCEL_V = 13;
	private static final int CANCEL_SIZE = 10;
	private static final int ACCEPT_U = 178;
	private static final int ACCEPT_V = 25;
	private static final int ACCEPT_HOVER_V = 37;
	private static final int ACCEPT_SIZE = 11;

	private int purgeTarget = -1;

	public FoundryScreen(FoundryMenu menu, Inventory inv, Component title)
	{
		super(menu, inv, title, TEXTURE);
		this.imageWidth = 176;
		this.imageHeight = 227;
	}

	@Override
	protected void init()
	{
		super.init();
		this.inventoryLabelY = 134;
		this.inventoryLabelX = 8;
	}

	@Override
	protected void drawBackgroundTexture(GuiGraphics graphics)
	{
		graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	protected void drawContainerBackgroundPre(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
	{
		final int width = Math.round(ARROW_WIDTH*Mth.clamp(menu.progress.get(), 0, 1));
		if(width > 0)
			graphics.blit(TEXTURE, leftPos+ARROW_X, topPos+ARROW_Y, ARROW_U, ARROW_V, width, ARROW_HEIGHT);
	}

	private int popupLeft()
	{
		return leftPos+(imageWidth-POPUP_WIDTH)/2;
	}

	private int popupTop()
	{
		return topPos+40;
	}

	private Rect2i cancelButton()
	{
		return new Rect2i(popupLeft()+POPUP_WIDTH/2-CANCEL_SIZE-8, popupTop()+POPUP_HEIGHT-CANCEL_SIZE-9, CANCEL_SIZE, CANCEL_SIZE);
	}

	private Rect2i acceptButton()
	{
		return new Rect2i(popupLeft()+POPUP_WIDTH/2+8, popupTop()+POPUP_HEIGHT-ACCEPT_SIZE-9, ACCEPT_SIZE, ACCEPT_SIZE);
	}

	private static boolean within(Rect2i rect, double x, double y)
	{
		return x >= rect.getX()&&x < rect.getX()+rect.getWidth()&&y >= rect.getY()&&y < rect.getY()+rect.getHeight();
	}

	private int tankAt(double mouseX, double mouseY)
	{
		final int left = leftPos+TANK_X;
		final int bottom = topPos+TANK_Y+TANK_HEIGHT;
		if(mouseX < left||mouseX >= left+TANK_WIDTH) return -1;
		int offset = 0;
		for(int i = 0; i < menu.tanks.length; i++)
		{
			int height = layerHeight(menu.tanks[i].getFluid());
			if(height <= 0) continue;
			if(mouseY >= bottom-offset-height&&mouseY < bottom-offset) return i;
			offset += height;
		}
		return -1;
	}

	private static int layerHeight(FluidStack fluid)
	{
		if(fluid.isEmpty()) return 0;
		return Math.max(1, Math.round(TANK_HEIGHT*fluid.getAmount()/(float)FoundryLogic.TANK_VOLUME));
	}

	private void nineSlice(GuiGraphics graphics, int u, int v, int x, int y, int width, int height)
	{
		final int c = SLICE_CORNER;
		final int inner = SLICE-2*c;
		graphics.blit(TEXTURE, x, y, c, c, u, v, c, c, 256, 256);
		graphics.blit(TEXTURE, x+width-c, y, c, c, u+SLICE-c, v, c, c, 256, 256);
		graphics.blit(TEXTURE, x, y+height-c, c, c, u, v+SLICE-c, c, c, 256, 256);
		graphics.blit(TEXTURE, x+width-c, y+height-c, c, c, u+SLICE-c, v+SLICE-c, c, c, 256, 256);
		graphics.blit(TEXTURE, x+c, y, width-2*c, c, u+c, v, inner, c, 256, 256);
		graphics.blit(TEXTURE, x+c, y+height-c, width-2*c, c, u+c, v+SLICE-c, inner, c, 256, 256);
		graphics.blit(TEXTURE, x, y+c, c, height-2*c, u, v+c, c, inner, 256, 256);
		graphics.blit(TEXTURE, x+width-c, y+c, c, height-2*c, u+SLICE-c, v+c, c, inner, 256, 256);
		graphics.blit(TEXTURE, x+c, y+c, width-2*c, height-2*c, u+c, v+c, inner, inner, 256, 256);
	}

	private void drawPurgePopup(GuiGraphics graphics, int mouseX, int mouseY)
	{
		final int x = popupLeft(), y = popupTop();
		final FluidStack fluid = menu.tanks[purgeTarget].getFluid();

		graphics.pose().pushPose();
		graphics.pose().translate(0, 0, 300);
		nineSlice(graphics, BACKGROUND_U, BACKGROUND_V, x, y, POPUP_WIDTH, POPUP_HEIGHT);
		nineSlice(graphics, BORDER_U, BORDER_V, x, y, POPUP_WIDTH, POPUP_HEIGHT);

		Component title = Component.translatable("gui.immersivegeology.foundry.purge");
		Component detail = fluid.getDisplayName().copy().append(Component.literal(" "+fluid.getAmount()+" mB"));
		graphics.drawString(font, title, x+(POPUP_WIDTH-font.width(title))/2, y+9, 0xffffffff, true);
		graphics.drawString(font, detail, x+(POPUP_WIDTH-font.width(detail))/2, y+21, 0xffffd24a, true);

		Rect2i cancel = cancelButton(), accept = acceptButton();
		graphics.blit(TEXTURE, cancel.getX(), cancel.getY(),
				within(cancel, mouseX, mouseY)?CANCEL_HOVER_U: CANCEL_U, CANCEL_V, CANCEL_SIZE, CANCEL_SIZE);
		graphics.blit(TEXTURE, accept.getX(), accept.getY(), ACCEPT_U,
				within(accept, mouseX, mouseY)?ACCEPT_HOVER_V: ACCEPT_V, ACCEPT_SIZE, ACCEPT_SIZE);
		graphics.pose().popPose();
	}

	@Override
	public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		super.render(graphics, mouseX, mouseY, partialTicks);
		if(purgeTarget >= 0&&purgeTarget < menu.tanks.length&&!menu.tanks[purgeTarget].getFluid().isEmpty())
			drawPurgePopup(graphics, mouseX, mouseY);
		else purgeTarget = -1;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if(purgeTarget >= 0)
		{
			if(within(acceptButton(), mouseX, mouseY))
			{
				sendUpdateToServer(FoundryMenu.purgeMessage(purgeTarget));
				purgeTarget = -1;
			}
			else if(within(cancelButton(), mouseX, mouseY)) purgeTarget = -1;
			return true;
		}
		int tank = tankAt(mouseX, mouseY);
		if(tank >= 0)
		{
			purgeTarget = tank;
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers)
	{
		if(purgeTarget >= 0&&key==GLFW.GLFW_KEY_ESCAPE)
		{
			purgeTarget = -1;
			return true;
		}
		return super.keyPressed(key, scanCode, modifiers);
	}

	@NotNull
	@Override
	protected List<InfoArea> makeInfoAreas()
	{
		return ImmutableList.of(
				new StackedTankArea(new Rect2i(leftPos+TANK_X, topPos+TANK_Y, TANK_WIDTH, TANK_HEIGHT)),
				new EnergyArea(new Rect2i(leftPos+ENERGY_X, topPos+ENERGY_Y, ENERGY_WIDTH, ENERGY_HEIGHT))
		);
	}

	private class StackedTankArea extends InfoArea
	{
		private StackedTankArea(Rect2i area)
		{
			super(area);
		}

		private int heightOf(FluidStack fluid)
		{
			if(fluid.isEmpty()) return 0;
			return Math.max(1, Math.round(area.getHeight()*fluid.getAmount()/(float)FoundryLogic.TANK_VOLUME));
		}

		@Override
		public void draw(GuiGraphics graphics)
		{
			graphics.pose().pushPose();
			MultiBufferSource.BufferSource buffer = graphics.bufferSource();
			int offset = 0;
			for(FluidTank tank : menu.tanks)
			{
				FluidStack fluid = tank.getFluid();
				int height = heightOf(fluid);
				if(height <= 0) continue;
				GuiHelper.drawRepeatedFluidSpriteGui(buffer, graphics.pose(), fluid,
						area.getX(), area.getY()+area.getHeight()-offset-height, area.getWidth(), height);
				offset += height;
			}
			buffer.endBatch();
			graphics.pose().popPose();
			graphics.blit(TEXTURE, leftPos+OVERLAY_X, topPos+OVERLAY_Y, OVERLAY_U, OVERLAY_V, OVERLAY_WIDTH, OVERLAY_HEIGHT);
		}

		@Override
		protected void fillTooltipOverArea(int mouseX, int mouseY, List<Component> tooltip)
		{
			final int bottom = area.getY()+area.getHeight();
			int offset = 0;
			int total = 0;
			List<Component> lines = new ArrayList<>();
			for(FluidTank tank : menu.tanks)
			{
				FluidStack fluid = tank.getFluid();
				int height = heightOf(fluid);
				if(height <= 0) continue;
				total += fluid.getAmount();
				boolean hovered = mouseY >= bottom-offset-height&&mouseY < bottom-offset;
				MutableComponent name = fluid.getDisplayName().copy();
				lines.add(0, hovered?name.withStyle(ChatFormatting.YELLOW): name);
				lines.add(1, Component.literal(fluid.getAmount()+" mB").withStyle(ChatFormatting.GRAY));
				offset += height;
			}
			if(lines.isEmpty()) tooltip.add(Component.translatable("gui.immersiveengineering.empty"));
			else tooltip.addAll(lines);
			tooltip.add(Component.literal(total+"/"+FoundryLogic.TANK_VOLUME+"mB").withStyle(ChatFormatting.DARK_GRAY));
		}
	}

	private class EnergyArea extends InfoArea
	{
		private EnergyArea(Rect2i area)
		{
			super(area);
		}

		@Override
		protected void fillTooltipOverArea(int mouseX, int mouseY, List<Component> tooltip)
		{
			tooltip.add(Component.literal(menu.energy.getEnergyStored()+"/"+menu.energy.getMaxEnergyStored()+" IF"));
		}

		@Override
		public void draw(GuiGraphics graphics)
		{
			final int max = menu.energy.getMaxEnergyStored();
			if(max <= 0) return;
			final int stored = Math.round(area.getHeight()*Mth.clamp(menu.energy.getEnergyStored()/(float)max, 0, 1));
			if(stored <= 0) return;
			graphics.fillGradient(
					area.getX(), area.getY()+area.getHeight()-stored,
					area.getX()+area.getWidth(), area.getY()+area.getHeight(),
					0xffb51500, 0xff600b00
			);
		}
	}
}
