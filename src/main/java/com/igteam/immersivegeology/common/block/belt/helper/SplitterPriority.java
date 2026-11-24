/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt.helper;

import java.util.Locale;

public enum SplitterPriority
{
	LEFT,
	NONE,
	RIGHT;

	public static SplitterPriority byIndex(int index)
	{
		SplitterPriority[] values = values();
		return index >= 0&&index < values.length?values[index]: NONE;
	}

	public String translationKey()
	{
		return "gui.immersivegeology.splitter.priority."+name().toLowerCase(Locale.ROOT);
	}
}
