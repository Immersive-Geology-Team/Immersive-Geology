/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers.RemoveFeaturesBiomeModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class IGWorldGen
{
	public static final RegistryObject<IGOreFeature> IG_CONFIG_ORE;
	private static final DeferredRegister<Feature<?>> FEATURE_REGISTER;
	private static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_REGISTER;
	private static final DeferredRegister<HeightProviderType<?>> HEIGHT_REGISTER;
	public static RegistryObject<HeightProviderType<IGHeightProvider>> IG_HEIGHT_PROVIDER;
	public static RegistryObject<PlacementModifierType<IGCountPlacement>> IG_COUNT_PLACEMENT;
	public static RegistryObject<PlacementModifierType<IGSparsePlacement>> IG_SPARSE_PLACEMENT;

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
		IG_SPARSE_PLACEMENT = PLACEMENT_REGISTER.register("ig_sparse", () -> {
			return () ->
			{
				return IGSparsePlacement.CODEC;
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
