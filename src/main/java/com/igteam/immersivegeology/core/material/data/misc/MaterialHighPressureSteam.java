/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.misc;

import com.igteam.immersivegeology.core.material.data.types.MaterialMisc;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;

import java.util.function.BiFunction;

public class MaterialHighPressureSteam extends MaterialMisc
{
	public MaterialHighPressureSteam()
	{
		super();
		removeMaterialFlags(ItemCategoryFlags.values());
		removeMaterialFlags(ModFlags.values());
		removeMaterialFlags(BlockCategoryFlags.values());
		addFlags(BlockCategoryFlags.FLUID);
	}

	@Override
	protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction()
	{
		return (flag, integer) -> (0x77777777);
	}
}
