/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world;

import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.mojang.serialization.Codec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import net.minecraftforge.common.util.Lazy;

public class IGHeightProvider extends HeightProvider
{
	public static final Codec<IGHeightProvider> CODEC;
	private final MineralEntry type;
	private final Lazy<HeightProvider> internalProvider;

	public IGHeightProvider(MineralEntry type) {
		this.type = type;
		this.internalProvider = Lazy.of(() -> {
			IGServerConfig.Ores.OreConfig config = IGServerConfig.ORES.ores.get(type);
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
		return null;//IGWorldGen.IG_HEIGHT_PROVIDER.get();
	}

	static {
		CODEC = MineralEntry.CODEC.xmap(IGHeightProvider::new, (p) -> {
			return p.type;
		});
	}
}