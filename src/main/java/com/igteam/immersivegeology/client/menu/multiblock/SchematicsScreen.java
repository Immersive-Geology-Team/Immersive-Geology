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
import blusunrize.immersiveengineering.client.gui.elements.GuiButtonBoolean;
import blusunrize.immersiveengineering.client.gui.elements.GuiButtonCheckbox;
import blusunrize.immersiveengineering.client.gui.elements.GuiButtonIE;
import blusunrize.immersiveengineering.client.gui.elements.GuiButtonState;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import blusunrize.immersiveengineering.client.utils.GuiHelper;
import com.igteam.immersivegeology.common.menu.SchematicOutputArea;
import com.igteam.immersivegeology.common.menu.SchematicsContainerMenu;
import com.igteam.immersivegeology.common.menu.SchematicsContainerMenu.SchematicSlot;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableInt;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SchematicsScreen extends IEContainerScreen<SchematicsContainerMenu>
{
	private static final ResourceLocation TEXTURE = IGLib.makeTextureLocation("drawing_table");


	private GuiButtonCheckbox mirrorSchematicBtn;
	private GuiButtonSchematic next, back;
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

		this.next = this.addRenderableWidget(GuiButtonSchematic.create(leftPos + 106, topPos + 23, 0, Component.literal(""), btn -> {
			this.minecraft.tell(menu::nextSchematic);
			CompoundTag nbt = new CompoundTag();
			nbt.putInt("instruction", 1);
			sendUpdateToServer(nbt);
		}));

		this.back = this.addRenderableWidget(GuiButtonSchematic.create(leftPos + 106, topPos + 43, 19, Component.literal(""), btn -> {
			// Should help against Concurrent Modification
			this.minecraft.tell(menu::previousSchematic);	// This updates Client Side stuff

			// Tell the Menu to update Server Side stuff
			CompoundTag nbt = new CompoundTag();
			nbt.putInt("instruction", 2);
			sendUpdateToServer(nbt);
		}));
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
			pose.pushPose();
			pose.translate(leftPos + 174, topPos + 8.75f, 0);
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
		for(int i = 0; i < menu.ownSlotCount; i++)
		{
			Slot s = menu.getSlot(i);
			if(s instanceof SchematicSlot schematicSlot)
				areas.add(new SchematicOutputArea(schematicSlot, leftPos, topPos));
		}
		return areas;
	}

	public enum SchematicState
	{
		NORMAL,
		MIRRORED;

		public MutableComponent getDescription()
		{
			return Component.translatable("immersivegeology.gui.schematic_table.mirror_state_" + ordinal());
		}
	}

	public static class GuiButtonSchematic extends Button
	{
		protected final ResourceLocation texture;
		protected final int texU;
		protected final int texV;

		public static GuiButtonSchematic create(int x, int y, int vOffset, Component name, Button.OnPress onPress)
		{
			return new GuiButtonSchematic(x,y,18,18, name, TEXTURE, 231,1 + vOffset, onPress);
		}

		public GuiButtonSchematic(int x, int y, int w, int h, Component name, ResourceLocation texture, int u, int v, Button.OnPress handler)
		{
			super(x, y, w, h, name, handler, DEFAULT_NARRATION);
			this.texture = texture;
			this.texU = u;
			this.texV = v;
		}

		int[] hoverOffset;

		public GuiButtonSchematic setHoverOffset(int x, int y)
		{
			this.hoverOffset = new int[]{x, y};
			return this;
		}

		private boolean isPressable(double mouseX, double mouseY)
		{
			return this.active&&this.visible&&mouseX >= this.getX()&&mouseY >= this.getY()&&mouseX < this.getX()+this.width&&mouseY < this.getY()+this.height;
		}

		@Override
		public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
		{
			Minecraft mc = Minecraft.getInstance();
			Font fontrenderer = mc.font;
			this.isHovered = isPressable(mouseX, mouseY);
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
			RenderSystem.blendFunc(770, 771);
			if(hoverOffset!=null&&this.isHovered)
				graphics.blit(texture, getX(), getY(), texU+hoverOffset[0], texV+hoverOffset[1], width, height);
			else
				graphics.blit(texture, getX(), getY(), texU, texV, width, height);
			if(!getMessage().getString().isEmpty())
			{
				int txtCol = 0xE0E0E0;
				if(!this.active)
					txtCol = 0xA0A0A0;
				else if(this.isHovered)
					txtCol = Lib.COLOUR_I_ImmersiveOrange;
				graphics.drawCenteredString(fontrenderer, getMessage(), this.getX()+this.width/2, this.getY()+(this.height-8)/2, txtCol);
			}
		}
	}
}
