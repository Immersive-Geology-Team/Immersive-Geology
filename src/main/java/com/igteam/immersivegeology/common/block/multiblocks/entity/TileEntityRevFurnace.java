package com.igteam.immersivegeology.common.block.multiblocks.entity;

import com.igteam.immersivegeology.common.block.multiblocks.logic.RevFurnaceLogic;
import com.igteam.immersivegeology.common.block.multiblocks.shim.IGMultiblockTile;

public class TileEntityRevFurnace extends IGMultiblockTile<RevFurnaceLogic.State>
{
	private static final RevFurnaceLogic LOGIC = new RevFurnaceLogic();

	public TileEntityRevFurnace()
	{
		super(new int[]{12, 6, 6}, LOGIC);
	}
}
