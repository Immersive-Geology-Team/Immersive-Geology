package com.igteam.immersivegeology.core.material.data.chemical;

import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialChemical;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;

import java.util.function.Function;

public class MaterialHydrochloricAcid extends MaterialChemical
{
	public MaterialHydrochloricAcid()
	{
		super(MetalEnum.Zinc, MetalEnum.Titanium, MetalEnum.Cobalt, MetalEnum.Nickel, MetalEnum.Chromium, MetalEnum.Manganese, MetalEnum.Copper, MetalEnum.Silver, MetalEnum.Neodymium, MetalEnum.Uranium);
	}
}
