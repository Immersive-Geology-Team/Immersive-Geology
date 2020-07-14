package com.igteam.immersivegeology.common.blocks;

import net.minecraft.item.Item;

public interface IIGBlock
{
	boolean hasFlavour();

	Item getItemBlock();

	String getNameForFlavour();
}
