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
import com.google.common.collect.ImmutableList;
import com.igteam.immersivegeology.common.block.multiblocks.gui.BulkBlastFurnaceMenu;
import com.igteam.immersivegeology.common.block.multiblocks.logic.BulkBlastFurnaceLogic;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BulkBlastFurnaceScreen extends IEContainerScreen<BulkBlastFurnaceMenu>
{
	private static final ResourceLocation TEXTURE = IGLib.makeTextureLocation("multiblocks/bulk_blast_furnace");

	private static final int GAUGE_Y = 41;
	private static final int GAUGE_HEIGHT = 46;
	private static final int GAUGE_WIDTH = 7;
	private static final int HEAT_X = 13;
	private static final int FLUX_X = 25;
	private static final int FUEL_X = 37;

	private static final int TANK_X = 132;
	private static final int TANK_Y = 40;
	private static final int TANK_WIDTH = 16;
	private static final int TANK_HEIGHT = 47;

	private static final int PROGRESS_X = 53;
	private static final int PROGRESS_Y = 93;
	private static final int PROGRESS_WIDTH = 69;
	private static final int PROGRESS_HEIGHT = 7;

	private static final int STATUS_Y = 108;

	private static final int CHIP_SIZE = 10;
	private static final int CHIP_OFF_U = 178;
	private static final int CHIP_ON_U = 190;
	private static final int CHIP_V = 1;
	private static final int CHIP_Y = 7;
	private static final int[] CHIP_X = {146, 157};

	private static final int FLOOR_COLOUR = 0xff5a3320;
	private static final int MARKER_COLOUR = 0xffffffff;
	private static final int CARBON_MARKER_COLOUR = 0xffffc046;
	private static final int CAP_SHADE_COLOUR = 0x99000000;
	private static final int CAP_MARKER_COLOUR = 0xff7a7a7a;
	private static final int GOOD_ZONE_COLOUR = 0xff4caf50;
	private static final int FUEL_COLOUR = 0xff6b513c;
	private static final int FLUX_COLOUR = 0xffd8c47a;
	private static final int PROGRESS_COLOUR = 0xffff9800;
	private static final int HEATING_COLOUR = 0xffe0542e;

	private static final String[] STATUS_KEYS = {"idle", "needs_heat", "needs_flux", "needs_fuel", "output_full", "ready", "heating", "smelting"};
	private static final int[] STATUS_COLOURS = {0xb0a0a0, 0xe8c07a, 0xe8c07a, 0xe8c07a, 0xe87a7a, 0x9ddf9d, 0xffc046, 0xffc046};

	public BulkBlastFurnaceScreen(BulkBlastFurnaceMenu menu, Inventory inv, Component title)
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
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
	{
		super.renderLabels(graphics, mouseX, mouseY);
		drawHeatGauge(graphics);
		drawRatioGauge(graphics, FLUX_X, menu.flux.get(), menu.fluxNeeded.get(), FLUX_COLOUR);
		drawRatioGauge(graphics, FUEL_X, menu.fuel.get(), menu.fuelNeeded.get(), FUEL_COLOUR);
		drawProgress(graphics);
		drawChips(graphics);
	}

	private void drawHeatGauge(GuiGraphics graphics)
	{
		final float max = BulkBlastFurnaceLogic.MAX_HEAT;
		final float heat = menu.heat.get();

		fillGauge(graphics, HEAT_X, menu.heatFloor.get()/max, FLOOR_COLOUR);
		fillGauge(graphics, HEAT_X, heat/max, heatColour(heat));

		final float cap = menu.heatCap.get();
		if(cap < max)
		{
			graphics.fill(HEAT_X, GAUGE_Y, HEAT_X+GAUGE_WIDTH, gaugeY(cap/max), CAP_SHADE_COLOUR);
			gaugeMarker(graphics, HEAT_X, cap/max, CAP_MARKER_COLOUR);
		}

		final int required = menu.heatRequired.get();
		if(required > 0) gaugeMarker(graphics, HEAT_X, required/max, MARKER_COLOUR);
		final float carbonTarget = menu.carbonTarget.get();
		if(carbonTarget > required) gaugeMarker(graphics, HEAT_X, carbonTarget/max, CARBON_MARKER_COLOUR);
	}

	private void drawRatioGauge(GuiGraphics graphics, int x, float have, float needed, int colour)
	{
		if(needed <= 0) return;
		goodZone(graphics, x, needed);
		fillGauge(graphics, x, have/(needed*2), colour);
		gaugeMarker(graphics, x, 0.5f, have >= Math.floor(needed)?GOOD_ZONE_COLOUR: MARKER_COLOUR);
	}

	private void goodZone(GuiGraphics graphics, int x, float needed)
	{
		final float units = menu.material.get();
		if(units <= 0) return;
		final float ideal = needed/units;
		if(ideal <= 0) return;
		final float width = Math.max(ideal, BulkBlastFurnaceLogic.MIN_RATIO_TOLERANCE);
		final float half = (1-BulkBlastFurnaceLogic.GOOD_QUALITY)*width/(2*ideal);
		final int low = gaugeY(0.5f-half);
		final int high = gaugeY(0.5f+half);
		graphics.fill(x-1, high, x, low, GOOD_ZONE_COLOUR);
		graphics.fill(x+GAUGE_WIDTH, high, x+GAUGE_WIDTH+1, low, GOOD_ZONE_COLOUR);
	}

	private void drawProgress(GuiGraphics graphics)
	{
		final int status = Mth.clamp(menu.status.get(), 0, STATUS_KEYS.length-1);
		final boolean heating = status==6;
		final float progress = heating?menu.heatingProgress.get(): menu.progress.get();
		final int width = Math.round(PROGRESS_WIDTH*Mth.clamp(progress, 0, 1));
		if(width > 0)
			graphics.fill(PROGRESS_X, PROGRESS_Y, PROGRESS_X+width, PROGRESS_Y+PROGRESS_HEIGHT, heating?HEATING_COLOUR: PROGRESS_COLOUR);

		graphics.drawString(font, Component.translatable("gui.immersivegeology.bulk_blast_furnace."+STATUS_KEYS[status]),
				8, STATUS_Y, STATUS_COLOURS[status], true);
	}

	private void drawChips(GuiGraphics graphics)
	{
		final int states = menu.preheaterStates.get();
		for(int i = 0; i < CHIP_X.length; i++)
		{
			final int state = (states >> (i*2))&0b11;
			final int u = state==BulkBlastFurnaceLogic.PREHEATER_ACTIVE?CHIP_ON_U: CHIP_OFF_U;
			graphics.blit(TEXTURE, CHIP_X[i], CHIP_Y, u, CHIP_V, CHIP_SIZE, CHIP_SIZE);
		}
	}

	private int gaugeY(float fraction)
	{
		return GAUGE_Y+GAUGE_HEIGHT-Math.round(GAUGE_HEIGHT*Mth.clamp(fraction, 0, 1));
	}

	private void fillGauge(GuiGraphics graphics, int x, float fraction, int colour)
	{
		final int height = Math.round(GAUGE_HEIGHT*Mth.clamp(fraction, 0, 1));
		if(height > 0) graphics.fill(x, GAUGE_Y+GAUGE_HEIGHT-height, x+GAUGE_WIDTH, GAUGE_Y+GAUGE_HEIGHT, colour);
	}

	private void gaugeMarker(GuiGraphics graphics, int x, float fraction, int colour)
	{
		final int y = gaugeY(fraction);
		graphics.fill(x-1, y-1, x+GAUGE_WIDTH+1, y, colour);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		super.render(graphics, mouseX, mouseY, partialTicks);
		gaugeTooltip(graphics, mouseX, mouseY, HEAT_X, List.of(
				Component.translatable("gui.immersivegeology.bulk_blast_furnace.heat"),
				Component.literal(Math.round(menu.heat.get())+" / "+BulkBlastFurnaceLogic.MAX_HEAT),
				Component.translatable("gui.immersivegeology.bulk_blast_furnace.heat_minimum", menu.heatRequired.get()),
				Component.translatable("gui.immersivegeology.bulk_blast_furnace.carbon_target", Math.round(menu.carbonTarget.get())),
				Component.translatable("gui.immersivegeology.bulk_blast_furnace.heat_cap", Math.round(menu.heatCap.get()))
		));
		gaugeTooltip(graphics, mouseX, mouseY, FUEL_X, List.of(
				Component.translatable("gui.immersivegeology.bulk_blast_furnace.fuel"),
				Component.literal(trim(menu.fuel.get())+" / "+trim(menu.fuelNeeded.get()))
		));
		gaugeTooltip(graphics, mouseX, mouseY, FLUX_X, List.of(
				Component.translatable("gui.immersivegeology.bulk_blast_furnace.flux"),
				Component.literal(trim(menu.flux.get())+" / "+trim(menu.fluxNeeded.get()))
		));
		chipTooltips(graphics, mouseX, mouseY);
	}

	private void chipTooltips(GuiGraphics graphics, int mouseX, int mouseY)
	{
		final int states = menu.preheaterStates.get();
		for(int i = 0; i < CHIP_X.length; i++)
		{
			final int left = leftPos+CHIP_X[i];
			final int top = topPos+CHIP_Y;
			if(mouseX < left||mouseX >= left+CHIP_SIZE||mouseY < top||mouseY >= top+CHIP_SIZE) continue;
			final int state = (states >> (i*2))&0b11;
			final String key = switch(state)
					{
						case BulkBlastFurnaceLogic.PREHEATER_ACTIVE -> "preheater_active";
						case BulkBlastFurnaceLogic.PREHEATER_IDLE -> "preheater_idle";
						default -> "preheater_missing";
					};
			graphics.renderTooltip(font, Component.translatable("gui.immersivegeology.bulk_blast_furnace."+key), mouseX, mouseY);
		}
	}

	private void gaugeTooltip(GuiGraphics graphics, int mouseX, int mouseY, int x, List<Component> lines)
	{
		final int left = leftPos+x;
		final int top = topPos+GAUGE_Y;
		if(mouseX < left||mouseX >= left+GAUGE_WIDTH||mouseY < top||mouseY >= top+GAUGE_HEIGHT) return;
		graphics.renderComponentTooltip(font, new ArrayList<>(lines), mouseX, mouseY);
	}

	private static String trim(float value)
	{
		return value==Math.round(value)?String.valueOf(Math.round(value)): String.format("%.1f", value);
	}

	private static int heatColour(float heat)
	{
		float t = Mth.clamp(heat/BulkBlastFurnaceLogic.MAX_HEAT, 0, 1);
		int red = 0x80+Math.round(0x7f*t);
		int green = Math.round(0xc0*t);
		return 0xff000000|(red << 16)|(green << 8);
	}

	@NotNull
	@Override
	protected List<InfoArea> makeInfoAreas()
	{
		return ImmutableList.of(new CombinedTankArea(new Rect2i(leftPos+TANK_X, topPos+TANK_Y, TANK_WIDTH, TANK_HEIGHT)));
	}

	private class CombinedTankArea extends InfoArea
	{
		private final Rect2i rect;

		private CombinedTankArea(Rect2i rect)
		{
			super(rect);
			this.rect = rect;
		}

		@Override
		public void draw(GuiGraphics graphics)
		{
			final FluidStack metal = menu.metalTank.getFluid();
			final FluidStack slag = menu.slagTank.getFluid();
			final int metalHeight = fluidHeight(metal);
			final int slagHeight = fluidHeight(slag);

			graphics.pose().pushPose();
			MultiBufferSource.BufferSource buffer = graphics.bufferSource();
			if(metalHeight > 0)
				GuiHelper.drawRepeatedFluidSpriteGui(buffer, graphics.pose(), metal,
						rect.getX(), rect.getY()+rect.getHeight()-metalHeight, rect.getWidth(), metalHeight);
			if(slagHeight > 0)
				GuiHelper.drawRepeatedFluidSpriteGui(buffer, graphics.pose(), slag,
						rect.getX(), rect.getY()+rect.getHeight()-metalHeight-slagHeight, rect.getWidth(), slagHeight);
			buffer.endBatch();
			graphics.pose().popPose();
		}

		@Override
		protected void fillTooltipOverArea(int mouseX, int mouseY, List<Component> tooltip)
		{
			final FluidStack metal = menu.metalTank.getFluid();
			final FluidStack slag = menu.slagTank.getFluid();
			final int metalHeight = fluidHeight(metal);
			final int slagHeight = fluidHeight(slag);
			final int bottom = rect.getY()+rect.getHeight();
			final int metalTop = bottom-metalHeight;
			final int slagTop = metalTop-slagHeight;

			final boolean overMetal = metalHeight > 0&&mouseY >= metalTop&&mouseY < bottom;
			final boolean overSlag = slagHeight > 0&&mouseY >= slagTop&&mouseY < metalTop;

			if(!slag.isEmpty()) addFluidLines(tooltip, slag, overSlag);
			if(!metal.isEmpty()) addFluidLines(tooltip, metal, overMetal);
			if(metal.isEmpty()&&slag.isEmpty()) tooltip.add(Component.translatable("gui.immersiveengineering.empty"));
			tooltip.add(Component.translatable("gui.immersivegeology.bulk_blast_furnace.tank_total",
					metal.getAmount()+slag.getAmount(), BulkBlastFurnaceLogic.TOTAL_TANK_CAPACITY).withStyle(ChatFormatting.GRAY));
		}

		private void addFluidLines(List<Component> tooltip, FluidStack fluid, boolean hovered)
		{
			MutableComponent name = fluid.getDisplayName().copy();
			tooltip.add(hovered?name.withStyle(ChatFormatting.YELLOW): name);
			tooltip.add(Component.literal(fluid.getAmount()+" mB").withStyle(ChatFormatting.GRAY));
		}

		private int fluidHeight(FluidStack fluid)
		{
			return Math.round(rect.getHeight()*(fluid.getAmount()/(float)BulkBlastFurnaceLogic.TOTAL_TANK_CAPACITY));
		}
	}
}
