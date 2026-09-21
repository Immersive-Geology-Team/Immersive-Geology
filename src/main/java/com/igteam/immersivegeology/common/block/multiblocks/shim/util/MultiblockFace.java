package com.igteam.immersivegeology.common.block.multiblocks.shim.util;

import net.minecraft.util.math.BlockPos;

public record MultiblockFace(BlockPos posInMultiblock, RelativeBlockFace face)
{
	public MultiblockFace(int x, int y, int z, RelativeBlockFace face)
	{
		this(new BlockPos(x, y, z), face);
	}
}
