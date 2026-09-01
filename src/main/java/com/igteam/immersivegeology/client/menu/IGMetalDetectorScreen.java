/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.menu;

import com.igteam.immersivegeology.common.block.entity.device.DepositGrade;
import com.igteam.immersivegeology.common.block.entity.device.IGMetalDetectorEntity;
import com.igteam.immersivegeology.common.menu.IGMetalDetectorMenu;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class IGMetalDetectorScreen extends AbstractContainerScreen<IGMetalDetectorMenu>
{
	public static final ResourceLocation TEXTURE =
			new ResourceLocation(IGLib.MODID, "textures/gui/multiblocks/metal_detector.png");

	private static final int CELL_PITCH = 4;
	private static final int CELL_SIZE = 3;
	private static final int RADAR_X = 54;
	private static final int RADAR_Y = 8;

	private static final int BAR_Y = 10;
	private static final int BAR_W = 7;
	private static final int BAR_H = 46;
	private static final int ENERGY_X = 130;
	private static final int PROGRESS_X = 39;

	private static final int COLOUR_EMPTY_CELL = 0xFF1A1F1A;
	private static final int COLOUR_GRID = 0xFF243024;
	private static final int COLOUR_ORIGIN = 0xFFE0E0E0;
	private static final int COLOUR_ENERGY = 0xFFD54B18;
	private static final int COLOUR_PROGRESS = 0xFF4BC4FF;

	private static final int COLOUR_PROGRESS_IDLE = 0xFF3A6E85;
	private static final int COLOUR_OFFLINE = 0x99000000;

	private static final int COLOUR_NOTICE_FILL = 0xE0071207;
	private static final int COLOUR_NOTICE_EDGE = 0xFF4BFF6E;
	private static final int NOTICE_W = 62;
	private static final int NOTICE_H = 22;

	private static final float PING_TRAVEL = 36f;
	private static final float PING_GAP = 12f;
	private static final float PING_GAP_CHECK = 60f;

	private static final float PING_GAP_IDLE = 160f;
	private static final int PING_GREEN = 0x4BFF6E;

	private static final int PING_TRAIL = 3;
	private static final float PING_TRAIL_GAP = 0.55f;
	private static final float PING_TRAIL_FALLOFF = 0.45f;

	private static final long BLIP_DURATION = 600L;

	private final long[] blipStarts =
			new long[IGMetalDetectorEntity.RADAR_SIZE*IGMetalDetectorEntity.RADAR_SIZE];

	private float lastRing = 0f;
	private boolean ringPrimed = false;

	private float pingPhase = 0f;
	private float pingGap = PING_GAP_IDLE;
	private long pingNanos = 0L;

	private float displayedProgress = -1f;
	private long lastFrameNanos = 0L;

	private static float brightnessFor(DepositGrade grade)
	{
		return switch(grade)
				{
					case EMPTY -> 0f;
					case UNVERIFIED -> 0.30f;
					case DEPLETED -> 0.45f;
					case POOR -> 0.65f;
					case NORMAL -> 0.85f;
					case RICH -> 1.0f;
				};
	}

	public IGMetalDetectorScreen(IGMetalDetectorMenu menu, Inventory inventory, Component title)
	{
		super(menu, inventory, title);
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	@Override
	protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
	{
		graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		renderRadar(graphics);
		renderEnergyBar(graphics);
		renderProgressBar(graphics);
	}

	private void renderRadar(GuiGraphics graphics)
	{
		IGMetalDetectorEntity detector = menu.getDetector();
		int size = IGMetalDetectorEntity.RADAR_SIZE;
		int originX = leftPos+RADAR_X;
		int originY = topPos+RADAR_Y;

		for(int cellZ = 0; cellZ < size; cellZ++)
		{
			for(int cellX = 0; cellX < size; cellX++)
			{
				int x = originX+cellX*CELL_PITCH;
				int y = originY+cellZ*CELL_PITCH;

				MaterialInterface<?> material = detector!=null?detector.getRadarMaterial(cellX, cellZ): null;
				if(material==null)
				{
					graphics.fill(x, y, x+CELL_SIZE, y+CELL_SIZE, COLOUR_EMPTY_CELL);
					continue;
				}

				DepositGrade grade = detector.getGrade(cellX, cellZ);
				int base = material.getColor(BlockCategoryFlags.ORE_BLOCK, 0);
				graphics.fill(x, y, x+CELL_SIZE, y+CELL_SIZE, shade(base, brightnessFor(grade)));
			}
		}

		int centre = IGMetalDetectorEntity.RADAR_RADIUS;
		int crossX = originX+centre*CELL_PITCH;
		int crossY = originY+centre*CELL_PITCH;
		graphics.fill(originX, crossY+1, originX+size*CELL_PITCH-1, crossY+2, COLOUR_GRID);
		graphics.fill(crossX+1, originY, crossX+2, originY+size*CELL_PITCH-1, COLOUR_GRID);
		graphics.fill(crossX, crossY, crossX+CELL_SIZE, crossY+CELL_SIZE, COLOUR_ORIGIN);

		boolean online = detector!=null&&detector.isRunning();
		if(online) renderPing(graphics, originX, originY);
		renderBlips(graphics, originX, originY);

		if(!online)
		{
			graphics.fill(originX, originY, originX+size*CELL_PITCH-1, originY+size*CELL_PITCH-1, COLOUR_OFFLINE);
		}
		else if(detector!=null&&!detector.hasVisibleDeposits()) renderNoDeposits(graphics, originX, originY);
	}

	/**
	 * An empty grid is ambiguous on its own - it looks the same whether the survey came back with nothing or has
	 * not run yet - so once there is nothing left to draw, the grid says so.
	 */
	private void renderNoDeposits(GuiGraphics graphics, int originX, int originY)
	{
		int span = IGMetalDetectorEntity.RADAR_SIZE*CELL_PITCH-1;
		int left = originX+(span-NOTICE_W)/2;
		int top = originY+(span-NOTICE_H)/2;

		graphics.fill(left, top, left+NOTICE_W, top+NOTICE_H, COLOUR_NOTICE_FILL);
		graphics.fill(left, top, left+NOTICE_W, top+1, COLOUR_NOTICE_EDGE);
		graphics.fill(left, top+NOTICE_H-1, left+NOTICE_W, top+NOTICE_H, COLOUR_NOTICE_EDGE);
		graphics.fill(left, top, left+1, top+NOTICE_H, COLOUR_NOTICE_EDGE);
		graphics.fill(left+NOTICE_W-1, top, left+NOTICE_W, top+NOTICE_H, COLOUR_NOTICE_EDGE);

		int inner = NOTICE_W-6;
		List<FormattedCharSequence> lines = font.split(
				Component.translatable("gui.immersivegeology.metal_detector.no_deposits"), inner);
		if(lines.isEmpty()) return;

		// The box is fixed to the grid, so the wording is scaled to the box rather than the other way round; a
		// longer translation shrinks instead of spilling over the edge of the screen recess.
		int widest = 1;
		for(FormattedCharSequence line : lines) widest = Math.max(widest, font.width(line));
		int stacked = lines.size()*font.lineHeight;
		float scale = Math.min(1f, Math.min((float)inner/widest, (NOTICE_H-4f)/stacked));

		graphics.pose().pushPose();
		graphics.pose().translate(left+NOTICE_W/2f, top+NOTICE_H/2f, 0f);
		graphics.pose().scale(scale, scale, 1f);
		int y = -stacked/2;
		for(FormattedCharSequence line : lines)
		{
			graphics.drawString(font, line, -font.width(line)/2, y, COLOUR_NOTICE_EDGE, false);
			y += font.lineHeight;
		}
		graphics.pose().popPose();
	}

	private void renderPing(GuiGraphics graphics, int originX, int originY)
	{
		float time = advancePing();

		float ring = (time/PING_TRAVEL)*(IGMetalDetectorEntity.RADAR_RADIUS+1);
		trackSweep(ring);

		float alpha = 1f-Math.min(1f, time/PING_TRAVEL)*0.35f;
		for(int trail = 0; trail < PING_TRAIL; trail++)
		{
			drawRing(graphics, originX, originY, ring-trail*PING_TRAIL_GAP, alpha);
			alpha *= PING_TRAIL_FALLOFF;
		}
	}

	private float advancePing()
	{
		long now = System.nanoTime();
		float seconds = pingNanos==0L?0f: Math.min(0.25f, (now-pingNanos)/1_000_000_000f);
		pingNanos = now;
		pingPhase += seconds*20f;

		if(pingPhase >= PING_TRAVEL) pingGap = gapForMode();
		float cycle = PING_TRAVEL+pingGap;
		if(pingPhase >= cycle) pingPhase -= cycle;
		return pingPhase;
	}

	private float gapForMode()
	{
		return !menu.isIdle()?PING_GAP: menu.isSweeping()?PING_GAP_CHECK: PING_GAP_IDLE;
	}

	private void drawRing(GuiGraphics graphics, int originX, int originY, float ring, float alpha)
	{
		float edge = IGMetalDetectorEntity.RADAR_RADIUS;
		if(ring < 0f||ring > edge+1f) return;

		// Fade across the final cell so the sweep leaves the grid rather than popping off the edge of it.
		if(ring > edge) alpha *= 1f-(ring-edge);
		if(alpha <= 0.02f) return;

		float centreX = originX+edge*CELL_PITCH+CELL_SIZE/2f;
		float centreY = originY+edge*CELL_PITCH+CELL_SIZE/2f;
		float half = Math.min(ring, edge)*CELL_PITCH+CELL_SIZE/2f;

		int left = Math.round(centreX-half);
		int top = Math.round(centreY-half);
		int right = Math.round(centreX+half);
		int bottom = Math.round(centreY+half);

		if(right-left < 2||bottom-top < 2) return;

		int colour = ((int)(Math.min(1f, alpha)*255)<<24)|PING_GREEN;
		graphics.fill(left, top, right, top+1, colour);
		graphics.fill(left, bottom-1, right, bottom, colour);
		graphics.fill(left, top, left+1, bottom, colour);
		graphics.fill(right-1, top, right, bottom, colour);
	}

	private void trackSweep(float ring)
	{
		float from = lastRing;
		boolean sameSweep = ringPrimed&&ring >= from;
		ringPrimed = true;
		lastRing = ring;
		if(!sameSweep) return;

		IGMetalDetectorEntity detector = menu.getDetector();
		if(detector==null) return;

		int size = IGMetalDetectorEntity.RADAR_SIZE;
		int centre = IGMetalDetectorEntity.RADAR_RADIUS;
		long now = System.currentTimeMillis();
		for(int cellZ = 0; cellZ < size; cellZ++)
		{
			for(int cellX = 0; cellX < size; cellX++)
			{
				// The ring is a square, so a cell is reached at its Chebyshev distance from the middle.
				int distance = Math.max(Math.abs(cellX-centre), Math.abs(cellZ-centre));
				if(distance <= from||distance > ring) continue;
				// Anything the grid draws, which by the time a survey settles is only confirmed deposits anyway.
				if(detector.getRadarMaterial(cellX, cellZ)==null) continue;
				blipStarts[cellZ*size+cellX] = now;
			}
		}
	}

	private boolean hasMark(int cell)
	{
		IGMetalDetectorEntity detector = menu.getDetector();
		if(detector==null||cell < 0) return false;
		int size = IGMetalDetectorEntity.RADAR_SIZE;
		return detector.getRadarMaterial(cell%size, cell/size)!=null;
	}

	private void renderBlips(GuiGraphics graphics, int originX, int originY)
	{
		long now = System.currentTimeMillis();
		int size = IGMetalDetectorEntity.RADAR_SIZE;
		for(int cell = 0; cell < blipStarts.length; cell++)
		{
			if(blipStarts[cell]==0L) continue;
			long age = now-blipStarts[cell];
			if(age > BLIP_DURATION)
			{
				blipStarts[cell] = 0L;
				continue;
			}
			// The check that raised this blip may have been the one that emptied the cell; drop it with the mark.
			if(!hasMark(cell)) continue;
			drawBlip(graphics, originX, originY, cell%size, cell/size, 1f-(float)age/BLIP_DURATION);
		}
	}

	private void drawBlip(GuiGraphics graphics, int originX, int originY, int cellX, int cellZ, float fade)
	{
		int x = originX+cellX*CELL_PITCH;
		int y = originY+cellZ*CELL_PITCH;

		int spread = Math.round(fade*3f);
		int colour = ((int)(Math.min(1f, fade)*255)<<24)|PING_GREEN;
		int left = x-spread;
		int top = y-spread;
		int right = x+CELL_SIZE+spread;
		int bottom = y+CELL_SIZE+spread;
		graphics.fill(left, top, right, top+1, colour);
		graphics.fill(left, bottom-1, right, bottom, colour);
		graphics.fill(left, top, left+1, bottom, colour);
		graphics.fill(right-1, top, right, bottom, colour);
	}

	private static int shade(int rgb, float brightness)
	{
		int r = (int)(((rgb >> 16)&0xFF)*brightness);
		int g = (int)(((rgb >> 8)&0xFF)*brightness);
		int b = (int)((rgb&0xFF)*brightness);
		return 0xFF000000|(r << 16)|(g << 8)|b;
	}

	private void renderEnergyBar(GuiGraphics graphics)
	{
		fillBar(graphics, ENERGY_X, (float)menu.getEnergyStored()/menu.getEnergyCapacity(), COLOUR_ENERGY);
	}

	private void renderProgressBar(GuiGraphics graphics)
	{
		int colour = menu.isIdle()?COLOUR_PROGRESS_IDLE: COLOUR_PROGRESS;
		fillBar(graphics, PROGRESS_X, smoothProgress()/100f, colour);
	}

	private float smoothProgress()
	{
		float target = menu.getProgress();
		long now = System.nanoTime();
		float seconds = lastFrameNanos==0L?0f: Math.min(0.25f, (now-lastFrameNanos)/1_000_000_000f);
		lastFrameNanos = now;

		if(displayedProgress < 0f) displayedProgress = target;

		float delta = target-displayedProgress;
		if(delta < -20f) displayedProgress = target;
		else displayedProgress += delta*Math.min(1f, seconds*6f);
		return displayedProgress;
	}

	private void fillBar(GuiGraphics graphics, int x, float fraction, int colour)
	{
		int filled = Math.round(Math.max(0f, Math.min(1f, fraction))*BAR_H);
		if(filled <= 0) return;

		int left = leftPos+x;
		int bottom = topPos+BAR_Y+BAR_H;
		graphics.fill(left, bottom-filled, left+BAR_W, bottom, colour);
	}

	@Override
	public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
	{
		renderBackground(graphics);
		super.render(graphics, mouseX, mouseY, partialTick);
		renderTooltips(graphics, mouseX, mouseY);
		renderTooltip(graphics, mouseX, mouseY);
	}

	private void renderTooltips(GuiGraphics graphics, int mouseX, int mouseY)
	{
		if(isOver(mouseX, mouseY, ENERGY_X, BAR_Y, BAR_W, BAR_H))
		{
			graphics.renderTooltip(font, Component.translatable("gui.immersivegeology.metal_detector.energy",
					menu.getEnergyStored(), menu.getEnergyCapacity()), mouseX, mouseY);
			return;
		}
		if(isOver(mouseX, mouseY, PROGRESS_X, BAR_Y, BAR_W, BAR_H))
		{
			Component line;
			if(!menu.isIdle())
				line = Component.translatable("gui.immersivegeology.metal_detector.surveying", menu.getProgress());
			else if(menu.isSweeping())
				line = Component.translatable("gui.immersivegeology.metal_detector.rechecking");
			else
				line = Component.translatable("gui.immersivegeology.metal_detector.standby",
						menu.getSecondsToNextCheck());
			graphics.renderTooltip(font, line, mouseX, mouseY);
			return;
		}

		IGMetalDetectorEntity detector = menu.getDetector();
		if(detector==null) return;

		int size = IGMetalDetectorEntity.RADAR_SIZE;
		int cellX = (mouseX-leftPos-RADAR_X)/CELL_PITCH;
		int cellZ = (mouseY-topPos-RADAR_Y)/CELL_PITCH;
		if(cellX < 0||cellX >= size||cellZ < 0||cellZ >= size) return;

		MaterialInterface<?> material = detector.getRadarMaterial(cellX, cellZ);
		if(material==null) return;

		DepositGrade grade = detector.getGrade(cellX, cellZ);
		List<Component> lines = new ArrayList<>(3);
		lines.add(material.getTranslation());
		lines.add(Component.translatable("gui.immersivegeology.metal_detector.grade."
				+grade.name().toLowerCase(java.util.Locale.ROOT)).withStyle(style -> style.withColor(grade.colour)));
		lines.add(Component.translatable("gui.immersivegeology.metal_detector.offset",
				(cellX-IGMetalDetectorEntity.RADAR_RADIUS)*16, (cellZ-IGMetalDetectorEntity.RADAR_RADIUS)*16));
		graphics.renderComponentTooltip(font, lines, mouseX, mouseY);
	}

	private boolean isOver(int mouseX, int mouseY, int x, int y, int width, int height)
	{
		int localX = mouseX-leftPos;
		int localY = mouseY-topPos;
		return localX >= x&&localX < x+width&&localY >= y&&localY < y+height;
	}

	@Override
	protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY)
	{
		// maybe later
	}
}
