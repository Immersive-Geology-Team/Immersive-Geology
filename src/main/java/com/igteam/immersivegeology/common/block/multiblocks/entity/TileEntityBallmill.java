package com.igteam.immersivegeology.common.block.multiblocks.entity;

import com.igteam.immersivegeology.common.block.multiblocks.logic.BallmillLogic;
import com.igteam.immersivegeology.common.block.multiblocks.shim.IGMultiblockTile;
import net.minecraft.item.ItemStack;

public class TileEntityBallmill extends IGMultiblockTile<BallmillLogic.State>
{
	private static final BallmillLogic LOGIC = new BallmillLogic();

	//May adjust the IG shim here to take in the Structure itself...
	public TileEntityBallmill()
	{
		super(new int[]{4, 4, 5}, LOGIC);
	}

	@Override
	public ItemStack getOriginalBlock()
	{
		return ItemStack.EMPTY;
	}
}
