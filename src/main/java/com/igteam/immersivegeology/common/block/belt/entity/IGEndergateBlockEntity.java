/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt.entity;

import com.igteam.immersivegeology.common.block.belt.IGBeltBlock;
import com.igteam.immersivegeology.common.block.belt.IGEndergateBlock;
import com.igteam.immersivegeology.common.block.belt.transport.BeltTransport;
import com.igteam.immersivegeology.common.block.belt.transport.Lane;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class IGEndergateBlockEntity extends IGBeltBlockEntity
{
	public static final float EMERGE_POINT = 0.5f;
	private static final int STACKS_PER_BLOCK = 4;
	private static final String TRANSIT = "transit";
	private static final String TICKS = "ticks";
	private static final String STACK = "stack";

	private static final class Transit
	{
		private ItemStack stack;
		private int ticksLeft;

		private Transit(ItemStack stack, int ticksLeft)
		{
			this.stack = stack;
			this.ticksLeft = ticksLeft;
		}
	}

	private final Map<Lane, Deque<Transit>> transit = new EnumMap<>(Lane.class);

	public IGEndergateBlockEntity(BlockPos pos, BlockState state)
	{
		super(IGRegistrationHolder.ENDERGATE.get(), pos, state);
		for(Lane lane : Lane.values())
			transit.put(lane, new ArrayDeque<>());
	}

	public static int travelTicks(int distance)
	{
		return (int)Math.ceil(distance*16/BeltTransport.SPEED);
	}

	public int inTransit(Lane lane)
	{
		return transit.get(lane).size();
	}

	public ItemStack send(Lane lane, ItemStack stack, int distance, boolean simulate)
	{
		Deque<Transit> queue = transit.get(lane);
		int travel = travelTicks(distance);
		if(queue.size() >= distance*STACKS_PER_BLOCK)
			return stack;
		Transit last = queue.peekLast();
		if(last!=null&&last.ticksLeft > travel-BeltTransport.SPACING/BeltTransport.SPEED)
			return stack;
		if(!simulate)
		{
			queue.addLast(new Transit(stack.copy(), travel));
			setChanged();
		}
		return ItemStack.EMPTY;
	}

	public void tickTransit(Level level, BlockState state, IGEndergateBlock block)
	{
		boolean empty = transit.values().stream().allMatch(Deque::isEmpty);
		if(empty)
			return;
		for(Deque<Transit> queue : transit.values())
			for(Transit entry : queue)
				if(entry.ticksLeft > 0)
					entry.ticksLeft--;
		var partner = block.partner(level, worldPosition, state);
		if(partner.isEmpty()||!(level.getBlockEntity(partner.get()) instanceof IGBeltBlockEntity exit))
			return;
		BlockState exitState = level.getBlockState(partner.get());
		if(!(exitState.getBlock() instanceof IGBeltBlock exitBlock))
			return;
		for(Lane lane : Lane.values())
		{
			Deque<Transit> queue = transit.get(lane);
			Transit front = queue.peekFirst();
			if(front==null||front.ticksLeft > 0)
				continue;
			ItemStack left = exit.insert(lane, EMERGE_POINT, front.stack, exitBlock.lanePath(exitState, lane, exit.isSideLoad()), false);
			if(left.isEmpty())
				queue.pollFirst();
			else
				front.stack = left;
			setChanged();
		}
	}

	@Override
	public List<ItemStack> takeContents()
	{
		List<ItemStack> contents = new ArrayList<>(super.takeContents());
		for(Deque<Transit> queue : transit.values())
		{
			queue.forEach(entry -> contents.add(entry.stack));
			queue.clear();
		}
		return contents;
	}

	@Override
	public void load(@NotNull CompoundTag tag)
	{
		super.load(tag);
		CompoundTag saved = tag.getCompound(TRANSIT);
		for(Lane lane : Lane.values())
		{
			Deque<Transit> queue = transit.get(lane);
			queue.clear();
			ListTag list = saved.getList(lane.name(), Tag.TAG_COMPOUND);
			for(int i = 0; i < list.size(); i++)
			{
				CompoundTag entry = list.getCompound(i);
				ItemStack stack = ItemStack.of(entry.getCompound(STACK));
				if(!stack.isEmpty())
					queue.addLast(new Transit(stack, entry.getInt(TICKS)));
			}
		}
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		super.saveAdditional(tag);
		CompoundTag saved = new CompoundTag();
		for(Lane lane : Lane.values())
		{
			ListTag list = new ListTag();
			for(Transit entry : transit.get(lane))
			{
				CompoundTag compound = new CompoundTag();
				compound.put(STACK, entry.stack.save(new CompoundTag()));
				compound.putInt(TICKS, entry.ticksLeft);
				list.add(compound);
			}
			saved.put(lane.name(), list);
		}
		tag.put(TRANSIT, saved);
	}
}
