/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.chemical;

import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialChemical;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;

import java.util.function.Function;

public class MaterialSodiumHydroxide extends MaterialChemical
{
	public MaterialSodiumHydroxide()
	{
		super(MetalEnum.Unobtanium);
	}

	@Override
	protected Function<IFlagType<?>, Integer> materialColorFunction() {
		return ((p) -> (0xe3ce77));
	}
}
