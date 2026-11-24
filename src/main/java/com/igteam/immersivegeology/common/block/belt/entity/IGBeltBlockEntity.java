/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt.entity;

import com.igteam.immersivegeology.common.block.belt.IGBeltBlock;
import com.igteam.immersivegeology.common.block.belt.network.LanePath;
import com.igteam.immersivegeology.common.block.belt.helper.BeltInsertHandler;
import com.igteam.immersivegeology.common.block.belt.transport.BeltTransport;
import com.igteam.immersivegeology.common.block.belt.transport.Lane;
import com.igteam.immersivegeology.common.block.belt.transport.LaneItem;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class IGBeltBlockEntity extends BlockEntity
{
	private static final String LEFT_LANE = "left_lane";
	private static final String RIGHT_LANE = "right_lane";
	private static final String SIDE_LOAD = "side_load";

	private final Map<Lane, List<LaneItem>> lanes = new EnumMap<>(Lane.class);
	private boolean sideLoad;
	private boolean syncPending;
	private Lane nextInsertLane = Lane.LEFT;
	private final Map<Direction, LazyOptional<IItemHandler>> handlers = new EnumMap<>(Direction.class);
	private LazyOptional<IItemHandler> topHandler = LazyOptional.empty();

	public IGBeltBlockEntity(BlockPos pos, BlockState state)
	{
		this(IGRegistrationHolder.BELT.get(), pos, state);
	}

	protected IGBeltBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
		for(Lane lane : Lane.values())
			lanes.put(lane, new ArrayList<>());
	}

	public List<LaneItem> lane(Lane lane)
	{
		return lanes.get(lane);
	}

	public boolean isEmpty()
	{
		return lanes.values().stream().allMatch(List::isEmpty);
	}

	public boolean isSideLoad()
	{
		return sideLoad;
	}

	public void setSideLoad(boolean sideLoad)
	{
		if(this.sideLoad!=sideLoad)
		{
			this.sideLoad = sideLoad;
			markLanesChanged();
		}
	}

	public void markLanesChanged()
	{
		setChanged();
		syncPending = true;
	}

	public void flushSync()
	{
		if(!syncPending||level==null||level.isClientSide)
			return;
		syncPending = false;
		PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)).send(ClientboundBlockEntityDataPacket.create(this));
	}

	public ItemStack insert(Lane lane, float entry, ItemStack stack, LanePath path, boolean simulate)
	{
		return insert(lane, entry, stack, path, simulate, false);
	}

	public ItemStack insert(Lane lane, float entry, ItemStack stack, LanePath path, boolean simulate, boolean routed)
	{
		if(stack.isEmpty())
			return ItemStack.EMPTY;
		List<LaneItem> items = lanes.get(lane);
		double gap = BeltTransport.SPACING/path.length();
		LaneItem merge = null;
		for(LaneItem item : items)
			if(Math.abs(item.progress()-entry) < gap)
			{
				if(!routed&&merge==null&&item.progress() <= entry+gap/2&&ItemHandlerHelper.canItemStacksStack(item.stack(), stack)&&item.stack().getCount() < item.stack().getMaxStackSize())
					merge = item;
				else
					return stack;
			}
		if(merge!=null)
		{
			int moved = Math.min(stack.getCount(), merge.stack().getMaxStackSize()-merge.stack().getCount());
			if(!simulate)
			{
				merge.stack().grow(moved);
				markLanesChanged();
			}
			return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount()-moved);
		}
		if(!simulate)
		{
			LaneItem added = new LaneItem(stack.copy(), entry);
			added.setRouted(routed);
			int index = 0;
			while(index < items.size()&&items.get(index).progress() > entry)
				index++;
			items.add(index, added);
			markLanesChanged();
		}
		return ItemStack.EMPTY;
	}

	public ItemStack insertAnyLane(IGBeltBlock block, BlockState state, float entry, ItemStack stack, boolean simulate)
	{
		for(int attempt = 0; attempt < 2; attempt++)
		{
			Lane lane = attempt==0?nextInsertLane: other(nextInsertLane);
			ItemStack left = insert(lane, entry, stack, block.lanePath(state, lane, sideLoad), simulate);
			if(left.getCount()!=stack.getCount())
			{
				if(!simulate)
					nextInsertLane = other(lane);
				return left;
			}
		}
		return stack;
	}

	public List<ItemStack> takeContents()
	{
		List<ItemStack> contents = new ArrayList<>();
		for(List<LaneItem> lane : lanes.values())
		{
			lane.forEach(item -> contents.add(item.stack()));
			lane.clear();
		}
		return contents;
	}

	private static Lane other(Lane lane)
	{
		return lane==Lane.LEFT?Lane.RIGHT: Lane.LEFT;
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side)
	{
		if(capability==ForgeCapabilities.ITEM_HANDLER&&getBlockState().getBlock() instanceof IGBeltBlock)
		{
			if(side==null||side==Direction.UP||side==Direction.DOWN)
			{
				if(!topHandler.isPresent())
					topHandler = LazyOptional.of(() -> new BeltInsertHandler(this, null));
				return topHandler.cast();
			}
			return handlers.computeIfAbsent(side, s -> LazyOptional.of(() -> new BeltInsertHandler(this, s))).cast();
		}
		return super.getCapability(capability, side);
	}

	@Override
	public void invalidateCaps()
	{
		super.invalidateCaps();
		topHandler.invalidate();
		handlers.values().forEach(LazyOptional::invalidate);
		handlers.clear();
	}

	@Override
	public void load(@NotNull CompoundTag tag)
	{
		super.load(tag);
		readLane(tag.getList(LEFT_LANE, Tag.TAG_COMPOUND), lanes.get(Lane.LEFT));
		readLane(tag.getList(RIGHT_LANE, Tag.TAG_COMPOUND), lanes.get(Lane.RIGHT));
		sideLoad = tag.getBoolean(SIDE_LOAD);
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		super.saveAdditional(tag);
		tag.put(LEFT_LANE, writeLane(lanes.get(Lane.LEFT)));
		tag.put(RIGHT_LANE, writeLane(lanes.get(Lane.RIGHT)));
		tag.putBoolean(SIDE_LOAD, sideLoad);
	}

	@Override
	public @NotNull CompoundTag getUpdateTag()
	{
		return saveWithoutMetadata();
	}

	@Override
	@Nullable
	public Packet<ClientGamePacketListener> getUpdatePacket()
	{
		return ClientboundBlockEntityDataPacket.create(this);
	}

	private static void readLane(ListTag list, List<LaneItem> lane)
	{
		lane.clear();
		for(int i = 0; i < list.size(); i++)
		{
			LaneItem item = LaneItem.load(list.getCompound(i));
			if(!item.stack().isEmpty())
				lane.add(item);
		}
		lane.sort((a, b) -> Float.compare(b.progress(), a.progress()));
	}

	private static ListTag writeLane(List<LaneItem> lane)
	{
		ListTag list = new ListTag();
		for(LaneItem item : lane)
			list.add(item.save());
		return list;
	}
}
