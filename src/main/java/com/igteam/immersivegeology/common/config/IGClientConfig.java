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
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(value = Dist.CLIENT, modid = IGLib.MODID, bus = Bus.MOD)
public class IGClientConfig
{
	public final static DoubleValue multiblockSpecialRenderDistanceModifier;
	public final static BooleanValue doSpecialRenderGravitySeparator;
	public final static BooleanValue doSpecialRenderCoreDrill;
	public final static BooleanValue doSpecialRenderPelletizer;
	public final static BooleanValue doSpecialRenderChemicalReactor;
	public final static BooleanValue doSpecialRenderRotaryKiln;
	public final static BooleanValue doSpecialRenderSteamTurbine;
	public final static BooleanValue doSpecialRenderAlternator;
	public final static BooleanValue doSpecialRenderCentrifuge;
	public final static BooleanValue doSpecialRenderBallmill;

	public static final ForgeConfigSpec CONFIG_SPEC;

	static
	{
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		multiblockSpecialRenderDistanceModifier = builder.comment("This modifies the distance a special multiblock renderer is visible from Default is 2.5").defineInRange("multiblockSpecialRenderDistanceModifier", 2.5, 0, Double.MAX_VALUE);

		doSpecialRenderGravitySeparator = builder
				.comment("This controls if the animations and special client rendering applies to the Gravity Separator")
				.define("gravity_separator_renderer", true);

		doSpecialRenderCoreDrill = builder
				.comment("This controls if the animations and special client rendering applies to the Core Drill")
				.define("core_dill_renderer", true);

		doSpecialRenderPelletizer = builder
				.comment("This controls if the animations and special client rendering applies to the Pelletizer")
				.define("pelletizer_renderer", true);

		doSpecialRenderSteamTurbine = builder
				.comment("This controls if the animations and special client rendering applies to the Steam Turbine")
				.define("steam_turbine_renderer", true);

		doSpecialRenderAlternator = builder
				.comment("This controls if the animations and special client rendering applies to the Alternator")
				.define("alternator_renderer", true);

		doSpecialRenderChemicalReactor = builder
				.comment("This controls if the animations and special client rendering applies to the Chemical Reactor")
				.define("chemical_reactor_renderer", true);

		doSpecialRenderRotaryKiln = builder
				.comment("This controls if the animations and special client rendering applies to the Rotary Kiln")
				.define("rotary_kiln_renderer", true);

		doSpecialRenderCentrifuge = builder
				.comment("This controls if the animations and special client rendering applies to the Centrifuge")
				.define("centrifuge_renderer", true);

		doSpecialRenderBallmill = builder
				.comment("This controls if the animations and special client rendering applies to the Ballmill")
				.define("ballmill_renderer", true);

		CONFIG_SPEC = builder.build();
	}
}
