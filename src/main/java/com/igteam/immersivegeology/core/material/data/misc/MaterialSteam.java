/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.misc;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.types.MaterialMisc;
import com.igteam.immersivegeology.core.material.helper.flags.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraftforge.fluids.FluidType.Properties;

import java.util.function.BiFunction;

public class MaterialSteam extends MaterialMisc
{
	public MaterialSteam()
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
		return (flag, integer) -> (0x77ffffff);
	}

	@Override
	public Properties getFluidProperties(IFlagType<?> flag)
	{
		// Temperature is in Kelvin, this steam is "Low Pressure"
		return super.getFluidProperties(flag).temperature(500);
	}
}
