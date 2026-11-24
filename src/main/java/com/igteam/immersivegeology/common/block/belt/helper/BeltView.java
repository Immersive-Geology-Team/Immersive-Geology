/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt.helper;

import com.igteam.immersivegeology.common.block.belt.network.BeltNode;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

public interface BeltView
{
	@Nullable
	BeltNode at(BlockPos pos);

	boolean isOpen(BlockPos pos);
}
