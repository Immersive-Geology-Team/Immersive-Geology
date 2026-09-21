package com.igteam.immersivegeology.common.block.multiblocks.shim.component;

import com.igteam.immersivegeology.common.block.multiblocks.shim.env.IMultiblockContext;

public interface IServerTickableComponent<State> extends IMultiblockComponent<State>
{
	void tickServer(IMultiblockContext<State> context);
}
