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
import blusunrize.lib.manual.gui.GuiButtonManual;
import blusunrize.lib.manual.gui.ManualScreen;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGGraphLayoutManager;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeChain;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeNode;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

public class IGRecipeOverview extends SpecialManualElements
{
	private final GeologyMaterial material;
	private IGRecipeNode selectedNode = null;
	private final IGRecipeChain chain_to_display;
	private Button closeButton;
	private final Map<IGRecipeNode, Button> nodeButtons = new LinkedHashMap<>();

	private static final ResourceLocation TEXTURE_CLOSE = IGLib.makeTextureLocation("manual/close");

	private static final int PANEL_SIZE = 101;
	private static final int CLOSE_SIZE = 16;
	private static final int CLOSE_MARGIN = 1;
	private static final int NODE_SIZE = 16;
	private static final int MAX_STEP = 32;
	private static final int MIN_STEP = 18;
	private static final int MAX_HEIGHT = 140;
	private static final int SCROLLBAR_WIDTH = 3;

	private boolean layoutDone = false;
	private boolean scrollable = false;
	private int gridMinX, gridMinY, stepX = MAX_STEP, stepY = MAX_STEP;
	private int offsetX = 0, contentHeight = NODE_SIZE, visibleHeight = NODE_SIZE;
	private int scrollY = 0;

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
		ensureLayout();
		return visibleHeight;
	}

	private void ensureLayout()
	{
		if(layoutDone) return;
		layoutDone = true;
		if(chain_to_display==null||chain_to_display.getRootNodes().isEmpty()) return;

		manager.layoutChain(chain_to_display);

		Set<IGRecipeNode> seen = new HashSet<>();
		Deque<IGRecipeNode> queue = new ArrayDeque<>(chain_to_display.getRootNodes());
		int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
		gridMinX = Integer.MAX_VALUE;
		gridMinY = Integer.MAX_VALUE;
		while(!queue.isEmpty())
		{
			IGRecipeNode node = queue.poll();
			if(!seen.add(node)) continue;
			gridMinX = Math.min(gridMinX, node.getX());
			gridMinY = Math.min(gridMinY, node.getY());
			maxX = Math.max(maxX, node.getX());
			maxY = Math.max(maxY, node.getY());
			queue.addAll(node.getChildren());
		}
		if(seen.isEmpty()) return;

		int columns = maxX-gridMinX+1;
		int rows = maxY-gridMinY+1;

		stepX = columns > 1?Math.min(MAX_STEP, (manual.pageWidth-NODE_SIZE)/(columns-1)): MAX_STEP;
		stepY = rows > 1?Math.min(MAX_STEP, Math.max(MIN_STEP, (MAX_HEIGHT-NODE_SIZE)/(rows-1))): MAX_STEP;

		contentHeight = (rows-1)*stepY+NODE_SIZE;
		visibleHeight = Math.min(contentHeight, MAX_HEIGHT);
		scrollable = contentHeight > visibleHeight;

		int contentWidth = (columns-1)*stepX+NODE_SIZE;
		int usable = manual.pageWidth-(scrollable?SCROLLBAR_WIDTH+2: 0);
		offsetX = Math.max(0, (usable-contentWidth)/2);
	}

	private int nodePixelX(IGRecipeNode node)
	{
		return offsetX+(node.getX()-gridMinX)*stepX;
	}

	private int nodePixelY(IGRecipeNode node)
	{
		return (node.getY()-gridMinY)*stepY-scrollY;
	}

	@Override
	public void mouseDragged(int x, int y, double clickX, double clickY, double mx, double my, double lastX, double lastY, int mouseButton)
	{
		if(!scrollable||selectedNode!=null) return;
		int delta = (int)Math.round(lastY-my);
		if(delta==0) return;
		scrollY = Math.max(0, Math.min(contentHeight-visibleHeight, scrollY+delta));
	}

	private void drawScrollbar(GuiGraphics graphics)
	{
		int trackX = manual.pageWidth-SCROLLBAR_WIDTH;
		graphics.fill(trackX, 0, trackX+SCROLLBAR_WIDTH, visibleHeight, 0x33000000);
		int thumb = Math.max(8, visibleHeight*visibleHeight/contentHeight);
		int travel = visibleHeight-thumb;
		int thumbY = travel<=0?0: (int)((long)scrollY*travel/(contentHeight-visibleHeight));
		graphics.fill(trackX, thumbY, trackX+SCROLLBAR_WIDTH, thumbY+thumb, 0x88cb7f32);
	}

	private int panelX(ManualScreen screen)
	{
		return (screen.getManual().pageWidth/2)-50;
	}

	@Override
	public void onOpened(ManualScreen gui, int x, int y, List<Button> pageButtons)
	{
		super.onOpened(gui, x, y, pageButtons);
		ensureLayout();
		selectedNode = null;
		nodeButtons.clear();

		closeButton = new IconButton(x+manual.pageWidth-CLOSE_SIZE-CLOSE_MARGIN, y+CLOSE_MARGIN, CLOSE_SIZE,
				TEXTURE_CLOSE, btn -> selectedNode = null);
		closeButton.visible = false;
		closeButton.active = false;
		pageButtons.add(closeButton);

		if(chain_to_display==null) return;
		Set<IGRecipeNode> seen = new HashSet<>();
		Deque<IGRecipeNode> queue = new ArrayDeque<>(chain_to_display.getRootNodes());
		while(!queue.isEmpty())
		{
			IGRecipeNode node = queue.poll();
			if(!seen.add(node)) continue;
			Button button = new GuiButtonManual(gui, x+nodePixelX(node), y+nodePixelY(node), NODE_SIZE, NODE_SIZE,
					Component.empty(), btn -> selectedNode = node)
					.setColour(0x00000000, 0x33cb7f32);
			nodeButtons.put(node, button);
			pageButtons.add(button);
			queue.addAll(node.getChildren());
		}
	}

	private static class IconButton extends Button
	{
		private static final int TEXTURE_SIZE = 16;

		private final ResourceLocation texture;

		IconButton(int x, int y, int size, ResourceLocation texture, OnPress handler)
		{
			super(x, y, size, size, Component.empty(), handler, DEFAULT_NARRATION);
			this.texture = texture;
		}

		@Override
		public void renderWidget(GuiGraphics graphics, int mx, int my, float partial)
		{
			isHovered = mx >= getX()&&mx < getX()+width&&my >= getY()&&my < getY()+height;
			RenderSystem.enableBlend();
			if(isHovered) graphics.fill(getX(), getY(), getX()+width, getY()+height, 0x33cb7f32);
			graphics.blit(texture, getX(), getY(), 0, 0, width, height, TEXTURE_SIZE, TEXTURE_SIZE);
		}
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
		boolean detailOpen = selectedNode!=null;
		if(closeButton!=null)
		{
			closeButton.visible = detailOpen;
			closeButton.active = detailOpen;
		}
		for(Map.Entry<IGRecipeNode, Button> entry : nodeButtons.entrySet())
		{
			Button button = entry.getValue();
			button.visible = !detailOpen;
			button.active = !detailOpen;
			if(scrollable) button.setY(y+nodePixelY(entry.getKey()));
		}

		if(chain_to_display != null) {
			rendered_nodes.clear();
			IGRecipeChain chain = chain_to_display;
			List<IGRecipeNode> roots = chain.getRootNodes();

			drawHackySubtext(graphics, screen);

			// Check if the chain has a root node.
			if(!roots.isEmpty()) {
				ensureLayout();

				int baseX = x;
				int baseY = y;

				// Render the entire chain tree.
				graphics.pose().pushPose();
				if(selectedNode == null)
				{
					boolean clipped = scrollable;
					if(clipped)
					{
						Matrix4f pose = graphics.pose().last().pose();
						int originX = (int)pose.m30();
						int originY = (int)pose.m31();
						float scale = pose.m00();
						graphics.enableScissor(originX, originY,
								originX+(int)(manual.pageWidth*scale), originY+(int)(visibleHeight*scale));
					}

					for(IGRecipeNode root : roots)
					{
						renderChain(graphics, screen, root, baseX, baseY, mx, my, 0x66666666, rendered_nodes);
					}

					for(IGRecipeNode root : roots)
					{
						root.resetRender();
					}

					if(clipped)
					{
						graphics.disableScissor();
						drawScrollbar(graphics);
					}
				}
				graphics.pose().popPose();

				if(selectedNode != null)
				{
					IGRecipeMethod method = selectedNode.getMethod();

					graphics.pose().pushPose();
					graphics.pose().translate(0,0,999);

					graphics.pose().pushPose();
					{
						graphics.pose().translate( ((float)screen.getManual().pageWidth/ 2) - 16,(screen.getManual().pageHeight) - ((float)(screen.getManual().pageHeight)/ 4),0);
						
						graphics.pose().scale(2,2,1);
						graphics.renderItem(method.getIconStack(), x, y, mx, my);
					}
					graphics.pose().popPose();

					int renderX = panelX(screen);
					int renderY = 0;

					ManualUtils.drawTexturedRect(graphics, method.getMethod().getGuiLocation(), renderX, renderY, PANEL_SIZE, PANEL_SIZE, 0,1,0,1);
					method.render(graphics,screen,renderX,renderY, mx, my);

					graphics.pose().popPose();
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

		int nodeX = baseX + nodePixelX(node);
		int nodeY = baseY + nodePixelY(node);

		// Retrieve the method wrapped by this node.
		IGRecipeMethod method = node.getMethod();

		method.renderMB(graphics, method.getIconStack(), nodeX, nodeY, mx, my);

		for (IGRecipeNode child : node.getChildren()) {

			drawConnectionLine(graphics, baseX, baseY, node, child, color);

			if(child.shouldRender) {
				renderChain(graphics, screen, child, baseX, baseY, mx, my, color, visited);
				child.shouldRender = false;
			}
		}
	}

	private void drawConnectionLine(GuiGraphics graphics, int baseX, int baseY, IGRecipeNode from, IGRecipeNode to, int color) {
		int x1 = baseX + nodePixelX(from) + NODE_SIZE/2;
		int y1 = baseY + nodePixelY(from) + NODE_SIZE/2;
		int x2 = baseX + nodePixelX(to) + NODE_SIZE/2;
		int y2 = baseY + nodePixelY(to) + NODE_SIZE/2;

		double deltaX = x2 - x1;
		double deltaY = y2 - y1;
		float angle = (float) Math.atan2(deltaY, deltaX);
		int length = (int)Math.round(Math.sqrt(deltaX * deltaX + deltaY * deltaY));

		drawDirectLine(graphics, x1, y1, angle, length, color);
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
