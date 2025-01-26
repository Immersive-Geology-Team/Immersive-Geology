/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.misc;

import com.igteam.immersivegeology.core.material.data.types.MaterialMisc;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;

public class MaterialBlueprint extends MaterialMisc
{
	public MaterialBlueprint()
	{
		super();
		this.name = "multiblock_plans";
		addFlags(ItemCategoryFlags.BLUEPRINT);
	}

}
