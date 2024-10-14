/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.chemical.mantle;

import com.igteam.immersivegeology.core.material.data.types.MaterialChemical;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;

import java.util.function.Function;

public class MaterialMoltenMantle extends MaterialMetal
{
	public MaterialMoltenMantle()
	{
		super();
		removeMaterialFlags(BlockCategoryFlags.SLAB,
				ItemCategoryFlags.ROD,
				ItemCategoryFlags.WIRE,
				ItemCategoryFlags.DUST,
				ItemCategoryFlags.METAL_OXIDE,
				ItemCategoryFlags.COMPOUND_DUST,
				MaterialFlags.HAS_SLURRY, ItemCategoryFlags.INGOT,
				ItemCategoryFlags.GEAR,
				ItemCategoryFlags.PLATE,
				ItemCategoryFlags.NUGGET,
				ItemCategoryFlags.CRYSTAL);
	}

	@Override
	protected Function<IFlagType<?>, Integer> materialColorFunction() {
		return ((p) -> (p.equals(BlockCategoryFlags.FLUID) || p.equals(ItemCategoryFlags.BUCKET)) ? 0xEE5024 : 0x222222);
	}
}
