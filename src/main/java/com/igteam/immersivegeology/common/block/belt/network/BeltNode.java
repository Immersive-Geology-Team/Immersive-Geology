/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt.network;

import net.minecraft.core.Direction;

import javax.annotation.Nullable;

// Keeps info on the full network ensures shapes are calculated properly
public record BeltNode(Direction facing, boolean slopes, boolean acceptsBack, boolean acceptsSides, boolean joinsCoveredRun, boolean vertical, boolean tunnelEntry, @Nullable Direction tunnelSide)
{
	public BeltNode(Direction facing, boolean slopes, boolean acceptsBack, boolean acceptsSides, boolean joinsCoveredRun, boolean vertical)
	{
		this(facing, slopes, acceptsBack, acceptsSides, joinsCoveredRun, vertical, false, null);
	}

	public static BeltNode basic(Direction facing, boolean covered)
	{
		return new BeltNode(facing, true, true, true, covered, false);
	}

	public static BeltNode fixed(Direction facing)
	{
		return new BeltNode(facing, false, true, true, false, false);
	}

	public static BeltNode extraction(Direction facing)
	{
		return new BeltNode(facing, false, false, false, false, false);
	}

	public static BeltNode splitter(Direction facing)
	{
		return new BeltNode(facing, false, true, false, false, false);
	}

	public static BeltNode endergateEntry(Direction facing)
	{
		return new BeltNode(facing, false, true, true, false, false, true, facing);
	}

	public static BeltNode endergateExit(Direction facing)
	{
		return new BeltNode(facing, false, false, true, false, false, false, facing.getOpposite());
	}

	public static BeltNode riser(Direction facing, boolean covered)
	{
		return new BeltNode(facing, false, true, true, covered, true);
	}

	public Direction left()
	{
		return facing.getCounterClockWise();
	}

	public Direction right()
	{
		return facing.getClockWise();
	}
}
