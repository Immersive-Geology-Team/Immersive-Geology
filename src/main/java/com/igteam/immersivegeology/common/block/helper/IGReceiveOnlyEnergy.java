/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.helper;

import net.minecraftforge.energy.IEnergyStorage;

public class IGReceiveOnlyEnergy implements IEnergyStorage
{
	private final IEnergyStorage delegate;

	private IGReceiveOnlyEnergy(IEnergyStorage delegate)
	{
		this.delegate = delegate;
	}

	public static IEnergyStorage of(IEnergyStorage delegate)
	{
		return new IGReceiveOnlyEnergy(delegate);
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
		if(maxReceive <= 0) return 0;
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
		return delegate.canReceive();
	}
}
