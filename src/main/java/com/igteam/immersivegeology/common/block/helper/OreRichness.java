/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.helper;

import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import java.util.Locale;

public enum OreRichness
{
	POOR,
	NORMAL,
	RICH;

	public ItemCategoryFlags toCategory()
	{
		return this == POOR ? ItemCategoryFlags.POOR_ORE : (this == NORMAL ? ItemCategoryFlags.NORMAL_ORE : ItemCategoryFlags.RICH_ORE);
	}

	public String getSanitizedName()
	{
		return name().toLowerCase(Locale.ROOT);
	}
}
