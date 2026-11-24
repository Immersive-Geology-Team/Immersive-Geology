/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.renderer;

import com.igteam.immersivegeology.common.block.belt.shape.BeltShapes;
import com.igteam.immersivegeology.common.block.belt.IGBeltBlock;
import com.igteam.immersivegeology.common.block.belt.entity.IGBeltBlockEntity;
import com.igteam.immersivegeology.common.block.belt.transport.Lane;
import com.igteam.immersivegeology.common.block.belt.transport.LaneItem;
import com.igteam.immersivegeology.common.block.belt.network.LanePath;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class IGBeltRenderer<T extends IGBeltBlockEntity> implements BlockEntityRenderer<T>
{
	private static final float ITEM_SCALE = 0.4f;
	private static final double LIFT = 1/32d;
	private static final double COPY_STEP = 0.02;
	private static final int TWO_COPIES = 2;
	private static final int THREE_COPIES = 33;

	@Override
	public void render(@NotNull T belt, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffers, int light, int overlay)
	{
		BlockState state = belt.getBlockState();
		if(!(state.getBlock() instanceof IGBeltBlock block)||belt.isEmpty())
			return;
		Direction facing = state.getValue(IGBeltBlock.FACING);
		ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
		int seed = (int)belt.getBlockPos().asLong();
		for(Lane lane : Lane.values())
		{
			LanePath path = block.lanePath(state, lane, belt.isSideLoad());
			for(LaneItem item : belt.lane(lane))
			{
				ItemStack stack = item.stack();
				if(stack.isEmpty())
					continue;
				float progress = Mth.lerp(partialTick, item.previous(), item.progress());
				if(!block.drawsItem(state, progress))
					continue;
				Vec3 centre = BeltShapes.turnPoint(path.point(progress), facing);
				Vec3 direction = BeltShapes.turnPoint(path.direction(progress), facing);
				float yaw = (float)Math.toDegrees(Math.atan2(direction.x, direction.z));
				float pitch = (float)Math.toDegrees(Math.asin(Mth.clamp(direction.y, -1, 1)));
				int copies = stack.getCount() >= THREE_COPIES?3: stack.getCount() >= TWO_COPIES?2: 1;
				for(int copy = 0; copy < copies; copy++)
				{
					poseStack.pushPose();
					poseStack.translate((centre.x+8)/16, centre.y/16+LIFT+copy*COPY_STEP, (centre.z+8)/16);
					poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
					poseStack.mulPose(Axis.XP.rotationDegrees(-pitch+90));
					poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
					itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, overlay, poseStack, buffers, belt.getLevel(), seed+copy);
					poseStack.popPose();
				}
			}
		}
	}
}
