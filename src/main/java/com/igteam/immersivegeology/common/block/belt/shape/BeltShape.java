/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt.shape;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum BeltShape implements StringRepresentable
{
	STRAIGHT,
	FROM_LEFT,
	FROM_RIGHT,
	FROM_BOTH,
	RAMP_START,
	RAMP,
	RAMP_END,
	RAMP_START_DOWN,
	RAMP_DOWN,
	RAMP_END_DOWN,
	TO_RISER_LEFT,
	TO_RISER_RIGHT,
	VALLEY_RISE,
	VALLEY_RISE_END,
	VALLEY_DROP,
	VALLEY_DROP_END,
	TO_RISER_LEFT_FROM_RIGHT,
	TO_RISER_RIGHT_FROM_LEFT;

	public boolean isFlat()
	{
		return this==STRAIGHT||this==FROM_LEFT||this==FROM_RIGHT||this==FROM_BOTH||this==TO_RISER_LEFT||this==TO_RISER_RIGHT||this==TO_RISER_LEFT_FROM_RIGHT||this==TO_RISER_RIGHT_FROM_LEFT;
	}

	public BeltShape sideInputs()
	{
		return switch(this)
		{
			case FROM_LEFT, FROM_RIGHT, FROM_BOTH -> this;
			case TO_RISER_LEFT_FROM_RIGHT -> FROM_RIGHT;
			case TO_RISER_RIGHT_FROM_LEFT -> FROM_LEFT;
			default -> STRAIGHT;
		};
	}

	public boolean isDownhill()
	{
		return this==RAMP_START_DOWN||this==RAMP_DOWN||this==RAMP_END_DOWN||this==VALLEY_DROP||this==VALLEY_DROP_END;
	}

	public BeltShape uphill()
	{
		return switch(this)
		{
			case RAMP_START_DOWN -> RAMP_START;
			case RAMP_DOWN -> RAMP;
			case RAMP_END_DOWN -> RAMP_END;
			case VALLEY_DROP -> VALLEY_RISE;
			case VALLEY_DROP_END -> VALLEY_RISE_END;
			default -> this;
		};
	}

	@Override
	public @NotNull String getSerializedName()
	{
		return name().toLowerCase(Locale.ROOT);
	}
}
