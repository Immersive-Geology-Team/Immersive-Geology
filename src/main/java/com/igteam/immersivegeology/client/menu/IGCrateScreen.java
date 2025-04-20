/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.menu;

import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.common.gui.CrateMenu;
import blusunrize.immersiveengineering.common.network.MessageContainerUpdate;
import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.common.menu.IGCrateMenu;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.network.PacketDistributor;

import java.util.Objects;

public class IGCrateScreen<C extends IGCrateMenu> extends IEContainerScreen<C>
{
	private EditBox nameField;

	public IGCrateScreen(C container, Inventory inventoryPlayer, Component title, ResourceLocation background)
	{
		super(container, inventoryPlayer, title, background);
		this.imageHeight = 186;
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY+2, -557004, true);
		graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY-2, -557004, true);
	}

	public static class StandardIGCrate extends IGCrateScreen<IGCrateMenu>
	{
		public StandardIGCrate(IGCrateMenu container, Inventory inventoryPlayer, Component title)
		{
			super(container, inventoryPlayer, title, resolveBackgroundFromTitle(title));
		}

		// A hacky method to switch the background texture without needing to set up a network packet for it.
		private static ResourceLocation resolveBackgroundFromTitle(Component title)
		{
			try
			{
				String contentString = title.getContents().toString();
				String formatString = contentString.substring(contentString.lastIndexOf("args=[")+6, contentString.lastIndexOf("]}"));
				String sanitized = formatString.replace(" ", "");
				MetalEnum material = MetalEnum.valueOf(sanitized);
				return new ResourceLocation(IGLib.MODID, "textures/gui/block/crate_"+material.getName()+".png");
			}catch(Exception ignored)
			{
				return new ResourceLocation(IGLib.MODID, "textures/gui/block/crate.png");
			}
		}

		@Override
		protected void slotClicked(Slot pSlot, int pSlotId, int pMouseButton, ClickType pType)
		{
			super.slotClicked(pSlot, pSlotId, pMouseButton, pType);
		}
	}
}
