/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.menu.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.gui.info.EnergyInfoArea;
import blusunrize.immersiveengineering.client.gui.info.FluidInfoArea;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import blusunrize.immersiveengineering.client.utils.IERenderTypes;
import blusunrize.immersiveengineering.client.utils.TransformingVertexBuilder;
import blusunrize.immersiveengineering.common.util.fakeworld.TemplateWorld;
import com.google.common.collect.ImmutableList;
import com.igteam.immersivegeology.client.IGShaders;
import com.igteam.immersivegeology.client.helper.FluidCuboid;
import com.igteam.immersivegeology.client.helper.IGFluidRenderHelper;
import com.igteam.immersivegeology.client.renderer.IGRenderTypes;
import com.igteam.immersivegeology.common.block.multiblocks.IGGeothermalExchangerMultiblock;
import com.igteam.immersivegeology.common.block.multiblocks.gui.GeothermalExchangerMenu;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GeothermalConversionRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Predicate;

public class GeothermalExchangerScreen extends IEContainerScreen<GeothermalExchangerMenu>
{
	private static final ResourceLocation TEXTURE = IGLib.makeTextureLocation("multiblocks/geothermal_exchanger");
	public static double lastPrintedErrorTimeMs;
	private final TemplateWorld structureWorld;
	private final MultiblockRenderInfo renderInfo;

	public GeothermalExchangerScreen(GeothermalExchangerMenu menu, Inventory inv, Component title)
	{
		super(menu, inv, title, TEXTURE);
		this.imageWidth = 234;
		this.imageHeight = 185;
		Level level = inv.player.level();
		List<StructureTemplate.StructureBlockInfo> structure = IGGeothermalExchangerMultiblock.INSTANCE.getStructure(level);
		this.renderInfo = new MultiblockRenderInfo(structure);
		this.structureWorld = new TemplateWorld(structure, this.renderInfo, level.registryAccess());
	}

	@Override
	protected void init()
	{
		super.init();
		this.inventoryLabelY = this.imageHeight - 94;
		this.inventoryLabelX = 38;
	}

	@Override
	protected void drawBackgroundTexture(GuiGraphics graphics)
	{
		PoseStack pose = graphics.pose();

		graphics.blit(TEXTURE, leftPos+198, topPos+29, 33, 187, 31, 62);
		graphics.blit(TEXTURE, leftPos+5, topPos+29, 1, 187, 31, 62);

		pose.pushPose();
		{
			pose.translate(leftPos+193, topPos+56, 0);
			pose.pushPose();

			pose.translate(4.5, 3.5, 0);
			pose.mulPose(new Quaternionf().rotateAxis(75*Mth.DEG_TO_RAD, new Vector3f(0, 0, 1)));
			pose.mulPose(new Quaternionf().rotateAxis(-(this.menu.heat.get())*Mth.DEG_TO_RAD, new Vector3f(0, 0, 1)));
			pose.translate(-4.5, -3.5, 0);
			graphics.blit(TEXTURE, 0, 0, 65, 187, 32, 7);
			pose.popPose();
		}
		pose.popPose();
		pose.pushPose();
		{
			pose.translate(leftPos+28, topPos+56, 0);
			pose.pushPose();

			pose.translate(4.5, 3.5, 0);
			pose.mulPose(new Quaternionf().rotateAxis(75*Mth.DEG_TO_RAD, new Vector3f(0, 0, 1)));
			pose.mulPose(new Quaternionf().rotateAxis((this.menu.cooling_rate.get())*Mth.DEG_TO_RAD, new Vector3f(0, 0, 1)));
			pose.translate(-4.5, -3.5, 0);
			graphics.blit(TEXTURE, 0,0, 65, 187, 32, 7);
			pose.popPose();
		}
		pose.popPose();

		graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
	{
		super.renderLabels(graphics, mouseX, mouseY);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		super.render(graphics, mouseX, mouseY, partialTicks);
		MultiBufferSource bufferSource = graphics.bufferSource();
		PoseStack pose = graphics.pose();
		renderMultiblock(pose, graphics, bufferSource);
	}

	@Override
	public void renderBackground(@NotNull GuiGraphics graphics)
	{
		super.renderBackground(graphics);
	}

	private Transformation createRenderTransform() {
		return new Transformation(
				null,
				(new Quaternionf()).rotateXYZ((float) Math.toRadians(25.0), 0.0F, 0.0F),
				null,
				(new Quaternionf()).rotateXYZ(0.0F, (float) Math.toRadians(-45.0), 0.0F)
		);
	}

	//As the projection doesn't show the NORTH,EAST,UP faces we can ignore them here.
	FluidCuboid fluidCube = FluidCuboid.builder().face(true, 0, Direction.SOUTH,Direction.WEST).face(false,0,Direction.UP).build();

	private void renderMultiblock(PoseStack pose, GuiGraphics graphics, MultiBufferSource bufferSource)
	{
		Level level = Minecraft.getInstance().level;
		if(level==null)
			return;

		TemplateMultiblock multiblock = IGGeothermalExchangerMultiblock.INSTANCE;
		ClientMultiblocks.MultiblockManualData renderProperties = ClientMultiblocks.get(multiblock);
		List<StructureTemplate.StructureBlockInfo> structure = multiblock.getStructure(level);
		BlockRenderDispatcher blockRender = Minecraft.getInstance().getBlockRenderer();
		ItemRenderer itemRender = Minecraft.getInstance().getItemRenderer();
		ItemStack multiblockRenderIcon = new ItemStack(multiblock.getBlock());
		int[] structureDimensions = calculateStructureDimensions(structure);
		int structureHeight = structureDimensions[0];
		int structureWidth = structureDimensions[1];
		int structureLength = structureDimensions[2];

		// Placeholder pose offsets
		float scale = multiblock.getManualScale()*0.65f;
		float renderPosX = leftPos+116;
		float renderPosY = topPos+46f;

		float maxDimension = Math.max(structureHeight, Math.max(structureWidth, structureLength));
		PoseStack.Pose lastEntryBeforeTry = pose.last();

		try
		{
			pose.pushPose();
			// Setup initial translation and scaling
			pose.translate(renderPosX, renderPosY, maxDimension);
			pose.scale(scale, -scale, 1.0F);

			// Apply additional transformation and rotation
			Transformation additionalTransform = createRenderTransform();
			pose.pushTransformation(additionalTransform);
			pose.mulPose(new Quaternionf().rotateXYZ(0.0F, 1.5707964F, 0.0F));

			// Center the structure
			pose.translate(
					(float)structureLength/-2.0F,
					(float)structureHeight/-2.0F,
					(float)structureWidth/-2.0F
			);

			pose.pushPose();
			pose.translate(-1f,4,0);
			BakedModel itemModel = itemRender.getModel(multiblockRenderIcon, level, null, 0);
			pose.translate(1.5, 0.5, 1.5);
			itemRender.renderModelLists(itemModel, multiblockRenderIcon,
					0xffffffff, OverlayTexture.NO_OVERLAY, pose,
					bufferSource.getBuffer(IERenderTypes.TRANSLUCENT_FULLBRIGHT));
			pose.popPose();

			IGShaders.setGeothermalRenderData(0.8f);
			int blockIndex = 0;
			TransformingVertexBuilder translucentFullbright = new TransformingVertexBuilder(bufferSource, IGRenderTypes.GEOTHERMAL_DISPLAY);

			for(int h = -1; h < structureHeight; ++h) {
				for(int l = 0; l < structureLength; ++l) {
					for(int w = 0; w < structureWidth; ++w) {
						BlockPos pos = new BlockPos(l, h, w);
						BlockState state = this.structureWorld.getBlockState(pos);
						int overlay = OverlayTexture.NO_OVERLAY;
						if (state.isAir())
						{
							if(blockIndex > 65) continue;
							int heatState = unpackHeatStateAtIndex(blockIndex);
							Block heatBlock = GeothermalConversionRecipe.getBlockFromIndex(level, heatState);

							pose.pushPose();
							pose.translate((float)l, (float)h, (float)w);
							ModelData modelData = ModelData.EMPTY;
							BlockState extraState = heatBlock.defaultBlockState();
							BakedModel model = blockRender.getBlockModel(extraState);
							modelData = model.getModelData(this.structureWorld, pos, extraState, modelData);
							if(heatBlock.defaultBlockState().getFluidState().is(Fluids.EMPTY))
							{

								pose.pushPose();
								pose.translate(-0.5,0.5,0.5);
								blockRender.getModelRenderer().tesselateBlock(this.structureWorld, model, extraState, pos, pose, translucentFullbright, false, this.structureWorld.random, state.getSeed(pos), overlay, modelData, (RenderType)null);
								pose.popPose();
							}
							if(!heatBlock.defaultBlockState().getFluidState().is(Fluids.EMPTY))
							{
								pose.pushPose();
								Fluid fluid = heatBlock.defaultBlockState().getFluidState().getType();
								IClientFluidTypeExtensions fluidAttributes = IClientFluidTypeExtensions.of(fluid);
								TextureAtlasSprite flowing = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidAttributes.getFlowingTexture());
								TextureAtlasSprite still = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidAttributes.getStillTexture());
								pose.translate(-0.5,0.5,0.5);
								IGFluidRenderHelper.renderCuboid(pose, translucentFullbright, fluidCube, still, flowing, new Vector3f(0,0,0), new Vector3f(1,1,1), 0xffffffff, 0xffffffff);
								pose.popPose();
							}

							pose.popPose();
							blockIndex++;
						}
					}
				}
			}
			pose.popPose(); // pop center transform
		} catch(Exception e)
		{
			long now = System.currentTimeMillis();
			if(now > lastPrintedErrorTimeMs+1000L)
			{
				e.printStackTrace();
				lastPrintedErrorTimeMs = now;
			}

			while(lastEntryBeforeTry!=pose.last())
			{
				pose.popPose();
			}
		}
	}

	public int unpackHeatStateAtIndex(int index) {
		return this.menu.BLOCK_MAP_DATA.get()[index];
	}

	private int[] calculateStructureDimensions(List<StructureTemplate.StructureBlockInfo> structure) {
		int structureHeight = 0;
		int structureWidth = 0;
		int structureLength = 0;

		for (StructureTemplate.StructureBlockInfo block : structure) {
			structureHeight = Math.max(structureHeight, block.pos().getY() + 1);
			structureWidth = Math.max(structureWidth, block.pos().getZ() + 1);
			structureLength = Math.max(structureLength, block.pos().getX() + 1);
		}

		return new int[] { structureHeight, structureWidth, structureLength };
	}

	@Override
	protected void drawContainerBackgroundPre(@Nonnull GuiGraphics graphics, float f, int mx, int my)
	{

	}

	@NotNull
	@Override
	protected List<InfoArea> makeInfoAreas()
	{
		return ImmutableList.of(
				new FluidInfoArea(this.menu.tanks[0], new Rect2i(this.leftPos + 157, this.topPos + 23, 16, 47), 101, 187, 20, 51, TEXTURE),
				new FluidInfoArea(this.menu.tanks[1], new Rect2i(this.leftPos + 61, this.topPos + 23, 16, 47), 101, 187, 20, 51, TEXTURE),
				new EnergyInfoArea(this.leftPos + 206,this.topPos + 98, this.menu.energy_storage));
	}

	static class MultiblockRenderInfo implements Predicate<BlockPos>
	{
		public Map<BlockPos, StructureBlockInfo> data = new HashMap();
		private final int structureHeight;
		private final int structureLength;
		private final int structureWidth;
		private final int maxBlockIndex;
		private int showLayer = -1;
		private int blockIndex;

		MultiblockRenderInfo(List<StructureTemplate.StructureBlockInfo> structure) {
			int structureHeight = 0;
			int structureWidth = 0;
			int structureLength = 0;
			Iterator var5 = structure.iterator();

			while(var5.hasNext()) {
				StructureTemplate.StructureBlockInfo block = (StructureTemplate.StructureBlockInfo)var5.next();
				structureHeight = Math.max(structureHeight, block.pos().getY() + 1);
				structureWidth = Math.max(structureWidth, block.pos().getZ() + 1);
				structureLength = Math.max(structureLength, block.pos().getX() + 1);
				this.data.put(block.pos(), block);
			}

			this.maxBlockIndex = this.blockIndex = structureHeight * structureLength * structureWidth;
			this.structureHeight = structureHeight;
			this.structureLength = structureLength;
			this.structureWidth = structureWidth;
		}

		void setShowLayer(int layer) {
			this.showLayer = layer;
			if (layer < 0) {
				this.reset();
			} else {
				this.blockIndex = (layer + 1) * this.structureLength * this.structureWidth - 1;
			}

		}

		public void reset() {
			this.blockIndex = this.maxBlockIndex;
		}

		void step() {
			int start = this.blockIndex;

			do {
				if (++this.blockIndex >= this.maxBlockIndex) {
					this.blockIndex = 0;
				}
			} while(this.isEmpty(this.blockIndex) && this.blockIndex != start);

		}

		private boolean isEmpty(int index) {
			int y = index / (this.structureLength * this.structureWidth);
			int r = index % (this.structureLength * this.structureWidth);
			int x = r / this.structureWidth;
			int z = r % this.structureWidth;
			return !this.data.containsKey(new BlockPos(x, y, z));
		}

		int getLimiter() {
			return this.blockIndex;
		}

		public boolean test(BlockPos blockPos) {
			int index = blockPos.getZ() + this.structureWidth * (blockPos.getX() + this.structureLength * blockPos.getY());
			return index <= this.getLimiter();
		}
	}
}
