/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.generators;

import com.google.common.collect.ImmutableList;
import com.igteam.immersivegeology.common.block.IGOreBlock.OreRichness;
import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.common.world.*;
import com.igteam.immersivegeology.common.world.IGOreFeature.IGOreFeatureConfig;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.mojang.serialization.Lifecycle;
import net.minecraft.Util;
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
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.TargetBlockState;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IGWorldGenerationProvider
{
	public static List<DataProvider> makeProviders(
			PackOutput output, CompletableFuture<Provider> vanillaRegistries, ExistingFileHelper exFiles
	)
	{
		IGLib.IG_LOGGER.info("Generating Data for IGWorldGenerationProvider");
		final RegistrySetBuilder registryBuilder = new RegistrySetBuilder();

		final Map<MineralEnum, FeatureRegistration> mineral_features = new HashMap<>();
		for(MineralEnum entry : IGServerConfig.ORES.ores.keySet())
		{
			final FeatureRegistration type_registration = new FeatureRegistration(IGLib.rl(entry.getName()));
			mineral_features.put(entry, type_registration);
		}

		registryBuilder.add(Registries.CONFIGURED_FEATURE, ctx -> bootstrapConfiguredFeatures(ctx, mineral_features));
		registryBuilder.add(Registries.PLACED_FEATURE, ctx -> bootstrapPlacedFeatures(ctx, mineral_features));
		registryBuilder.add(Keys.BIOME_MODIFIERS, ctx -> bootstrapBiomeModifiers(ctx, mineral_features));

		return List.of(new DatapackBuiltinEntriesProvider(output, vanillaRegistries, registryBuilder, Set.of(IGLib.MODID)));
	}

	private static void bootstrapConfiguredFeatures(BootstapContext<ConfiguredFeature<?, ?>> ctx, Map<MineralEnum, FeatureRegistration> oreFeatures)
	{
		for(final Entry<MineralEnum, FeatureRegistration> entry : oreFeatures.entrySet())
		{
			MineralEnum data = entry.getKey();
			// Register the configured feature
			entry.getValue().registerConfigured(ctx, new ConfiguredFeature<>(IGWorldGen.IG_CONFIG_ORE.get(), new IGOreFeatureConfig(data, IGOreFeatureConfig.hash(data.name()), Optional.empty())));
		}
	}

	private static void bootstrapPlacedFeatures(BootstapContext<PlacedFeature> ctx, Map<MineralEnum, FeatureRegistration> oreFeatures) {
		// Register all placed features for the ores
		for (final Entry<MineralEnum, FeatureRegistration> entry : oreFeatures.entrySet()) {
			final MineralEnum type = entry.getKey();
			final List<PlacementModifier> placements = List.of(
					HeightRangePlacement.of(new IGHeightProvider(type)),
					InSquarePlacement.spread(),
					new IGCountPlacement(type)
			);
			// Register the placed feature
			entry.getValue().registerPlaced(ctx, placements);
		}
	}

	private static void bootstrapBiomeModifiers(BootstapContext<BiomeModifier> ctx, Map<MineralEnum, FeatureRegistration> oreFeatures) {
		final HolderGetter<Biome> biomeReg = ctx.lookup(Registries.BIOME);
		// Register all biome modifiers for the features
		for (final FeatureRegistration entry : oreFeatures.values()) {
			final HolderSet<Biome> biomes;
			if (entry.inBiomes != null) {
				biomes = biomeReg.getOrThrow(entry.inBiomes);
			} else {
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

		private FeatureRegistration(ResourceLocation name) {
			this(name, BiomeTags.IS_OVERWORLD);  // Ensure that BiomeTags.IS_OVERWORLD is not null
		}

		private FeatureRegistration(ResourceLocation name, @Nullable TagKey<Biome> inBiomes) {
			this.name = name != null ? name : new ResourceLocation("default:feature_name");
			this.inBiomes = inBiomes;
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
