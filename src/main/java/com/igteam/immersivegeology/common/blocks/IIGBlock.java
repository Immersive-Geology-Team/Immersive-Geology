package com.igteam.immersivegeology.common.blocks;

import net.minecraft.item.Item;

public interface IIGBlock
{
	boolean hasFlavour();
	public Item getItemBlock();
	String getNameForFlavour();
}
