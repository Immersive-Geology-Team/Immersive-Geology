/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.helper;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelper;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.CapabilityPosition;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import blusunrize.immersiveengineering.api.utils.SafeChunkUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class MultiblockCapabilityReference<T>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MultiblockCapabilityReference.class);

	protected final Capability<T> cap;

	protected MultiblockCapabilityReference(Capability<T> cap)
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

	// Factory methods
	public static <T> Map<Direction, MultiblockCapabilityReference<T>> forAllNeighbors(BlockEntity local, Capability<T> cap)
	{
		Map<Direction, MultiblockCapabilityReference<T>> neighbors = new EnumMap<>(Direction.class);
		for(Direction side : Direction.values())
		{
			neighbors.put(side, forNeighbor(local, cap, side));
		}
		return neighbors;
	}

	public static <T> MultiblockCapabilityReference<T> forNeighbor(BlockEntity local, Capability<T> cap, @Nonnull Direction side)
	{
		return forRelative(local, cap, BlockPos.ZERO.relative(side), side);
	}

	public static <T> MultiblockCapabilityReference<T> forRelative(BlockEntity local, Capability<T> cap, BlockPos offset, Direction side)
	{
		return forBlockEntityAt(local, () -> {
			return new DirectionalBlockPos(local.getBlockPos().offset(offset), side.getOpposite());
		}, () -> {
			return new DirectionalBlockPos(local.getBlockPos(), side);
		}, cap);
	}

	public static <T> MultiblockCapabilityReference<T> forBlockEntityAt(BlockEntity local,
																		Supplier<DirectionalBlockPos> targetPos,
																		Supplier<DirectionalBlockPos> requesterPos,
																		Capability<T> cap)
	{
		Objects.requireNonNull(local);
		return new MBCapReference<>(local::getLevel, targetPos, requesterPos, cap);
	}

	// Internal implementation class
	private static class MBCapReference<T> extends MultiblockCapabilityReference<T>
	{
		private final Supplier<Level> getLevel;
		private final Supplier<DirectionalBlockPos> getTargetPos;
		private final Supplier<DirectionalBlockPos> getRequesterPos;
		@Nonnull
		private LazyOptional<T> currentCap = LazyOptional.empty();
		private DirectionalBlockPos lastTargetPos;
		private DirectionalBlockPos lastRequesterPos;
		private Level lastWorld;
		private BlockEntity lastBE;
		private boolean usingRequesterContext = false;

		public MBCapReference(Supplier<Level> getLevel,
							  Supplier<DirectionalBlockPos> getTargetPos,
							  Supplier<DirectionalBlockPos> getRequesterPos,
							  Capability<T> cap)
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
			this.updateLazyOptional();
			return this.currentCap.orElse(null);
		}

		@Override
		public boolean isPresent()
		{
			this.updateLazyOptional();
			return this.currentCap.isPresent();
		}

		private void updateLazyOptional()
		{
			Level currWorld = this.getLevel.get();
			DirectionalBlockPos currTargetPos = this.getTargetPos.get();
			DirectionalBlockPos currRequesterPos = this.getRequesterPos.get();

			if(currWorld!=null&&currTargetPos!=null&&currRequesterPos!=null)
			{
				if(currWorld!=this.lastWorld||
						!currTargetPos.equals(this.lastTargetPos)||
						!currRequesterPos.equals(this.lastRequesterPos)||
						!this.currentCap.isPresent()||
						(this.lastBE!=null&&this.lastBE.isRemoved()))
				{

					if(this.currentCap.isPresent()&&this.lastBE!=null&&this.lastBE.isRemoved())
					{
						LOGGER.warn("The tile entity {} (class {}) was removed, but the value {} provided by it for the capability {} is still marked as valid. This is likely a bug in the mod(s) adding the tile entity/the capability",
								this.lastBE, this.lastBE.getClass(), this.currentCap.orElseThrow(RuntimeException::new), this.cap.getName());
					}

					this.lastBE = SafeChunkUtils.getSafeBE(currWorld, currTargetPos.position());
					if(this.lastBE!=null)
					{
						// First try the standard approach (neighbor's perspective)
						this.currentCap = this.lastBE.getCapability(this.cap, currTargetPos.side());
						this.usingRequesterContext = false;

						// If that doesn't work, try with requester context (for generators)
						Direction side = currRequesterPos.side();
						if(!this.currentCap.isPresent())
						{
							if(lastBE instanceof IMultiblockBE<?> mbe)
							{
								IMultiblockBEHelper<?> helper = mbe.getHelper();
								IMultiblockContext<?> context = helper.getContext();
								if(context!=null)
								{
									CapabilityPosition capPos = new CapabilityPosition(helper.getPositionInMB().relative(side.getOpposite()), RelativeBlockFace.from(context.getLevel().getOrientation(),side.getAxis().equals(Axis.Y) ? side.getOpposite() : side));
									// Check if this is a generator-type multiblock
									this.currentCap = helper.getMultiblock().logic().getCapability((IMultiblockContext)context, capPos, ForgeCapabilities.ENERGY);
									this.usingRequesterContext = true;
								}
							}
						}
					}
					else
					{
						this.currentCap = LazyOptional.empty();
						this.usingRequesterContext = false;
					}

					this.lastWorld = currWorld;
					this.lastTargetPos = currTargetPos;
					this.lastRequesterPos = currRequesterPos;
				}
			}
			else
			{
				this.currentCap = LazyOptional.empty();
				this.lastWorld = null;
				this.lastTargetPos = null;
				this.lastRequesterPos = null;
				this.lastBE = null;
				this.usingRequesterContext = false;
			}
		}

		public boolean isUsingRequesterContext()
		{
			return this.usingRequesterContext;
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
