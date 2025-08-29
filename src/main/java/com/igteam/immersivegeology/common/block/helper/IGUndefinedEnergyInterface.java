/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.helper;

import net.minecraftforge.energy.IEnergyStorage;

// A Dummy class to used when you can't check NullEnergyStorage (energy generating multiblocks use it)
public class IGUndefinedEnergyInterface implements IEnergyStorage
{
	public static IGUndefinedEnergyInterface INSTANCE = new IGUndefinedEnergyInterface();
	@Override
	public int receiveEnergy(int i, boolean b)
	{
		return 0;
	}

	@Override
	public int extractEnergy(int i, boolean b)
	{
		return 0;
	}

	@Override
	public int getEnergyStored()
	{
		return 0;
	}

	@Override
	public int getMaxEnergyStored()
	{
		return 0;
	}

	@Override
	public boolean canExtract()
	{
		return false;
	}

	@Override
	public boolean canReceive()
	{
		return false;
	}
}
