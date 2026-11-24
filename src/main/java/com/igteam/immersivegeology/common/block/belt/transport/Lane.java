/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt.transport;

import net.minecraft.core.Direction;

public enum Lane
{
	LEFT(-3),
	RIGHT(3);

	private final double offset;

	Lane(double offset)
	{
		this.offset = offset;
	}

	public double offset()
	{
		return offset;
	}

	public Direction side(Direction facing)
	{
		return this==LEFT?facing.getCounterClockWise(): facing.getClockWise();
	}

	public static Lane onSide(Direction facing, Direction side, Lane fallback)
	{
		if(side==facing.getCounterClockWise())
			return LEFT;
		if(side==facing.getClockWise())
			return RIGHT;
		return fallback;
	}
}
