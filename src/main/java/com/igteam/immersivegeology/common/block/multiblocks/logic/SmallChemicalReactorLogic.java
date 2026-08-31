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
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.*;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
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
import com.igteam.immersivegeology.common.block.multiblocks.logic.ChemicalReactorLogic.ChemicalReactorTanks;
import com.igteam.immersivegeology.common.block.multiblocks.logic.SmallChemicalReactorLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.BasicChemicalProcessor;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGMultiblockState;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.ISkinnableMultiblockLogic;
import com.igteam.immersivegeology.common.block.multiblocks.part.SmallChemicalReactorPart;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BasicChemicalRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.ChemicalRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.ChemicalRepairRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.SmallChemicalReactorShape;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MiscEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SmallChemicalReactorLogic implements ISkinnableMultiblockLogic<State>, IServerTickableComponent<SmallChemicalReactorLogic.State>, IClientTickableComponent<SmallChemicalReactorLogic.State> {
    public static final BlockPos REDSTONE_IN = new BlockPos(0,1,2);
    public static final int ENERGY_CAPACITY = 24000;
    private static final Set<CapabilityPosition> ENERGY_POS;
    private static final MultiblockFace ITEM_OUTPUT, ITEM_INPUT_OUTPUT;
    private static final MultiblockFace FLUID_OUTPUT;
    private static final CapabilityPosition FLUID_OUTPUT_CAP;
    private static final Set<CapabilityPosition> FLUID_INPUT_CAPS;
    private static final Set<BlockPos> FLUID_INPUTS;
    private static final BlockPos ITEM_INPUT;

    static
    {
        ITEM_INPUT = new BlockPos(2, 0, 0);
        ENERGY_POS = Set.of(new CapabilityPosition(0, 1, 0, RelativeBlockFace.UP));
        FLUID_OUTPUT = new MultiblockFace(3, 0, 0, RelativeBlockFace.LEFT);
        ITEM_OUTPUT = new MultiblockFace(2, 0, 2, RelativeBlockFace.BACK);

        ITEM_INPUT_OUTPUT = new MultiblockFace(2, 0, 2, RelativeBlockFace.BACK);

        FLUID_OUTPUT_CAP = new CapabilityPosition(3, 0, 0, RelativeBlockFace.LEFT);

        FLUID_INPUT_CAPS = Set.of(
                new CapabilityPosition(3, 0, 2, RelativeBlockFace.LEFT),
                new CapabilityPosition(0, 0, 1, RelativeBlockFace.RIGHT));

        FLUID_INPUTS = FLUID_INPUT_CAPS.stream().map(CapabilityPosition::posInMultiblock).collect(Collectors.toSet());
    }

    @Override
    public void tickClient(IMultiblockContext<State> iMultiblockContext) {

    }

    @Override
    public void tickServer(IMultiblockContext<State> ctx) {
        SmallChemicalReactorLogic.State state = ctx.getState();
        Level rawLevel = ctx.getLevel().getRawLevel();
        boolean isMirrored = ctx.getLevel().getOrientation().mirrored();
        boolean isEnabled = state.rsState.isEnabled(ctx);
        ItemStack repairStack = state.inventory.getStackInSlot(2);
        if(!repairStack.isEmpty())
        {
            boolean validRepair = ChemicalRepairRecipe.isValidRepairItem(rawLevel, repairStack);
            if(validRepair)
            {
                int repairAmount = ChemicalRepairRecipe.getRepairAmount(rawLevel, repairStack);
                if(state.damage >= repairAmount)
                {
                    repairStack.shrink(1);
                    if(repairStack.getCount() == 0) repairStack = ItemStack.EMPTY;
                    state.inventory.setStackInSlot(2, repairStack);
                    state.damage -= repairAmount;
                    ctx.requestMasterBESync();
                }
            }
        }

        if(state.tanks.output.getFluid().getAmount() > 0)
        {
            drainOutputTank(state, ctx, state.fluidOutput);
        }

        if(state.damage > 99) return;


        if(state.tanks.output.getSpace() == 0) return;

        if(isEnabled) insertRecipeToProcess(state, ctx);
        state.processor.tickServer(state, ctx.getLevel(), state.rsState.isEnabled(ctx));


        if(!state.rsState.isEnabled(ctx))
        {
            // If we have no valid recipe, we attempt to 'output' the items in the input inventory, just in case the user put wrong items for a recipe in.
            // E.G wanted to switch recipe without pumping fluid out.
            ItemStack itemStack = state.inventory.getStackInSlot(0);
            if(!itemStack.isEmpty() && state.processor.getQueue().stream().noneMatch((q) ->
            {
                BasicChemicalRecipe rec = q.getRecipe(ctx.getLevel().getRawLevel());
                if(rec != null) return rec.itemInput.testIgnoringSize(itemStack);
                return true;
            }))
            {
                ItemStack stack = Utils.insertStackIntoInventory(state.input_output, itemStack.copyWithCount(1), true);
                if(stack.isEmpty())
                {
                    Utils.insertStackIntoInventory(state.input_output, itemStack.copyWithCount(1), false);
                    itemStack.shrink(1);

                    ctx.requestMasterBESync();
                }
            }
            if(state.processor.getQueueSize() > 0)
            {
                state.clearProcessor();
                ctx.requestMasterBESync();
            }
        }
    }

    private static void insertRecipeToProcess(State state, IMultiblockContext<SmallChemicalReactorLogic.State>  ctx)
    {
        IMultiblockLevel mbLevel = ctx.getLevel();
        Level level = mbLevel.getRawLevel();
        SmallChemicalReactorTanks fluidTanks = state.tanks;
        BasicChemicalRecipe recipe = state.getRecipeForInputs(level);
        if(recipe!=null)
        {
            MultiblockProcessInMachine<BasicChemicalRecipe> process = new MultiblockProcessInMachine<>(recipe, 0);
            process.setInputAmounts(recipe.itemInput.getCount());
            int size = (fluidTanks.leftInput().isEmpty()?0: 1)
                    +(fluidTanks.rightInput().isEmpty()?0: 1);

            int[] intArray = new int[size];
            int index = 0;

            if(!fluidTanks.leftInput().isEmpty()) intArray[index++] = 0;
            if(!fluidTanks.rightInput().isEmpty()) intArray[index] = 1;
            ItemStack inputStack = state.inventory.getStackInSlot(0).copy();
            int recipeInputRequirements = 0;
            List<MultiblockProcess<BasicChemicalRecipe, ProcessContextInMachine<BasicChemicalRecipe>>> processQueue = state.processor.getQueue();

            for(MultiblockProcess<BasicChemicalRecipe, ProcessContextInMachine<BasicChemicalRecipe>> p : processQueue)
            {
                BasicChemicalRecipe checkRecipe = p.getRecipe(level);
                if(checkRecipe == null) continue;
                IngredientWithSize input = checkRecipe.itemInput;
                if(input.testIgnoringSize(inputStack))
                {
                    recipeInputRequirements += input.getCount();
                }
            }

            boolean hasInputForNewRecipe = inputStack.getCount() >= (recipeInputRequirements + recipe.itemInput.getCount());

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
                ctx.requestMasterBESync();
            }
        }
    }

    private void drainOutputTank(SmallChemicalReactorLogic.State state, IMultiblockContext<SmallChemicalReactorLogic.State> context, CapabilityReference<IFluidHandler> outputRef)
    {
        int outSize = Math.min(FluidType.BUCKET_VOLUME, state.tanks.output().getFluidAmount());
        FluidStack out = Utils.copyFluidStackWithAmount(state.tanks.output().getFluid(), outSize, false);
        IFluidHandler output = outputRef.getNullable();

        if(output==null)
            return;

        int accepted = output.fill(out, FluidAction.SIMULATE);
        if(accepted > 0)
        {
            int drained = output.fill(Utils.copyFluidStackWithAmount(out, Math.min(out.getAmount(), accepted), false), FluidAction.EXECUTE);
            state.tanks.output().drain(drained, FluidAction.EXECUTE);
            context.markMasterDirty();
            context.requestMasterBESync();
        }
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
            if(FLUID_INPUT_CAPS.contains(position))
            {
                if(position.side()!=null)
                {
                    if(position.side().equals(RelativeBlockFace.LEFT)) return state.inputCapFront.cast(ctx);
                    if(position.side().equals(RelativeBlockFace.RIGHT)) return state.inputCapBack.cast(ctx);
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
            if(position.posInMultiblock().equals(ITEM_OUTPUT.posInMultiblock()) && position.side() == ITEM_OUTPUT.face()){
                return state.outputHandler.cast(ctx);
            }
        }

        return LazyOptional.empty();
    }

    @Override
    public State createInitialState(IInitialMultiblockContext<State> capability) {
        return new SmallChemicalReactorLogic.State(capability);
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return SmallChemicalReactorShape.GETTER;
    }

    public static class State implements IGMultiblockState, ProcessContext.ProcessContextInMachine<BasicChemicalRecipe> {
        public final AveragingEnergyStorage energy = new AveragingEnergyStorage(ENERGY_CAPACITY);
        public float damage;
        private boolean isInvalidated = false;
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();

        public final SlotwiseItemHandler inventory;
        public final SmallChemicalReactorTanks tanks = new SmallChemicalReactorTanks();
        private final StoredCapability<IFluidHandler> inputCapBack;
        private final StoredCapability<IFluidHandler> inputCapFront;
        private final StoredCapability<IItemHandler> itemInputCap;
        private final StoredCapability<IItemHandler> outputHandler;
        private final StoredCapability<IFluidHandler> outputCap;
        private final StoredCapability<IEnergyStorage> energyCap;
        private final CapabilityReference<IItemHandler> input_output;
        private final CapabilityReference<IFluidHandler> fluidOutput;

        private final BasicChemicalProcessor processor;
        private final MultiblockProcessor.InMachineProcessor<BasicChemicalRecipe> dummy;


        public State(IInitialMultiblockContext<State> ctx){
            final Supplier<@Nullable Level> getLevel = ctx.levelSupplier();
            final Runnable markDirty = ctx.getMarkDirtyRunnable();

            this.damage = 0;
            this.energyCap = new StoredCapability<>(IGReceiveOnlyEnergy.of(this.energy));
            this.inventory = new SlotwiseItemHandler(List.of(
                    new IOConstraint(true, i -> BasicChemicalRecipe.acceptableCatalyst(getLevel.get(), i)),
                    IOConstraint.OUTPUT,
                    new IOConstraint(true, i -> ChemicalRepairRecipe.isValidRepairItem(getLevel.get(), i))
            ), ctx.getMarkDirtyRunnable());
            this.input_output = ctx.getCapabilityAt(ForgeCapabilities.ITEM_HANDLER, ITEM_INPUT_OUTPUT);

            this.outputHandler = new StoredCapability<>(new WrappingItemHandler(
                    inventory, false, true, new IntRange(1, 2)
            ));

            this.fluidOutput = ctx.getCapabilityAt(ForgeCapabilities.FLUID_HANDLER, new MultiblockFace(FLUID_OUTPUT_CAP.side(), FLUID_OUTPUT_CAP.posInMultiblock().east()));
            this.processor = new BasicChemicalProcessor(4, 0, 4, ctx.getMarkDirtyRunnable(), BasicChemicalRecipe.RECIPES::getById);

            this.inputCapBack = new StoredCapability<>(new ArrayFluidHandler(true, true, markDirty, this.tanks.leftInput));
            this.inputCapFront = new StoredCapability<>(new ArrayFluidHandler(true, true, markDirty, this.tanks.rightInput));
            this.outputCap = new StoredCapability<>(ArrayFluidHandler.drainOnly(this.tanks.output, markDirty));
            this.itemInputCap = new StoredCapability<>(this.inventory);
            this.dummy = new BasicChemicalProcessor(4, 0, 4, ctx.getMarkDirtyRunnable(), BasicChemicalRecipe.RECIPES::getById);
        }

        public @Nullable BasicChemicalRecipe getRecipeForInputs(Level level)
        {
            return BasicChemicalRecipe.findRecipe(level, tanks.leftInput.getFluid(), tanks.rightInput.getFluid(), inventory.getStackInSlot(0));
        }

        @Override
        public boolean additionalCanProcessCheck(MultiblockProcess<BasicChemicalRecipe, ?> process, Level level)
        {
            return true;
        }

        @Override
        public void readSaveNBT(CompoundTag nbt){
            this.energy.deserializeNBT(nbt.get("energy"));
            this.tanks.readNBT(nbt.getCompound("tanks"));
            this.inventory.deserializeNBT(nbt.getCompound("inventory"));
            this.processor.fromNBT(nbt.get("processor"), MultiblockProcessInMachine::new);
            this.damage = nbt.getFloat("damage");
            this.isInvalidated = nbt.getBoolean("invalid");
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt){
            nbt.put("energy", this.energy.serializeNBT());
            nbt.put("tanks", this.tanks.toNBT());
            nbt.put("processor", this.processor.toNBT());
            nbt.put("inventory", this.inventory.serializeNBT());
            nbt.putFloat("damage", this.damage);
            nbt.putBoolean("invalid", this.isInvalidated);
        }

        public void clearProcessor()
        {
            this.processor.fromNBT(dummy.toNBT(), MultiblockProcessInMachine::new);
        }

        @Override
        public int[] getOutputTanks()
        {
            return new int[]{2};
        }

        @Override
        public int[] getOutputSlots()
        {
            return new int[]{1};
        }

        @Override
        public IFluidTank[] getInternalTanks()
        {
            return new FluidTank[]{tanks.leftInput, tanks.rightInput, tanks.output};
        }

        public SlotwiseItemHandler getInventory()
        {
            return inventory;
        }

        public SmallChemicalReactorTanks getChemicalReactorTanks()
        {
            return this.tanks;
        }

        public AveragingEnergyStorage getEnergy()
        {
            return energy;
        }

		public Supplier<Float> getDamage()
		{
            return () -> this.damage;
		}

        @Override
        public void invalidate(@NotNull IMultiblockContext<?> ctx)
        {
            if(!isInvalidated)
            {
                if(ctx.getState() instanceof State state)
                {
                    Level level = ctx.getLevel().getRawLevel();
                    if(!level.isClientSide&&state.damage > 1)
                    {
                        boolean isMirrored = ctx.getLevel().getOrientation().mirrored();
                        BlockPos realPos = ctx.getLevel().toAbsolute(new BlockPos(isMirrored?2: 1, 1, 0));
                        scatterFailureDebris(level, realPos, state.damage);
                    }
                }
                isInvalidated = true;
            }

            this.energyCap.get(ctx).invalidate();
            this.inputCapBack.get(ctx).invalidate();
            this.inputCapFront.get(ctx).invalidate();
            this.outputHandler.get(ctx).invalidate();
            this.itemInputCap.get(ctx).invalidate();
            this.outputCap.get(ctx).invalidate();
        }

        public static void scatterFailureDebris(Level level, BlockPos origin, float damage)
        {
            if(!level.isAreaLoaded(origin, 1))
                return;

            MutableBlockPos cursor = new MutableBlockPos();
            cursor.set(origin);
            RandomSource random = level.getRandom();
            BlockState debris = MiscEnum.RustyMetal.getBlock(BlockCategoryFlags.SHEETMETAL_BLOCK).defaultBlockState();

            for(int x = 0; x <= 1; x++)
                for(int z = 0; z <= 1; z++)
                    for(int y = 0; y < 4; y++)
                        if(random.nextInt(1, 95) < damage)
                            level.setBlock(cursor.offset(x, y, z), debris, 1|2);
        }
    }

    public record SmallChemicalReactorTanks(FluidTank leftInput, FluidTank rightInput, FluidTank output)
    {
        private static final int TANK_BUFFER_CAPACITY = FluidType.BUCKET_VOLUME*8;

        public SmallChemicalReactorTanks()
        {
            this(new FluidTank(TANK_BUFFER_CAPACITY), new FluidTank(TANK_BUFFER_CAPACITY), new FluidTank(TANK_BUFFER_CAPACITY));
        }

        public SmallChemicalReactorTanks(FluidTank leftInput, FluidTank rightInput, FluidTank output)
        {
            this.leftInput = leftInput;
            this.rightInput = rightInput;
            this.output = output;
        }

        public Tag toNBT()
        {
            CompoundTag tag = new CompoundTag();
            tag.put("leftIn", this.leftInput.writeToNBT(new CompoundTag()));
            tag.put("rightIn", this.rightInput.writeToNBT(new CompoundTag()));
            tag.put("out", this.output.writeToNBT(new CompoundTag()));
            return tag;
        }

        public void readNBT(CompoundTag tag)
        {
            this.leftInput.readFromNBT(tag.getCompound("leftIn"));
            this.rightInput.readFromNBT(tag.getCompound("rightIn"));
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