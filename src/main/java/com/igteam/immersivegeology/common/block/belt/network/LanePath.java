/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt.network;

import com.igteam.immersivegeology.common.block.belt.transport.Lane;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public final class LanePath
{
	public static final double SURFACE = 4;
	private static final int ARC_SEGMENTS = 8;

	private final Vec3[] points;
	private final double[] distances;
	private final int corner;

	private LanePath(List<Vec3> points, int corner)
	{
		this.points = points.toArray(new Vec3[0]);
		this.distances = new double[this.points.length];
		for(int i = 1; i < this.points.length; i++)
			distances[i] = distances[i-1]+this.points[i].distanceTo(this.points[i-1]);
		this.corner = corner;
	}

	public double length()
	{
		return distances[distances.length-1];
	}

	public float cornerProgress()
	{
		return (float)(distances[corner]/length());
	}

	public Vec3 point(float progress)
	{
		double target = Math.max(0, Math.min(1, progress))*length();
		for(int i = 1; i < points.length; i++)
			if(target <= distances[i]||i==points.length-1)
			{
				double span = distances[i]-distances[i-1];
				double t = span <= 0?0: (target-distances[i-1])/span;
				return points[i-1].lerp(points[i], Math.max(0, Math.min(1, t)));
			}
		return points[points.length-1];
	}

	public Vec3 direction(float progress)
	{
		float step = (float)(0.5/length());
		Vec3 ahead = point(Math.min(1, progress+step)), behind = point(Math.max(0, progress-step));
		Vec3 delta = ahead.subtract(behind);
		return delta.lengthSqr() < 1e-9?new Vec3(0, 0, -1): delta.normalize();
	}

	private static LanePath of(int corner, double... coordinates)
	{
		List<Vec3> points = new ArrayList<>();
		for(int i = 0; i < coordinates.length; i += 3)
			points.add(new Vec3(coordinates[i], coordinates[i+1], coordinates[i+2]));
		return new LanePath(points, corner);
	}

	public static LanePath straight(Lane lane)
	{
		double x = lane.offset();
		return of(0, x, SURFACE, 8, x, SURFACE, -8);
	}

	public static LanePath turn(Lane lane, boolean fromLeft)
	{
		double radius = (lane==Lane.LEFT)==fromLeft?5: 11;
		double centreX = fromLeft?-8: 8;
		List<Vec3> points = new ArrayList<>();
		for(int i = 0; i <= ARC_SEGMENTS; i++)
		{
			double angle = Math.PI/2*i/ARC_SEGMENTS;
			double x = centreX+(fromLeft?1: -1)*radius*Math.sin(angle);
			points.add(new Vec3(x, SURFACE, -8+radius*Math.cos(angle)));
		}
		return new LanePath(points, 0);
	}

	public static LanePath slope(Lane lane, double fromY, double toY)
	{
		double x = lane.offset();
		return of(0, x, fromY, 8, x, toY, -8);
	}

	public static LanePath slopeThenFlat(Lane lane, double fromY, double toY, double flatFrom)
	{
		double x = lane.offset();
		return of(1, x, fromY, 8, x, toY, flatFrom, x, toY, -8);
	}

	public static LanePath flatThenSlope(Lane lane, double fromY, double toY, double slopeFrom)
	{
		double x = lane.offset();
		return of(1, x, fromY, 8, x, fromY, slopeFrom, x, toY, -8);
	}

	public static LanePath upWall(Lane lane, double bottom, double top)
	{
		double x = lane.offset();
		return of(1, x, SURFACE, 8, x, SURFACE, -4, x, bottom, -4, x, top, -4);
	}

	public static LanePath shaft(Lane lane, double top)
	{
		double x = lane.offset();
		return of(0, x, 0, -4, x, top, -4);
	}

	public static LanePath downWall(Lane lane, double top)
	{
		double x = lane.offset();
		return of(1, x, top, 4, x, SURFACE, 4, x, SURFACE, -8);
	}
}
