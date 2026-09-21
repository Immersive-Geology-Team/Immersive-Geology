package com.igteam.immersivegeology.common.block.multiblocks.shim.logic;

import com.igteam.immersivegeology.common.block.multiblocks.shim.component.IMultiblockComponent;
import com.igteam.immersivegeology.common.block.multiblocks.shim.env.IInitialMultiblockContext;

public interface IMultiblockLogic<State extends IMultiblockState> extends IMultiblockComponent<State>
{
	State createInitialState(IInitialMultiblockContext<State> context);
}
