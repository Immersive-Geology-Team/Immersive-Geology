package com.igteam.immersivegeology.common.block.multiblocks.shim.util;

import net.minecraft.util.EnumFacing;

public record MultiblockOrientation(EnumFacing front, boolean mirrored)
{
	public EnumFacing rotateHorizontal(EnumFacing relative)
	{
		EnumFacing result = relative;
		int steps = switch(front)
		{
			case NORTH -> 0;
			case EAST -> 1;
			case SOUTH -> 2;
			case WEST -> 3;
			default -> 0;
		};
		for(int i = 0; i < steps; i++) result = result.rotateY();
		return result;
	}
}
