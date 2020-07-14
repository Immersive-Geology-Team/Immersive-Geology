package com.igteam.immersivegeology.common.world.climate;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public final class ClimateCache
{
	private final Map<ChunkPos, ClimateData> backingMap = new HashMap<>();

	@Nonnull
	public ClimateData get(BlockPos pos)
	{
		return get(new ChunkPos(pos));
	}

	@Nonnull
	public ClimateData get(ChunkPos pos)
	{
		return backingMap.getOrDefault(pos, ClimateData.DEFAULT);
	}

	public void update(ChunkPos pos, float temperature, float rainfall)
	{
		backingMap.put(pos, new ClimateData(temperature, rainfall));
	}
}
