/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client;

import com.igteam.immersivegeology.common.block.belt.network.BeltLines;
import com.igteam.immersivegeology.common.block.belt.item.IGBeltItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Optional;

public class BeltLineGuideRenderer
{
	@SubscribeEvent
	public void onRenderLevel(RenderLevelStageEvent event)
	{
		if(event.getStage()!=RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS)
			return;
		Minecraft minecraft = Minecraft.getInstance();
		Player player = minecraft.player;
		if(player==null||minecraft.level==null||!(minecraft.hitResult instanceof BlockHitResult hit)||hit.getType()!=HitResult.Type.BLOCK)
			return;
		for(InteractionHand hand : InteractionHand.values())
		{
			ItemStack stack = player.getItemInHand(hand);
			if(!(stack.getItem() instanceof IGBeltItem item)||!item.placesLines())
				continue;
			BlockPlaceContext context = item.updatePlacementContext(new BlockPlaceContext(player, hand, stack, hit));
			if(context==null||!context.canPlace())
				return;
			Optional<BeltLines.Line> line = item.line(player, context);
			line.ifPresent(found -> render(event, minecraft, found));
			return;
		}
	}

	private static double height(BeltLines.Line line, double u)
	{
		double centre = u+0.5;
		int step = (int)Math.floor(centre);
		double t = centre-step;
		int next = Math.min(line.distance(), step+1);
		return line.height(step)+(line.height(next)-line.height(step))*t;
	}

	private static void render(RenderLevelStageEvent event, Minecraft minecraft, BeltLines.Line line)
	{
		PoseStack poseStack = event.getPoseStack();
		Vec3 camera = event.getCamera().getPosition();
		poseStack.pushPose();
		poseStack.translate(-camera.x, -camera.y, -camera.z);
		MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
		VertexConsumer consumer = buffers.getBuffer(RenderType.debugQuads());
		Vec3 start = Vec3.atBottomCenterOf(line.anchor()).relative(line.facing(), 0.5);
		float y = line.anchor().getY()+GuideStrip.LIFT;
		new GuideStrip(consumer, poseStack.last().pose(), start, line.distance(), line.facing(), y,
				u -> (int)Math.floor(u)+1 <= line.buildable()?GuideStrip.AT_MAX: GuideStrip.TOO_FAR, u -> height(line, u)).draw();
		poseStack.popPose();
		buffers.endBatch(RenderType.debugQuads());
	}
}
