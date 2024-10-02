/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.chemical.mantle;

import com.igteam.immersivegeology.core.material.data.types.MaterialChemical;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;

public class MaterialMoltenMantle extends MaterialChemical
{
	public MaterialMoltenMantle()
	{
		super();
		removeMaterialFlags(MaterialFlags.IS_CHEMICAL);
	}
}
