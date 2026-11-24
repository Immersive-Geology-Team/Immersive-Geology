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

public enum SplitterHalf implements StringRepresentable
{
	LEFT,
	RIGHT;

	public SplitterHalf other()
	{
		return this==LEFT?RIGHT: LEFT;
	}

	@Override
	public @NotNull String getSerializedName()
	{
		return name().toLowerCase(Locale.ROOT);
	}
}
