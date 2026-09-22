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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import blusunrize.immersiveengineering.api.energy.immersiveflux.IFluxReceiver;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.IBlockState;
import com.igteam.immersivegeology.common.block.multiblocks.structure.IGStructureFormer;
import blusunrize.immersiveengineering.common.util.Utils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.IFluidTank;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public abstract class IGMultiblockTile<S extends IMultiblockState> extends TileEntityMultiblockPart<IGMultiblockTile<S>>
		implements ITickable, IFluxReceiver
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

	@Nullable
	private IEnergyStorage energyFor(@Nullable EnumFacing side)
	{
		if(!formed) return null;
		return getCapability(CapabilityEnergy.ENERGY, side);
	}

	@Override
	public int receiveEnergy(@Nullable EnumFacing from, int energy, boolean simulate)
	{
		if(world!=null&&world.isRemote) return 0;
		IEnergyStorage storage = energyFor(from);
		return storage==null?0: storage.receiveEnergy(energy, simulate);
	}

	@Override
	public int getEnergyStored(@Nullable EnumFacing from)
	{
		IEnergyStorage storage = energyFor(null);
		return storage==null?0: storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(@Nullable EnumFacing from)
	{
		IEnergyStorage storage = energyFor(null);
		return storage==null?0: storage.getMaxEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(@Nullable EnumFacing from)
	{
		return energyFor(from)!=null;
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

	private ItemStack originalBlock = ItemStack.EMPTY;

	private boolean master = false;
	private BlockPos masterPos;

	public void setFormedAt(int[] posInMB, ItemStack original, boolean isMaster, EnumFacing structureFacing,
							BlockPos masterPosition)
	{
		this.formed = true;
		this.offset = posInMB;
		this.mirrored = false;
		this.master = isMaster;
		this.facing = structureFacing;
		this.masterPos = masterPosition;
		this.originalBlock = original==null?ItemStack.EMPTY: original.copy();
		markDirty();
	}

	@Override
	@SuppressWarnings("unchecked")
	public IGMultiblockTile<S> master()
	{
		if(master) return this;
		if(world==null||masterPos==null) return null;
		TileEntity te = world.getTileEntity(masterPos);
		return getClass().isInstance(te)?(IGMultiblockTile<S>)te: null;
	}

	@Override
	public void disassemble()
	{
		if(world==null||world.isRemote||!formed) return;

		BlockPos origin = getOrigin();
		EnumFacing structureFacing = getFacing();
		int height = structureDimensions[0];
		int length = structureDimensions[1];
		int width = structureDimensions[2];

		List<BlockPos> positions = new ArrayList<>();
		List<ItemStack> originals = new ArrayList<>();

		for(int y = 0; y < height; y++)
			for(int z = 0; z < length; z++)
				for(int x = 0; x < width; x++)
				{
					BlockPos target = origin.add(
							IGStructureFormer.rotateOffset(new BlockPos(x, y, z), structureFacing));
					TileEntity te = world.getTileEntity(target);
					if(!(te instanceof IGMultiblockTile<?> part)||!part.formed) continue;

					part.formed = false;
					positions.add(target);
					originals.add(part.getOriginalBlock());
				}

		for(int i = 0; i < positions.size(); i++)
		{
			BlockPos target = positions.get(i);
			ItemStack original = originals.get(i);
			IBlockState restored = original.isEmpty()?null: Utils.getStateFromItemStack(original);
			if(restored!=null) world.setBlockState(target, restored, 3);
			else world.setBlockToAir(target);
		}
	}

	@Override
	public boolean isDummy()
	{
		return !master;
	}

	@Override
	public ItemStack getOriginalBlock()
	{
		return originalBlock;
	}

	@Override
	public BlockPos getOrigin()
	{
		EnumFacing facing = getFacing();
		EnumFacing width = facing.rotateY();
		int along = -offset[2];
		int across = offset[0];
		return getPos().add(
				-(facing.getXOffset()*along+width.getXOffset()*across),
				-offset[1],
				-(facing.getZOffset()*along+width.getZOffset()*across));
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
		master = nbt.getBoolean("mbMaster");
		masterPos = nbt.hasKey("mbMasterPos")
				?net.minecraft.nbt.NBTUtil.getPosFromTag(nbt.getCompoundTag("mbMasterPos"))
				: null;
		originalBlock = nbt.hasKey("originalBlock")
				?new ItemStack(nbt.getCompoundTag("originalBlock"))
				: ItemStack.EMPTY;
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
		nbt.setBoolean("mbMaster", master);
		if(masterPos!=null) nbt.setTag("mbMasterPos", net.minecraft.nbt.NBTUtil.createPosTag(masterPos));
		if(!originalBlock.isEmpty())
			nbt.setTag("originalBlock", originalBlock.writeToNBT(new NBTTagCompound()));
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
