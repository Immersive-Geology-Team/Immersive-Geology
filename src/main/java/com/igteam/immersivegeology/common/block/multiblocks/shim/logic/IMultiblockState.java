package com.igteam.immersivegeology.common.block.multiblocks.shim.logic;

import com.igteam.immersivegeology.common.block.multiblocks.shim.env.IMultiblockContext;
import net.minecraft.nbt.NBTTagCompound;

public interface IMultiblockState
{
	void writeSaveNBT(NBTTagCompound nbt);

	void readSaveNBT(NBTTagCompound nbt);

	default void writeSyncNBT(NBTTagCompound nbt)
	{
		writeSaveNBT(nbt);
	}

	default void readSyncNBT(NBTTagCompound nbt)
	{
		readSaveNBT(nbt);
	}

	default void invalidate(IMultiblockContext<?> context)
	{
	}
}
