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
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.placement.RepeatingPlacement;

public class IGCountPlacement extends RepeatingPlacement
{
	public static final Codec<IGCountPlacement> CODEC;
	private final MineralEntry type;

	public IGCountPlacement(MineralEntry type) {
		this.type = type;
	}

	protected int count(RandomSource p_191913_, BlockPos p_191914_) {
		return IGServerConfig.ORES.ores.get(this.type).veinsPerChunk.get();
	}

	public PlacementModifierType<?> type() {
		return null;//IGWorldGen.IG_COUNT_PLACEMENT.get();
	}

	static {
		CODEC = MineralEntry.CODEC.xmap(IGCountPlacement::new, (p) -> {
			return p.type;
		});
	}
}
