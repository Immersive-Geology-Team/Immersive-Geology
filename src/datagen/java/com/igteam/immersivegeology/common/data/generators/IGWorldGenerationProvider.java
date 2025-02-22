/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.generators;

import blusunrize.immersiveengineering.common.world.IEOreFeature;
import blusunrize.immersiveengineering.common.world.IEOreFeature.IEOreFeatureConfig;
import blusunrize.immersiveengineering.common.world.IEWorldGen;
import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.common.world.*;
import com.igteam.immersivegeology.common.world.features.IGOreFeature.IGOreFeatureConfig;
import com.igteam.immersivegeology.common.world.modifiers.IGOreRemovalModifier;
import com.igteam.immersivegeology.common.world.placements.IGCountPlacement;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.*;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers.AddFeaturesBiomeModifier;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.holdersets.AnyHolderSet;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
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

		// Mineral features (ores)
		final Map<IWorldGenConfig, FeatureRegistration> mineralFeatures = new HashMap<>();
		for(IWorldGenConfig entry : IGServerConfig.ORES.ores.keySet()) {
			final FeatureRegistration typeRegistration = new FeatureRegistration(IGLib.rl(entry.getName()), entry.getPreferredBiome());
			typeRegistration.setSedimentary(entry.instance().isValidStoneFormation(StoneFormation.SEDIMENTARY));
			mineralFeatures.put(entry, typeRegistration);
		}

		// Evaporate features
		final Map<IWorldGenConfig, FeatureRegistration> evaporiteFeatures = new HashMap<>();
		for (IWorldGenConfig entry : IGServerConfig.EVAPORITES.evaporates.keySet()) {
			final FeatureRegistration typeRegistration = new FeatureRegistration(IGLib.rl(entry.getName() + "_evaporate"), entry.getPreferredBiome());
			evaporiteFeatures.put(entry, typeRegistration);
		}

		// Register both sets of features; This doesn't feel that great, but eh.
		registerFeatureSet(registryBuilder,mineralFeatures,evaporiteFeatures);

		providers.add(new DatapackBuiltinEntriesProvider(output, vanillaRegistries, registryBuilder, Set.of(IGLib.MODID)));
		return providers;
	}

	private static void registerFeatureSet(
			RegistrySetBuilder registryBuilder,
			Map<IWorldGenConfig, FeatureRegistration> oreFeatures,
			Map<IWorldGenConfig, FeatureRegistration> evaporateFeatures
	) {
		registryBuilder.add(Registries.CONFIGURED_FEATURE, ctx -> bootstrapConfiguredFeatures(ctx, oreFeatures, evaporateFeatures));
		registryBuilder.add(Registries.PLACED_FEATURE, ctx -> bootstrapPlacedFeatures(ctx, oreFeatures, evaporateFeatures));
		registryBuilder.add(Keys.BIOME_MODIFIERS, ctx -> bootstrapBiomeModifiers(ctx, oreFeatures, evaporateFeatures));
	}

	private static void bootstrapConfiguredFeatures(BootstapContext<ConfiguredFeature<?, ?>> ctx, Map<IWorldGenConfig, FeatureRegistration> oreFeatures, Map<IWorldGenConfig, FeatureRegistration> evaporiteFeatures)
	{
		for(final Entry<IWorldGenConfig, FeatureRegistration> entry : oreFeatures.entrySet())
		{
			IWorldGenConfig data = entry.getKey();
			entry.getValue().registerConfigured(ctx, new ConfiguredFeature<>(IGWorldGen.IG_CONFIG_ORE.get(), new IGOreFeatureConfig(data, IGOreFeatureConfig.hash(data.name()), data.getMinSpawnTemp(), data.getMaxSpawnTemp(), data.getMinDownfall(), data.getMaxDownfall())));
		}

		for (Map.Entry<IWorldGenConfig, FeatureRegistration> entry : evaporiteFeatures.entrySet())
		{
			IWorldGenConfig data = entry.getKey();
			if(data.getDefaultBlockstate() == null) {
				IGLib.IG_LOGGER.error("Cannot use default block state on this world spawning material");
				continue;
			}
			// Register the configured feature using EvaporateFeature and its config
			entry.getValue().registerConfigured(ctx, new ConfiguredFeature<>(
					IGWorldGen.EVAPORITE_FEATURE.get(),
					new BlockStateConfiguration(data.getDefaultBlockstate()) // Assuming IWorldGenConfig provides block state
			));
		}
	}

	private static void bootstrapPlacedFeatures(BootstapContext<PlacedFeature> ctx, Map<IWorldGenConfig, FeatureRegistration> oreFeatures,  Map<IWorldGenConfig, FeatureRegistration> evaporateFeatures) {
		// Register all placed features for the ores
		IGLib.IG_LOGGER.info("Starting Placement Registration");
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

		for (final Entry<IWorldGenConfig, FeatureRegistration> entry : evaporateFeatures.entrySet()) {
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

	private static void bootstrapBiomeModifiers(BootstapContext<BiomeModifier> ctx, Map<IWorldGenConfig, FeatureRegistration> oreFeatures, Map<IWorldGenConfig, FeatureRegistration> evaporateFeatures) {
		final HolderGetter<Biome> biomeReg = ctx.lookup(Registries.BIOME);
		// Register all biome modifiers for the features
		for (final FeatureRegistration entry : oreFeatures.values())
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
			ResourceKey<BiomeModifier> key = ResourceKey.create(Keys.BIOME_MODIFIERS, entry.name);
			ctx.register(key, modifier);
		}
		// Register all biome modifiers for the features
		for (final FeatureRegistration entry : evaporateFeatures.values())
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
					biomes, HolderSet.direct(entry.placed), Decoration.SURFACE_STRUCTURES
			);
			ResourceKey<BiomeModifier> key = ResourceKey.create(Keys.BIOME_MODIFIERS, entry.name);
			ctx.register(key, modifier);
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
