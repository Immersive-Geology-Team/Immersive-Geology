/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world.placements;

import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.common.world.IGWorldGen;
import com.igteam.immersivegeology.common.world.IWorldGenConfig;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.placement.RepeatingPlacement;
import org.jetbrains.annotations.NotNull;

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
		IGServerConfig.Ores.OreConfig oconfig = IGServerConfig.ORES.ores.get(type);
		if(oconfig != null) return oconfig.veinsPerChunk.get();
		IGServerConfig.Evaporates.EvaporateConfig econfig = IGServerConfig.EVAPORITES.evaporates.get(type);
		return econfig.veinsPerChunk.get();
	}

	@Override
	public @NotNull Stream<BlockPos> getPositions(PlacementContext ctx, RandomSource rnd, BlockPos pos)
	{
		return IntStream.range(0, this.count(rnd, pos)).mapToObj((p_191912_) -> {
			return pos;
		});
	}

	public @NotNull PlacementModifierType<?> type() {
		return IGWorldGen.IG_COUNT_PLACEMENT.get();
	}

	static {
		CODEC = IWorldGenConfig.CODEC.xmap(IGCountPlacement::new, (p) -> {
			return p.type;
		});
	}
}
