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
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.placement.RepeatingPlacement;

import java.util.Map.Entry;

public class IGCountPlacement extends RepeatingPlacement
{
	public static final Codec<IGCountPlacement> CODEC;
	private final MineralEntry type;

	public IGCountPlacement(MineralEntry type) {
		this.type = type;
	}

	protected int count(RandomSource source, BlockPos pos) {
		Entry<MineralEntry, OreConfig> entry_matched = IGServerConfig.ORES.ores.entrySet().stream().filter((e -> e.getKey().getName().equalsIgnoreCase(type.getName()))).findFirst().get();
		IGServerConfig.Ores.OreConfig config = entry_matched.getValue();
		return config.veinsPerChunk.get();
	}

	public PlacementModifierType<?> type() {
		return IGWorldGen.IG_COUNT_PLACEMENT.get();
	}

	static {
		CODEC = MineralEntry.CODEC.xmap(IGCountPlacement::new, (p) -> {
			return p.type;
		});
	}
}
