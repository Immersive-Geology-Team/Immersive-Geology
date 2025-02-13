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
import blusunrize.lib.manual.SpecialManualElements;
import blusunrize.lib.manual.gui.ManualScreen;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeChain;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class IGRecipeOverview extends SpecialManualElements
{
	private final GeologyMaterial material;
	private IGRecipeNode selectedNode = null;
	private boolean setChainPositions = false;
	private final IGRecipeChain chain_to_display;

	private static final ResourceLocation TEXTURE_ARROWS = IGLib.makeTextureLocation("manual/arrows");

	public IGRecipeOverview(ManualInstance manual, GeologyMaterial material, IGRecipeChain chain_to_display)
	{
		super(manual);
		this.material = material;
		this.chain_to_display = chain_to_display;
	}

	public IGRecipeOverview(ManualInstance manual, GeologyMaterial material, int priority)
	{
		super(manual);
		this.material = material;
		this.chain_to_display = material.getRecipeChains().stream().filter(c -> c.getPriority() == priority).findFirst().get();
	}

	@Override
	public int getPixelsTaken()
	{
		return 100;
	}

	private void drawCenteredStringScaled(GuiGraphics graphics, Font fr, String s, int x, int y, int colour, boolean shadow) {
		int xx = (int)Math.floor((double)x - (double)fr.width(s) / 2.0);
		double var10000 = (double)y;
		Objects.requireNonNull(fr);
		int yy = (int)Math.floor(var10000 - 9.0 / 2.0);
		graphics.drawString(fr, s, xx, yy, colour, shadow);
	}

	private void drawHackySubtext(GuiGraphics graphics, ManualScreen screen)
	{
		String subtext = I18n.get("manual.immersivegeology." + chain_to_display.getName() + ".subtext");
		drawCenteredStringScaled(graphics, this.manual.fontRenderer(), this.manual.formatEntrySubtext(subtext), screen.getManual().pageWidth / 2,  -6, this.manual.getSubTitleColour(), true);
	}

	@Override
	public void render(GuiGraphics graphics, ManualScreen screen, int x, int y, int mx, int my)
	{
		if(chain_to_display != null) {
			IGRecipeChain chain = chain_to_display;
			IGRecipeNode root = chain.getRoot();

			drawHackySubtext(graphics, screen);

			// Check if the chain has a root node.
			if(root != null) {
				if(!setChainPositions)
				{
					chain.layoutRecipeChain(root, x, y, 32, 48);
					setChainPositions = true;
				}

				// For now, assume the nodes already have valid x, y positions. The above method should have set them correctly.
				int baseX = x + (screen.getManual().pageWidth / 2) - 10;
				int baseY = y + 16;
				// Render the entire chain tree.
				graphics.pose().pushPose();
				if(selectedNode == null) renderChain(graphics, screen, root, baseX, baseY, mx, my, 0xff121212);
				graphics.pose().popPose();

				if(selectedNode != null)
				{
					IGRecipeMethod method = selectedNode.getMethod();

					graphics.pose().pushPose();
					graphics.pose().translate(0,0,999);
					graphics.renderItem( new ItemStack(Items.BARRIER), -8,0);

					graphics.pose().pushPose();
					{
						graphics.pose().translate( ((float)screen.getManual().pageWidth/ 2) - 16,(screen.getManual().pageHeight) - ((float)(screen.getManual().pageHeight)/ 4),0);
						
						graphics.pose().scale(2,2,1);
						graphics.renderItem(method.getIconStack(), x, y, mx, my);
					}
					graphics.pose().popPose();

					int renderX = (screen.getManual().pageWidth / 2) - 50;
					int renderY = 0;

					ManualUtils.drawTexturedRect(graphics, method.getMethod().getGuiLocation(), renderX, renderY,101,101, 0,1,0,1);
					method.render(graphics,screen,renderX,renderY, mx, my);

					graphics.pose().popPose();

					if(mx > -8 && mx < 16 && my > -8 && my < 16)
					{
						if(GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_1)==GLFW.GLFW_PRESS)
						{
							selectedNode = null;
						}
						graphics.renderTooltip(manual.fontRenderer(), Component.literal("Close"), mx, my);
					}

				}
			}
		}
	}


	private void renderChain(GuiGraphics graphics, ManualScreen screen, IGRecipeNode node, int baseX, int baseY, int mx, int my, int color) {
		// Compute the on-screen position for this node.
		int nodeX = baseX + node.getX();
		int nodeY = baseY + node.getY();

		// Retrieve the method wrapped by this node.
		IGRecipeMethod method = node.getMethod();

		// Render the main body of the recipe method.
		if(mx > nodeX-24 && mx < (nodeX + (24 + 16)) && my > nodeY-8 && my < (nodeY + 24))
		{
			if(GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS)
			{
				selectedNode = node;
			}
		}

		method.renderMB(graphics, method.getIconStack(), nodeX, nodeY, mx, my);


		for (IGRecipeNode child : node.getChildren()) {
			int childX = baseX + child.getX();
			int childY = baseY + child.getY();

			// For example, assume the parent's output is at (nodeX + 70, nodeY + 15)
			// and the child's input is at (childX, childY + 15). Adjust offsets as needed.
			drawDirectLine(graphics, nodeX + 8, nodeY + 8, childX + 8, childY + 8, color);

			// Recursively render the child node.
			renderChain(graphics, screen, child, baseX, baseY, mx, my, color);
		}
	}

	private void drawDirectLine(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color) {
		if (x1 == x2) {
			// Vertical line
			graphics.vLine(x1, y1, y2, color);
			ManualUtils.drawTexturedRect(graphics, TEXTURE_ARROWS, x1 - 4, y1 + 12, 9,10, 10/32f, 0, 20/32f, 10/32f);
		} else if (y1 == y2) {
			// Horizontal line
			graphics.hLine(x1, x2, y1, color);

			int midX = (x1 + x2) / 2;
			graphics.pose().pushPose();
			graphics.pose().translate(0,-0.5f,0);

			ManualUtils.drawTexturedRect(graphics, TEXTURE_ARROWS, midX-2, y1-4, 9,10, 0, 9/32f,0, 10/32f);
			graphics.pose().popPose();
		} else {
			// L-shaped connector
			int midX = (x1 + x2) / 2;

			graphics.hLine(x1, midX, y1, color); // Horizontal from (x1, y1) to (midX, y1)
			graphics.vLine(midX, y1, y2, color); // Vertical from (midX, y1) to (midX, y2)
			graphics.hLine(midX, x2, y2, color); // Horizontal from (midX, y2) to (x2, y2)

			ManualUtils.drawTexturedRect(graphics, TEXTURE_ARROWS, midX - 4, y1 + 12, 9,10, 10/32f, 0, 20/32f, 10/32f);
		}
	}

	@Override
	public boolean listForSearch(String s)
	{
		return false;
	}
}
