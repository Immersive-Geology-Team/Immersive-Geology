package com.igteam.immersivegeology.common.block.multiblocks.structure;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class IGBlockMapping
{
	private IGBlockMapping()
	{
	}

	public static ItemStack toStack(IBlockState state)
	{
		if(state==null||state.getBlock()==Blocks.AIR) return ItemStack.EMPTY;

		Block block = state.getBlock();
		Item item = Item.getItemFromBlock(block);
		if(item==Items.AIR) return ItemStack.EMPTY;
		return new ItemStack(item, 1, block.damageDropped(state));
	}
}
