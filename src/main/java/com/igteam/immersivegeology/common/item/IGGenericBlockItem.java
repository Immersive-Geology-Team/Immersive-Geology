package com.igteam.immersivegeology.common.item;

import com.igteam.immersivegeology.common.block.IGGenericBlock;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class IGGenericBlockItem extends ItemBlock
{
	private final IGGenericBlock igBlock;

	public IGGenericBlockItem(IGGenericBlock block)
	{
		super(block);
		this.igBlock = block;
	}

	public IGGenericBlock getIGBlock()
	{
		return igBlock;
	}

	public int getColor(ItemStack stack, int tintIndex)
	{
		return igBlock.getMaterial().getColor(igBlock.getCategory(), tintIndex);
	}
}
