package com.igteam.immersivegeology.common.blocks.helpers;

import net.minecraft.block.BlockState;

public interface IOverlayColor {
	public int getOverlayColor(BlockState state);
	public String getOverlayName();
}
