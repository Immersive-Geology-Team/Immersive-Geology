/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.chemical;

import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialChemical;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;

import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialBrine extends MaterialChemical
{

	public MaterialBrine()
	{
		super(MineralEnum.Carnallite, MineralEnum.Rocksalt, MineralEnum.Saltpeter);
		removeMaterialFlags(BlockCategoryFlags.FLUID);
	}

	@Override
	protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
		return ((p, i) -> (0xffBCA271));
	}
}
