package com.igteam.immersivegeology.common.block.multiblocks.shim;

import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import com.igteam.immersivegeology.common.block.multiblocks.shim.component.IClientTickableComponent;
import com.igteam.immersivegeology.common.block.multiblocks.shim.component.IServerTickableComponent;
import com.igteam.immersivegeology.common.block.multiblocks.shim.env.IGMultiblockContext;
import com.igteam.immersivegeology.common.block.multiblocks.shim.env.IGMultiblockLevel;
import com.igteam.immersivegeology.common.block.multiblocks.shim.env.IMultiblockContext;
import com.igteam.immersivegeology.common.block.multiblocks.shim.env.IMultiblockLevel;
import com.igteam.immersivegeology.common.block.multiblocks.shim.env.IInitialMultiblockContext;
import com.igteam.immersivegeology.common.block.multiblocks.shim.logic.IMultiblockLogic;
import com.igteam.immersivegeology.common.block.multiblocks.shim.logic.IMultiblockState;
import com.igteam.immersivegeology.common.block.multiblocks.shim.util.CapabilityPosition;
import com.igteam.immersivegeology.common.block.multiblocks.shim.util.MultiblockOrientation;
import com.igteam.immersivegeology.common.block.multiblocks.shim.util.RelativeBlockFace;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.IFluidTank;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public abstract class IGMultiblockTile<S extends IMultiblockState> extends TileEntityMultiblockPart<IGMultiblockTile<S>>
		implements ITickable
{
	private final IMultiblockLogic<S> logic;

	private S state;
	private IGMultiblockContext<S> context;
	private NBTTagCompound pendingState;

	protected IGMultiblockTile(int[] structureDimensions, IMultiblockLogic<S> logic)
	{
		super(structureDimensions);
		this.logic = logic;
	}

	public IMultiblockLogic<S> getLogic()
	{
		return logic;
	}

	public S getState()
	{
		IGMultiblockTile<S> master = master();
		if(master!=this&&master!=null) return master.getState();
		if(state==null) state = logic.createInitialState(createInitialContext());
		if(pendingState!=null)
		{
			state.readSaveNBT(pendingState);
			pendingState = null;
		}
		return state;
	}

	public IMultiblockContext<S> getContext()
	{
		IGMultiblockTile<S> master = master();
		if(master!=this&&master!=null) return master.getContext();
		if(context==null)
			context = new IGMultiblockContext<>(
					this::getState,
					this::buildLevel,
					this::markDirty,
					this::syncToClients,
					() -> !isInvalid()&&formed,
					pos -> markDirty());
		return context;
	}

	private IInitialMultiblockContext<S> createInitialContext()
	{
		return new IInitialMultiblockContext<S>()
		{
			@Override
			public Supplier<World> levelSupplier()
			{
				return () -> world;
			}

			@Override
			public Runnable getMarkDirtyRunnable()
			{
				return IGMultiblockTile.this::markDirty;
			}

			@Override
			public Runnable getSyncRunnable()
			{
				return IGMultiblockTile.this::syncToClients;
			}
		};
	}

	private IMultiblockLevel buildLevel()
	{
		if(world==null) return null;
		return new IGMultiblockLevel(world, getOrigin(), new MultiblockOrientation(getFacing(), getIsMirrored()));
	}

	protected boolean getIsMirrored()
	{
		return mirrored;
	}

	protected void syncToClients()
	{
		if(world!=null&&!world.isRemote)
			markContainingBlockForUpdate(null);
	}

	@Override
	public void update()
	{
		if(!formed||isDummy()) return;
		if(world==null) return;

		IMultiblockContext<S> ctx = getContext();
		if(world.isRemote)
		{
			if(logic instanceof IClientTickableComponent<?> client)
				((IClientTickableComponent<S>)client).tickClient(ctx);
		}
		else if(logic instanceof IServerTickableComponent<?> server)
			((IServerTickableComponent<S>)server).tickServer(ctx);
	}

	@Nullable
	protected CapabilityPosition toCapabilityPosition(@Nullable EnumFacing side)
	{
		BlockPos posInMB = new BlockPos(offset[0], offset[1], offset[2]);
		if(side==null) return new CapabilityPosition(posInMB, null);
		MultiblockOrientation orientation = new MultiblockOrientation(getFacing(), getIsMirrored());
		return new CapabilityPosition(posInMB, RelativeBlockFace.from(orientation, side));
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side)
	{
		if(formed&&logic.getCapability(getContext(), toCapabilityPosition(side), capability)!=null) return true;
		return super.hasCapability(capability, side);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side)
	{
		if(formed)
		{
			T value = logic.getCapability(getContext(), toCapabilityPosition(side), capability);
			if(value!=null) return value;
		}
		return super.getCapability(capability, side);
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbt, boolean descPacket)
	{
		super.readCustomNBT(nbt, descPacket);
		if(!nbt.hasKey("logicState")) return;

		NBTTagCompound stateTag = nbt.getCompoundTag("logicState");
		if(state==null)
		{
			pendingState = stateTag;
			return;
		}
		if(descPacket) state.readSyncNBT(stateTag);
		else state.readSaveNBT(stateTag);
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt, boolean descPacket)
	{
		super.writeCustomNBT(nbt, descPacket);
		if(isDummy()||state==null) return;

		NBTTagCompound stateTag = new NBTTagCompound();
		if(descPacket) state.writeSyncNBT(stateTag);
		else state.writeSaveNBT(stateTag);
		nbt.setTag("logicState", stateTag);
	}

	@Override
	public float[] getBlockBounds()
	{
		return null;
	}

	@Override
	protected IFluidTank[] getAccessibleFluidTanks(EnumFacing side)
	{
		return new IFluidTank[0];
	}

	@Override
	protected boolean canFillTankFrom(int iTank, EnumFacing side, net.minecraftforge.fluids.FluidStack resource)
	{
		return false;
	}

	@Override
	protected boolean canDrainTankFrom(int iTank, EnumFacing side)
	{
		return false;
	}
}
