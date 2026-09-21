package com.igteam.immersivegeology.common.block.multiblocks.shim.component;

import net.minecraftforge.energy.IEnergyStorage;

public class IGReceiveOnlyEnergy implements IEnergyStorage
{
	private final IEnergyStorage delegate;

	private IGReceiveOnlyEnergy(IEnergyStorage delegate)
	{
		this.delegate = delegate;
	}

	public static IGReceiveOnlyEnergy of(IEnergyStorage delegate)
	{
		return new IGReceiveOnlyEnergy(delegate);
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
		return delegate.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate)
	{
		return 0;
	}

	@Override
	public int getEnergyStored()
	{
		return delegate.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored()
	{
		return delegate.getMaxEnergyStored();
	}

	@Override
	public boolean canExtract()
	{
		return false;
	}

	@Override
	public boolean canReceive()
	{
		return true;
	}
}
