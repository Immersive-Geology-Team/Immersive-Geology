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
import com.mojang.serialization.Codec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import net.minecraftforge.common.util.Lazy;

import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

public class IGHeightProvider extends HeightProvider
{
	public static final Codec<IGHeightProvider> CODEC = MineralEntry.CODEC.xmap(IGHeightProvider::new, p -> p.entry);

	private final MineralEntry entry;
	private final Lazy<HeightProvider> internalProvider;

	public IGHeightProvider(MineralEntry entry) {
		this.entry = entry;
		this.internalProvider = Lazy.of(() -> {
			Entry<MineralEntry, OreConfig> entry_matched = IGServerConfig.ORES.ores.entrySet().stream().filter((e -> e.getKey().getName().equalsIgnoreCase(entry.getName()))).findFirst().get();
			IGServerConfig.Ores.OreConfig config = entry_matched.getValue();
			VerticalAnchor vaMin = (pContext) -> {
				return (Integer)config.minY.get();
			};
			VerticalAnchor vaMax = (pContext) -> {
				return (Integer)config.maxY.get();
			};
			return TrapezoidHeight.of(vaMin, vaMax);
		});
	}

	public int sample(RandomSource random, WorldGenerationContext context) {
		return this.internalProvider.get().sample(random, context);
	}

	public HeightProviderType<?> getType() {
		return IGWorldGen.IG_HEIGHT_PROVIDER.get();
	}

}