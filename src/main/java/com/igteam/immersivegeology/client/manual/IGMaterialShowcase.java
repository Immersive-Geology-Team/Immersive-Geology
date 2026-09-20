/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.manual;

import blusunrize.lib.manual.ManualInstance;
import blusunrize.lib.manual.ManualUtils;
import blusunrize.lib.manual.PositionedItemStack;
import blusunrize.lib.manual.SpecialManualElements;
import blusunrize.lib.manual.gui.ManualScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Locale;

public class IGMaterialShowcase extends SpecialManualElements
{
	private static final int SLOT_SIZE = 16;
	private static final int SCALE = 2;
	private static final int MARGIN = 4;

	private final List<ItemStack> variants;
	private final PositionedItemStack cycle;

	public IGMaterialShowcase(ManualInstance manual, List<ItemStack> variants)
	{
		super(manual);
		this.variants = List.copyOf(variants);
		this.cycle = new PositionedItemStack(this.variants, 0, 0);
	}

	@Override
	public int getPixelsTaken()
	{
		return variants.isEmpty()?0: SLOT_SIZE*SCALE+MARGIN*2;
	}

	@Override
	public void render(GuiGraphics graphics, ManualScreen gui, int x, int y, int mx, int my)
	{
		highlighted = ItemStack.EMPTY;
		if(variants.isEmpty()) return;

		ItemStack stack = cycle.getStackAtCurrentTime();
		int size = SLOT_SIZE*SCALE;
		int left = x+(manual.pageWidth-size)/2;
		int top = y+MARGIN;

		graphics.pose().pushPose();
		graphics.pose().scale(SCALE, SCALE, SCALE);
		ManualUtils.renderItemStack(graphics, stack, left/SCALE, top/SCALE, false);
		graphics.pose().popPose();

		if(mx >= left&&mx < left+size&&my >= top&&my < top+size) highlighted = stack;

		RenderSystem.enableBlend();
		renderHighlightedTooltip(graphics, mx, my);
	}

	@Override
	public boolean listForSearch(String searchTag)
	{
		for(ItemStack stack : variants)
			if(stack.getHoverName().getString().toLowerCase(Locale.ENGLISH).contains(searchTag)) return true;
		return false;
	}
}
