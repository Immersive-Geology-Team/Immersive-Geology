/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt.helper;

import com.igteam.immersivegeology.common.block.belt.IGEndergateBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public final class EndergateLinks
{
	public static final int GUIDE_SEARCH_FACTOR = 2;

	private EndergateLinks()
	{
	}

	public enum GuideStatus
	{
		IN_RANGE,
		AT_MAX,
		TOO_FAR
	}

	public record Guide(BlockPos entry, int distance, GuideStatus status)
	{
	}

	private record Found(BlockPos pos, EndergateRole role, int distance)
	{
	}

	private static Optional<Found> nearest(BlockGetter level, BlockPos from, Direction step, Direction facing, int limit)
	{
		for(int distance = 1; distance <= limit; distance++)
		{
			BlockPos pos = from.relative(step, distance);
			BlockState state = level.getBlockState(pos);
			if(state.getBlock() instanceof IGEndergateBlock&&state.getValue(IGEndergateBlock.FACING)==facing)
				return Optional.of(new Found(pos, state.getValue(IGEndergateBlock.ROLE), distance));
		}
		return Optional.empty();
	}

	public static EndergateRole roleFor(BlockGetter level, BlockPos pos, Direction facing, int range)
	{
		return nearest(level, pos, facing.getOpposite(), facing, range)
				.filter(found -> found.role()==EndergateRole.ENTRY)
				.map(found -> EndergateRole.EXIT)
				.orElse(EndergateRole.ENTRY);
	}

	public static Optional<BlockPos> partner(BlockGetter level, BlockPos pos, BlockState state, int range)
	{
		Direction facing = state.getValue(IGEndergateBlock.FACING);
		boolean entry = state.getValue(IGEndergateBlock.ROLE)==EndergateRole.ENTRY;
		Direction step = entry?facing: facing.getOpposite();
		EndergateRole wanted = entry?EndergateRole.EXIT: EndergateRole.ENTRY;
		return nearest(level, pos, step, facing, range).filter(found -> found.role()==wanted).map(Found::pos);
	}

	public static Optional<Guide> guide(BlockGetter level, BlockPos target, Direction facing, int range)
	{
		return nearest(level, target, facing.getOpposite(), facing, range*GUIDE_SEARCH_FACTOR)
				.filter(found -> found.role()==EndergateRole.ENTRY)
				.map(found -> new Guide(found.pos(), found.distance(), status(found.distance(), range)));
	}

	public static final String LINK_TAG = "immersivegeology_endergate_link";
	private static final String LINK_POS = "pos";
	private static final String LINK_FACING = "facing";
	private static final String LINK_DIMENSION = "dimension";

	public record Lock(BlockPos entry, Direction facing, int distance)
	{
	}

	public static void remember(ItemStack stack, Level level, BlockPos entry, Direction facing)
	{
		CompoundTag link = new CompoundTag();
		link.putLong(LINK_POS, entry.asLong());
		link.putInt(LINK_FACING, facing.get2DDataValue());
		link.putString(LINK_DIMENSION, level.dimension().location().toString());
		stack.getOrCreateTag().put(LINK_TAG, link);
	}

	public static void forget(ItemStack stack)
	{
		if(stack.hasTag())
		{
			stack.getTag().remove(LINK_TAG);
			if(stack.getTag().isEmpty())
				stack.setTag(null);
		}
	}

	public static Optional<Lock> lock(Level level, ItemStack stack, BlockPos target, int range)
	{
		if(!stack.hasTag()||!stack.getTag().contains(LINK_TAG))
			return Optional.empty();
		CompoundTag link = stack.getTag().getCompound(LINK_TAG);
		if(!level.dimension().location().toString().equals(link.getString(LINK_DIMENSION)))
			return Optional.empty();
		BlockPos entry = BlockPos.of(link.getLong(LINK_POS));
		Direction facing = Direction.from2DDataValue(link.getInt(LINK_FACING));
		BlockState state = level.getBlockState(entry);
		if(!(state.getBlock() instanceof IGEndergateBlock)||state.getValue(IGEndergateBlock.FACING)!=facing||state.getValue(IGEndergateBlock.ROLE)!=EndergateRole.ENTRY)
			return Optional.empty();
		if(partner(level, entry, state, range).isPresent())
			return Optional.empty();
		BlockPos offset = target.subtract(entry);
		int distance = offset.getX()*facing.getStepX()+offset.getZ()*facing.getStepZ();
		boolean onLine = offset.getY()==0&&distance >= 1&&offset.equals(BlockPos.ZERO.relative(facing, distance));
		if(!onLine)
			return Optional.empty();
		return Optional.of(new Lock(entry, facing, distance));
	}

	public static GuideStatus status(int distance, int range)
	{
		if(distance > range)
			return GuideStatus.TOO_FAR;
		return distance==range?GuideStatus.AT_MAX: GuideStatus.IN_RANGE;
	}
}
