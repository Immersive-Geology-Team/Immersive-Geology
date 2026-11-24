/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt.network;

import com.igteam.immersivegeology.common.block.belt.IGBeltBlock;
import com.igteam.immersivegeology.common.block.belt.helper.BeltCoverHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class BeltLines
{
	public static final int MAX_LENGTH = 64;
	public static final int MIN_LENGTH = 2;

	private static final Map<UUID, Anchor> SERVER = new HashMap<>();
	private static final Map<UUID, Anchor> CLIENT = new HashMap<>();

	private BeltLines()
	{
	}

	private record Anchor(ResourceKey<Level> dimension, BlockPos pos, Direction facing, Block block)
	{
	}

	public record Line(BlockPos anchor, Direction facing, int distance, int rise, int slopeStart, int buildable)
	{
		public boolean complete()
		{
			return buildable==distance;
		}

		public int height(int step)
		{
			return Integer.signum(rise)*Math.max(0, Math.min(Math.abs(rise), step-slopeStart));
		}

		public BlockPos cell(int step)
		{
			return anchor.relative(facing, step).above(height(step));
		}

		private Line withBuildable(int buildable)
		{
			return new Line(anchor, facing, distance, rise, slopeStart, buildable);
		}
	}

	private static Map<UUID, Anchor> anchors(Level level)
	{
		return level.isClientSide?CLIENT: SERVER;
	}

	public static void remember(Player player, Level level, BlockPos pos, Direction facing, Block block)
	{
		anchors(level).put(player.getUUID(), new Anchor(level.dimension(), pos.immutable(), facing, block));
	}

	public static void forget(Player player, Level level)
	{
		anchors(level).remove(player.getUUID());
	}

	public static boolean isFilled(BlockState state, Block block, Direction facing)
	{
		if(!(state.getBlock() instanceof IGBeltBlock)||state.getValue(IGBeltBlock.FACING)!=facing)
			return false;
		return state.is(block)||BeltCoverHandler.counterpart(block).filter(state::is).isPresent();
	}

	public static Optional<Line> line(Level level, Player player, Block block, BlockPos target)
	{
		Anchor anchor = anchors(level).get(player.getUUID());
		if(anchor==null||anchor.dimension()!=level.dimension()||anchor.block()!=block)
			return Optional.empty();
		Direction facing = anchor.facing();
		if(!isFilled(level.getBlockState(anchor.pos()), block, facing))
			return Optional.empty();
		BlockPos offset = target.subtract(anchor.pos());
		int distance = offset.getX()*facing.getStepX()+offset.getZ()*facing.getStepZ();
		int rise = offset.getY();
		if(distance < MIN_LENGTH||distance > MAX_LENGTH||Math.abs(rise) > distance||!offset.equals(BlockPos.ZERO.relative(facing, distance).above(rise)))
			return Optional.empty();
		if(isBeltMember(level.getBlockState(anchor.pos().relative(facing))))
			return Optional.empty();
		int available = player.getAbilities().instabuild?Integer.MAX_VALUE: count(player.getInventory(), block.asItem());
		int latest = distance-Math.abs(rise);
		Line best = null;
		for(int i = 0; i <= latest; i++)
		{
			int slopeStart = rise > 0?latest-i: i;
			Line candidate = new Line(anchor.pos(), facing, distance, rise, slopeStart, 0);
			if(crossesBelt(level, candidate))
				continue;
			Line plan = measure(level, block, candidate, available);
			if(plan.complete())
				return Optional.of(plan);
			if(best==null||plan.buildable() > best.buildable())
				best = plan;
		}
		return Optional.ofNullable(best);
	}

	private static boolean isBeltMember(BlockState state)
	{
		return state.getBlock() instanceof IBeltConnectable;
	}

	private static boolean crossesBelt(Level level, Line plan)
	{
		for(int step = 1; step <= plan.distance(); step++)
			if(isBeltMember(level.getBlockState(plan.cell(step))))
				return true;
		return false;
	}

	private static Line measure(Level level, Block block, Line plan, int available)
	{
		int cost = 0;
		int buildable = 0;
		for(int step = 1; step <= plan.distance(); step++)
		{
			if(!slopeFits(level, plan, step))
				break;
			BlockState state = level.getBlockState(plan.cell(step));
			if(!state.canBeReplaced()||++cost > available)
				break;
			buildable = step;
		}
		return plan.withBuildable(buildable);
	}

	private static boolean slopeFits(Level level, Line plan, int step)
	{
		int change = plan.height(step)-plan.height(step-1);
		if(change==0)
			return true;
		BlockPos low = change > 0?plan.cell(step-1): plan.cell(step);
		BlockPos under = change > 0?plan.cell(step).below(): plan.cell(step-1).below();
		return isOpen(level.getBlockState(low.above()))&&!(level.getBlockState(under).getBlock() instanceof IBeltConnectable);
	}

	@SuppressWarnings("deprecation")
	private static boolean isOpen(BlockState state)
	{
		return state.isAir()||state.canBeReplaced();
	}

	public static int count(Inventory inventory, Item item)
	{
		int total = 0;
		for(int slot = 0; slot < inventory.getContainerSize(); slot++)
		{
			ItemStack stack = inventory.getItem(slot);
			if(stack.is(item))
				total += stack.getCount();
		}
		return total;
	}

	public static boolean consume(Inventory inventory, Item item, ItemStack preferred)
	{
		if(!preferred.isEmpty()&&preferred.is(item))
		{
			preferred.shrink(1);
			return true;
		}
		for(int slot = 0; slot < inventory.getContainerSize(); slot++)
		{
			ItemStack stack = inventory.getItem(slot);
			if(stack.is(item))
			{
				stack.shrink(1);
				return true;
			}
		}
		return false;
	}
}
