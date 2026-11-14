/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.logic;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelper;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.*;
import blusunrize.immersiveengineering.client.utils.TextUtils;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInMachine;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor.InMachineProcessor;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext.ProcessContextInMachine;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler.IOConstraint;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler.IntRange;
import com.igteam.immersivegeology.common.block.helper.IGReceiveOnlyEnergy;
import com.igteam.immersivegeology.common.block.multiblocks.logic.ChemicalReactorLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGMultiblockState;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGPositionalOverlayText;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.ChemicalRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.ChemicalReactorShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage;
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
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
	private static final Set<BlockPos> TANK_RIGHT_POSITIONS;
	private static final Set<BlockPos> TANK_BACK_POSITIONS;
	private static final Set<BlockPos> TANK_FRONT_POSITIONS;
	private static final Set<BlockPos> REACTOR_CHAMBER_POSITIONS;
	public static final int ENERGY_CAPACITY = 64000;

	static
	{
		ITEM_INPUT = new BlockPos(4, 5, 4);
		ENERGY_POS = Set.of(new CapabilityPosition(6, 1, 6, RelativeBlockFace.UP));
		FLUID_OUTPUT = new MultiblockFace(5, 0, 8, RelativeBlockFace.BACK);
		ITEM_OUTPUT = new MultiblockFace(3, 0, 8, RelativeBlockFace.BACK);
		ITEM_INPUT_OUTPUT = new MultiblockFace(4, -1, 4, RelativeBlockFace.UP);
		FLUID_OUTPUT_CAP = new CapabilityPosition(5, 0, 8, RelativeBlockFace.BACK);
		FLUID_INPUT_CAPS = Set.of(new CapabilityPosition(0, 0, 5, RelativeBlockFace.RIGHT),
				new CapabilityPosition(8, 0, 3, RelativeBlockFace.LEFT),
				new CapabilityPosition(3, 0, 0, RelativeBlockFace.FRONT));
		FLUID_INPUTS = FLUID_INPUT_CAPS.stream().map(CapabilityPosition::posInMultiblock).collect(Collectors.toSet());
		TANK_LEFT_POSITIONS = generateBlockPositions(new BlockPos(0,1,3), new BlockPos(1,3,4));
		TANK_RIGHT_POSITIONS = generateBlockPositions(new BlockPos(7,1,4), new BlockPos(8,3,5));
		TANK_BACK_POSITIONS = generateBlockPositions(new BlockPos(4,1,0), new BlockPos(5,3,1));
		TANK_FRONT_POSITIONS = generateBlockPositions(new BlockPos(3,1,7), new BlockPos(4,3,8));
		REACTOR_CHAMBER_POSITIONS = generateBlockPositions(new BlockPos(3,1,3), new BlockPos(5,4,5));
	}

	@Override
	public void dropExtraItems(State state, Consumer<ItemStack> drop)
	{
		MBInventoryUtils.dropItems(state.getInventory(), drop);
	}

	@Override
	public void tickServer(IMultiblockContext<State> ctx)
	{
		State state = ctx.getState();
		boolean isEnabled = state.rsState.isEnabled(ctx);
		if(isEnabled) insertRecipeToProcess(state, ctx);

		state.processor.tickServer(state, ctx.getLevel(), state.rsState.isEnabled(ctx));

		if(state.tanks.output.getFluid().getAmount() > 0)
		{
			drainOutputTank(state, ctx, state.fluidOutput);
		}

		if(!state.rsState.isEnabled(ctx))
		{
			// If we have no valid recipe, we attempt to 'output' the items in the input inventory, just in case the user put wrong items for a recipe in.
			// E.G wanted to switch recipe without pumping fluid out.
			ItemStack itemStack = state.inventory.getStackInSlot(0);
			if(!itemStack.isEmpty() && state.processor.getQueue().stream().noneMatch((q) ->
			{
				ChemicalRecipe rec = q.getRecipe(ctx.getLevel().getRawLevel());
				if(rec != null) return rec.itemInput.testIgnoringSize(itemStack);
				return true;
			}))
			{
				ItemStack stack = Utils.insertStackIntoInventory(state.input_output, itemStack.copyWithCount(1), true);
				if(stack.isEmpty())
				{
					Utils.insertStackIntoInventory(state.input_output, itemStack.copyWithCount(1), false);
					itemStack.shrink(1);
				}
			}
			if(state.processor.getQueueSize() > 0)
			{
				state.clearProcessor();
			}
		}

		ctx.requestMasterBESync();
	}

	private static void insertRecipeToProcess(State state, IMultiblockContext<State>  ctx)
	{
		IMultiblockLevel mbLevel = ctx.getLevel();
		Level level = mbLevel.getRawLevel();
		ChemicalReactorTanks fluidTanks = state.tanks;
		RecipeHolder<ChemicalRecipe> recipe = state.getRecipeForInputs(level);
		if(recipe!=null)
		{
			MultiblockProcessInMachine<ChemicalRecipe> process = new MultiblockProcessInMachine<>(recipe, 0);
			process.setInputAmounts(recipe.value().itemInput.getCount());
			int size = (fluidTanks.leftInput.isEmpty()?0: 1)
					+(fluidTanks.backInput.isEmpty()?0: 1)
					+(fluidTanks.rightInput.isEmpty()?0: 1);

			int[] intArray = new int[size];
			int index = 0;

			if(!fluidTanks.leftInput.isEmpty()) intArray[index++] = 0;
			if(!fluidTanks.backInput.isEmpty()) intArray[index++] = 1;
			if(!fluidTanks.rightInput.isEmpty()) intArray[index] = 2;
			ItemStack inputStack = state.inventory.getStackInSlot(0).copy();
			int recipeInputRequirements = 0;
			List<MultiblockProcess<ChemicalRecipe, ProcessContextInMachine<ChemicalRecipe>>> processQueue = state.processor.getQueue();

			for(MultiblockProcess<ChemicalRecipe, ProcessContextInMachine<ChemicalRecipe>> p : processQueue)
			{
				ChemicalRecipe checkRecipe = p.getRecipe(level);
				if(checkRecipe == null) continue;
				IngredientWithSize input = checkRecipe.itemInput;
				if(input.testIgnoringSize(inputStack))
				{
					recipeInputRequirements += input.getCount();
				}
			}

			boolean hasInputForNewRecipe = inputStack.getCount() >= (recipeInputRequirements + recipe.value().itemInput.getCount());

			if(hasInputForNewRecipe)
			{
				process.setInputTanks(intArray);
				if(state.processor.addProcessToQueue(process, level, true))
				{
					state.processor.addProcessToQueue(process, level, false);
					ctx.markMasterDirty();
				}
			}
		}
		else
		{
			if(state.processor.getQueueSize() > 0)
			{
				state.clearProcessor();
			}
		}
	}

	@Override
	public State createInitialState(IInitialMultiblockContext<State> capability)
	{
		return new ChemicalReactorLogic.State(capability);
	}

	@Override
	public void registerCapabilities(CapabilityRegistrar<State> register)
	{
		register.register(EnergyStorage.BLOCK, (state, position) ->
				position.side()==null||ENERGY_POS.contains(position)?state.energyCap:null);
		register.register(FluidHandler.BLOCK, (state, position) -> {
			if(FLUID_INPUT_CAPS.contains(position)&&position.side()!=null)
			{
				// Things are strange, it detects things BACKWARDS, so RIGHT is the 'LEFT' Input and so on.
				if(position.side()==RelativeBlockFace.LEFT) return state.inputCapRight;
				if(position.side()==RelativeBlockFace.RIGHT) return state.inputCapLeft;
				if(position.side()==RelativeBlockFace.FRONT) return state.inputCapBack;
			}
			if(FLUID_OUTPUT_CAP.equals(position)) return state.outputCap;
			return null;
		});
		register.register(ItemHandler.BLOCK, (state, position) -> {
			if(position.posInMultiblock().equals(ITEM_INPUT)) return state.itemInputCap;
			if(position.posInMultiblock().equals(ITEM_OUTPUT.posInMultiblock())&&position.side()==ITEM_OUTPUT.face())
				return state.outputHandler;
			return null;
		});
	}

	private void drainOutputTank(ChemicalReactorLogic.State state, IMultiblockContext<ChemicalReactorLogic.State> context, Supplier<@Nullable IFluidHandler> outputRef)
	{
		int outSize = Math.min(FluidType.BUCKET_VOLUME, state.tanks.output.getFluidAmount());
		FluidStack out = Utils.copyFluidStackWithAmount(state.tanks.output.getFluid(), outSize, false);
		IFluidHandler output = outputRef.get();

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
		if(state == null) return List.of();

		BlockPos posInMultiblock = helper.getPositionInMB();

		if(TANK_LEFT_POSITIONS.contains(posInMultiblock)) return List.of(TextUtils.formatFluidStack(state.tanks.leftInput.getFluid()));
		if(TANK_BACK_POSITIONS.contains(posInMultiblock)) return List.of(TextUtils.formatFluidStack(state.tanks.backInput.getFluid()));
		if(TANK_RIGHT_POSITIONS.contains(posInMultiblock)) return List.of(TextUtils.formatFluidStack(state.tanks.rightInput.getFluid()));
		if(TANK_FRONT_POSITIONS.contains(posInMultiblock)) return List.of(TextUtils.formatFluidStack(state.tanks.output.getFluid()));
		if(REACTOR_CHAMBER_POSITIONS.contains(posInMultiblock)) {
			ItemStack input = state.inventory.getStackInSlot(0);
			if(!input.isEmpty()) return List.of(Component.literal(input.getCount() + " " + input.getHoverName().getString()), Component.literal("Processing: " + state.processor.getQueueSize() + " / " + state.processor.getMaxQueueSize()));
		}
		return List.of(Component.literal("Processing: " + state.processor.getQueueSize() + " / " + state.processor.getMaxQueueSize()));
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

	public static class State implements IGMultiblockState, ProcessContext.ProcessContextInMachine<ChemicalRecipe>
	{
		public final AveragingEnergyStorage energy = new AveragingEnergyStorage(ENERGY_CAPACITY);
		public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();
		public final ChemicalReactorTanks tanks = new ChemicalReactorTanks();
		private final MultiblockProcessor.InMachineProcessor<ChemicalRecipe> processor;
		private final IEnergyStorage energyCap;
		private final IFluidHandler outputCap;

		public final SlotwiseItemHandler inventory;
		private final IItemHandler itemInputCap;
		private final Supplier<@Nullable IItemHandler> input_output;
		private final IItemHandler outputHandler;

		private final Supplier<RecipeHolder<ChemicalRecipe>> cachedRecipe;
		private final IFluidHandler inputCapLeft;
		private final IFluidHandler inputCapBack;
		private final IFluidHandler inputCapRight;

		private final Supplier<@Nullable IFluidHandler> fluidOutput;

		public State(IInitialMultiblockContext<State> ctx)
		{
			final Supplier<@Nullable Level> getLevel = ctx.levelSupplier();
			final Runnable markDirty = ctx.getMarkDirtyRunnable();
			this.energyCap = IGReceiveOnlyEnergy.of(this.energy);

			this.inventory = new SlotwiseItemHandler(List.of(
					new IOConstraint(true, i -> ChemicalRecipe.acceptableCatalyst(getLevel.get(), i)),
					IOConstraint.OUTPUT
			), ctx.getMarkDirtyRunnable());
			this.input_output = ctx.getCapabilityAt(Capabilities.ItemHandler.BLOCK, ITEM_INPUT_OUTPUT);

			this.outputHandler = new WrappingItemHandler(
					inventory, false, true, new IntRange(1, 2)
			);

			this.fluidOutput = ctx.getCapabilityAt(Capabilities.FluidHandler.BLOCK, new MultiblockFace(FLUID_OUTPUT_CAP.side(), FLUID_OUTPUT_CAP.posInMultiblock().south()));
			this.processor = new InMachineProcessor<>(4, 0, 4, ctx.getMarkDirtyRunnable(), ChemicalRecipe.RECIPES::getById);

			this.inputCapLeft = new ArrayFluidHandler(true, true, markDirty, this.tanks.leftInput);
			this.inputCapBack = new ArrayFluidHandler(true, true, markDirty, this.tanks.backInput);
			this.inputCapRight = new ArrayFluidHandler(true, true, markDirty, this.tanks.rightInput);
			this.outputCap = ArrayFluidHandler.drainOnly(this.tanks.output, markDirty);
			cachedRecipe = () -> ChemicalRecipe.findRecipe(getLevel.get(), tanks.leftInput.getFluid(), tanks.backInput.getFluid(), tanks.rightInput.getFluid(), inventory.getStackInSlot(0));
			this.itemInputCap = this.inventory;
		}

		@Override
		public void readSaveNBT(CompoundTag nbt, HolderLookup.Provider provider)
		{
			this.energy.deserializeNBT(provider, nbt.get("energy"));
			this.tanks.readNBT(nbt.getCompound("tanks"), provider);
			this.inventory.deserializeNBT(provider, nbt.getCompound("inventory"));
			this.processor.fromNBT(nbt.get("processor"), (getRecipe, data, p) -> new MultiblockProcessInMachine<>(getRecipe, data), provider);
		}

		public void clearProcessor()
		{
			this.processor.clear();
		}

		@Override
		public void writeSaveNBT(CompoundTag nbt, HolderLookup.Provider provider)
		{
			nbt.put("energy", this.energy.serializeNBT(provider));
			nbt.put("tanks", this.tanks.toNBT(provider));
			nbt.put("processor", this.processor.toNBT(provider));
			nbt.put("inventory", this.inventory.serializeNBT(provider));
		}

		@Override
		public void writeSyncNBT(CompoundTag nbt, HolderLookup.Provider provider)
		{
			writeSaveNBT(nbt, provider);
		}

		@Override
		public void readSyncNBT(CompoundTag nbt, HolderLookup.Provider provider)
		{
			readSaveNBT(nbt, provider);
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
				for(SizedFluidIngredient f : recipe.fluidIn)
				{
					if(f.test(fluid.getFluid()))
					{
						fluid_pass = fluid.getFluidAmount() >= f.amount();
					}
				}
			}

			boolean item_pass = recipe.itemOutput.get().isEmpty() || getInventory().getRawHandler().insertItem(1, recipe.itemOutput.get(), true).isEmpty();

			return fluid_pass && item_pass;
		}

		@Override
		public IFluidTank[] getInternalTanks()
		{
			return new FluidTank[]{tanks.leftInput, tanks.backInput, tanks.rightInput, tanks.output};
		}

		public ChemicalReactorTanks getChemicalReactorTanks()
		{
			return this.tanks;
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

		public @Nullable RecipeHolder<ChemicalRecipe> getRecipeForInputs(Level level)
		{
			return ChemicalRecipe.findRecipe(level, tanks.leftInput.getFluid(), tanks.backInput.getFluid(), tanks.rightInput.getFluid(), inventory.getStackInSlot(0));
		}

		@Override
		public void invalidate(@NotNull IMultiblockContext<?> context)
		{
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

		public Tag toNBT(HolderLookup.Provider provider)
		{
			CompoundTag tag = new CompoundTag();
			tag.put("leftIn", this.leftInput.writeToNBT(provider, new CompoundTag()));
			tag.put("rightIn", this.rightInput.writeToNBT(provider, new CompoundTag()));
			tag.put("backIn", this.backInput.writeToNBT(provider, new CompoundTag()));
			tag.put("out", this.output.writeToNBT(provider, new CompoundTag()));
			return tag;
		}

		public void readNBT(CompoundTag tag, HolderLookup.Provider provider)
		{
			this.leftInput.readFromNBT(provider, tag.getCompound("leftIn"));
			this.rightInput.readFromNBT(provider, tag.getCompound("rightIn"));
			this.backInput.readFromNBT(provider, tag.getCompound("backIn"));
			this.output.readFromNBT(provider, tag.getCompound("out"));
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