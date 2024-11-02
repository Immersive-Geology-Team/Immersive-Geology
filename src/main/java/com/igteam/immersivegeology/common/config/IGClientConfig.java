/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.config;

import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(value = Dist.CLIENT, modid = IGLib.MODID, bus = Bus.MOD)
public class IGClientConfig
{
	public final static DoubleValue multiblockSpecialRenderDistanceModifier;

	public static final ForgeConfigSpec CONFIG_SPEC;

	static
	{
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		multiblockSpecialRenderDistanceModifier = builder.comment("This modifies the distance a special multiblock renderer is visible from Default is 2.5").defineInRange("multiblockSpecialRenderDistanceModifier", 2.5, 0, Double.MAX_VALUE);

		CONFIG_SPEC = builder.build();
	}
}
