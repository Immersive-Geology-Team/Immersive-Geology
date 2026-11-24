/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt.transport;

import com.igteam.immersivegeology.common.block.belt.*;
import com.igteam.immersivegeology.common.block.belt.entity.IGBeltBlockEntity;
import com.igteam.immersivegeology.common.block.belt.helper.LevelBeltView;
import com.igteam.immersivegeology.common.block.belt.network.BeltNode;
import com.igteam.immersivegeology.common.block.belt.shape.BeltShapeResolver;
import com.igteam.immersivegeology.common.block.belt.shape.BeltShapes;
import com.igteam.immersivegeology.common.block.belt.network.LanePath;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

public final class BeltTransport
{
	public static final double SPEED = 1;
	public static final double SPACING = 4;
	public static final float SIDE_ENTRY = 0.5f;
	public static final double BLOCKS_PER_TICK = SPEED/16;
	private static final double DROP_SPEED = 0.1;
	private static final double DROP_RISE = 0.12;
	private static final double DROP_FORWARD = 0.2;
	private static final double DROP_LIFT = 0.15;

	private BeltTransport()
	{
	}

	public static void tick(Level level, BlockPos pos, BlockState state, IGBeltBlockEntity belt)
	{
		if(!(state.getBlock() instanceof IGBeltBlock block))
			return;
		if(!level.isClientSide)
			block.serverTick(level, pos, state, belt);
		if(belt.isEmpty())
			return;
		boolean moving = block.isMoving(state);
		for(Lane lane : Lane.values())
		{
			List<LaneItem> items = belt.lane(lane);
			if(items.isEmpty())
				continue;
			if(!moving)
			{
				items.forEach(LaneItem::hold);
				continue;
			}
			LanePath path = block.lanePath(state, lane, belt.isSideLoad());
			double step = SPEED/path.length();
			double gap = SPACING/path.length();
			double limit = 1;
			for(LaneItem item : items)
			{
				float before = item.progress();
				float after = (float)Math.max(before, Math.min(before+step, Math.min(limit, block.holdPoint(state, item))));
				item.advanceTo(after);
				if(!level.isClientSide)
					block.onItemMoved(level, pos, state, belt, lane, item, before);
				limit = item.progress()-gap;
			}
			if(!level.isClientSide)
			{
				if(items.removeIf(item -> item.stack().isEmpty()))
					belt.markLanesChanged();
				handOff(level, pos, state, block, belt, lane, items);
			}
		}
		if(!level.isClientSide)
			belt.flushSync();
	}

	private static void handOff(Level level, BlockPos pos, BlockState state, IGBeltBlock block, IGBeltBlockEntity belt, Lane lane, List<LaneItem> items)
	{
		Iterator<LaneItem> iterator = items.iterator();
		while(iterator.hasNext())
		{
			LaneItem item = iterator.next();
			if(item.stack().isEmpty())
			{
				iterator.remove();
				belt.markLanesChanged();
				continue;
			}
			if(item.progress() < 1)
				return;
			ItemStack left = block.deliver(level, pos, state, lane, item.stack(), false);
			if(left.getCount()!=item.stack().getCount())
			{
				belt.markLanesChanged();
				if(left.isEmpty())
				{
					iterator.remove();
					continue;
				}
				item.setStack(left);
			}
			return;
		}
	}

	public static ItemStack deliver(Level level, BlockPos pos, BlockState state, IGBeltBlock block, Lane lane, ItemStack stack, boolean simulate)
	{
		BeltNode node = block.beltNode(state);
		if(node==null)
			return stack;
		LevelBeltView view = new LevelBeltView(level);
		BlockPos target = BeltShapeResolver.outputTarget(view, pos);
		if(target.equals(pos))
			return stack;
		Direction worldSide = lane.side(node.facing());
		BlockState targetState = level.getBlockState(target);
		if(targetState.getBlock() instanceof IGBeltBlock targetBlock&&level.getBlockEntity(target) instanceof IGBeltBlockEntity targetBelt)
			return intoBelt(targetState, targetBlock, targetBelt, target, pos, lane, worldSide, stack, simulate);
		boolean sideLoad = level.getBlockEntity(pos) instanceof IGBeltBlockEntity belt&&belt.isSideLoad();
		LanePath path = block.lanePath(state, lane, sideLoad);
		Vec3 end = BeltShapes.turnPoint(path.point(1), node.facing());
		Vec3 exit = new Vec3(pos.getX()+(end.x+8)/16, pos.getY()+end.y/16, pos.getZ()+(end.z+8)/16)
				.add(node.facing().getStepX()*DROP_FORWARD, DROP_LIFT, node.facing().getStepZ()*DROP_FORWARD);
		Vec3 motion = new Vec3(node.facing().getStepX()*DROP_SPEED, node.vertical()?DROP_RISE: 0, node.facing().getStepZ()*DROP_SPEED);
		return intoWorld(level, node.facing(), target, targetState, stack, simulate, exit, motion);
	}

	@Nullable
	private static Direction horizontalFrom(BlockPos target, BlockPos from)
	{
		int dx = from.getX()-target.getX(), dz = from.getZ()-target.getZ();
		if(dx==0&&dz==0)
			return null;
		return Direction.fromDelta(Integer.signum(dx), 0, Integer.signum(dz));
	}

	private static ItemStack intoBelt(BlockState state, IGBeltBlock block, IGBeltBlockEntity belt, BlockPos target, BlockPos from, Lane sourceLane, Direction worldSide, ItemStack stack, boolean simulate)
	{
		BeltNode node = block.beltNode(state);
		if(node==null)
			return stack;
		Direction facing = node.facing();
		Direction side = horizontalFrom(target, from);
		Lane lane;
		float entry;
		if(side==null||side==facing.getOpposite())
		{
			if(side!=null&&!block.acceptsItemsFrom(state, side))
				return stack;
			lane = Lane.onSide(facing, worldSide, sourceLane);
			entry = 0;
		}
		else if(side==node.left()||side==node.right())
		{
			if(!block.acceptsItemsFrom(state, side)||!block.acceptsSideLane(state, side, worldSide))
				return stack;
			Lane near = side==node.left()?Lane.LEFT: Lane.RIGHT;
			if(node.vertical())
			{
				lane = near;
				entry = block.lanePath(state, lane, belt.isSideLoad()).cornerProgress();
			}
			else if(belt.isSideLoad()||block.takesBothSides(state))
			{
				lane = near;
				entry = block.sideEntry(state);
			}
			else
			{
				lane = sourceLane;
				entry = 0;
			}
		}
		else
			return stack;
		return belt.insert(lane, entry, stack, block.lanePath(state, lane, belt.isSideLoad()), simulate);
	}

	private static ItemStack intoWorld(Level level, Direction facing, BlockPos target, BlockState targetState, ItemStack stack, boolean simulate, Vec3 exit, Vec3 motion)
	{
		BlockEntity entity = level.getBlockEntity(target);
		if(entity!=null)
		{
			IItemHandler handler = entity.getCapability(ForgeCapabilities.ITEM_HANDLER, facing.getOpposite()).orElse(null);
			if(handler!=null)
				return ItemHandlerHelper.insertItemStacked(handler, stack, simulate);
		}
		if(!targetState.getCollisionShape(level, target).isEmpty())
			return stack;
		if(simulate)
			return ItemStack.EMPTY;
		ItemEntity dropped = new ItemEntity(level, exit.x, exit.y, exit.z, stack.copy(), motion.x, motion.y, motion.z);
		dropped.setDefaultPickUpDelay();
		level.addFreshEntity(dropped);
		return ItemStack.EMPTY;
	}

	public static ItemStack dropBelow(Level level, BlockPos pos, ItemStack stack)
	{
		BlockPos below = pos.below();
		BlockEntity entity = level.getBlockEntity(below);
		if(entity!=null)
		{
			IItemHandler handler = entity.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP).orElse(null);
			if(handler!=null)
				return ItemHandlerHelper.insertItemStacked(handler, stack, false);
		}
		if(!level.getBlockState(below).getCollisionShape(level, below).isEmpty())
			return stack;
		ItemEntity dropped = new ItemEntity(level, pos.getX()+.5, pos.getY()-.25, pos.getZ()+.5, stack.copy(), 0, -0.1, 0);
		dropped.setDefaultPickUpDelay();
		level.addFreshEntity(dropped);
		return ItemStack.EMPTY;
	}
}
