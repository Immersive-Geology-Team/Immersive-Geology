/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.misc;

import com.igteam.immersivegeology.core.material.data.types.MaterialMisc;
import com.igteam.immersivegeology.core.material.helper.flags.*;
import net.minecraftforge.fluids.FluidType.Properties;

import java.util.function.BiFunction;

public class MaterialMoltenSlag extends MaterialMisc
{
	public MaterialMoltenSlag()
	{
		super();
		this.name = "slag";
		removeMaterialFlags(ItemCategoryFlags.values());
		removeMaterialFlags(ModFlags.values());
		removeMaterialFlags(BlockCategoryFlags.values());
		addFlags(BlockCategoryFlags.FLUID, MaterialFlags.IS_MOLTEN_METAL);
	}

	@Override
	protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction()
	{
		return (flag, integer) -> (0xff6e5946);
	}

	@Override
	public Properties getFluidProperties(IFlagType<?> flag)
	{
		return super.getFluidProperties(flag).temperature(1700);
	}
}
