/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt.helper;

import com.igteam.immersivegeology.common.block.belt.*;
import com.igteam.immersivegeology.common.block.belt.entity.IGBeltBlockEntity;
import com.igteam.immersivegeology.common.block.belt.network.LanePath;
import com.igteam.immersivegeology.common.block.belt.transport.BeltTransport;
import com.igteam.immersivegeology.common.block.belt.transport.Lane;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public record BeltInsertHandler(IGBeltBlockEntity belt, @Nullable Direction side) implements IItemHandler
{
	@Override
	public int getSlots()
	{
		return 1;
	}

	@Override
	public @NotNull ItemStack getStackInSlot(int slot)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
	{
		BlockState state = belt.getBlockState();
		if(!(state.getBlock() instanceof IGBeltBlock block)||belt.getLevel()==null||belt.getLevel().isClientSide&&!simulate)
			return stack;
		Direction facing = state.getValue(IGBeltBlock.FACING);
		if(side==null||side==Direction.UP||side==Direction.DOWN||side==facing.getOpposite())
			return belt.insertAnyLane(block, state, 0, stack, simulate);
		if(side==facing)
			return stack;
		Lane lane = Lane.onSide(facing, side, Lane.LEFT);
		LanePath path = block.lanePath(state, lane, belt.isSideLoad());
		return belt.insert(lane, BeltTransport.SIDE_ENTRY, stack, path, simulate);
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot)
	{
		return 64;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack)
	{
		return true;
	}
}
