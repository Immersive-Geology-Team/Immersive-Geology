package com.igteam.immersivegeology.common.block.multiblocks.structure;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGContent;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class IGBloomeryStructure extends IGMultiblockStructure
{
	public static final IGBloomeryStructure INSTANCE = new IGBloomeryStructure();
	public static final String NAME = "bloomery";

	private IGBloomeryStructure()
	{
		super(NAME,
				new ResourceLocation(IGLib.MODID, "multiblocks/bloomery"),
				new BlockPos(0, 0, 0),
				new BlockPos(0, 1, 1));
	}

	@Override
	public IBlockState getPartState()
	{
		Block block = IGContent.getBlock(NAME);
		return block==null?Blocks.AIR.getDefaultState(): block.getDefaultState();
	}
}
