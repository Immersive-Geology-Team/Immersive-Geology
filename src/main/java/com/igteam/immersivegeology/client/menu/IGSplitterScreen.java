/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.menu;

import com.igteam.immersivegeology.common.block.belt.helper.SplitterPriority;
import com.igteam.immersivegeology.common.menu.IGSplitterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class IGSplitterScreen extends AbstractContainerScreen<IGSplitterMenu>
{
	private static final int PANEL = 0xFFC6C6C6;
	private static final int LIGHT = 0xFFFFFFFF;
	private static final int SHADOW = 0xFF555555;
	private static final int OUTLINE = 0xFF000000;
	private static final int SLOT_DARK = 0xFF373737;
	private static final int SLOT_FILL = 0xFF8B8B8B;
	private static final int LABEL = 0x404040;

	private static final int BUTTON_X = 62;
	private static final int BUTTON_WIDTH = 28;
	private static final int BUTTON_GAP = 2;
	private static final int BUTTON_HEIGHT = 16;
	private static final int OUTPUT_ROW = 20;
	private static final int INPUT_ROW = 45;

	private final List<Button> inputButtons = new ArrayList<>();
	private final List<Button> outputButtons = new ArrayList<>();

	public IGSplitterScreen(IGSplitterMenu menu, Inventory inventory, Component title)
	{
		super(menu, inventory, title);
		imageWidth = 176;
		imageHeight = 166;
		inventoryLabelY = IGSplitterMenu.INVENTORY_Y-11;
	}

	@Override
	protected void init()
	{
		super.init();
		inputButtons.clear();
		outputButtons.clear();
		for(SplitterPriority priority : SplitterPriority.values())
		{
			int x = leftPos+BUTTON_X+priority.ordinal()*(BUTTON_WIDTH+BUTTON_GAP);
			inputButtons.add(addRenderableWidget(priorityButton(priority, x, topPos+INPUT_ROW, IGSplitterMenu.BUTTON_INPUT_BASE)));
			outputButtons.add(addRenderableWidget(priorityButton(priority, x, topPos+OUTPUT_ROW, IGSplitterMenu.BUTTON_OUTPUT_BASE)));
		}
	}

	private Button priorityButton(SplitterPriority priority, int x, int y, int base)
	{
		return Button.builder(Component.translatable(priority.translationKey()), button -> {
			if(minecraft!=null&&minecraft.gameMode!=null)
				minecraft.gameMode.handleInventoryButtonClick(menu.containerId, base+priority.ordinal());
		}).bounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT).build();
	}

	@Override
	protected void containerTick()
	{
		super.containerTick();
		for(SplitterPriority priority : SplitterPriority.values())
		{
			inputButtons.get(priority.ordinal()).active = menu.inputPriority()!=priority;
			outputButtons.get(priority.ordinal()).active = menu.outputPriority()!=priority;
		}
	}

	@Override
	public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
	{
		renderBackground(graphics);
		super.render(graphics, mouseX, mouseY, partialTick);
		renderTooltip(graphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
	{
		int x = leftPos, y = topPos, w = imageWidth, h = imageHeight;
		graphics.fill(x+2, y+2, x+w-2, y+h-2, PANEL);
		graphics.fill(x+2, y, x+w-3, y+1, OUTLINE);
		graphics.fill(x+3, y+h-1, x+w-2, y+h, OUTLINE);
		graphics.fill(x, y+2, x+1, y+h-3, OUTLINE);
		graphics.fill(x+w-1, y+3, x+w, y+h-2, OUTLINE);
		graphics.fill(x+1, y+1, x+2, y+2, OUTLINE);
		graphics.fill(x+w-3, y+1, x+w-2, y+2, OUTLINE);
		graphics.fill(x+w-2, y+2, x+w-1, y+3, OUTLINE);
		graphics.fill(x+1, y+h-3, x+2, y+h-2, OUTLINE);
		graphics.fill(x+2, y+h-2, x+3, y+h-1, OUTLINE);
		graphics.fill(x+w-2, y+h-2, x+w-1, y+h-1, OUTLINE);
		graphics.fill(x+2, y+1, x+w-3, y+3, LIGHT);
		graphics.fill(x+1, y+2, x+3, y+h-3, LIGHT);
		graphics.fill(x+3, y+3, x+4, y+4, LIGHT);
		graphics.fill(x+3, y+h-3, x+w-2, y+h-1, SHADOW);
		graphics.fill(x+w-3, y+3, x+w-1, y+h-2, SHADOW);
		graphics.fill(x+w-4, y+h-4, x+w-3, y+h-3, SHADOW);
		slot(graphics, x+IGSplitterMenu.FILTER_X, y+IGSplitterMenu.FILTER_Y);
		for(int row = 0; row < 3; row++)
			for(int column = 0; column < 9; column++)
				slot(graphics, x+IGSplitterMenu.INVENTORY_X+column*18, y+IGSplitterMenu.INVENTORY_Y+row*18);
		for(int column = 0; column < 9; column++)
			slot(graphics, x+IGSplitterMenu.INVENTORY_X+column*18, y+IGSplitterMenu.HOTBAR_Y);
	}

	private static void slot(GuiGraphics graphics, int x, int y)
	{
		graphics.fill(x-1, y-1, x+17, y+17, SLOT_DARK);
		graphics.fill(x, y, x+17, y+17, LIGHT);
		graphics.fill(x, y, x+16, y+16, SLOT_FILL);
	}

	@Override
	protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY)
	{
		super.renderLabels(graphics, mouseX, mouseY);
		graphics.drawString(font, Component.translatable("gui.immersivegeology.splitter.input"), 8, INPUT_ROW+4, LABEL, false);
		String output = menu.hasFilter()?"gui.immersivegeology.splitter.filter_output": "gui.immersivegeology.splitter.output";
		graphics.drawString(font, Component.translatable(output), 8, OUTPUT_ROW+4, LABEL, false);
	}
}
