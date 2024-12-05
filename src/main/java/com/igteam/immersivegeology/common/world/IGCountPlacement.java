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
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.placement.RepeatingPlacement;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class IGCountPlacement extends RepeatingPlacement
{
	public static final Codec<IGCountPlacement> CODEC;
	private final IWorldGenConfig type;

	public IGCountPlacement(IWorldGenConfig type) {
		this.type = type;
	}

	protected int count(RandomSource source, BlockPos pos) {
		IGServerConfig.Ores.OreConfig config = IGServerConfig.ORES.ores.get(type);
		return config.veinsPerChunk.get();
	}

	@Override
	public Stream<BlockPos> getPositions(PlacementContext ctx, RandomSource rnd, BlockPos pos)
	{
		return IntStream.range(0, this.count(rnd, pos)).mapToObj((p_191912_) -> {
			return pos;
		});
	}

	public PlacementModifierType<?> type() {
		return IGWorldGen.IG_COUNT_PLACEMENT.get();
	}

	static {
		CODEC = IWorldGenConfig.CODEC.xmap(IGCountPlacement::new, (p) -> {
			return p.type;
		});
	}
}
