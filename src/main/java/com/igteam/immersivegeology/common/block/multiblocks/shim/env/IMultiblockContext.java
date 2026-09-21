package com.igteam.immersivegeology.common.block.multiblocks.shim.env;

import com.igteam.immersivegeology.common.block.multiblocks.shim.util.MultiblockFace;
import com.igteam.immersivegeology.common.block.multiblocks.shim.util.RelativeBlockFace;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

public interface IMultiblockContext<State>
{
	State getState();

	IMultiblockLevel getLevel();

	void markMasterDirty();

	void requestMasterBESync();

	BooleanSupplier isValid();

	void setComparatorOutputFor(BlockPos posInMultiblock, int value);

	int getRedstoneInputValue(BlockPos posInMultiblock, @Nullable RelativeBlockFace face, int defaultValue);

	default int getRedstoneInputValue(MultiblockFace face, int defaultValue)
	{
		return getRedstoneInputValue(face.posInMultiblock(), face.face(), defaultValue);
	}

	default int getRedstoneInputValue(BlockPos posInMultiblock, int defaultValue)
	{
		return getRedstoneInputValue(posInMultiblock, null, defaultValue);
	}

	default void markDirtyAndSync()
	{
		markMasterDirty();
		requestMasterBESync();
	}
}
