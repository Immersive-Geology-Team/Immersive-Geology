package com.igteam.immersivegeology.common.world.chunk.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/*
 * Sourced from TerraFirmaCraft Github
 * Original Author: alcatrazEscapee
 * Modified for IG by Muddykat
 */

public class ChunkData implements ICapabilitySerializable<CompoundNBT>
{

	/**
	 * Helper method, since lazy optionals and instanceof checks together are ugly
	 */
	public static LazyOptional<ChunkData> get(IChunk chunk)
	{
		if(chunk instanceof Chunk)
		{
			return ((Chunk)chunk).getCapability(ChunkDataCapability.CAPABILITY);
		}
		return LazyOptional.empty();
	}

	private final LazyOptional<ChunkData> capability = LazyOptional.of(() -> this);

	private float rainfall;
	private float regionalTemp;
	private float avgTemp;

	public ChunkData()
	{
		rainfall = 250;
		regionalTemp = 0;
		avgTemp = 10;
	}

	//Original Used RockData stored in chunks to change rock layers.

	public float getRainfall()
	{
		return rainfall;
	}

	public void setRainfall(float rainfall)
	{
		this.rainfall = rainfall;
	}

	public float getRegionalTemp()
	{
		return regionalTemp;
	}

	public void setRegionalTemp(float regionalTemp)
	{
		this.regionalTemp = regionalTemp;
	}

	public float getAvgTemp()
	{
		return avgTemp;
	}

	public void setAvgTemp(float avgTemp)
	{
		this.avgTemp = avgTemp;
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
	{
		return cap==ChunkDataCapability.CAPABILITY?capability.cast(): LazyOptional.empty();
	}

	@Override
	public CompoundNBT serializeNBT()
	{
		CompoundNBT nbt = new CompoundNBT();

		nbt.putFloat("rainfall", rainfall);
		nbt.putFloat("regionalTemp", regionalTemp);
		nbt.putFloat("avgTemp", avgTemp);

		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt)
	{
		if(nbt!=null)
		{
			rainfall = nbt.getFloat("rainfall");
			regionalTemp = nbt.getFloat("regionalTemp");
			avgTemp = nbt.getFloat("avgTemp");
		}
	}
}
