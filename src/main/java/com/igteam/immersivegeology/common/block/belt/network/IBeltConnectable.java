/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt.network;

import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public interface IBeltConnectable
{
	@Nullable
	BeltNode beltNode(BlockState state);
}
