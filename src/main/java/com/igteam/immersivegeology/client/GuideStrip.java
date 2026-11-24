/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleUnaryOperator;

public record GuideStrip(VertexConsumer consumer, Matrix4f pose, Vec3 start, double length, Direction facing, float y, DoubleToIntFunction colour, DoubleUnaryOperator rise)
{
	public static final float LIFT = 0.03f;
	public static final int IN_RANGE = 0xE8C547;
	public static final int AT_MAX = 0x4FD16B;
	public static final int TOO_FAR = 0xE0473E;

	private static final double HALF_WIDTH = 0.125;
	private static final float NEAR_ALPHA = 0.85f;
	private static final float FAR_ALPHA = 0.3f;
	private static final double CHEVRON_PITCH = 0.5;
	private static final double CHEVRON_APEX = 0.3;
	private static final double CHEVRON_GAP = 0.08;
	private static final double CHEVRON_SLOPE = 1;

	public GuideStrip(VertexConsumer consumer, Matrix4f pose, Vec3 start, double length, Direction facing, float y, DoubleToIntFunction colour)
	{
		this(consumer, pose, start, length, facing, y, colour, u -> 0);
	}

	public void draw()
	{
		double u = 0;
		while(u+CHEVRON_PITCH <= length+1e-6)
		{
			int rgb = colour.applyAsInt(u+CHEVRON_PITCH/2);
			double apex = u+CHEVRON_APEX, back = HALF_WIDTH*CHEVRON_SLOPE;
			for(int side : new int[]{1, -1})
			{
				double w = HALF_WIDTH*side;
				quad(rgb, u, 0, apex, 0, apex-back, w, u, w);
				quad(rgb, apex+CHEVRON_GAP, 0, u+CHEVRON_PITCH, 0, u+CHEVRON_PITCH, w, apex+CHEVRON_GAP-back, w);
			}
			u += CHEVRON_PITCH;
		}
		if(u < length)
		{
			int rgb = colour.applyAsInt((u+length)/2);
			for(int side : new int[]{1, -1})
				quad(rgb, u, 0, length, 0, length, HALF_WIDTH*side, u, HALF_WIDTH*side);
		}
	}

	private void quad(int rgb, double u0, double v0, double u1, double v1, double u2, double v2, double u3, double v3)
	{
		point(rgb, u0, v0);
		point(rgb, u1, v1);
		point(rgb, u2, v2);
		point(rgb, u3, v3);
	}

	private void point(int rgb, double u, double v)
	{
		Direction across = facing.getClockWise();
		double x = start.x+facing.getStepX()*u+across.getStepX()*v;
		double z = start.z+facing.getStepZ()*u+across.getStepZ()*v;
		float alpha = (float)(NEAR_ALPHA+(FAR_ALPHA-NEAR_ALPHA)*Math.min(1, u/Math.max(length, 1e-6)));
		vertex(consumer, pose, x, y+rise.applyAsDouble(u), z, rgb, alpha);
	}

	public static void vertex(VertexConsumer consumer, Matrix4f pose, double x, double y, double z, int rgb, float alpha)
	{
		consumer.vertex(pose, (float)x, (float)y, (float)z).color((rgb >> 16 & 0xFF)/255f, (rgb >> 8 & 0xFF)/255f, (rgb & 0xFF)/255f, alpha).endVertex();
	}
}
