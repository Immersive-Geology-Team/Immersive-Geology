package com.igteam.immersivegeology.common.block.multiblocks.shim.env;

import com.igteam.immersivegeology.common.block.multiblocks.shim.logic.IMultiblockState;
import net.minecraft.world.World;

import java.util.function.Supplier;

public interface IInitialMultiblockContext<State extends IMultiblockState>
{
	Supplier<World> levelSupplier();

	Runnable getMarkDirtyRunnable();

	Runnable getSyncRunnable();
}
