package com.igteam.immersivegeology.common.block.multiblocks.entity;

import com.igteam.immersivegeology.common.block.multiblocks.logic.BloomeryLogic;
import com.igteam.immersivegeology.common.block.multiblocks.shim.IGMultiblockTile;

public class TileEntityBloomery extends IGMultiblockTile<BloomeryLogic.State>
{
	private static final BloomeryLogic LOGIC = new BloomeryLogic();

	public TileEntityBloomery()
	{
		super(new int[]{3, 2, 2}, LOGIC);
	}
}
