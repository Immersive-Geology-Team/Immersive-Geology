/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world.features.helper;

import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.common.config.IGServerConfig.Evaporates.EvaporateConfig;
import com.igteam.immersivegeology.common.world.IWorldGenConfig;
import com.igteam.immersivegeology.common.world.features.IGOreFeature;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record IGEvaporateConfig(IWorldGenConfig entry, long seed, double temp_range_min, double temp_range_max, double downfall_min, double downfall_max) implements FeatureConfiguration
{
	public static final MapCodec<IGEvaporateConfig> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
		return instance.group(
				IWorldGenConfig.CODEC.fieldOf("entry").forGetter((c) -> c.entry),
				Codec.either(Codec.STRING, Codec.LONG)
						.xmap((e) -> e.map(IGOreFeature.IGOreFeatureConfig::hash, (l) -> l), Either::right)
						.fieldOf("random_name").forGetter((c) -> c.seed),
				Codec.DOUBLE.fieldOf("temp_range_min").forGetter((c) -> c.temp_range_min),
				Codec.DOUBLE.fieldOf("temp_range_max").forGetter((c) -> c.temp_range_max),
				Codec.DOUBLE.fieldOf("downfall_min").forGetter((c) -> c.downfall_min),
				Codec.DOUBLE.fieldOf("downfall_max").forGetter((c) -> c.downfall_max)
		).apply(instance, IGEvaporateConfig::new);
	});

	public int getSize() {
		return getConfig().veinSize.get();
	}

	private static final Map<String, Long> HASH_CACHE = new ConcurrentHashMap<>();

	public static long hash(String name) {
		return HASH_CACHE.computeIfAbsent(name, k -> {
			RandomSupport.Seed128bit seed128 = RandomSupport.seedFromHashOf(k);
			return seed128.seedLo() ^ seed128.seedHi();
		});
	}

	public int getRarity() {
		return getConfig().rarity.get();
	}

	public EvaporateConfig getConfig()
	{
		return IGServerConfig.EVAPORITES.evaporates.get(this.entry);
	}

	public IWorldGenConfig type() {
		return this.entry;
	}

	public int getChanceToGenerate()
	{
		IGServerConfig.Evaporates.EvaporateConfig config = IGServerConfig.EVAPORITES.evaporates.get(this.entry);
		return config.generationChance.get();
	}

	public double getDensity()
	{
		IGServerConfig.Evaporates.EvaporateConfig config = IGServerConfig.EVAPORITES.evaporates.get(this.entry);
		return config.density.get();
	}

	public boolean canSpawn()
	{
		IGServerConfig.Evaporates.EvaporateConfig config = IGServerConfig.EVAPORITES.evaporates.get(this.entry);
		return !config.disabled.get();
	}
}