/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world;

import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.common.config.IGServerConfig.Ores.OreConfig;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.mojang.serialization.Codec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraftforge.common.util.Lazy;

import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

public class IGHeightProvider extends HeightProvider
{
	public static final Codec<IGHeightProvider> CODEC = IWorldGenConfig.CODEC.xmap(IGHeightProvider::new, p -> p.entry);

	private final IWorldGenConfig entry;
	private final Lazy<HeightProvider> internalProvider;

	public IGHeightProvider(IWorldGenConfig entry) {
		this.entry = entry;
		this.internalProvider = Lazy.of(() -> {
			IGServerConfig.Ores.OreConfig config = IGServerConfig.ORES.ores.get(entry);
			if(config != null)
			{
				VerticalAnchor vaMin = (pContext) -> {
					return (Integer)config.minY.get();
				};
				VerticalAnchor vaMax = (pContext) -> {
					return (Integer)config.maxY.get();
				};

				return UniformHeight.of(vaMin, vaMax);
			}
			IGServerConfig.Evaporates.EvaporateConfig evaporateConfig  = IGServerConfig.EVAPORITES.evaporates.get(entry);
			if(evaporateConfig != null)
			{
				VerticalAnchor vaMin = (pContext) -> {
					return (Integer)evaporateConfig.minY.get();
				};
				VerticalAnchor vaMax = (pContext) -> {
					return (Integer)evaporateConfig.maxY.get();
				};

				return UniformHeight.of(vaMin, vaMax);
			}
			IGLib.IG_LOGGER.error("Unable to find config for spawn height for spawner: {}", entry.getName());
			return UniformHeight.of(VerticalAnchor.absolute(0), VerticalAnchor.absolute(0));
		});
	}

	public int sample(RandomSource random, WorldGenerationContext context) {
		return this.internalProvider.get().sample(random, context);
	}

	public HeightProviderType<?> getType() {
		return IGWorldGen.IG_HEIGHT_PROVIDER.get();
	}

}