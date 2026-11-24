/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt.helper;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum BeltRun implements StringRepresentable
{
	SINGLE,
	START,
	MIDDLE,
	END;

	public static BeltRun of(boolean coveredBehind, boolean coveredAhead)
	{
		if(coveredBehind&&coveredAhead)
			return MIDDLE;
		if(coveredBehind)
			return END;
		if(coveredAhead)
			return START;
		return SINGLE;
	}

	public boolean coveredBehind()
	{
		return this==MIDDLE||this==END;
	}

	public boolean coveredAhead()
	{
		return this==MIDDLE||this==START;
	}

	public BeltRun reversed()
	{
		return of(coveredAhead(), coveredBehind());
	}

	@Override
	public @NotNull String getSerializedName()
	{
		return name().toLowerCase(Locale.ROOT);
	}
}
