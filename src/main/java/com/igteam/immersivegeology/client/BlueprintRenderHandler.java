/*
 * TwistedGate
 * Copyright (c) 2025
 *
 * A large portion of the code here was pulled from Immersive Petroleum's Projector and modified to suit Immersive Geologies Purposes.
 */

package com.igteam.immersivegeology.client;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler.IMultiblock;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import com.igteam.immersivegeology.client.renderer.IGRenderTypes;
import com.igteam.immersivegeology.common.item.blueprint.BlueprintProjection;
import com.igteam.immersivegeology.common.item.blueprint.BlueprintProjection.Info;
import com.igteam.immersivegeology.common.item.blueprint.IGBlueprintSettings;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MiscEnum;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = IGLib.MODID, value = Dist.CLIENT)
public class BlueprintRenderHandler {

	@SubscribeEvent
	public static void renderLevelStage(RenderLevelStageEvent event)
	{
		if(event.getStage() == Stage.AFTER_TRIPWIRE_BLOCKS)
		{
			renderMultiblockBlueprint(event);
		}
	}

	private static void renderMultiblockBlueprint(RenderLevelStageEvent event)
	{
		Minecraft mc = Minecraft.getInstance();
		if(mc.player == null) return;

		ItemStack mainItem = mc.player.getMainHandItem();
		ItemStack secondItem = mc.player.getOffhandItem();
		PoseStack matrix = event.getPoseStack();
		matrix.pushPose();
		{
			Vec3 renderView = mc.gameRenderer.getMainCamera().getPosition();
			matrix.translate(-renderView.x, -renderView.y, -renderView.z);
			if(secondItem.getTag()!=null)
			{
				Item blueprint = MiscEnum.Blueprint.getItem(ItemCategoryFlags.BLUEPRINT);
				// Allows multiple schematics to be visible at once as long as they're in the hot bar.
				for(int i = 0;i <= 10;i++){
					ItemStack stack = (i == 10 ? secondItem : mc.player.getInventory().getItem(i));
					if(stack.is(blueprint) && secondItem.hasTag() && secondItem.getTag().contains("settings", Tag.TAG_COMPOUND))
					{
						matrix.pushPose();
						{
							IGBlueprintSettings settings =  new IGBlueprintSettings(stack);
							renderSchematic(matrix, settings, mc.player, mc.player.level(), event.getPartialTick());
						}
						matrix.popPose();
					}
				}
			}
			else
			if(mainItem.getTag() != null)
			{
				Item blueprint = MiscEnum.Blueprint.getItem(ItemCategoryFlags.BLUEPRINT);
				boolean off = mainItem.is(blueprint) && mainItem.hasTag() && mainItem.getTag().contains("settings", Tag.TAG_COMPOUND);
				if(off)
				{
					matrix.pushPose();
					{
						IGBlueprintSettings settings =  new IGBlueprintSettings(mainItem);
						renderSchematicGrid(matrix, settings, mc.player.level());
					}
					matrix.popPose();
				}
			}
		}
		matrix.popPose();
	}

	public static void renderSchematicGrid(PoseStack matrix, IGBlueprintSettings settings, Level world)
	{
		if(settings.getMultiblock() == null) return;
		final MutableBlockPos hit = new MutableBlockPos(FULL_MAX.getX(), FULL_MAX.getY(), FULL_MAX.getZ());
		final MutableBoolean isPlaced = new MutableBoolean(false);
		if(settings.getPos() != null)
		{
			hit.set(settings.getPos());
			isPlaced.setTrue();
		}

		if(!hit.equals(FULL_MAX))
		{
			BlueprintProjection projection = new BlueprintProjection(world, settings.getMultiblock());
			projection.setRotation(settings.getRotation());
			projection.setFlip(settings.isMirrored());
			Vec3i mb_size = settings.getMultiblock().getSize(world);
			matrix.translate(hit.getX(), hit.getY(), hit.getZ());

			MultiBufferSource.BufferSource mainBuffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
			matrix.pushPose();
			{
					if(settings.getRotation().equals(Rotation.CLOCKWISE_180) || settings.getRotation().equals(Rotation.NONE))
					{
						matrix.translate(-(Math.floorDiv(mb_size.getX(),2)), -mb_size.getY(), -(Math.floorDiv(mb_size.getZ(),2)));
						renderGrid(mainBuffer, matrix, Vec3.ZERO, new Vec3(mb_size.getX(), mb_size.getY(), mb_size.getZ()), 16, 0.25f, 0xffffff);
					}
					else
					{
						matrix.translate(-(Math.floorDiv(mb_size.getZ(),2)), -mb_size.getY(), -(Math.floorDiv(mb_size.getX(),2)));
						renderGrid(mainBuffer, matrix, Vec3.ZERO, new Vec3(mb_size.getZ(), mb_size.getY(), mb_size.getX()), 16, 0.25f, 0xffffff);
					}
			}
			matrix.popPose();

			mainBuffer.endBatch();
		}
	}

	public enum RenderLayer{
		ALL, BAD, PERFECT
	}
	static final MutableBlockPos FULL_MAX = new MutableBlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
	public static void renderSchematic(PoseStack matrix, IGBlueprintSettings settings, Player player, Level world, float partialTicks){
		if(settings.getMultiblock() == null) return;
		ItemStack heldStack = player.getMainHandItem();
		final MutableBlockPos hit = new MutableBlockPos(FULL_MAX.getX(), FULL_MAX.getY(), FULL_MAX.getZ());
		final MutableBoolean isPlaced = new MutableBoolean(false);
		if(settings.getPos() != null)
		{
			hit.set(settings.getPos());
			isPlaced.setTrue();
		}

		if(!hit.equals(FULL_MAX))
		{
			BlueprintProjection projection = new BlueprintProjection(world, settings.getMultiblock());
			projection.setRotation(settings.getRotation());
			projection.setFlip(settings.isMirrored());
			Vec3i mb_size = settings.getMultiblock().getSize(world);

			final List<Pair<RenderLayer, Info>> toRender = new ArrayList<>();
			final MutableInt currentLayer = new MutableInt();
			final MutableInt badBlocks = new MutableInt();
			final MutableInt goodBlocks = new MutableInt();
			BiPredicate<Integer, Info> bipred = (layer, info) -> {
				if(badBlocks.getValue() == 0 && layer > currentLayer.getValue()){
					currentLayer.setValue(layer);
				}else if(!Objects.equals(layer, currentLayer.getValue())){
					return true; // breaks the internal loop
				}
				if(isPlaced.booleanValue()){ // Render only slices when placed
					if(Objects.equals(layer, currentLayer.getValue())){
						BlockPos realPos = info.tPos.offset(hit);
						BlockState toCompare = world.getBlockState(realPos);
						BlockState tState = info.getModifiedState(world, realPos);

						boolean skip = false;
						if(tState == toCompare){
							toRender.add(Pair.of(RenderLayer.PERFECT, info));
							goodBlocks.increment();
							skip = true;
						}else{
							// Making it this far only needs an air check,
							// the other already proved to be false.
							if(!toCompare.isAir()){
								toRender.add(Pair.of(RenderLayer.BAD, info));
								skip = true;
							}else{
								badBlocks.increment();
							}
						}

						if(skip){
							return false;
						}
					}
				}

				toRender.add(Pair.of(RenderLayer.ALL, info));
				return false;
			};
			projection.processAll(bipred);

			boolean perfect = (goodBlocks.getValue() == projection.getBlockCount());
			int current_layer = currentLayer.getValue();

			int total_blocks_available = projection.getLayerSize(current_layer);
			for(int i = 0; i < current_layer; i++)
			{
				total_blocks_available += projection.getLayerSize(i);
			}

			int block_left = total_blocks_available - badBlocks.getValue();
			boolean hasImperfection = (goodBlocks.getValue() != block_left);

			MutableBlockPos min = new MutableBlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
			MutableBlockPos max = new MutableBlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
			float flicker = world.random.nextFloat() / 2F + 0.25F;
			matrix.translate(hit.getX(), hit.getY(), hit.getZ());
			toRender.sort((a, b) -> {
				int ao = a.getFirst().ordinal();
				int bo = b.getFirst().ordinal();
				if(ao > bo){
					return 1;
				}else if(ao < bo){
					return -1;
				}
				return 0;
			});
			MultiBufferSource.BufferSource mainBuffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

			matrix.pushPose();
			{
				if(!perfect)
				{
					if(settings.getRotation().equals(Rotation.CLOCKWISE_180) || settings.getRotation().equals(Rotation.NONE))
					{
						matrix.translate(-(Math.floorDiv(mb_size.getX(),2)), -mb_size.getY(), -(Math.floorDiv(mb_size.getZ(),2)));
						matrix.translate(0, currentLayer.getValue(), 0);
						renderGrid(mainBuffer, matrix, Vec3.ZERO, new Vec3(mb_size.getX(), mb_size.getY(), mb_size.getZ()), 16, 0.25f, hasImperfection ? 0xff0000 : 0xffffff);
					}
					else
					{
						matrix.translate(-(Math.floorDiv(mb_size.getZ(),2)), -mb_size.getY(), -(Math.floorDiv(mb_size.getX(),2)));
						matrix.translate(0, currentLayer.getValue(), 0);
						renderGrid(mainBuffer, matrix, Vec3.ZERO, new Vec3(mb_size.getZ(), mb_size.getY(), mb_size.getX()), 16, 0.25f, hasImperfection ? 0xff0000 : 0xffffff);
					}

				}
			}
			matrix.popPose();

			for(Pair<RenderLayer, BlueprintProjection.Info> pair:toRender)
			{
				BlueprintProjection.Info rInfo = pair.getSecond();
				boolean held = heldStack.getItem() == rInfo.getRawState().getBlock().asItem();
				switch(pair.getFirst())
				{
					case ALL ->
					{
						float[] color = held ? new float[]{0.2f, 1.0f, 0.5f} : new float[]{0.2f, 0.5f, 1.0f};

						matrix.pushPose();
						{
							renderPhantom(matrix, world, rInfo, settings.isMirrored(), flicker, color, partialTicks);

							if(held)
							{
								renderCenteredOutlineBox(mainBuffer, matrix, 0x44ff44);
							}
						}
						matrix.popPose();
					}
					case BAD ->
					{
						matrix.pushPose();
						{
							matrix.translate(rInfo.tPos.getX(), rInfo.tPos.getY(), rInfo.tPos.getZ());

							renderCenteredOutlineBox(mainBuffer, matrix, 0xFF0000);
						}
						matrix.popPose();
					}
					case PERFECT ->
					{
						int x = rInfo.tPos.getX();
						int y = rInfo.tPos.getY();
						int z = rInfo.tPos.getZ();

						min.set(Math.min(x, min.getX()), Math.min(y, min.getY()), Math.min(z, min.getZ()));
						max.set(Math.max(x, max.getX()), Math.max(y, max.getY()), Math.max(z, max.getZ()));
					}
				}
			}
			if(perfect)
			{
				matrix.pushPose();
				{
					renderOutlineBox(mainBuffer, matrix, min, max, 0x00BF00);
				}
				matrix.popPose();
			}
			mainBuffer.endBatch();
		}
	}

	private static final Tesselator PHANTOM_TESSELATOR = new Tesselator();
	private static void renderPhantom(PoseStack matrix, Level realWorld, BlueprintProjection.Info rInfo, boolean mirror, float flicker, float[] color, float partialTicks){
		BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
		ModelBlockRenderer blockRenderer = dispatcher.getModelRenderer();
		BlockColors blockColors = Minecraft.getInstance().getBlockColors();

		// Centers the preview block
		matrix.translate(rInfo.tPos.getX(), rInfo.tPos.getY(), rInfo.tPos.getZ());

		MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(PHANTOM_TESSELATOR.getBuilder());

		BlockState state = rInfo.getModifiedState(realWorld, rInfo.tPos);
		state.updateNeighbourShapes(realWorld, rInfo.tPos, 3);

		ModelData modelData = ModelData.EMPTY;
		BlockEntity te = rInfo.templateWorld.getBlockEntity(rInfo.tBlockInfo.pos());
		if(te != null){
			te.setBlockState(state);
			modelData = te.getModelData();
		}

		RenderShape blockrendertype = state.getRenderShape();
		switch(blockrendertype){
			case MODEL -> {
				BakedModel ibakedmodel = dispatcher.getBlockModel(state);
				int i = blockColors.getColor(state, null, null, 0);
				float red = (i >> 16 & 0xFF) / 255F;
				float green = (i >> 8 & 0xFF) / 255F;
				float blue = (i & 0xFF) / 255F;

				modelData = ibakedmodel.getModelData(rInfo.templateWorld, rInfo.tBlockInfo.pos(), state, modelData);
				IGShaders.setBlueprintData(partialTicks, color[0],color[1],color[2]);
				VertexConsumer vc = buffer.getBuffer(IGRenderTypes.BLUEPRINT);
				matrix.scale(0.5f, 0.5f,0.5f);
				matrix.translate(0.5f,0.5f,0.5f);
				blockRenderer.renderModel(matrix.last(), vc, state, ibakedmodel, red, green, blue, 0xF000F0, OverlayTexture.NO_OVERLAY, modelData, null);
				matrix.translate(-0.5f,-0.5f,-0.5f);
				matrix.scale(2f, 2f,2f);
			}
			case ENTITYBLOCK_ANIMATED -> {
				ItemStack stack = new ItemStack(state.getBlock());
				matrix.scale(0.5f, 0.5f,0.5f);
				matrix.translate(0.5f,0.5f,0.5f);
				Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.NONE, 0xF000F0, OverlayTexture.NO_OVERLAY, matrix, buffer, null, 0);
				matrix.translate(-0.5f,-0.5f,-0.5f);
				matrix.scale(2f, 2f,2f);
			}
			default -> {}
		}

		buffer.endBatch();
	}

	private static void renderCenteredOutlineBox(MultiBufferSource buffer, PoseStack matrix, int rgb){
		renderBox(buffer, matrix, Vec3.ZERO, new Vec3(1, 1, 1), rgb);
	}

	private static void renderOutlineBox(MultiBufferSource buffer, PoseStack matrix, Vec3i min, Vec3i max, int rgb){
		renderBox(buffer, matrix, Vec3.atLowerCornerOf(min), Vec3.atLowerCornerOf(max).add(1, 1, 1), rgb);
	}

	private static void renderGrid(
			MultiBufferSource buffer,
			PoseStack matrix,
			Vec3 origin,
			Vec3 normal,
			float gridSize,
			float stepSize,
			int rgb
	) {
		VertexConsumer builder = buffer.getBuffer(RenderType.LINES);
		float alpha = 0.3f;
		int rgba = rgb | (((int) (alpha * 255)) << 24);

		for(float a = 0; a <= normal.z; a+=stepSize)
		{
			matrix.translate(0,0,a);
			line(builder, matrix, origin, origin.add(normal), 0b010, 0b110, rgba);
			matrix.translate(0,0,-a);
		}
		for(float a = 0; a <= normal.x; a+=stepSize)
		{
			matrix.translate(a, 0, 0);
			line(builder, matrix, origin, origin.add(normal), 0b010, 0b011, rgba);
			matrix.translate(-a, 0, 0);
		}
	}

	private static void renderBox(MultiBufferSource buffer, PoseStack matrix, Vec3 min, Vec3 max, int rgb){
		VertexConsumer builder = buffer.getBuffer(RenderType.LINES);

		float alpha = 0.3f;

		int rgba = rgb | (((int) (alpha * 255)) << 24);

		line(builder, matrix, min, max, 0b010, 0b110, rgba);
		line(builder, matrix, min, max, 0b110, 0b111, rgba);
		line(builder, matrix, min, max, 0b111, 0b011, rgba);
		line(builder, matrix, min, max, 0b011, 0b010, rgba);

		line(builder, matrix, min, max, 0b010, 0b000, rgba);
		line(builder, matrix, min, max, 0b110, 0b100, rgba);
		line(builder, matrix, min, max, 0b011, 0b001, rgba);
		line(builder, matrix, min, max, 0b111, 0b101, rgba);

		line(builder, matrix, min, max, 0b000, 0b100, rgba);
		line(builder, matrix, min, max, 0b100, 0b101, rgba);
		line(builder, matrix, min, max, 0b101, 0b001, rgba);
		line(builder, matrix, min, max, 0b001, 0b000, rgba);
	}

	private static void line(VertexConsumer out, PoseStack mat, Vec3 min, Vec3 max, int startBits, int endBits, int rgba){
		Vector3f start = combine(min, max, startBits);
		Vector3f end = combine(min, max, endBits);
		Vector3f delta = new Vector3f(end);
		delta.sub(start);
		out.vertex(mat.last().pose(), start.x(), start.y(), start.z())
				.color(rgba)
				.normal(mat.last().normal(), delta.x(), delta.y(), delta.z())
				.endVertex();
		out.vertex(mat.last().pose(), end.x(), end.y(), end.z())
				.color(rgba)
				.normal(mat.last().normal(), delta.x(), delta.y(), delta.z())
				.endVertex();
	}

	private static Vector3f combine(Vec3 start, Vec3 end, int mixBits){
		final float eps = 0.01f;
		return new Vector3f(
				(float) ((mixBits & 4) != 0 ? end.x + eps : start.x - eps),
				(float) ((mixBits & 2) != 0 ? end.y + eps : start.y - eps),
				(float) ((mixBits & 1) != 0 ? end.z + eps : start.z - eps)
		);
	}
}

