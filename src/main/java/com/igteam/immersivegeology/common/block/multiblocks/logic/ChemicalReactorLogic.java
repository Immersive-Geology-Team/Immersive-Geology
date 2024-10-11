/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.logic;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelper;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.*;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.client.BlockOverlayUtils;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.utils.TextUtils;
import blusunrize.immersiveengineering.common.blocks.multiblocks.blockimpl.MultiblockLevel;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.interfaces.MBOverlayText;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInMachine;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor.InMachineProcessor;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler.IOConstraint;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler.IntRange;
import com.igteam.immersivegeology.common.block.multiblocks.logic.ChemicalReactorLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGPositionalOverlayText;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.ChemicalRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.ChemicalReactorShape;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ChemicalReactorLogic implements IMultiblockLogic<ChemicalReactorLogic.State>, IGPositionalOverlayText<State>, IServerTickableComponent<ChemicalReactorLogic.State>
{
	public static final BlockPos REDSTONE_IN = new BlockPos(5, 1, 6);
	private static final Set<CapabilityPosition> ENERGY_POS;
	private static final MultiblockFace FLUID_OUTPUT;
	private static final MultiblockFace ITEM_OUTPUT, ITEM_INPUT_OUTPUT;
	private static final CapabilityPosition FLUID_OUTPUT_CAP;
	private static final Set<CapabilityPosition> FLUID_INPUT_CAPS;
	private static final Set<BlockPos> FLUID_INPUTS;
	private static final BlockPos ITEM_INPUT;

	private static final Set<BlockPos> TANK_LEFT_POSITIONS;
	private static final Set<BlockPos> TANK_BACK_POSITIONS;
	private static final Set<BlockPos> TANK_RIGHT_POSITIONS;
	private static final Set<BlockPos> TANK_FRONT_POSITIONS;
	private static final Set<BlockPos> REACTOR_CHAMBER_POSITIONS;

	static
	{
		ITEM_INPUT = new BlockPos(4, 5, 4);
		ENERGY_POS = Set.of(new CapabilityPosition(6, 0, 6, RelativeBlockFace.BACK), new CapabilityPosition(6, 1, 6, RelativeBlockFace.BACK));
		FLUID_OUTPUT = new MultiblockFace(5, 0, 8, RelativeBlockFace.BACK);
		ITEM_OUTPUT = new MultiblockFace(3, 0, 9, RelativeBlockFace.BACK);
		ITEM_INPUT_OUTPUT = new MultiblockFace(4, -1, 4, RelativeBlockFace.UP);
		FLUID_OUTPUT_CAP = new CapabilityPosition(5, 0, 8, RelativeBlockFace.BACK);
		FLUID_INPUT_CAPS = Set.of(new CapabilityPosition(0, 0, 5, RelativeBlockFace.LEFT),
				new CapabilityPosition(8, 0, 3, RelativeBlockFace.RIGHT),
				new CapabilityPosition(3, 0, 0, RelativeBlockFace.BACK));
		FLUID_INPUTS = FLUID_INPUT_CAPS.stream().map(CapabilityPosition::posInMultiblock).collect(Collectors.toSet());
		TANK_LEFT_POSITIONS = generateBlockPositions(new BlockPos(0,1,3), new BlockPos(1,3,4));
		TANK_BACK_POSITIONS = generateBlockPositions(new BlockPos(4,1,0), new BlockPos(5,3,1));
		TANK_RIGHT_POSITIONS = generateBlockPositions(new BlockPos(7,1,4), new BlockPos(8,3,5));
		TANK_FRONT_POSITIONS = generateBlockPositions(new BlockPos(3,1,7), new BlockPos(4,3,8));
		REACTOR_CHAMBER_POSITIONS = generateBlockPositions(new BlockPos(3,1,3), new BlockPos(5,4,5));
	}

	@Override
	public void tickServer(IMultiblockContext<State> ctx)
	{
		State state = ctx.getState();
		state.processor.tickServer(state, ctx.getLevel(), state.rsState.isEnabled(ctx));
		insertRecipeToProcess(state, ctx.getLevel());

		if(state.tanks.output.getFluid().getAmount() > 0)
		{
			drainOutputTank(state, ctx, state.fluidOutput);
		}

		ctx.markDirtyAndSync();
	}

	private static void insertRecipeToProcess(State state, IMultiblockLevel level)
	{
		ChemicalReactorTanks fluidTanks = state.tanks;
		ChemicalRecipe recipe = state.getRecipeForInputs(level.getRawLevel());
		if(recipe!=null)
		{
			MultiblockProcessInMachine<ChemicalRecipe> process = new MultiblockProcessInMachine<>(recipe, 0);
			int size = (fluidTanks.leftInput.isEmpty()?0: 1)
					+(fluidTanks.backInput.isEmpty()?0: 1)
					+(fluidTanks.rightInput.isEmpty()?0: 1);

			int[] intArray = new int[size];
			int index = 0;

			if(!fluidTanks.leftInput.isEmpty()) intArray[index++] = 0;
			if(!fluidTanks.backInput.isEmpty()) intArray[index++] = 1;
			if(!fluidTanks.rightInput.isEmpty()) intArray[index] = 2;

			process.setInputTanks(intArray);
			state.processor.addProcessToQueue(process, level.getRawLevel(), false);
		}
		if(!state.inventory.getStackInSlot(1).isEmpty()){
			state.processOutput(state.inventory.getStackInSlot(1), level);
		}

		ItemStack itemStack = state.inventory.getStackInSlot(0);
		if(!itemStack.isEmpty() && state.processor.getQueue().stream().noneMatch((q) -> state.additionalCanProcessCheck(q, level.getRawLevel())))
		{
			ItemStack stack = Utils.insertStackIntoInventory(state.input_output, itemStack.copyWithCount(1), true);
			if(stack.isEmpty())
			{
				Utils.insertStackIntoInventory(state.input_output, itemStack.copyWithCount(1), false);
				itemStack.shrink(1);
			}
		}
	}

	@Override
	public State createInitialState(IInitialMultiblockContext<State> capability)
	{
		return new ChemicalReactorLogic.State(capability);
	}

	@Override
	public <T> LazyOptional<T> getCapability(IMultiblockContext<State> ctx, CapabilityPosition position, Capability<T> cap)
	{
		final State state = ctx.getState();
		if(cap==ForgeCapabilities.ENERGY&&(position.side()==null||ENERGY_POS.contains(position)))
		{
			return state.energyCap.cast(ctx);
		}

		if(cap==ForgeCapabilities.FLUID_HANDLER)
		{
			if(FLUID_INPUTS.contains(position.posInMultiblock()))
			{
				if(position.side()!=null)
				{
					// Things are strange, it detects things BACKWARDS, so RIGHT is the 'LEFT' Input and so on.
					if(position.side().equals(RelativeBlockFace.RIGHT)) return state.inputCapLeft.cast(ctx);
					if(position.side().equals(RelativeBlockFace.FRONT)) return state.inputCapBack.cast(ctx);
					if(position.side().equals(RelativeBlockFace.LEFT)) return state.inputCapRight.cast(ctx);
				}
			}

			if(FLUID_OUTPUT_CAP.equals(position))
			{
				return state.outputCap.cast(ctx);
			}
		}

		if(cap==ForgeCapabilities.ITEM_HANDLER)
		{
			if(position.posInMultiblock().equals(ITEM_INPUT))
			{
				return state.itemInputCap.cast(ctx);
			}
		}

		return LazyOptional.empty();
	}

	private void drainOutputTank(ChemicalReactorLogic.State state, IMultiblockContext<ChemicalReactorLogic.State> context, CapabilityReference<IFluidHandler> outputRef)
	{
		int outSize = Math.min(FluidType.BUCKET_VOLUME, state.tanks.output.getFluidAmount());
		FluidStack out = Utils.copyFluidStackWithAmount(state.tanks.output.getFluid(), outSize, false);
		IFluidHandler output = outputRef.getNullable();

		if(output==null)
			return;

		int accepted = output.fill(out, FluidAction.SIMULATE);
		if(accepted > 0)
		{
			int drained = output.fill(Utils.copyFluidStackWithAmount(out, Math.min(out.getAmount(), accepted), false), FluidAction.EXECUTE);
			state.tanks.output.drain(drained, FluidAction.EXECUTE);
			context.markMasterDirty();
			context.requestMasterBESync();
		}
	}

	@Override
	public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType)
	{
		return ChemicalReactorShape.GETTER;
	}

	@Nullable
	@Override
	public List<Component> getOverlayText(State state, Player player, IMultiblockBEHelper<ChemicalReactorLogic.State> helper)
	{

		BlockPos posInMultiblock = helper.getPositionInMB();

		if(TANK_LEFT_POSITIONS.contains(posInMultiblock)) return List.of(TextUtils.formatFluidStack(state.tanks.leftInput.getFluid()));
		if(TANK_BACK_POSITIONS.contains(posInMultiblock)) return List.of(TextUtils.formatFluidStack(state.tanks.backInput.getFluid()));
		if(TANK_RIGHT_POSITIONS.contains(posInMultiblock)) return List.of(TextUtils.formatFluidStack(state.tanks.rightInput.getFluid()));
		if(TANK_FRONT_POSITIONS.contains(posInMultiblock)) return List.of(TextUtils.formatFluidStack(state.tanks.output.getFluid()));
		if(REACTOR_CHAMBER_POSITIONS.contains(posInMultiblock)) {
			ItemStack input = state.inventory.getStackInSlot(0);
			if(!input.isEmpty()) return List.of(Component.literal(input.toString()));
		}
		return List.of();
	}

	private static Set<BlockPos> generateBlockPositions(BlockPos bottomLeft, BlockPos topRight) {
		Set<BlockPos> positions = new HashSet<>();

		for (int x = bottomLeft.getX(); x <= topRight.getX(); x++) {
			for (int y = bottomLeft.getY(); y <= topRight.getY(); y++) {
				for (int z = bottomLeft.getZ(); z <= topRight.getZ(); z++) {
					positions.add(new BlockPos(x, y, z));
				}
			}
		}

		return positions;
	}

	public static class State implements IMultiblockState, ProcessContext.ProcessContextInMachine<ChemicalRecipe>
	{
		public final AveragingEnergyStorage energy = new AveragingEnergyStorage(8192);
		public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();
		public final ChemicalReactorTanks tanks = new ChemicalReactorTanks();
		private final MultiblockProcessor.InMachineProcessor<ChemicalRecipe> processor;
		private final StoredCapability<IEnergyStorage> energyCap;
		private final StoredCapability<IFluidHandler> outputCap;

		public final SlotwiseItemHandler inventory;
		private final StoredCapability<IItemHandler> itemInputCap;
		private final CapabilityReference<IItemHandler> output;
		private final CapabilityReference<IItemHandler> input_output;
		private final StoredCapability<IItemHandler> outputHandler;

		private final Supplier<ChemicalRecipe> cachedRecipe;
		private final StoredCapability<IFluidHandler> inputCapLeft;
		private final StoredCapability<IFluidHandler> inputCapBack;
		private final StoredCapability<IFluidHandler> inputCapRight;

		private final CapabilityReference<IFluidHandler> fluidOutput;

		public State(IInitialMultiblockContext<State> ctx)
		{
			final Supplier<@Nullable Level> getLevel = ctx.levelSupplier();
			final Runnable markDirty = ctx.getMarkDirtyRunnable();
			this.energyCap = new StoredCapability<>(this.energy);
			this.inventory = new SlotwiseItemHandler(List.of(
					new IOConstraint(true, i -> ChemicalRecipe.acceptableCatalyst(getLevel.get(), i)),
					IOConstraint.OUTPUT), ctx.getMarkDirtyRunnable());

			this.output = ctx.getCapabilityAt(ForgeCapabilities.ITEM_HANDLER, ITEM_OUTPUT);
			this.input_output = ctx.getCapabilityAt(ForgeCapabilities.ITEM_HANDLER, ITEM_INPUT_OUTPUT);

			this.outputHandler = new StoredCapability<>(new WrappingItemHandler(
					inventory, false, true, new IntRange(1, 2)
			));

			this.fluidOutput = ctx.getCapabilityAt(ForgeCapabilities.FLUID_HANDLER, new MultiblockFace(FLUID_OUTPUT_CAP.side(), FLUID_OUTPUT_CAP.posInMultiblock().south()));
			this.processor = new InMachineProcessor<>(1, 0, 1, ctx.getMarkDirtyRunnable(), ChemicalRecipe.RECIPES::getById);

			this.inputCapLeft = new StoredCapability<>(new ArrayFluidHandler(true, true, markDirty, this.tanks.leftInput));
			this.inputCapBack = new StoredCapability<>(new ArrayFluidHandler(true, true, markDirty, this.tanks.backInput));
			this.inputCapRight = new StoredCapability<>(new ArrayFluidHandler(true, true, markDirty, this.tanks.rightInput));
			this.outputCap = new StoredCapability<>(ArrayFluidHandler.drainOnly(this.tanks.output, markDirty));
			cachedRecipe = () -> ChemicalRecipe.findRecipe(getLevel.get(), tanks.leftInput.getFluid(), tanks.backInput.getFluid(), tanks.rightInput.getFluid(), inventory.getStackInSlot(0));
			this.itemInputCap = new StoredCapability<>(this.inventory);
		}

		@Override
		public void readSaveNBT(CompoundTag nbt)
		{
			this.energy.deserializeNBT(nbt.get("energy"));
			this.tanks.readNBT(nbt.getCompound("tanks"));
			this.inventory.deserializeNBT(nbt.getCompound("inventory"));
			this.processor.fromNBT(nbt.get("processor"), MultiblockProcessInMachine::new);
		}

		@Override
		public void writeSaveNBT(CompoundTag nbt)
		{
			nbt.put("energy", this.energy.serializeNBT());
			nbt.put("tanks", this.tanks.toNBT());
			nbt.put("processor", this.processor.toNBT());
			nbt.put("inventory", this.inventory.serializeNBT());
		}

		@Override
		public void writeSyncNBT(CompoundTag nbt)
		{
			writeSaveNBT(nbt);
		}

		@Override
		public void readSyncNBT(CompoundTag nbt)
		{
			readSaveNBT(nbt);
		}

		@Override
		public AveragingEnergyStorage getEnergy()
		{
			return energy;
		}

		@Override
		public boolean additionalCanProcessCheck(MultiblockProcess<ChemicalRecipe, ?> process, Level level)
		{
			ChemicalRecipe recipe = process.getRecipe(level);
			if(recipe==null) return false;

			boolean fluid_pass = false;

			for(IFluidTank fluid : getInternalTanks())
			{
				for(FluidTagInput f : recipe.fluidIn)
				{
					if(f.test(fluid.getFluid()))
					{
						fluid_pass = fluid.getFluidAmount() >= f.getAmount();
					}
				}
			}

			boolean item_pass = getInventory().insertItem(1, recipe.itemOutput, true) != ItemStack.EMPTY;

			return fluid_pass && item_pass;
		}

		@Override
		public void onProcessFinish(MultiblockProcess<ChemicalRecipe, ?> process, Level level)
		{
		}

		public void processOutput(ItemStack itemStack, IMultiblockLevel level){
			ItemStack stack = Utils.insertStackIntoInventory(this.output, itemStack.copyWithCount(1), true);
			if (stack.isEmpty()) {
				Utils.insertStackIntoInventory(this.output, itemStack.copyWithCount(1), false);
				itemStack.shrink(1);
			}
		}

		@Override
		public IFluidTank[] getInternalTanks()
		{
			return new FluidTank[]{tanks.leftInput, tanks.backInput, tanks.rightInput, tanks.output};
		}

		@Override
		public int[] getOutputTanks()
		{
			return new int[]{3};
		}

		@Override
		public int[] getOutputSlots()
		{
			return new int[]{1};
		}

		@Override
		public SlotwiseItemHandler getInventory()
		{
			return inventory;
		}

		public @Nullable ChemicalRecipe getRecipeForInputs(Level level)
		{
			return ChemicalRecipe.findRecipe(level, tanks.leftInput.getFluid(), tanks.backInput.getFluid(), tanks.rightInput.getFluid(), inventory.getStackInSlot(0));
		}
	}

	public record ChemicalReactorTanks(FluidTank leftInput, FluidTank rightInput, FluidTank backInput, FluidTank output)
	{
		private static final int TANK_BUFFER_CAPACITY = FluidType.BUCKET_VOLUME*32;

		public ChemicalReactorTanks()
		{
			this(new FluidTank(TANK_BUFFER_CAPACITY), new FluidTank(TANK_BUFFER_CAPACITY), new FluidTank(TANK_BUFFER_CAPACITY), new FluidTank(TANK_BUFFER_CAPACITY));
		}

		public ChemicalReactorTanks(FluidTank leftInput, FluidTank rightInput, FluidTank backInput, FluidTank output)
		{
			this.leftInput = leftInput;
			this.rightInput = rightInput;
			this.backInput = backInput;
			this.output = output;
		}

		public Tag toNBT()
		{
			CompoundTag tag = new CompoundTag();
			tag.put("leftIn", this.leftInput.writeToNBT(new CompoundTag()));
			tag.put("rightIn", this.rightInput.writeToNBT(new CompoundTag()));
			tag.put("backIn", this.backInput.writeToNBT(new CompoundTag()));
			tag.put("out", this.output.writeToNBT(new CompoundTag()));
			return tag;
		}

		public void readNBT(CompoundTag tag)
		{
			this.leftInput.readFromNBT(tag.getCompound("leftIn"));
			this.rightInput.readFromNBT(tag.getCompound("rightIn"));
			this.backInput.readFromNBT(tag.getCompound("backIn"));
			this.output.readFromNBT(tag.getCompound("out"));
		}

		public FluidTank leftInput()
		{
			return this.leftInput;
		}

		public FluidTank rightInput()
		{
			return this.rightInput;
		}

		public FluidTank backInput()
		{
			return this.backInput;
		}

		public FluidTank output()
		{
			return this.output;
		}

		public BlockPos getLeftTankPos(boolean isMirrored)
		{
			BlockPos pos = new BlockPos(-4, 1, 0);
			if(isMirrored) pos = new BlockPos(3, 1, 0);
			return pos;
		}

		public BlockPos getRightTankPos(boolean isMirrored)
		{
			BlockPos pos = new BlockPos(3, 1, 1);
			if(isMirrored) pos = new BlockPos(-4, 1, 1);
			return pos;
		}

		public BlockPos getBackTankPos(boolean isMirrored)
		{
			BlockPos pos = new BlockPos(0, 1, -3);
			if(isMirrored) pos = new BlockPos(-1, 1, -3);
			return pos;
		}

		public BlockPos getOutputTankPos(boolean isMirrored)
		{
			BlockPos pos = new BlockPos(-1, 1, 4);
			if(isMirrored) pos = new BlockPos(0, 1, 4);
			return pos;
		}

		public int getCapacity()
		{
			return TANK_BUFFER_CAPACITY;
		}
	}

}