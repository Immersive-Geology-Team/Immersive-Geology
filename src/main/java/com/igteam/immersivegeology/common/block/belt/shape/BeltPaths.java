/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt.shape;

import com.igteam.immersivegeology.common.block.belt.network.LanePath;
import com.igteam.immersivegeology.common.block.belt.transport.Lane;

public final class BeltPaths
{
	private static final double RAMP_LOW = 5.65685;
	private static final double RAMP_HIGH = RAMP_LOW+16;
	private static final double RAMP_END_TOP = 20;
	private static final double RAMP_END_FLAT = -6.34315;
	private static final double RISER_TOP = 16;
	private static final double OUTLET_TOP = 20;

	private BeltPaths()
	{
	}

	public static LanePath forShape(BeltShape shape, Lane lane, boolean sideLoad)
	{
		return switch(shape)
		{
			case FROM_LEFT, TO_RISER_RIGHT_FROM_LEFT -> sideLoad?LanePath.straight(lane): LanePath.turn(lane, true);
			case FROM_RIGHT, TO_RISER_LEFT_FROM_RIGHT -> sideLoad?LanePath.straight(lane): LanePath.turn(lane, false);
			case RAMP_START -> LanePath.slope(lane, LanePath.SURFACE, RAMP_LOW);
			case RAMP -> LanePath.slope(lane, RAMP_LOW, RAMP_HIGH);
			case RAMP_END -> LanePath.slopeThenFlat(lane, RAMP_LOW, RAMP_END_TOP, RAMP_END_FLAT);
			case RAMP_START_DOWN -> LanePath.slope(lane, RAMP_LOW, LanePath.SURFACE);
			case RAMP_DOWN -> LanePath.slope(lane, RAMP_HIGH, RAMP_LOW);
			case RAMP_END_DOWN -> LanePath.flatThenSlope(lane, RAMP_END_TOP, RAMP_LOW, -RAMP_END_FLAT);
			case VALLEY_RISE -> LanePath.upWall(lane, LanePath.SURFACE, RAMP_HIGH);
			case VALLEY_RISE_END -> LanePath.upWall(lane, LanePath.SURFACE, RISER_TOP+LanePath.SURFACE);
			case VALLEY_DROP -> LanePath.downWall(lane, RAMP_HIGH);
			case VALLEY_DROP_END -> LanePath.downWall(lane, RISER_TOP+LanePath.SURFACE);
			default -> LanePath.straight(lane);
		};
	}

	public static LanePath forRiser(RiserShape shape, boolean outlet, Lane lane)
	{
		double top = outlet?OUTLET_TOP: RISER_TOP;
		return shape==RiserShape.STRAIGHT?LanePath.shaft(lane, top): LanePath.upWall(lane, LanePath.SURFACE, top);
	}
}
