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
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGGraphLayoutManager;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeChain;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeNode;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Quaternionf;
import org.joml.Vector3f;
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

	private final HashSet<IGRecipeNode> rendered_nodes = new HashSet<>();

	IGGraphLayoutManager manager = new IGGraphLayoutManager();

	@Override
	public void render(GuiGraphics graphics, ManualScreen screen, int x, int y, int mx, int my)
	{
		if(chain_to_display != null) {
			rendered_nodes.clear();
			IGRecipeChain chain = chain_to_display;
			List<IGRecipeNode> roots = chain.getRootNodes();

			drawHackySubtext(graphics, screen);

			// Check if the chain has a root node.
			if(!roots.isEmpty()) {
				if(!setChainPositions)
				{
					manager.layoutChain(chain);
					setChainPositions = true;
				}

				int baseX = x;
				int baseY = y;

				// Render the entire chain tree.
				graphics.pose().pushPose();
				if(selectedNode == null)
				{

					for(IGRecipeNode root : roots)
					{
						renderChain(graphics, screen, root, baseX, baseY, mx, my, 0x66666666, rendered_nodes);
					}

					for(IGRecipeNode root : roots)
					{
						root.resetRender();
					}
				}
				graphics.pose().popPose();

				if(selectedNode != null)
				{
					IGRecipeMethod method = selectedNode.getMethod();

					graphics.pose().pushPose();
					graphics.pose().translate(0,0,999);
					graphics.renderItem( new ItemStack(Items.BARRIER), -32,0);

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

					if(mx > -32 && mx < -16 && my > -8 && my < 16)
					{
						if(GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_1)==GLFW.GLFW_PRESS)
						{
							selectedNode = null;
						}
					}
				}
			}
		}
	}


	private void renderChain(GuiGraphics graphics, ManualScreen screen, IGRecipeNode node, int baseX, int baseY, int mx, int my, int color, Set<IGRecipeNode> visited) {
		// Compute the on-screen position for this node.
		if(!visited.add(node))
		{
			return;
		}

		int nodeX = baseX + (node.getX() * 32);
		int nodeY = baseY + (node.getY() * 32);

		// Retrieve the method wrapped by this node.
		IGRecipeMethod method = node.getMethod();

		// Render the main body of the recipe method.
		if(mx > nodeX&&(nodeX+16) > mx && my > nodeY&&(nodeY+16) > my)
		{
			//graphics.renderTooltip(screen.getMinecraft().font, Component.literal("P: " + p), mx, my);
			if (GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS) {
				selectedNode = node;
			}
		}

		method.renderMB(graphics, method.getIconStack(), nodeX, nodeY, mx, my);

		for (IGRecipeNode child : node.getChildren()) {

			drawConnectionLine(graphics, baseX, baseY, node, child, color);

			if(child.shouldRender) {
				renderChain(graphics, screen, child, baseX, baseY, mx, my, color, visited);
				child.shouldRender = false;
			}
		}
	}

	private final int NODE_SIZE = 16;
	private void drawConnectionLine(GuiGraphics graphics, int baseX, int baseY, IGRecipeNode from, IGRecipeNode to, int color) {
		// Get center points of nodes
		int x1 = baseX + (from.getX() + (NODE_SIZE / 2) * 32);
		int y1 = baseY + (from.getY() + (NODE_SIZE / 2) * 32);
		int x2 = baseX + (to.getX() + (NODE_SIZE / 2) * 32);
		int y2 = baseY + (to.getY() + (NODE_SIZE / 2) * 32);

		// Calculate angle between points
		double deltaX = x2 - x1;
		double deltaY = y2 - y1;
		float angle = (float) Math.atan2(deltaY, deltaX);

		// Calculate direct distance between points
		double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY) * 32;
		drawDirectLine(graphics, baseX + (from.getX() * 32) + (NODE_SIZE / 2) , baseY + (from.getY() * 32)+ (NODE_SIZE / 2) , angle, (int)distance, color);
	}


	private void drawDirectLine(GuiGraphics graphics, int x1, int y1, float angle, int length, int color) {
		PoseStack pose = graphics.pose();
		pose.pushPose();
		{
			pose.translate(x1, y1, 0);
			pose.pushPose();
			{
				pose.mulPose(new Quaternionf().rotateAxis(angle, new Vector3f(0, 0, 1)));
				graphics.fill(0, -1, length, 1, color);

			}
			pose.popPose();
		}
		pose.popPose();
	}

	@Override
	public boolean listForSearch(String s)
	{
		return false;
	}
}
