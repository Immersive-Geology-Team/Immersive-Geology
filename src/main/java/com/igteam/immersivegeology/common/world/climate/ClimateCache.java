package com.igteam.immersivegeology.common.world.climate;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public final class ClimateCache {
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
