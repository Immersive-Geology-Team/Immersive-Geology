package com.igteam.immersivegeology.common.util;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

/*
 * Sourced from TerraFirmaCraft Github
 * Author: alcatrazEscapee
 */

/**
 * A no-op implementation of {@link net.minecraftforge.common.capabilities.Capability.IStorage} for capabilities that require custom serialize / deserialization logic
 *
 * @param <T> The capability class
 */
public final class NoopStorage<T> implements Capability.IStorage<T>
{
	@Nullable
	@Override
	public INBT writeNBT(Capability<T> capability, T instance, Direction side)
	{
		return null;
	}

	@Override
	public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt)
	{
	}
}