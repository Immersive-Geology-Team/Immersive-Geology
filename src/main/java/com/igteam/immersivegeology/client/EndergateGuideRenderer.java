/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client;

import com.igteam.immersivegeology.common.block.belt.helper.EndergateLinks;
import com.igteam.immersivegeology.common.block.belt.helper.EndergateLinks.Guide;
import com.igteam.immersivegeology.common.block.belt.helper.EndergateRole;
import com.igteam.immersivegeology.common.block.belt.IGEndergateBlock;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.util.Mth;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Matrix4f;

import java.util.Optional;

public class EndergateGuideRenderer
{
	private static final float MARKER_HALF = 0.3125f;
	private static final float MARKER_LIFT = 0.04f;
	private static final float MARKER_ALPHA = 0.45f;
	private static final float OUTLINE_ALPHA = 0.35f;

	@SubscribeEvent
	public void onHighlight(RenderHighlightEvent.Block event)
	{
		Level level = Minecraft.getInstance().level;
		if(level==null)
			return;
		BlockPos pos = event.getTarget().getBlockPos();
		BlockState state = level.getBlockState(pos);
		if(state.getBlock() instanceof IGEndergateBlock gate&&gate.partner(level, pos, state).isPresent())
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void onRenderLevel(RenderLevelStageEvent event)
	{
		if(event.getStage()!=RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS)
			return;
		Minecraft minecraft = Minecraft.getInstance();
		Player player = minecraft.player;
		Level level = minecraft.level;
		if(player==null||level==null||!(minecraft.hitResult instanceof BlockHitResult hit)||hit.getType()!=HitResult.Type.BLOCK)
			return;
		BlockState looked = level.getBlockState(hit.getBlockPos());
		Optional<Guide> guide;
		BlockPos target;
		Direction facing;
		boolean placing;
		if(looked.getBlock() instanceof IGEndergateBlock lookedGate)
		{
			Optional<BlockPos> partner = lookedGate.partner(level, hit.getBlockPos(), looked);
			if(partner.isEmpty())
				return;
			facing = looked.getValue(IGEndergateBlock.FACING);
			boolean entry = looked.getValue(IGEndergateBlock.ROLE)==EndergateRole.ENTRY;
			BlockPos entryPos = entry?hit.getBlockPos(): partner.get();
			target = entry?partner.get(): hit.getBlockPos();
			guide = Optional.of(new Guide(entryPos, entryPos.distManhattan(target), EndergateLinks.GuideStatus.AT_MAX));
			placing = false;
		}
		else
		{
			ItemStack heldStack = heldEndergate(player);
			if(heldStack.isEmpty()||player.isShiftKeyDown())
				return;
			IGEndergateBlock held = (IGEndergateBlock)((BlockItem)heldStack.getItem()).getBlock();
			target = looked.canBeReplaced()?hit.getBlockPos(): hit.getBlockPos().relative(hit.getDirection());
			Optional<EndergateLinks.Lock> lock = EndergateLinks.lock(level, heldStack, target, held.range());
			if(lock.isPresent())
			{
				facing = lock.get().facing();
				guide = Optional.of(new Guide(lock.get().entry(), lock.get().distance(), EndergateLinks.status(lock.get().distance(), held.range())));
			}
			else
			{
				facing = player.getDirection();
				guide = EndergateLinks.guide(level, target, facing, held.range());
			}
			placing = true;
			if(guide.isEmpty())
				return;
		}

		PoseStack poseStack = event.getPoseStack();
		Vec3 camera = event.getCamera().getPosition();
		poseStack.pushPose();
		poseStack.translate(-camera.x, -camera.y, -camera.z);
		MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
		VertexConsumer consumer = buffers.getBuffer(RenderType.debugQuads());
		draw(consumer, poseStack.last().pose(), guide.get(), target, facing, placing);
		buffers.endBatch(RenderType.debugQuads());
		if(!placing)
		{
			VertexConsumer lines = buffers.getBuffer(RenderType.lines());
			for(BlockPos gate : new BlockPos[]{guide.get().entry(), target})
				outline(lines, poseStack.last(), level, gate);
			buffers.endBatch(RenderType.lines());
		}
		poseStack.popPose();
	}

	private static void outline(VertexConsumer consumer, PoseStack.Pose pose, Level level, BlockPos pos)
	{
		BlockState state = level.getBlockState(pos);
		VoxelShape shape = state.getCollisionShape(level, pos, CollisionContext.empty());
		if(shape.isEmpty())
			return;
		int rgb = GuideStrip.AT_MAX;
		float r = (rgb >> 16 & 0xFF)/255f, g = (rgb >> 8 & 0xFF)/255f, b = (rgb & 0xFF)/255f;
		shape.forAllEdges((x0, y0, z0, x1, y1, z1) -> {
			float dx = (float)(x1-x0), dy = (float)(y1-y0), dz = (float)(z1-z0);
			float length = Mth.sqrt(dx*dx+dy*dy+dz*dz);
			dx /= length;
			dy /= length;
			dz /= length;
			consumer.vertex(pose.pose(), (float)(pos.getX()+x0), (float)(pos.getY()+y0), (float)(pos.getZ()+z0)).color(r, g, b, OUTLINE_ALPHA).normal(pose.normal(), dx, dy, dz).endVertex();
			consumer.vertex(pose.pose(), (float)(pos.getX()+x1), (float)(pos.getY()+y1), (float)(pos.getZ()+z1)).color(r, g, b, OUTLINE_ALPHA).normal(pose.normal(), dx, dy, dz).endVertex();
		});
	}

	private static ItemStack heldEndergate(Player player)
	{
		for(InteractionHand hand : InteractionHand.values())
		{
			ItemStack stack = player.getItemInHand(hand);
			if(stack.getItem() instanceof BlockItem item&&item.getBlock() instanceof IGEndergateBlock)
				return stack;
		}
		return ItemStack.EMPTY;
	}

	private static int colour(EndergateLinks.GuideStatus status)
	{
		return switch(status)
		{
			case IN_RANGE -> GuideStrip.IN_RANGE;
			case AT_MAX -> GuideStrip.AT_MAX;
			case TOO_FAR -> GuideStrip.TOO_FAR;
		};
	}

	private static void draw(VertexConsumer consumer, Matrix4f pose, Guide guide, BlockPos target, Direction facing, boolean marker)
	{
		int rgb = colour(guide.status());
		float y = guide.entry().getY()+GuideStrip.LIFT;
		Vec3 start = Vec3.atBottomCenterOf(guide.entry()).relative(facing, 0.5);
		Vec3 end = Vec3.atBottomCenterOf(target).relative(facing, marker?-MARKER_HALF: -0.5);
		new GuideStrip(consumer, pose, start, start.distanceTo(end), facing, y, u -> rgb).draw();

		if(!marker)
			return;
		Vec3 centre = Vec3.atBottomCenterOf(target);
		float mx = (float)centre.x, mz = (float)centre.z, my = guide.entry().getY()+MARKER_LIFT;
		GuideStrip.vertex(consumer, pose, mx-MARKER_HALF, my, mz-MARKER_HALF, rgb, MARKER_ALPHA);
		GuideStrip.vertex(consumer, pose, mx-MARKER_HALF, my, mz+MARKER_HALF, rgb, MARKER_ALPHA);
		GuideStrip.vertex(consumer, pose, mx+MARKER_HALF, my, mz+MARKER_HALF, rgb, MARKER_ALPHA);
		GuideStrip.vertex(consumer, pose, mx+MARKER_HALF, my, mz-MARKER_HALF, rgb, MARKER_ALPHA);
	}
}
