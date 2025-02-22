/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.renderer;

import com.igteam.immersivegeology.client.BlueprintRenderHandler.RenderLayer;
import com.igteam.immersivegeology.common.item.blueprint.BlueprintProjection;
import com.igteam.immersivegeology.common.item.blueprint.BlueprintProjection.Info;
import com.igteam.immersivegeology.common.item.blueprint.IGBlueprintSettings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.igteam.immersivegeology.client.BlueprintRenderHandler.*;

public class IGSchematicRenderer
{
	private static final int COLOR_ERROR = 0xFF0000;
	private static final int COLOR_WARNING = 0xFFFF00;
	private static final int COLOR_SUCCESS = 0x00BF00;
	private static final int COLOR_HIGHLIGHT = 0x44FF44;
	private static final float[] COLOR_HELD = {0.2f, 1.0f, 0.5f};
	private static final float[] COLOR_NORMAL = {0.2f, 0.5f, 1.0f};
	static final MutableBlockPos FULL_MAX = new MutableBlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);

	public static void renderSchematic(PoseStack matrix, IGBlueprintSettings settings,
									   Player player, Level world) {
		if (settings.getMultiblock() == null) return;

		// Initialize position tracking
		final MutableBlockPos hit = initializePosition(settings);
		if (hit.equals(FULL_MAX)) return;

		// Setup projection
		BlueprintProjection projection = setupProjection(world, settings);
		Vec3i mbSize = settings.getMultiblock().getSize(world);

		// Pre-allocate collections with estimated sizes
		Map<BlockPos, Boolean> badStates = new HashMap<>(mbSize.getX() * mbSize.getY() * mbSize.getZ());
		List<Pair<RenderLayer, Info>> toRender = new ArrayList<>(projection.getBlockCount());

		// Process blocks
		RenderingState renderState = processBlocks(projection, world, hit, badStates, toRender);

		// Render results
		renderResults(matrix, renderState, settings, world, player, mbSize,
				badStates, toRender);
	}

	private static MutableBlockPos initializePosition(IGBlueprintSettings settings) {
		final MutableBlockPos hit = new MutableBlockPos(FULL_MAX.getX(), FULL_MAX.getY(), FULL_MAX.getZ());
		if (settings.getPos() != null) {
			hit.set(settings.getPos());
		}
		return hit;
	}

	private static BlueprintProjection setupProjection(Level world, IGBlueprintSettings settings) {
		BlueprintProjection projection = new BlueprintProjection(world, settings.getMultiblock());
		projection.setRotation(settings.getRotation());
		projection.setFlip(settings.isMirrored());
		return projection;
	}

	private static RenderingState processBlocks(BlueprintProjection projection, Level world,
												BlockPos hit, Map<BlockPos, Boolean> badStates, List<Pair<RenderLayer, Info>> toRender) {
		MutableInt currentLayer = new MutableInt();
		MutableInt badBlocks = new MutableInt();
		MutableInt goodBlocks = new MutableInt();
		AtomicInteger imperfectionLayer = new AtomicInteger(-1);

		projection.processAll((layer, info) -> processBlock(layer, info, world, hit,
				badStates, toRender, currentLayer, badBlocks, goodBlocks, imperfectionLayer));

		return new RenderingState(
				goodBlocks.getValue() == projection.getBlockCount(),
				imperfectionLayer.get() != -1,
				badStates.containsValue(false),
				currentLayer.getValue()
		);
	}

	private static boolean processBlock(int layer, Info info, Level world, BlockPos hit,
										Map<BlockPos, Boolean> badStates, List<Pair<RenderLayer, Info>> toRender,
										MutableInt currentLayer, MutableInt badBlocks, MutableInt goodBlocks,
										AtomicInteger imperfectionLayer) {

		// Update current layer if no bad blocks found
		if (badBlocks.getValue() == 0 && layer > currentLayer.getValue()) {
			currentLayer.setValue(layer);
		} else if (layer != currentLayer.getValue()) {
			return true; // Break the internal loop
		}

		// Process block at current layer
		if (hit != FULL_MAX && layer == currentLayer.getValue()) {
			BlockPos realPos = info.tPos.offset(hit);
			BlockState currentState = world.getBlockState(realPos);
			BlockState targetState = info.getModifiedState(world, realPos);

			// Check if block is in correct state
			if (targetState == currentState) {
				toRender.add(Pair.of(RenderLayer.PERFECT, info));
				goodBlocks.increment();
				return false;
			}

			// Check if block needs replacing
			if (!currentState.isAir()) {
				toRender.add(Pair.of(RenderLayer.BAD, info));
				// Check if block is of correct type but wrong state
				boolean isSameBlockType = targetState.getBlock().defaultBlockState()
						.equals(currentState.getBlock().defaultBlockState());
				badStates.put(info.tPos, isSameBlockType);
				imperfectionLayer.set(layer);
				return false;
			}

			badBlocks.increment();
		}

		toRender.add(Pair.of(RenderLayer.ALL, info));
		return false;
	}

	private static void renderResults(PoseStack matrix, RenderingState state,
									  IGBlueprintSettings settings, Level world, Player player, Vec3i mbSize,
									  Map<BlockPos, Boolean> badStates, List<Pair<RenderLayer, Info>> toRender) {

		// Sort render list by layer type
		toRender.sort(Comparator.comparingInt(a -> a.getFirst().ordinal()));

		// Setup rendering
		matrix.translate(settings.getPos().getX(), settings.getPos().getY(), settings.getPos().getZ());
		MultiBufferSource.BufferSource mainBuffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

		// Render grid for imperfect structures
		if (!state.perfect()) {
			matrix.pushPose();
			renderGridForRotation(matrix, mainBuffer, settings, mbSize, state);
			matrix.popPose();
		}

		// Track perfect structure bounds
		MutableBlockPos min = new MutableBlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
		MutableBlockPos max = new MutableBlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
		ItemStack heldStack = player.getMainHandItem();

		// Render each block
		for (Pair<RenderLayer, Info> pair : toRender) {
			Info info = pair.getSecond();
			boolean isHeld = heldStack.getItem() == info.getRawState().getBlock().asItem();

			switch (pair.getFirst()) {
				case ALL -> renderAllLayer(matrix, world, info, isHeld, state.hasWrongBlock(), mainBuffer);

				case BAD -> renderBadLayer(matrix, mainBuffer, info, badStates);

				case PERFECT -> updatePerfectBounds(min, max, info);
			}
		}

		// Render perfect structure outline
		if (state.perfect()) {
			matrix.pushPose();
			renderOutlineBox(mainBuffer, matrix, min, max, COLOR_SUCCESS);
			matrix.popPose();
		}

		mainBuffer.endBatch();
	}
	private static void renderGridForRotation(PoseStack matrix, MultiBufferSource.BufferSource buffer,
											  IGBlueprintSettings settings, Vec3i mbSize, RenderingState state) {
		boolean isHorizontalRotation = settings.getRotation().equals(Rotation.CLOCKWISE_180) ||
				settings.getRotation().equals(Rotation.NONE);

		int width = isHorizontalRotation ? mbSize.getX() : mbSize.getZ();
		int depth = isHorizontalRotation ? mbSize.getZ() : mbSize.getX();

		matrix.translate(
				-(Math.floorDiv(width, 2)),
				-mbSize.getY(),
				-(Math.floorDiv(depth, 2))
		);
		matrix.translate(0, state.currentLayer(), 0);

		int highlightColor = state.hasImperfection() ?
				(state.hasWrongBlock() ? COLOR_ERROR : COLOR_WARNING) : 0xffffff;

		renderGrid(buffer, matrix, Vec3.ZERO,
				new Vec3(width, mbSize.getY(), depth), 16, 0.25f, highlightColor);
	}

	private static void renderAllLayer(PoseStack matrix, Level world, Info info, boolean isHeld, boolean hasWrongBlock,
									   BufferSource buffer) {
		if (hasWrongBlock) return;

		matrix.pushPose();
		renderPhantom(matrix, world, info,
				isHeld ? COLOR_HELD : COLOR_NORMAL, 0);

		if (isHeld) {
			renderCenteredOutlineBox(buffer, matrix, COLOR_HIGHLIGHT);
		}
		matrix.popPose();
	}

	private static void renderBadLayer(PoseStack matrix, MultiBufferSource.BufferSource buffer,
									   Info info, Map<BlockPos, Boolean> badStates) {
		matrix.pushPose();
		matrix.translate(info.tPos.getX(), info.tPos.getY(), info.tPos.getZ());
		renderCenteredOutlineBox(buffer, matrix,
				badStates.get(info.tPos) ? COLOR_WARNING : COLOR_ERROR);
		matrix.popPose();
	}

	private static void updatePerfectBounds(MutableBlockPos min, MutableBlockPos max, Info info) {
		min.set(
				Math.min(info.tPos.getX(), min.getX()),
				Math.min(info.tPos.getY(), min.getY()),
				Math.min(info.tPos.getZ(), min.getZ())
		);
		max.set(
				Math.max(info.tPos.getX(), max.getX()),
				Math.max(info.tPos.getY(), max.getY()),
				Math.max(info.tPos.getZ(), max.getZ())
		);
	}

	private record RenderingState(
			boolean perfect,
			boolean hasImperfection,
			boolean hasWrongBlock,
			int currentLayer
	) {}
}
