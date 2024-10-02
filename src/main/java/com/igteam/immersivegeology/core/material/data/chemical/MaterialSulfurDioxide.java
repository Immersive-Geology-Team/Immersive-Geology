/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.chemical;

import com.igteam.immersivegeology.core.material.data.types.MaterialChemical;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;

import java.util.function.Function;

public class MaterialSulfurDioxide extends MaterialChemical
{

	public MaterialSulfurDioxide()
	{
		super();
		removeMaterialFlags(BlockCategoryFlags.SLURRY, MaterialFlags.IS_CHEMICAL);
	}

	@Override
	protected Function<IFlagType<?>, Integer> materialColorFunction() {
		return ((p) -> (0x444444));
	}
}
