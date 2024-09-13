package com.igteam.immersivegeology.core.material.data.chemical;

import com.igteam.immersivegeology.core.material.data.types.MaterialChemical;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;

import java.util.function.Function;

public class MaterialSodiumHydroxide extends MaterialChemical
{
	@Override
	protected Function<IFlagType<?>, Integer> materialColorFunction() {
		return ((p) -> (0xe3ce77));
	}
}
