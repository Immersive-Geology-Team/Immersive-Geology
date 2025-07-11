/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world;

import com.igteam.immersivegeology.common.world.features.IGEvaporateFeature;
import com.igteam.immersivegeology.common.world.features.IGOreFeature;
import com.igteam.immersivegeology.common.world.modifiers.IGOreRemovalModifier;
import com.igteam.immersivegeology.common.world.placements.IGCountPlacement;
import com.igteam.immersivegeology.common.world.placements.IGPlaceholderFeature;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IGWorldGen
{
	public static final RegistryObject<IGOreFeature> IG_CONFIG_ORE;
	private static final DeferredRegister<Feature<?>> FEATURE_REGISTER;
	private static final DeferredRegister<Feature<?>> TFC_FEATURE_REGISTER; // Used for inbuilt compat to prevent crashing when TFC not loaded.
	private static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_REGISTER;
	private static final DeferredRegister<HeightProviderType<?>> HEIGHT_REGISTER;

	public static RegistryObject<HeightProviderType<IGHeightProvider>> IG_HEIGHT_PROVIDER;
	public static RegistryObject<PlacementModifierType<IGCountPlacement>> IG_COUNT_PLACEMENT;
	public static RegistryObject<PlacementModifierType<IGSparsePlacement>> IG_SPARSE_PLACEMENT;
	public static RegistryObject<PlacementModifierType<IGDefaultPlacement>> IG_DEFAULT_PLACEMENT;
	public static final RegistryObject<Feature<BlockStateConfiguration>> EVAPORITE_FEATURE;

	public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS;
	public static final RegistryObject<Codec<IGOreRemovalModifier>> ORE_MODIFIER_CODEC;

	public static void init()
	{
		IGLib.IG_LOGGER.info("======== Registration of Immersive Geology World Generation ========");
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		IGLib.IG_LOGGER.info("- Features");
		FEATURE_REGISTER.register(bus);
		IGLib.IG_LOGGER.info("- TFC Compat Features");
		TFC_FEATURE_REGISTER.register(bus);
		IGLib.IG_LOGGER.info("- Placement");
		PLACEMENT_REGISTER.register(bus);
		IGLib.IG_LOGGER.info("- Height");
		HEIGHT_REGISTER.register(bus);
		IGLib.IG_LOGGER.info("- Biome Modifiers");
		BIOME_MODIFIER_SERIALIZERS.register(bus);
		IGLib.IG_LOGGER.info("- Structure Registration");
		IGStructureTypes.initialize(bus);
		IGLib.IG_LOGGER.info("Finished");
	}

	static
	{
		FEATURE_REGISTER = DeferredRegister.create(ForgeRegistries.FEATURES, IGLib.MODID);
		TFC_FEATURE_REGISTER = DeferredRegister.create(ForgeRegistries.FEATURES, "tfc");
		IG_CONFIG_ORE = FEATURE_REGISTER.register("ig_ore", IGOreFeature::new);

		if(!ModFlags.TFC.isLoaded())
		{
			TFC_FEATURE_REGISTER.register("cluster_vein", IGPlaceholderFeature::new);
			TFC_FEATURE_REGISTER.register("soil_disc", IGPlaceholderFeature::new);
		}

		BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, IGLib.MODID);

		ORE_MODIFIER_CODEC = BIOME_MODIFIER_SERIALIZERS.register("ore_removal", () ->Codec.unit(IGOreRemovalModifier::new));


		PLACEMENT_REGISTER = DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, IGLib.MODID);

		EVAPORITE_FEATURE = FEATURE_REGISTER.register("evaporate", () -> new IGEvaporateFeature(BlockStateConfiguration.CODEC));

		IG_COUNT_PLACEMENT = PLACEMENT_REGISTER.register("ig_count", () -> {
			return () -> {
				return IGCountPlacement.CODEC;
			};
		});

		IG_SPARSE_PLACEMENT = PLACEMENT_REGISTER.register("ig_sparse", () -> {
			return () ->
			{
				return IGSparsePlacement.PLACEMENT_CODEC;
			};
		});
		IG_DEFAULT_PLACEMENT = PLACEMENT_REGISTER.register("ig_default", () -> {
			return () ->
			{
				return IGDefaultPlacement.PLACEMENT_CODEC;
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
