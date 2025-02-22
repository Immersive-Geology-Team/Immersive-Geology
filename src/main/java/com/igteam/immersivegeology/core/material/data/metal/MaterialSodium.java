/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.metal;

import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;

import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialSodium extends MaterialMetal
{

	public MaterialSodium()
	{
		super();
		addFlags(ItemCategoryFlags.COMPOUND_DUST);
		removeMaterialFlags(ItemCategoryFlags.WIRE, ItemCategoryFlags.PLATE, ItemCategoryFlags.ROD, ItemCategoryFlags.GEAR);
		removeMaterialFlags(BlockCategoryFlags.SHEETMETAL_BLOCK,BlockCategoryFlags.SHEETMETAL_SLAB, BlockCategoryFlags.SHEETMETAL_STAIRS);
	}

	@Override
	protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
		return ((p, i) -> (0xd0d5db));
	}
}
