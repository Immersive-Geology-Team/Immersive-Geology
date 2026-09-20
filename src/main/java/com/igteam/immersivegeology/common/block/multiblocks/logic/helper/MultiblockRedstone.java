/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.logic.helper;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import net.minecraft.core.BlockPos;

public class MultiblockRedstone
{
	public static BlockPos[] allPositions(int width, int height, int depth)
	{
		BlockPos[] positions = new BlockPos[width*height*depth];
		int index = 0;
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				for(int z = 0; z < depth; z++)
					positions[index++] = new BlockPos(x, y, z);
		return positions;
	}

	public static boolean hasInput(IMultiblockContext<?> context, BlockPos[] positions)
	{
		for(BlockPos pos : positions)
			if(context.getRedstoneInputValue(pos, 0) > 0) return true;
		return false;
	}
}
