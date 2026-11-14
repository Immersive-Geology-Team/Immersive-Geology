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
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class IGWorldGen
{
	public static final DeferredHolder<Feature<?>, IGOreFeature> IG_CONFIG_ORE;
	private static final DeferredRegister<Feature<?>> FEATURE_REGISTER;
	private static final DeferredRegister<Feature<?>> TFC_FEATURE_REGISTER; // Used for inbuilt compat to prevent crashing when TFC not loaded.
	private static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_REGISTER;
	private static final DeferredRegister<HeightProviderType<?>> HEIGHT_REGISTER;

	public static DeferredHolder<HeightProviderType<?>, HeightProviderType<IGHeightProvider>> IG_HEIGHT_PROVIDER;
	public static DeferredHolder<PlacementModifierType<?>, PlacementModifierType<IGCountPlacement>> IG_COUNT_PLACEMENT;
	public static DeferredHolder<PlacementModifierType<?>, PlacementModifierType<IGSparsePlacement>> IG_SPARSE_PLACEMENT;
	public static DeferredHolder<PlacementModifierType<?>, PlacementModifierType<IGDefaultPlacement>> IG_DEFAULT_PLACEMENT;
	public static final DeferredHolder<Feature<?>, IGEvaporateFeature> EVAPORITE_FEATURE;

	public static final TagKey<Biome> SALT_FLATS_BIOMES = TagKey.create(
			Registries.BIOME,
			ResourceLocation.fromNamespaceAndPath(IGLib.MODID, "salt_flats")
	);

	public static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS;
	public static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<IGOreRemovalModifier>> ORE_MODIFIER_CODEC;

	public static void init(IEventBus bus)
	{
		IGLib.IG_LOGGER.info("======== Registration of Immersive Geology World Generation ========");
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
		FEATURE_REGISTER = DeferredRegister.create(Registries.FEATURE, IGLib.MODID);
		TFC_FEATURE_REGISTER = DeferredRegister.create(Registries.FEATURE, "tfc");
		IG_CONFIG_ORE = FEATURE_REGISTER.register("ig_ore", IGOreFeature::new);

		if(!ModFlags.TFC.isLoaded())
		{
			TFC_FEATURE_REGISTER.register("cluster_vein", IGPlaceholderFeature::new);
			TFC_FEATURE_REGISTER.register("soil_disc", IGPlaceholderFeature::new);
		}

		BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, IGLib.MODID);

		ORE_MODIFIER_CODEC = BIOME_MODIFIER_SERIALIZERS.register("ore_removal", () -> MapCodec.unit(IGOreRemovalModifier::new));


		PLACEMENT_REGISTER = DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, IGLib.MODID);

		EVAPORITE_FEATURE = FEATURE_REGISTER.register("evaporate_pool", IGEvaporateFeature::new);

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
