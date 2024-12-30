/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.generators;

import blusunrize.immersiveengineering.common.world.IECountPlacement;
import blusunrize.immersiveengineering.common.world.IEOreFeature;
import blusunrize.immersiveengineering.common.world.IEOreFeature.IEOreFeatureConfig;
import blusunrize.immersiveengineering.data.DataGenUtils;
import com.google.gson.JsonElement;
import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.common.world.*;
import com.igteam.immersivegeology.common.world.IGOreFeature.IGOreFeatureConfig;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.client.Minecraft;
import net.minecraft.core.*;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.common.world.ForgeBiomeModifiers.AddFeaturesBiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers.RemoveFeaturesBiomeModifier;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.forgespi.locating.ForgeFeature;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.holdersets.AnyHolderSet;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class IGWorldGenerationProvider
{
	static CompletableFuture<Provider> lookup;
	public static List<DataProvider> makeProviders(
			PackOutput output, CompletableFuture<Provider> vanillaRegistries, ExistingFileHelper exFiles
	)
	{
		IGLib.IG_LOGGER.info("Generating Data for IGWorldGenerationProvider");
		final RegistrySetBuilder registryBuilder = new RegistrySetBuilder();
		List<DataProvider> providers = new ArrayList<>();
		lookup = vanillaRegistries;

		final Map<IWorldGenConfig, FeatureRegistration> mineral_features = new HashMap<>();
		for(IWorldGenConfig entry : IGServerConfig.ORES.ores.keySet())
		{
			final FeatureRegistration type_registration = new FeatureRegistration(IGLib.rl(entry.getName()), entry.getPreferredBiome());
			type_registration.setSedimentary(entry.instance().isValidStoneFormation(StoneFormation.SEDIMENTARY));
			mineral_features.put(entry, type_registration);
		}

		registryBuilder.add(Registries.CONFIGURED_FEATURE, ctx -> bootstrapConfiguredFeatures(ctx, mineral_features));
		registryBuilder.add(Registries.PLACED_FEATURE, ctx -> bootstrapPlacedFeatures(ctx, mineral_features));
		registryBuilder.add(Keys.BIOME_MODIFIERS, ctx -> bootstrapBiomeModifiers(ctx, mineral_features));
		providers.add(new DatapackBuiltinEntriesProvider(output, vanillaRegistries, registryBuilder, Set.of(IGLib.MODID)));
		return providers;
	}

	private static void bootstrapConfiguredFeatures(BootstapContext<ConfiguredFeature<?, ?>> ctx, Map<IWorldGenConfig, FeatureRegistration> oreFeatures)
	{
		for(final Entry<IWorldGenConfig, FeatureRegistration> entry : oreFeatures.entrySet())
		{
			IWorldGenConfig data = entry.getKey();
			// Register the configured feature

			entry.getValue().registerConfigured(ctx, new ConfiguredFeature<>(IGWorldGen.IG_CONFIG_ORE.get(), new IGOreFeatureConfig(data, IGOreFeatureConfig.hash(data.name()), data.getPreferredBiome())));
		}
	}

	private static void bootstrapPlacedFeatures(BootstapContext<PlacedFeature> ctx, Map<IWorldGenConfig, FeatureRegistration> oreFeatures) {
		// Register all placed features for the ores
		for (final Entry<IWorldGenConfig, FeatureRegistration> entry : oreFeatures.entrySet()) {
			final IWorldGenConfig type = entry.getKey();
			final List<PlacementModifier> placements = List.of(
					HeightRangePlacement.of(new IGHeightProvider(type)),
					type.useSparsePlacement() ? IGSparsePlacement.spread() : InSquarePlacement.spread(),
					new IGCountPlacement(type)
			);
			// Register the placed feature
			entry.getValue().registerPlaced(ctx, placements);
		}
	}

	private static void bootstrapBiomeModifiers(BootstapContext<BiomeModifier> ctx, Map<IWorldGenConfig, FeatureRegistration> addFeatures) {
		final HolderGetter<Biome> biomeReg = ctx.lookup(Registries.BIOME);
		// Register all biome modifiers for the features
		for (final FeatureRegistration entry : addFeatures.values())
		{
			final HolderSet<Biome> biomes;
			if(entry.inBiomes!=null)
			{
				biomes = biomeReg.getOrThrow(entry.inBiomes);
			}
			else
			{
				biomes = new AnyHolderSet<>(new DummyRegistryLookup<>(biomeReg, Registries.BIOME));
			}
			final AddFeaturesBiomeModifier modifier = new AddFeaturesBiomeModifier(
					biomes, HolderSet.direct(entry.placed), Decoration.UNDERGROUND_ORES
			);

			ctx.register(ResourceKey.create(Keys.BIOME_MODIFIERS, entry.name), modifier);
		}
	}

	private static class FeatureRegistration {
		public Reference<ConfiguredFeature<?, ?>> configured;
		public Reference<PlacedFeature> placed;
		public final ResourceLocation name;
		@Nullable
		public final TagKey<Biome> inBiomes;
		private boolean isSedimentaryFeature = false;
		
		private FeatureRegistration(ResourceLocation name, Optional<TagKey<Biome>> optional) {
			this.name = name != null ? name : new ResourceLocation("default:feature_name");
			this.inBiomes = null;
			IGLib.IG_LOGGER.info("Name: {}", name) ;
		}

		private void registerConfigured(BootstapContext<ConfiguredFeature<?, ?>> ctx, ConfiguredFeature<?, ?> configured) {
			if (configured != null) {
				this.configured = ctx.register(ResourceKey.create(Registries.CONFIGURED_FEATURE, this.name), configured);
			} else {
				IGLib.IG_LOGGER.info("ConfiguredFeature is null for {}",this.name);
			}
		}

		private void registerPlaced(BootstapContext<PlacedFeature> ctx, List<PlacementModifier> placement) {
			if (placement != null) {
				this.placed = ctx.register(ResourceKey.create(Registries.PLACED_FEATURE, this.name), new PlacedFeature(configured, placement));
			} else {
				IGLib.IG_LOGGER.info("PlacementModifier is null for {}",this.name);
			}
		}

		public boolean isSedimentary()
		{
			return isSedimentaryFeature;
		}

		public void setSedimentary(boolean valid)
		{
			isSedimentaryFeature = valid;
		}
	}

	private record DummyRegistryLookup<T>(
			HolderGetter<T> getter, ResourceKey<? extends Registry<? extends T>> key
	) implements RegistryLookup<T>
	{
		@Override
		public @NotNull Lifecycle registryLifecycle()
		{
			return Lifecycle.stable();
		}

		@Override
		public @NotNull Stream<Reference<T>> listElements()
		{
			return Stream.empty();
		}

		@Override
		public @NotNull Stream<Named<T>> listTags()
		{
			return Stream.empty();
		}

		@Override
		public @NotNull Optional<Reference<T>> get(@NotNull ResourceKey<T> p_255645_)
		{
			return Optional.empty();
		}

		@Override
		public @NotNull Optional<Named<T>> get(@NotNull TagKey<T> p_256283_)
		{
			return Optional.empty();
		}

		@Override
		public boolean canSerializeIn(@NotNull HolderOwner<T> p_255875_)
		{
			return true;
		}
	}
}
