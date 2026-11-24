/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt.helper;

import com.igteam.immersivegeology.common.block.belt.network.BeltNode;
import com.igteam.immersivegeology.common.block.belt.network.IBeltConnectable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class LevelBeltView implements BeltView
{
	private final BlockGetter level;
	@Nullable
	private final BlockPos overridePos;
	@Nullable
	private final BlockState overrideState;

	public LevelBeltView(BlockGetter level)
	{
		this(level, null, null);
	}

	public LevelBeltView(BlockGetter level, @Nullable BlockPos overridePos, @Nullable BlockState overrideState)
	{
		this.level = level;
		this.overridePos = overridePos;
		this.overrideState = overrideState;
	}

	private BlockState state(BlockPos pos)
	{
		if(overridePos!=null&&overridePos.equals(pos))
			return overrideState;
		return level.getBlockState(pos);
	}

	@Override
	@Nullable
	public BeltNode at(BlockPos pos)
	{
		BlockState state = state(pos);
		if(state.getBlock() instanceof IBeltConnectable connectable)
			return connectable.beltNode(state);
		return null;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isOpen(BlockPos pos)
	{
		BlockState state = state(pos);
		return state.isAir()||state.canBeReplaced();
	}
}
