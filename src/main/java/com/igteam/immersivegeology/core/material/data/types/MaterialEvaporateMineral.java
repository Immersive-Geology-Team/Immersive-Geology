/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.types;

import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;

public class MaterialEvaporateMineral extends MaterialMineral
{
	public MaterialEvaporateMineral()
	{
		super();
		addFlags(BlockCategoryFlags.EVAPORATE);
		removeMaterialFlags(ItemCategoryFlags.SLAG, ItemCategoryFlags.POWDERED_SLAG,
				ItemCategoryFlags.NORMAL_ORE, ItemCategoryFlags.POOR_ORE,ItemCategoryFlags.RICH_ORE,
				ItemCategoryFlags.DIRTY_CRUSHED_ORE, ItemCategoryFlags.CRUSHED_ORE, BlockCategoryFlags.ORE_BLOCK);
	}
}
