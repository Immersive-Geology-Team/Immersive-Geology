package com.igteam.immersivegeology.client.gui;

import com.igteam.immersivegeology.common.gui.ContainerRevFurnace;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiRevFurnace extends GuiContainer
{
	private static final ResourceLocation TEXTURE =
			new ResourceLocation(IGLib.MODID, "textures/gui/multiblocks/reverberation_furnace.png");

	private final ContainerRevFurnace container;

	public GuiRevFurnace(InventoryPlayer inventoryPlayer, ContainerRevFurnace container)
	{
		super(container);
		this.container = container;
		this.xSize = 176;
		this.ySize = 166;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRenderer.drawString(I18n.format("tile."+IGLib.MODID+".reverberation_furnace.name"), 9, 6, 0x404040);
		fontRenderer.drawString(I18n.format("container.inventory"), 9, ySize-95, 0x404040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1f, 1f, 1f, 1f);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		drawSide(0, 39, 54);
		drawSide(4, 119, 134);
	}

	private void drawSide(int base, int flameX, int arrowX)
	{
		int process = container.clientValues[base];
		int processMax = container.clientValues[base+1];
		int burn = container.clientValues[base+2];
		int burnMax = container.clientValues[base+3];

		if(burnMax > 0)
		{
			int h = (int)(12*(burn/(float)burnMax));
			drawTexturedModalRect(guiLeft+flameX, guiTop+37+12-h, 179, 1+12-h, 9, h);
		}

		if(processMax > 0)
		{
			int w = (int)(22*(1-process/(float)processMax));
			drawTexturedModalRect(guiLeft+arrowX, guiTop+33, 177, 14, w, 16);
		}
	}
}
