package com.igteam.immersivegeology.common.block.multiblocks.structure;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGContent;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class IGBallmillStructure extends IGMultiblockStructure
{
	public static final IGBallmillStructure INSTANCE = new IGBallmillStructure();

	public static final String NAME = "ballmill";

	private IGBallmillStructure()
	{
		super(NAME,
				new ResourceLocation(IGLib.MODID, "multiblocks/ballmill"),
				new BlockPos(2, 0, 1),
				new BlockPos(4, 1, 3));
	}

	@Override
	public IBlockState getPartState()
	{
		Block block = IGContent.getBlock(NAME);
		return block==null?Blocks.AIR.getDefaultState(): block.getDefaultState();
	}
}
