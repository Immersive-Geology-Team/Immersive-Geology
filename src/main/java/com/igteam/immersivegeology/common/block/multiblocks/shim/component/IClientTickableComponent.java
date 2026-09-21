package com.igteam.immersivegeology.common.block.multiblocks.shim.component;

import com.igteam.immersivegeology.common.block.multiblocks.shim.env.IMultiblockContext;

public interface IClientTickableComponent<State> extends IMultiblockComponent<State>
{
	void tickClient(IMultiblockContext<State> context);
}
