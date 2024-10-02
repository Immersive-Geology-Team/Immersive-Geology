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

public class MaterialHydrochloricAcid extends MaterialChemical
{
	public MaterialHydrochloricAcid()
	{
		super(MetalEnum.Zinc, MetalEnum.Titanium, MetalEnum.Cobalt, MetalEnum.Nickel, MetalEnum.Chromium, MetalEnum.Manganese, MetalEnum.Copper, MetalEnum.Silver, MetalEnum.Neodymium, MetalEnum.Uranium);
	}
}
