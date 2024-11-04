/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world;

import blusunrize.immersiveengineering.common.world.FeatureMineralVein;
import blusunrize.immersiveengineering.common.world.IECountPlacement;
import blusunrize.immersiveengineering.common.world.IEHeightProvider;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IGWorldGen
{
	public static final RegistryObject<IGOreFeature> IG_CONFIG_ORE;
	private static final DeferredRegister<Feature<?>> FEATURE_REGISTER;
	private static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_REGISTER;
	private static final DeferredRegister<HeightProviderType<?>> HEIGHT_REGISTER;
	public static RegistryObject<HeightProviderType<IGHeightProvider>> IG_HEIGHT_PROVIDER;
	public static RegistryObject<PlacementModifierType<IGCountPlacement>> IG_COUNT_PLACEMENT;

	public static void init()
	{
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		FEATURE_REGISTER.register(bus);
		PLACEMENT_REGISTER.register(bus);
		HEIGHT_REGISTER.register(bus);
	}

	static
	{
		FEATURE_REGISTER = DeferredRegister.create(ForgeRegistries.FEATURES, IGLib.MODID);
		IG_CONFIG_ORE = FEATURE_REGISTER.register("ig_ore", IGOreFeature::new);
		PLACEMENT_REGISTER = DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, IGLib.MODID);
		IG_COUNT_PLACEMENT = PLACEMENT_REGISTER.register("ig_count", () -> {
			return () -> {
				return IGCountPlacement.CODEC;
			};
		});
		HEIGHT_REGISTER = DeferredRegister.create(Registries.HEIGHT_PROVIDER_TYPE, IGLib.MODID);
		IG_HEIGHT_PROVIDER = HEIGHT_REGISTER.register("ig_range", () -> {
			return () -> {
				return IGHeightProvider.CODEC;
			};
		});
	}
}
