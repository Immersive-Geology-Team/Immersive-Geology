package com.igteam.immersivegeology.common.block.multiblocks.shim.env;

import com.igteam.immersivegeology.common.block.multiblocks.shim.util.RelativeBlockFace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class IGMultiblockContext<State> implements IMultiblockContext<State>
{
	private final Supplier<State> state;
	private final Supplier<IMultiblockLevel> level;
	private final Runnable markDirty;
	private final Runnable sync;
	private final BooleanSupplier valid;
	private final Consumer<BlockPos> comparatorUpdate;

	public IGMultiblockContext(Supplier<State> state, Supplier<IMultiblockLevel> level, Runnable markDirty,
							   Runnable sync, BooleanSupplier valid, Consumer<BlockPos> comparatorUpdate)
	{
		this.state = state;
		this.level = level;
		this.markDirty = markDirty;
		this.sync = sync;
		this.valid = valid;
		this.comparatorUpdate = comparatorUpdate;
	}

	@Override
	public State getState()
	{
		return state.get();
	}

	@Override
	public IMultiblockLevel getLevel()
	{
		return level.get();
	}

	@Override
	public void markMasterDirty()
	{
		markDirty.run();
	}

	@Override
	public void requestMasterBESync()
	{
		sync.run();
	}

	@Override
	public BooleanSupplier isValid()
	{
		return valid;
	}

	@Override
	public void setComparatorOutputFor(BlockPos posInMultiblock, int value)
	{
		comparatorUpdate.accept(posInMultiblock);
	}

	@Override
	public int getRedstoneInputValue(BlockPos posInMultiblock, @Nullable RelativeBlockFace face, int defaultValue)
	{
		IMultiblockLevel mbLevel = level.get();
		if(mbLevel==null) return defaultValue;

		BlockPos absolute = mbLevel.toAbsolute(posInMultiblock);
		if(face==null) return mbLevel.getRawLevel().getRedstonePowerFromNeighbors(absolute);

		EnumFacing side = mbLevel.toAbsolute(face);
		return mbLevel.getRawLevel().getRedstonePower(absolute.offset(side), side);
	}
}
