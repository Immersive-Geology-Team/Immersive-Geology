package com.igteam.immersivegeology.common.block.multiblocks.structure;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGContent;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class IGRevFurnaceStructure extends IGMultiblockStructure
{
	public static final IGRevFurnaceStructure INSTANCE = new IGRevFurnaceStructure();
	public static final String NAME = "reverberation_furnace";

	private IGRevFurnaceStructure()
	{
		super(NAME,
				new ResourceLocation(IGLib.MODID, "multiblocks/reverberation_furnace"),
				new BlockPos(1, 0, 1),
				new BlockPos(1, 1, 5));
	}

	@Override
	public IBlockState getPartState()
	{
		Block block = IGContent.getBlock(NAME);
		return block==null?Blocks.AIR.getDefaultState(): block.getDefaultState();
	}
}
