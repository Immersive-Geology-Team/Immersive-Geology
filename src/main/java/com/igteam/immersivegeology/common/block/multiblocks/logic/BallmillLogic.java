package com.igteam.immersivegeology.common.block.multiblocks.logic;

import com.igteam.immersivegeology.common.block.multiblocks.recipe.BallmillRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shim.component.AveragingEnergyStorage;
import com.igteam.immersivegeology.common.block.multiblocks.shim.component.IClientTickableComponent;
import com.igteam.immersivegeology.common.block.multiblocks.shim.component.IGReceiveOnlyEnergy;
import com.igteam.immersivegeology.common.block.multiblocks.shim.component.IServerTickableComponent;
import com.igteam.immersivegeology.common.block.multiblocks.shim.component.RedstoneControl;
import com.igteam.immersivegeology.common.block.multiblocks.shim.env.IInitialMultiblockContext;
import com.igteam.immersivegeology.common.block.multiblocks.shim.env.IMultiblockContext;
import com.igteam.immersivegeology.common.block.multiblocks.shim.env.IMultiblockLevel;
import com.igteam.immersivegeology.common.block.multiblocks.shim.inventory.DroppingMultiblockOutput;
import com.igteam.immersivegeology.common.block.multiblocks.shim.inventory.InsertOnlyInventory;
import com.igteam.immersivegeology.common.block.multiblocks.shim.logic.IMultiblockLogic;
import com.igteam.immersivegeology.common.block.multiblocks.shim.logic.IMultiblockState;
import com.igteam.immersivegeology.common.block.multiblocks.shim.process.MultiblockProcessInWorld;
import com.igteam.immersivegeology.common.block.multiblocks.shim.process.MultiblockProcessor;
import com.igteam.immersivegeology.common.block.multiblocks.shim.process.ProcessContext.ProcessContextInWorld;
import com.igteam.immersivegeology.common.block.multiblocks.shim.util.CapabilityPosition;
import com.igteam.immersivegeology.common.block.multiblocks.shim.util.MultiblockFace;
import com.igteam.immersivegeology.common.block.multiblocks.shim.util.RelativeBlockFace;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.Set;

public class BallmillLogic implements IMultiblockLogic<BallmillLogic.State>,
		IServerTickableComponent<BallmillLogic.State>, IClientTickableComponent<BallmillLogic.State>
{
	public static final BlockPos REDSTONE_IN = new BlockPos(4, 1, 3);
	private static final int ENERGY_CAPACITY = 64000;
	public static final Set<CapabilityPosition> ENERGY_INPUTS =
			Set.of(new CapabilityPosition(0, 1, 3, RelativeBlockFace.UP));
	public static final MultiblockFace OUTPUT_POS = new MultiblockFace(2, 0, 4, RelativeBlockFace.FRONT);
	public static final CapabilityPosition ITEM_INPUT_CAP = new CapabilityPosition(0, 0, 1, RelativeBlockFace.RIGHT);

	public static final int ENERGY_CONSUMPTION_RATE = 80;

	@Override
	public void tickClient(IMultiblockContext<State> context)
	{
		State state = context.getState();
		if(state.renderAsActive) state.rotation = (float)((state.rotation+2.5)%360);
	}

	@Override
	public void tickServer(IMultiblockContext<State> context)
	{
		State state = context.getState();
		boolean wasActive = state.renderAsActive;

		state.energy.tick();
		state.renderAsActive = state.processor.tickServer(state, context.getLevel(), state.rsState.isEnabled(context));

		if(wasActive!=state.renderAsActive) context.requestMasterBESync();
	}

	@Override
	public State createInitialState(IInitialMultiblockContext<State> context)
	{
		return new State(context);
	}

	@Override
	public <T> T getCapability(IMultiblockContext<State> context, CapabilityPosition position, Capability<T> capability)
	{
		State state = context.getState();

		if(capability==CapabilityEnergy.ENERGY&&(position.side()==null||matchesEnergyInput(position)))
			return CapabilityEnergy.ENERGY.cast(state.energyCap);

		if(capability==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY&&ITEM_INPUT_CAP.equalsOrNullFace(position))
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(state.insertionHandler);

		return null;
	}

	private static boolean matchesEnergyInput(CapabilityPosition position)
	{
		for(CapabilityPosition input : ENERGY_INPUTS)
			if(input.equalsOrNullFace(position)) return true;
		return false;
	}

	public static class State implements IMultiblockState, ProcessContextInWorld<BallmillRecipe>
	{
		public final AveragingEnergyStorage energy = new AveragingEnergyStorage(ENERGY_CAPACITY);
		public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault(REDSTONE_IN);

		private final DroppingMultiblockOutput output;
		private final IEnergyStorage energyCap;
		private final IItemHandler insertionHandler;
		private final MultiblockProcessor<BallmillRecipe, ProcessContextInWorld<BallmillRecipe>> processor;

		private float rotation;
		private boolean renderAsActive;

		public State(IInitialMultiblockContext<State> context)
		{
			this.energyCap = IGReceiveOnlyEnergy.of(this.energy);
			this.output = new DroppingMultiblockOutput(OUTPUT_POS);
			this.processor = new MultiblockProcessor<>(64, 0, 8, context.getMarkDirtyRunnable());

			this.insertionHandler = new InsertOnlyInventory()
			{
				@Override
				protected ItemStack insert(ItemStack toInsert, boolean simulate)
				{
					ItemStack stack = toInsert.copy();
					BallmillRecipe recipe = BallmillRecipe.findRecipe(stack);
					if(recipe==null) return stack;

					MultiblockProcessInWorld<BallmillRecipe> process = new MultiblockProcessInWorld<>(recipe, stack);
					if(processor.addProcessToQueue(process, simulate))
						stack.shrink(recipe.input.inputSize);

					return stack;
				}
			};
		}

		@Override
		public void doProcessOutput(ItemStack result, IMultiblockLevel level)
		{
			output.insertOrDrop(result, level);
		}

		@Override
		public AveragingEnergyStorage getEnergy()
		{
			return energy;
		}

		public boolean shouldRenderActive()
		{
			return renderAsActive;
		}

		public float getRotation()
		{
			return rotation;
		}

		@Override
		public void writeSaveNBT(NBTTagCompound nbt)
		{
			energy.writeNBT(nbt);
			rsState.writeNBT(nbt);
			nbt.setTag("processor", processor.toNBT());
		}

		@Override
		public void readSaveNBT(NBTTagCompound nbt)
		{
			energy.readNBT(nbt);
			rsState.readNBT(nbt);
			processor.fromNBT(nbt.getTagList("processor", 10), tag -> {
				BallmillRecipe recipe = BallmillRecipe.loadFromNBT(tag);
				return recipe==null?null: new MultiblockProcessInWorld<>(recipe, ItemStack.EMPTY);
			});
		}

		@Override
		public void writeSyncNBT(NBTTagCompound nbt)
		{
			writeSaveNBT(nbt);
			nbt.setBoolean("renderActive", renderAsActive);
		}

		@Override
		public void readSyncNBT(NBTTagCompound nbt)
		{
			readSaveNBT(nbt);
			renderAsActive = nbt.getBoolean("renderActive");
		}
	}
}
