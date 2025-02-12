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
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod.RecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeChain;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeNode;
import com.igteam.immersivegeology.core.material.helper.material.recipe.methods.IGCrushingMethod;
import com.igteam.immersivegeology.core.registration.IGContent;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class IGRecipeOverview extends SpecialManualElements
{
	private final GeologyMaterial material;
	public IGRecipeOverview(ManualInstance manual, GeologyMaterial material)
	{
		super(manual);
		this.material = material;
	}

	@Override
	public int getPixelsTaken()
	{
		return 100;
	}
	private boolean setChainPositions = false;
	@Override
	public void render(GuiGraphics graphics, ManualScreen screen, int x, int y, int mx, int my)
	{
		// Get all the recipe chains for this material.
		Set<IGRecipeChain> chains = material.getRecipeChains();

		// For now, simply grab any single chain.
		Optional<IGRecipeChain> optional = chains.stream().findAny();
		if(optional.isPresent()) {
			IGRecipeChain chain = optional.get();
			IGRecipeNode root = chain.getRoot();

			if(!setChainPositions)
			{
				chain.layoutRecipeChain(root, x, y, 32, 48);
				setChainPositions = true;
			}

			// Check if the chain has a root node.
			if(root != null) {
				// Optionally: run your layout algorithm here to compute positions for each node.
				// For now, assume the nodes already have valid x, y positions.
				int baseX = x + (screen.getManual().pageWidth / 2) - 24;
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
						graphics.pose().translate( ((float)screen.getManual().pageWidth/ 2) - 16,((float)screen.getManual().pageHeight/ 2) - 24,0);
						
						graphics.pose().scale(2,2,1);
						graphics.renderItem(method.getIconStack(), x, y, mx, my);
					}
					graphics.pose().popPose();

					graphics.fill(0, 8, screen.getManual().pageWidth, screen.getManual().pageHeight-24, 0xffffffff);
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

	private IGRecipeNode selectedNode = null;

	private void renderChain(GuiGraphics graphics, ManualScreen screen, IGRecipeNode node, int baseX, int baseY, int mx, int my, int color) {
		// Compute the on-screen position for this node.
		int nodeX = baseX + node.getX();
		int nodeY = baseY + node.getY();

		// Retrieve the method wrapped by this node.
		IGRecipeMethod method = node.getMethod();

		// Render the main body of the recipe method.
		// (Assumes renderMB draws the node's icon and label, etc.)
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
			ManualUtils.drawTexturedRect(graphics, IGLib.makeTextureLocation("jei/downwards_arrow"), x1 - 4, y1 + 12, 9,10, 10/32f, 0, 20/32f, 10/32f);
		} else if (y1 == y2) {
			// Horizontal line
			graphics.hLine(x1, x2, y1, color);

			int midX = (x1 + x2) / 2;
			graphics.pose().pushPose();
			graphics.pose().translate(0,-0.5f,0);

			ManualUtils.drawTexturedRect(graphics, IGLib.makeTextureLocation("jei/downwards_arrow"), midX-2, y1-4, 9,10, 0, 9/32f,0, 10/32f);
			graphics.pose().popPose();
		} else {
			// L-shaped connector
			int midX = (x1 + x2) / 2;

			graphics.hLine(x1, midX, y1, color); // Horizontal from (x1, y1) to (midX, y1)
			graphics.vLine(midX, y1, y2, color); // Vertical from (midX, y1) to (midX, y2)
			graphics.hLine(midX, x2, y2, color); // Horizontal from (midX, y2) to (x2, y2)

			ManualUtils.drawTexturedRect(graphics, IGLib.makeTextureLocation("jei/downwards_arrow"), midX - 4, y1 + 12, 9,10, 10/32f, 0, 20/32f, 10/32f);

		}
	}

	@Override
	public boolean listForSearch(String s)
	{
		return false;
	}
}
