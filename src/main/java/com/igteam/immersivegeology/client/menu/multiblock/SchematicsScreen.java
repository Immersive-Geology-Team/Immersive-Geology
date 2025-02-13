/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.menu.multiblock;

import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.gui.elements.*;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import blusunrize.immersiveengineering.client.utils.GuiHelper;
import com.ibm.icu.impl.coll.BOCSU;
import com.igteam.immersivegeology.common.menu.SchematicOutputArea;
import com.igteam.immersivegeology.common.menu.SchematicsContainerMenu;
import com.igteam.immersivegeology.common.menu.SchematicsContainerMenu.SchematicSlot;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.datafixers.types.Func;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.widget.ScrollPanel;
import org.apache.commons.lang3.mutable.MutableInt;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SchematicsScreen extends IEContainerScreen<SchematicsContainerMenu>
{
	private static final ResourceLocation TEXTURE = IGLib.makeTextureLocation("blockentity/drawing_table");


	private GuiButtonCheckbox mirrorSchematicBtn;
	private GuiButtonSchematic next, back;
	private SchematicScrollPanel selectionPanel;
	private Float scrollAmount = 0f;

	public SchematicsScreen(SchematicsContainerMenu inventorySlotsIn, Inventory inv, Component title)
	{
		super(inventorySlotsIn, inv, title, TEXTURE);
		this.imageHeight = 218;
		this.imageWidth = 230;
	}

	@Override
	protected void init()
	{
		super.init();
		this.titleLabelY = 9;
		this.titleLabelX = 14;
		this.inventoryLabelX = 36;
		this.inventoryLabelY = 125;
		assert this.minecraft!=null;

		this.mirrorSchematicBtn = this.addRenderableWidget(new GuiButtonCheckbox(leftPos + 129, topPos + 112, Component.translatable("immersivegeology.gui.schematic_table.mirror").getString(), () -> menu.isMirroredSchematic, btn -> {
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("mirrored", !btn.getState());
			handleMirrorButtonClick(tag);
		}));

		this.selectionPanel = this.addRenderableWidget(new SchematicScrollPanel(minecraft, 110, 92, topPos + 20, leftPos + 9, menu.availableMultiblocks, (index) -> {
			this.minecraft.tell(() -> menu.jumpToSchematic(index));
			CompoundTag nbt = new CompoundTag();
			nbt.putInt("index", index);
			sendUpdateToServer(nbt);
			return true;
		}, (index) -> index == menu.selected_schematic, (amount) -> {
			scrollAmount = amount;
			return true;
		}));

		this.selectionPanel.setScrollAmount(scrollAmount);
	}

	@Override
	protected void drawBackgroundTexture(GuiGraphics graphics)
	{
		graphics.blit(TEXTURE, leftPos, topPos, 0, 0, 230, imageHeight);
		PoseStack pose = graphics.pose();

		float scale = 2.5f;
		float renderPosX = leftPos + 174.5f - (8 * scale);
		float renderPosY = topPos + 54.5f - (8 * scale);


		if(!menu.availableMultiblocks.isEmpty())
		{
			TemplateMultiblock mb = menu.availableMultiblocks.get(menu.selected_schematic);

			Vec3i structureSize = mb.getSize(Minecraft.getInstance().level);
			ItemStack renderStack = new ItemStack(mb.getBlock().asItem());
			float mbOriginalScale = this.font.width(mb.getDisplayName().getVisualOrderText());
			float maxScale = 80;
			float mbFontScale = (mbOriginalScale > maxScale) ? (maxScale / mbOriginalScale) : 1.0f;

			pose.pushPose();
			pose.translate(leftPos + 174, topPos + 8.75f, 0);
			pose.scale(mbFontScale,mbFontScale,mbFontScale);
			graphics.drawString(this.font, mb.getDisplayName(), -this.font.width(mb.getDisplayName().getVisualOrderText()) / 2,0, 0x666666,false);
			pose.popPose();

			pose.pushPose();
			float fontScale = 1;
			pose.scale(1,1,1);
			pose.translate((leftPos + 208f) * fontScale, (topPos + 31.5f) * fontScale, 0);
			graphics.drawString(this.font, String.valueOf(structureSize.getX()), 0, 0, 0x666666, false);
			pose.translate(0, 15 * fontScale, 0);
			graphics.drawString(this.font, String.valueOf(structureSize.getY()), 0, 0, 0x666666, false);
			pose.translate(0, 15 * fontScale, 0);
			graphics.drawString(this.font, String.valueOf(structureSize.getZ()), 0, 0, 0x666666, false);
			pose.popPose();

			pose.pushPose();
			pose.scale(scale,scale,scale);
			pose.translate(renderPosX / scale, renderPosY / scale, 0);

			graphics.renderItem(renderStack, 0, 0);
			pose.popPose();
		}
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x666666, false);
		graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x666666, false);
	}

	@Override
	protected void drawContainerBackgroundPre(@Nonnull GuiGraphics graphics, float f, int mx, int my)
	{

	}

	private void handleMirrorButtonClick(CompoundTag nbt)
	{
		if(!nbt.isEmpty())
		{
			sendUpdateToServer(nbt);
			getMenu().flipSchematic();
		}
	}

	@NotNull
	@Override
	protected List<InfoArea> makeInfoAreas()
	{
		List<InfoArea> areas = new ArrayList<>();
		return areas;
	}

	public static class SchematicScrollPanel extends ScrollPanel
	{
		private final List<TemplateMultiblock> schematics;
		private final int entryHeight;
		private final Minecraft client;
		private final Function<Integer, Boolean> callback;
		private final Function<Integer, Boolean> isSelectedCallback;
		private final Function<Float, Boolean> updateScroller;
		public SchematicScrollPanel(Minecraft client, int width, int height, int top, int left, List<TemplateMultiblock> schematics, Function<Integer, Boolean> select_callback, Function<Integer, Boolean> isSelectedCallback, Function<Float, Boolean> updateScroller)
		{
			super(client, width, height, top, left);
			this.schematics = schematics;
			this.entryHeight = 20;
			this.client = client;
			this.callback = select_callback;
			this.isSelectedCallback = isSelectedCallback;
			this.updateScroller = updateScroller;
		}

		public void setScrollAmount(float scroll)
		{
			this.scrollDistance = scroll;
		}

		@Override
		protected int getContentHeight()
		{
			return schematics.size() * entryHeight;
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button)
		{
			if(mouseX < left || mouseY < top || mouseX > (left+width) || mouseY > (top + height)) return false;

			int y = (int)(mouseY - top + scrollDistance);
			int index = y / entryHeight;
			if(index >= 0 && index < schematics.size()) {
				updateScroller.apply(scrollDistance);
				return callback.apply(index);
			}
			return false;
		}

		@Override
		protected void drawPanel(GuiGraphics graphics, int entryRight, int scrollY, Tesselator tesselator, int mouseX, int mouseY)
		{
			int y = scrollY;
			for (int i = 0; i < schematics.size(); i++)
			{
				TemplateMultiblock template = schematics.get(i);
				int entryTop = y+i*entryHeight;
				int entryBottom = entryTop+entryHeight;

				graphics.fill(left, entryTop, entryRight, entryBottom-1, 0xFFb4a99c);
				boolean hover = mouseY > entryTop && mouseY < entryBottom && isMouseOver(mouseX, mouseY);
				int color = isSelectedCallback.apply(i) ? 0xFFFFFF : (hover ? 0xAAFFAA : 0x666666);
				// Draw text
				graphics.drawString(client.font, template.getDisplayName().getString(), left+5, entryTop+5, color, false);
			}
		}

		@Override
		public NarrationPriority narrationPriority()
		{
			return NarrationPriority.NONE;
		}

		@Override
		public void updateNarration(@NotNull NarrationElementOutput narrationElementOutput) {}
	}

	public static class GuiButtonSchematic implements GuiEventListener
	{

		@Override
		public void setFocused(boolean b)
		{

		}

		@Override
		public boolean isFocused()
		{
			return false;
		}
	}
}
