package com.igteam.immersivegeology.common.world.climate;

import java.time.Month;

import com.igteam.immersivegeology.common.world.chunk.data.ChunkData;
import com.igteam.immersivegeology.common.world.chunk.data.ChunkDataCapability;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;

public class ClimateIG {
    private static final ClimateCache CACHE = new ClimateCache();


    public static float getAvgTemp(World world, BlockPos pos)
    {
        IChunk chunk = world.getChunk(pos);
        if (chunk instanceof Chunk)
        {
            return ((Chunk) chunk).getCapability(ChunkDataCapability.CAPABILITY).map(ChunkData::getAvgTemp).orElseGet(() -> getAvgTemp(pos));
        }
        return getAvgTemp(pos);
    }

    public static float getAvgTemp(BlockPos pos)
    {
        return ClimateHelper.monthFactor(CACHE.get(pos).getRegionalTemp(), 29.5f, pos.getZ());
    }

    public static float getRainfall(World world, BlockPos pos)
    {
        IChunk chunk = world.getChunk(pos);
        if (chunk instanceof Chunk)
        {
            return ((Chunk) chunk).getCapability(ChunkDataCapability.CAPABILITY).map(ChunkData::getRainfall).orElseGet(() -> getRainfall(pos));
        }
        return getRainfall(pos);
    }

    public static float getRainfall(BlockPos pos)
    {
        return CACHE.get(pos).getRainfall();
    }

    public static void update(ChunkPos pos, float temperature, float rainfall)
    {
        CACHE.update(pos, temperature, rainfall);
    }

    private ClimateIG() {}
}
