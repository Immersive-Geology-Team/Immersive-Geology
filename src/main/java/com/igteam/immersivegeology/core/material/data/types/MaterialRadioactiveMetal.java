/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.types;

import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;

public class MaterialRadioactiveMetal extends MaterialMetal
{
	public MaterialRadioactiveMetal()
	{
		super();
		removeMaterialFlags(ItemCategoryFlags.GEAR);
	}
	public int getPaletteVariation(ItemCategoryFlags flag){
		if(flag.equals(ItemCategoryFlags.INGOT)) return 7;
		return super.getPaletteVariation(flag);
	}

	public int heatValue()
	{
		return 2000;
	}
}
