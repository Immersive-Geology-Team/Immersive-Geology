package com.igteam.immersivegeology.common.block.multiblocks.shim.component;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.IEnergyStorage;

public class AveragingEnergyStorage implements IEnergyStorage
{
	private final int capacity;
	private final int maxTransfer;

	private int stored;
	private int averageInserted;
	private int insertedThisTick;

	public AveragingEnergyStorage(int capacity)
	{
		this(capacity, capacity);
	}

	public AveragingEnergyStorage(int capacity, int maxTransfer)
	{
		this.capacity = capacity;
		this.maxTransfer = maxTransfer;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
		int accepted = Math.min(capacity-stored, Math.min(maxTransfer, maxReceive));
		if(!simulate)
		{
			stored += accepted;
			insertedThisTick += accepted;
		}
		return accepted;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate)
	{
		int extracted = Math.min(stored, Math.min(maxTransfer, maxExtract));
		if(!simulate) stored -= extracted;
		return extracted;
	}

	public int consumeEnergy(int amount)
	{
		int consumed = Math.min(stored, amount);
		stored -= consumed;
		return consumed;
	}

	@Override
	public int getEnergyStored()
	{
		return stored;
	}

	@Override
	public int getMaxEnergyStored()
	{
		return capacity;
	}

	@Override
	public boolean canExtract()
	{
		return true;
	}

	@Override
	public boolean canReceive()
	{
		return true;
	}

	public int getAverageInsertion()
	{
		return averageInserted;
	}

	public void tick()
	{
		averageInserted = (averageInserted*3+insertedThisTick)/4;
		insertedThisTick = 0;
	}

	public void writeNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("energy", stored);
	}

	public void readNBT(NBTTagCompound nbt)
	{
		this.stored = Math.min(capacity, nbt.getInteger("energy"));
	}
}
