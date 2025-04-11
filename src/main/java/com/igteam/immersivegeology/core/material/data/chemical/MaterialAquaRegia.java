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
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;

import java.util.function.BiFunction;

public class MaterialAquaRegia extends MaterialChemical
{
	public MaterialAquaRegia() {
		super(MetalEnum.Gold, MetalEnum.Platinum);
	}

	@Override
	public void setupRecipeStages()
	{
		logged_recipes.add(getName());
	}

	@Override
	protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
		return ((p, i) -> (0xfff16b02));
	}

	@Override
	public boolean hasComplexNamingScheme()
	{
		return true;
	}
}
