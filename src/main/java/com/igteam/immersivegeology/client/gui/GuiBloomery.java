package com.igteam.immersivegeology.client.gui;

import com.igteam.immersivegeology.common.gui.ContainerBloomery;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBloomery extends GuiContainer
{
	private static final ResourceLocation TEXTURE =
			new ResourceLocation(IGLib.MODID, "textures/gui/multiblocks/bloomery.png");

	private final ContainerBloomery container;

	public GuiBloomery(InventoryPlayer inventoryPlayer, ContainerBloomery container)
	{
		super(container);
		this.container = container;
		this.xSize = 176;
		this.ySize = 166;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String title = I18n.format("tile."+IGLib.MODID+".bloomery.name");
		fontRenderer.drawString(title, 9, 6, 0x404040);
		fontRenderer.drawString(I18n.format("container.inventory"), 9, ySize-95, 0x404040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1f, 1f, 1f, 1f);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		if(container.clientBurnMax > 0)
		{
			int h = (int)(12*(container.clientBurn/(float)container.clientBurnMax));
			drawTexturedModalRect(guiLeft+54, guiTop+37+12-h, 179, 1+12-h, 9, h);
		}

		if(container.clientProcessMax > 0)
		{
			int w = (int)(22*(1-container.clientProcess/(float)container.clientProcessMax));
			drawTexturedModalRect(guiLeft+72, guiTop+13, 177, 14, w, 16);
		}
	}
}
