/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.helper;

import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IMultiblockComponent.CapabilityGetter;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IMultiblockComponent.CapabilityRegistrar;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelper;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.CapabilityPosition;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import blusunrize.immersiveengineering.api.utils.SafeChunkUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class MultiblockCapabilityReference<T>
{
	protected final BlockCapability<T, Direction> cap;

	protected MultiblockCapabilityReference(BlockCapability<T, Direction> cap)
	{
		this.cap = cap;
	}

	@Nullable
	public T getNullable()
	{
		return null; // Override in implementations
	}

	public boolean isPresent()
	{
		return getNullable()!=null;
	}

	public static <T> Map<Direction, MultiblockCapabilityReference<T>> forAllNeighbors(BlockEntity local, BlockCapability<T, Direction> cap)
	{
		Map<Direction, MultiblockCapabilityReference<T>> neighbors = new EnumMap<>(Direction.class);
		for(Direction side : Direction.values())
			neighbors.put(side, forNeighbor(local, cap, side));
		return neighbors;
	}

	public static <T> MultiblockCapabilityReference<T> forNeighbor(BlockEntity local, BlockCapability<T, Direction> cap, @Nonnull Direction side)
	{
		return forRelative(local, cap, BlockPos.ZERO.relative(side), side);
	}

	public static <T> MultiblockCapabilityReference<T> forRelative(BlockEntity local, BlockCapability<T, Direction> cap, BlockPos offset, Direction side)
	{
		return forBlockEntityAt(local,
				() -> new DirectionalBlockPos(local.getBlockPos().offset(offset), side.getOpposite()),
				() -> new DirectionalBlockPos(local.getBlockPos(), side), cap);
	}

	public static <T> MultiblockCapabilityReference<T> forBlockEntityAt(
			BlockEntity local, Supplier<DirectionalBlockPos> targetPos,
			Supplier<DirectionalBlockPos> requesterPos, BlockCapability<T, Direction> cap)
	{
		Objects.requireNonNull(local);
		return new MBCapReference<>(local::getLevel, targetPos, requesterPos, cap);
	}

	private static class MBCapReference<T> extends MultiblockCapabilityReference<T>
	{
		private final Supplier<Level> getLevel;
		private final Supplier<DirectionalBlockPos> getTargetPos;
		private final Supplier<DirectionalBlockPos> getRequesterPos;
		private BlockCapabilityCache<T, Direction> cache;
		private DirectionalBlockPos lastTargetPos;
		private Level lastWorld;
		private boolean usingRequesterContext;

		public MBCapReference(Supplier<Level> getLevel,
							  Supplier<DirectionalBlockPos> getTargetPos,
							  Supplier<DirectionalBlockPos> getRequesterPos,
							  BlockCapability<T, Direction> cap)
		{
			super(cap);
			this.getLevel = getLevel;
			this.getTargetPos = getTargetPos;
			this.getRequesterPos = getRequesterPos;
		}

		@Nullable
		@Override
		public T getNullable()
		{
			Level currWorld = getLevel.get();
			DirectionalBlockPos currTargetPos = getTargetPos.get();
			DirectionalBlockPos currRequesterPos = getRequesterPos.get();
			usingRequesterContext = false;
			if(currWorld==null||currTargetPos==null||currRequesterPos==null
					||!SafeChunkUtils.isChunkSafe(currWorld, currTargetPos.position()))
			{
				cache = null;
				lastWorld = null;
				lastTargetPos = null;
				return null;
			}

			if(currWorld!=lastWorld||!currTargetPos.equals(lastTargetPos))
			{
				cache = null;
				lastWorld = currWorld;
				lastTargetPos = currTargetPos;
			}

			T value;
			if(currWorld instanceof ServerLevel serverLevel)
			{
				if(cache==null)
					cache = BlockCapabilityCache.create(cap, serverLevel, currTargetPos.position(), currTargetPos.side());
				value = cache.getCapability();
			}
			else
				value = currWorld.getCapability(cap, currTargetPos.position(), currTargetPos.side());
			if(value!=null) return value;

			// Keep IG's requester-context fallback for generator connections.
			BlockEntity lastBE = SafeChunkUtils.getSafeBE(currWorld, currTargetPos.position());
			if(cap==Capabilities.EnergyStorage.BLOCK&&lastBE instanceof IMultiblockBE<?> mbe)
			{
				IMultiblockBEHelper<?> helper = mbe.getHelper();
				IMultiblockContext<?> context = helper.getContext();
				if(context!=null)
				{
					Direction side = currRequesterPos.side();
					CapabilityPosition capPos = new CapabilityPosition(
							helper.getPositionInMB().relative(side.getOpposite()),
							RelativeBlockFace.from(context.getLevel().getOrientation(), side.getAxis()==Axis.Y?side.getOpposite():side));
					usingRequesterContext = true;
					return getFromMultiblock(helper, capPos);
				}
			}
			return null;
		}

		@Nullable
		private <S extends IMultiblockState> T getFromMultiblock(IMultiblockBEHelper<S> helper, CapabilityPosition position)
		{
			S state = helper.getState();
			if(state==null) return null;
			AtomicReference<T> result = new AtomicReference<>();
			helper.getMultiblock().logic().registerCapabilities(new CapabilityRegistrar<>()
			{
				@Override
				public <C> void register(BlockCapability<C, Direction> capability, CapabilityGetter<C, S> getter)
				{
					if(capability==cap)
						result.set(cap.typeClass().cast(getter.getCapability(state, position)));
				}
			});
			return result.get();
		}

		public boolean isUsingRequesterContext()
		{
			return usingRequesterContext;
		}
	}

	public static class DirectionalBlockPos
	{
		private final BlockPos position;
		private final Direction side;

		public DirectionalBlockPos(BlockPos position, Direction side)
		{
			this.position = position;
			this.side = side;
		}

		public BlockPos position()
		{
			return position;
		}

		public Direction side()
		{
			return side;
		}

		@Override
		public boolean equals(Object obj)
		{
			if(this==obj) return true;
			if(!(obj instanceof DirectionalBlockPos other)) return false;
			return position.equals(other.position)&&side==other.side;
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(position, side);
		}

		@Override
		public String toString()
		{
			return "DirectionalBlockPos{pos="+position+", side="+side+"}";
		}
	}
}
