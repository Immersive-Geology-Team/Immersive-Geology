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
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.TargetBlockState;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class IGWorldGenerationProvider
{
	public static List<DataProvider> makeProviders(
			PackOutput output, CompletableFuture<Provider> vanillaRegistries, ExistingFileHelper exFiles
	)
	{
		final Map<MineralEntry, FeatureRegistration> oreFeatures = new HashMap<>();
		for(MineralEntry type : MineralEntry.VALUES)
		{
			final FeatureRegistration typeReg = new FeatureRegistration(IGLib.rl(type.getName()));
			IGLib.IG_LOGGER.info("Name of Registration: {}", type.getName());
			oreFeatures.put(type, typeReg);
		}

		final Registrations registrations = new Registrations(
				oreFeatures,
				new FeatureRegistration(IGLib.rl("mineral_veins"), null)
		);
		final RegistrySetBuilder registryBuilder = new RegistrySetBuilder();
		registryBuilder.add(Registries.CONFIGURED_FEATURE, ctx -> bootstrapConfiguredFeatures(ctx, registrations));
		registryBuilder.add(Registries.PLACED_FEATURE, ctx -> bootstrapPlacedFeatures(ctx, registrations));
		return List.of(
				new DatapackBuiltinEntriesProvider(output, vanillaRegistries, registryBuilder, Set.of(IGLib.MODID))
		);
	}

	private static void bootstrapConfiguredFeatures(
			BootstapContext<ConfiguredFeature<?, ?>> ctx, Registrations registrations
	)
	{
		final TagMatchTest replaceDeepslate = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
		final TagMatchTest replaceStone = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
		for(final Entry<MineralEntry, FeatureRegistration> entry : registrations.oreFeatures.entrySet())
		{
			final MineralEnum mineral = entry.getKey().getMineral();
			final OreRichness richness = entry.getKey().getRichness();
			final StoneEnum stone = entry.getKey().getStone();

			List<TargetBlockState> targetList = ImmutableList.of(
					OreConfiguration.target(replaceStone, mineral.getOreBlock(stone, richness).defaultBlockState()),
					OreConfiguration.target(replaceDeepslate, mineral.getOreBlock(stone, richness).defaultBlockState())
			);

			entry.getValue().registerConfigured(
					ctx, new ConfiguredFeature<>(IGWorldGen.IG_CONFIG_ORE.get(), new IGOreFeatureConfig(targetList, entry.getKey()))
			);

		}
	}

	private static void bootstrapPlacedFeatures(BootstapContext<PlacedFeature> ctx, Registrations registrations)
	{
		for(final Entry<MineralEntry, FeatureRegistration> entry : registrations.oreFeatures.entrySet())
		{
			final MineralEntry type = entry.getKey();
			final List<PlacementModifier> placements = List.of(
					HeightRangePlacement.of(new IGHeightProvider(type)),
					InSquarePlacement.spread(),
					new IGCountPlacement(type)
			);
			entry.getValue().registerPlaced(ctx, placements);
		}
		registrations.mineralVeins.registerPlaced(ctx, List.of());
	}


	private static class FeatureRegistration
	{
		public Reference<ConfiguredFeature<?, ?>> configured;
		public Reference<PlacedFeature> placed;
		public final ResourceLocation name;
		@Nullable
		public final TagKey<Biome> inBiomes;

		private FeatureRegistration(ResourceLocation name)
		{
			this(name, BiomeTags.IS_OVERWORLD);
		}

		private FeatureRegistration(ResourceLocation name, @Nullable TagKey<Biome> inBiomes)
		{
			this.name = name;
			this.inBiomes = inBiomes;
		}

		private void registerConfigured(
				BootstapContext<ConfiguredFeature<?, ?>> ctx, ConfiguredFeature<?, ?> configured
		)
		{
			this.configured = ctx.register(ResourceKey.create(Registries.CONFIGURED_FEATURE, this.name), configured);
		}

		private void registerPlaced(BootstapContext<PlacedFeature> ctx, List<PlacementModifier> placement)
		{
			this.placed = ctx.register(
					ResourceKey.create(Registries.PLACED_FEATURE, this.name), new PlacedFeature(configured, placement)
			);
		}
	}

	private record Registrations(
			List<FeatureRegistration> allFeatures,
			Map<MineralEntry, FeatureRegistration> oreFeatures,
			FeatureRegistration mineralVeins
	)
	{
		public Registrations(
				Map<MineralEntry, FeatureRegistration> oreFeatures, FeatureRegistration mineralVeins
		)
		{
			this(
					Util.make(new ArrayList<>(oreFeatures.values()), l -> l.add(mineralVeins)),
					oreFeatures,
					mineralVeins
			);
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
