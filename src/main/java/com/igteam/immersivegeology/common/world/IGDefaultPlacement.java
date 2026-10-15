/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world;

import com.igteam.immersivegeology.core.material.data.stone.IGStoneTypes;
import com.igteam.immersivegeology.core.material.helper.material.IStoneType;
import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.common.config.IGServerConfig.Ores.OreConfig;
import com.igteam.immersivegeology.common.world.features.IGOreFeature;
import com.igteam.immersivegeology.common.world.features.IGOreFeature.IGOreFeatureConfig;
import com.igteam.immersivegeology.common.world.features.IGOreFeature.Vein;
import com.igteam.immersivegeology.common.world.compat.IGTFCWorld;
import com.igteam.immersivegeology.common.world.features.helper.IGOreGenUtils;
import com.igteam.immersivegeology.common.world.placements.IGCountPlacement;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.living.MobSpawnEvent.SpawnPlacementCheck;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IGDefaultPlacement extends PlacementFilter
{
	public static final MapCodec<IGDefaultPlacement> PLACEMENT_CODEC;

	private final IWorldGenConfig entry;
	public IGDefaultPlacement(IWorldGenConfig entry) {
		this.entry = entry;
	}

	public boolean canPlaceVein(ChunkPos pos, long level_seed, OreConfig config) {
		if (config.veinSize.get() <= 0) return false;
		int chance_max = 1_000_000;
		RandomSource random = IGOreGenUtils.getReuseRandom(entry, level_seed, pos);
		return random.nextInt(chance_max) < config.generationChance.get();
	}

	public boolean canPlaceVeinEnd(ChunkPos pos, long level_seed, OreConfig config) {
		if (config.veinSize.get() <= 0) return false;
		int chance_max = 750_000;
		RandomSource random = IGOreGenUtils.getReuseRandom(entry, level_seed, pos);
		return random.nextInt(chance_max) < config.generationChance.get();
	}

	public boolean canSpawnAt(Holder<Biome> biome) {
		float biomeTemp = biome.value().getBaseTemperature();
		float biomeDownfall = biome.value().getModifiedClimateSettings().downfall();
		IGServerConfig.Ores.OreConfig config = IGServerConfig.ORES.ores.get(entry);
		if(config == null)
		{
			IGServerConfig.Evaporates.EvaporateConfig econfig = IGServerConfig.EVAPORITES.evaporates.get(entry);
			return econfig.veinsPerChunk.get() > 0;
		}
		if(!config.canSpawn.get()) return false;
		return biomeTemp >= config.min_temp.get() &&
				biomeTemp <= config.max_temp.get() &&
				biomeDownfall >= config.min_downfall.get() &&
				biomeDownfall <= config.max_downfall.get();
	}

	public PlacementModifierType<?> type() {
		return IGWorldGen.IG_DEFAULT_PLACEMENT.get();
	}

	static {
		PLACEMENT_CODEC = IWorldGenConfig.CODEC.fieldOf("value").xmap(IGDefaultPlacement::new, (p) -> {
			return p.entry;
		});
	}

	public boolean exposedPlace(long seed, WorldGenLevel level, ChunkPos chunkPos, @Nullable Graphics2D g2d)
	{
		BlockPos worldPos = chunkPos.getWorldPosition();
		OreConfig config = IGServerConfig.ORES.ores.get(entry);
		if(config == null)
		{
			return true;
		}

		Holder<Biome> biome = level.getBiome(worldPos);
		boolean isOverworld = biome.is(BiomeTags.IS_OVERWORLD);
		boolean isEnd = biome.is(BiomeTags.IS_END);
		boolean isNether = biome.is(BiomeTags.IS_NETHER);
		if(isOverworld || isNether || isEnd)
		{
			boolean possiblePlace = (isEnd ? canPlaceVeinEnd(chunkPos, seed, config) : canPlaceVein(chunkPos, seed, config)) && canSpawnAt(biome);
			if(!possiblePlace) return false;
			GeologyMaterial material = entry.instance();

			boolean canSpawnOverworld = declaredStoneHosts(material, Level.OVERWORLD.location())
					|| (tfcOverworldOverride(level) && IGTFCWorld.canHostInOverworld(material));
			boolean canSpawnNether = declaredStoneHosts(material, Level.NETHER.location());
			boolean canSpawnEnd = declaredStoneHosts(material, Level.END.location());
			RandomSource random = IGOreGenUtils.getReuseRandom(entry, level.getSeed(), chunkPos);
			Vein vein = IGOreFeature.createVein(random, config, entry);
			if((canSpawnOverworld && isOverworld) || (canSpawnNether && isNether) || (canSpawnEnd && isEnd))
			{
				int maxY = config.maxY.get();
				int minY =  config.minY.get();
				return IGOreGenUtils.isVeinWorthwhile(level, chunkPos,  maxY, minY, vein);
			}
		}
		else
		{
			boolean possiblePlace = canPlaceVein(chunkPos, seed, config) && canSpawnAt(biome);
			if(!possiblePlace) return false;
			RandomSource random = IGOreGenUtils.getReuseRandom(entry, level.getSeed(), chunkPos);
			Vein vein = IGOreFeature.createVein(random, config, entry);
			int maxY = config.maxY.get();
			int minY =  config.minY.get();
			return IGOreGenUtils.isVeinWorthwhile(level, chunkPos,  maxY, minY, vein);
		}
		return false;
	}
	@Override
	protected boolean shouldPlace(PlacementContext ctx, RandomSource rnd, BlockPos pos)
	{
		WorldGenLevel level = ctx.getLevel();
		ServerLevel serverLevel = level.getLevel();
		ResourceKey<Level> dimensionKey = serverLevel.dimension();
		ResourceLocation dimensionID = dimensionKey.location();
		Set<ResourceLocation> dimension_whitelist = getWhitelistedDimensions();
		if(dimension_whitelist.contains(dimensionID) || allowedByTFCOverworld(serverLevel))
		{
			return exposedPlace(serverLevel.getSeed(), ctx.getLevel(), new ChunkPos(pos), null);
		}
		return false;
	}

	/**
	 * Whether this material rides the TFC overworld override into a dimension its whitelist does not name. Off by
	 * config, off outside a TFC world, and off for materials TFC's rock cannot host in the first place.
	 */
	public boolean allowedByTFCOverworld(ServerLevel level)
	{
		return IGTFCWorld.isOverworld(level)
				&& IGTFCWorld.overridesDimensionWhitelist(level)
				&& IGTFCWorld.canHostInOverworld(entry.instance());
	}

	private static boolean tfcOverworldOverride(WorldGenLevel level)
	{
		return IGTFCWorld.isOverworld(level.getLevel()) && IGTFCWorld.overridesDimensionWhitelist(level.getLevel());
	}

	private static volatile Map<ResourceLocation, Set<StoneFormation>> declaredFormations;

	private static Map<ResourceLocation, Set<StoneFormation>> declaredFormations()
	{
		Map<ResourceLocation, Set<StoneFormation>> cached = declaredFormations;
		if(cached==null)
		{
			Map<ResourceLocation, Set<StoneFormation>> built = new HashMap<>();
			for(IStoneType stone : IGStoneTypes.all())
			{
				if(!stone.declaresPresence()||!stone.isStoneTypeValid()) continue;
				for(ResourceLocation dimension : stone.getDimensions())
				{
					built.computeIfAbsent(dimension, d -> EnumSet.noneOf(StoneFormation.class))
							.add(stone.instance().getStoneFormation());
				}
			}
			cached = Map.copyOf(built);
			declaredFormations = cached;
		}
		return cached;
	}

	private static boolean declaredStoneHosts(GeologyMaterial material, ResourceLocation dimension)
	{
		for(StoneFormation formation : declaredFormations().getOrDefault(dimension, Set.of()))
		{
			if(material.isValidStoneFormation(formation)) return true;
		}
		return false;
	}

	private Set<ResourceLocation> getWhitelistedDimensions()
	{
		OreConfig config = IGServerConfig.ORES.ores.get(entry);
		return config.dimension_whitelist.get().stream().map(ResourceLocation::parse).collect(Collectors.toSet());
	}
}
