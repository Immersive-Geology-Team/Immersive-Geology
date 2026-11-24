/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt.shape;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class BeltShapes
{
	private static final double COVERED_TOP = 15.99;
	private static final VoxelShape COVERED = Shapes.box(0, 0, 0, 1, COVERED_TOP/16, 1);
	private static final double COVERED_ROOF_LIFT = 2.47;
	private static final double COLLISION_LIMIT = 32;
	private static final double BED = 4;
	private static final double RAIL = 6;
	private static final double RAIL_WIDTH = 2;
	private static final double RAMP_ENTRY = 6;
	private static final int RAMP_STEPS = 8;
	private static final double RAMP_SURFACE_LOW = 5.65685;
	private static final double RAMP_SURFACE_HIGH = RAMP_SURFACE_LOW+16;
	private static final double RAMP_END_TOP = 20;
	private static final double VALLEY_WALL = 6;
	private static final double SPLITTER_TOP = 12;
	private static final double TUNNEL_INSET = 2;
	private static final double TUNNEL_DEPTH = 6;
	private static final double TUNNEL_TOP = 12;
	private static final double GATE_WALL_DEPTH = 2;
	private static final double GANTRY_BOTTOM = 8;
	private static final double GANTRY_TOP = 15;
	private static final double GANTRY_HALF_DEPTH = 3;
	private static final double GANTRY_SHORT = 10;

	private BeltShapes()
	{
	}

	public static VoxelShape canonical(BeltShape shape, boolean covered)
	{
		if(covered)
			return switch(shape.uphill())
			{
				case RAMP_START -> roof(16+COVERED_ROOF_LIFT, COVERED_TOP);
				case RAMP, RAMP_END -> roof(32+COVERED_ROOF_LIFT, COVERED_TOP);
				default -> COVERED;
			};
		return switch(shape.uphill())
		{
			case RAMP_START -> flat(true, true, RAMP_ENTRY);
			case RAMP -> ramp(RAMP_SURFACE_HIGH);
			case RAMP_END -> ramp(RAMP_END_TOP);
			case VALLEY_RISE, VALLEY_RISE_END -> Shapes.or(flat(true, true, BED), Block.box(0, 0, 0, 16, 16, VALLEY_WALL));
			case FROM_LEFT, TO_RISER_RIGHT_FROM_LEFT -> flat(false, true, BED);
			case FROM_RIGHT, TO_RISER_LEFT_FROM_RIGHT -> flat(true, false, BED);
			case FROM_BOTH -> flat(false, false, BED);
			default -> flat(true, true, BED);
		};
	}

	private static VoxelShape flat(boolean leftRail, boolean rightRail, double bed)
	{
		VoxelShape shape = Block.box(0, 0, 0, 16, bed, 16);
		if(leftRail)
			shape = Shapes.or(shape, Block.box(0, 0, 0, RAIL_WIDTH, RAIL, 16));
		if(rightRail)
			shape = Shapes.or(shape, Block.box(16-RAIL_WIDTH, 0, 0, 16, RAIL, 16));
		return shape;
	}

	private static VoxelShape ramp(double maxTop)
	{
		VoxelShape shape = Shapes.empty();
		double depth = 16.0/RAMP_STEPS;
		for(int i = 0; i < RAMP_STEPS; i++)
		{
			double north = i*depth;
			double top = Math.min(maxTop, RAMP_SURFACE_LOW+16-north);
			shape = Shapes.or(shape, Block.box(0, 0, north, 16, top, north+depth));
		}
		return shape;
	}

	private static VoxelShape roof(double peak, double floor)
	{
		VoxelShape shape = Shapes.empty();
		double depth = 16.0/RAMP_STEPS;
		for(int i = 0; i < RAMP_STEPS; i++)
		{
			double north = i*depth;
			double top = Math.max(floor, Math.min(COLLISION_LIMIT, peak-north));
			shape = Shapes.or(shape, Block.box(0, 0, north, 16, top, north+depth));
		}
		return shape.optimize();
	}

	public static VoxelShape riser(RiserShape shape, boolean covered, Direction facing)
	{
		if(covered)
			return COVERED;
		VoxelShape wall = Block.box(0, 0, 0, 16, 16, VALLEY_WALL);
		VoxelShape canonical = shape==RiserShape.STRAIGHT?wall: Shapes.or(wall, flat(shape!=RiserShape.FROM_LEFT&&shape!=RiserShape.FROM_BOTH, shape!=RiserShape.FROM_RIGHT&&shape!=RiserShape.FROM_BOTH, BED));
		return turn(canonical, quarterTurns(facing));
	}

	public static VoxelShape poweredCollision(BeltShape shape, boolean covered, Direction facing)
	{
		if(covered)
			return COVERED;
		return turn(flat(shape!=BeltShape.FROM_LEFT, shape!=BeltShape.FROM_RIGHT, BED), quarterTurns(facing));
	}

	public static VoxelShape poweredSelection(BeltShape shape, boolean covered, Direction facing)
	{
		if(covered)
			return COVERED;
		double minX = shape==BeltShape.FROM_LEFT?GANTRY_SHORT: 0;
		double maxX = shape==BeltShape.FROM_RIGHT?16-GANTRY_SHORT: 16;
		VoxelShape gantry = Block.box(minX, GANTRY_BOTTOM, 8-GANTRY_HALF_DEPTH, maxX, GANTRY_TOP, 8+GANTRY_HALF_DEPTH);
		return turn(Shapes.or(flat(shape!=BeltShape.FROM_LEFT, shape!=BeltShape.FROM_RIGHT, BED), gantry), quarterTurns(facing));
	}

	public static VoxelShape splitter(boolean covered)
	{
		return covered?COVERED: Block.box(0, 0, 0, 16, SPLITTER_TOP, 16);
	}

	public static VoxelShape extraction(boolean covered, Direction facing)
	{
		if(covered)
			return COVERED;
		VoxelShape tunnel = Block.box(TUNNEL_INSET, 0, 16-TUNNEL_DEPTH, 16-TUNNEL_INSET, TUNNEL_TOP, 16);
		return turn(Shapes.or(flat(true, true, BED), tunnel), quarterTurns(facing));
	}

	public static VoxelShape endergate(boolean covered, Direction facing)
	{
		if(covered)
			return COVERED;
		VoxelShape tunnel = Block.box(TUNNEL_INSET, 0, 16-TUNNEL_DEPTH-GATE_WALL_DEPTH, 16-TUNNEL_INSET, TUNNEL_TOP, 16-GATE_WALL_DEPTH);
		VoxelShape wall = Block.box(TUNNEL_INSET, 0, 16-GATE_WALL_DEPTH, 16-TUNNEL_INSET, RAIL, 16);
		return turn(Shapes.or(flat(true, true, BED), tunnel, wall), quarterTurns(facing));
	}

	public static VoxelShape switchFace(Direction side)
	{
		return switch(side)
		{
			case WEST -> Block.box(0, 0, 0, 1, 16, 16);
			case EAST -> Block.box(15, 0, 0, 16, 16, 16);
			case NORTH -> Block.box(0, 0, 0, 16, 16, 1);
			case SOUTH -> Block.box(0, 0, 15, 16, 16, 16);
			default -> Shapes.empty();
		};
	}

	public static Vec3 turnPoint(Vec3 canonical, Direction facing)
	{
		Vec3 point = canonical;
		for(int i = 0; i < quarterTurns(facing); i++)
			point = new Vec3(-point.z, point.y, point.x);
		return point;
	}

	private static int quarterTurns(Direction facing)
	{
		return Math.floorMod(facing.get2DDataValue()-Direction.NORTH.get2DDataValue(), 4);
	}

	public static VoxelShape rotate(VoxelShape canonical, BeltShape shape, Direction facing)
	{
		int turns = quarterTurns(facing);
		if(shape.isDownhill())
			turns = (turns+2)%4;
		return turn(canonical, turns);
	}

	private static VoxelShape turn(VoxelShape canonical, int turns)
	{
		VoxelShape result = Shapes.empty();
		for(AABB box : canonical.toAabbs())
		{
			AABB turned = box;
			for(int i = 0; i < turns; i++)
				turned = new AABB(1-turned.maxZ, turned.minY, turned.minX, 1-turned.minZ, turned.maxY, turned.maxX);
			result = Shapes.or(result, Shapes.create(turned));
		}
		return result.optimize();
	}
}
