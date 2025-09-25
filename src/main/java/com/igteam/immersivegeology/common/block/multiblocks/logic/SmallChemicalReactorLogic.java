/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.logic;

import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.*;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInMachine;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor.InMachineProcessor;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler.IOConstraint;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler.IntRange;
import com.igteam.immersivegeology.common.block.multiblocks.logic.SmallChemicalReactorLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.ISkinnableMultiblockLogic;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BasicChemicalRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.ChemicalRepairRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.SmallChemicalReactorShape;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
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
    public void tickServer(IMultiblockContext<State> iMultiblockContext) {

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

    public static class State implements IMultiblockState {
        public final AveragingEnergyStorage energy = new AveragingEnergyStorage(ENERGY_CAPACITY);
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();

        public final SlotwiseItemHandler inventory;
        public final SmallChemicalReactorTanks tanks = new SmallChemicalReactorTanks();
        private final StoredCapability<IFluidHandler> inputCapBack;
        private final StoredCapability<IFluidHandler> inputCapFront;
        private final StoredCapability<IItemHandler> itemInputCap;
        private final CapabilityReference<IItemHandler> input_output;
        private final StoredCapability<IItemHandler> outputHandler;
        private final CapabilityReference<IFluidHandler> fluidOutput;
        private final StoredCapability<IFluidHandler> outputCap;
        private final StoredCapability<IEnergyStorage> energyCap;

        private final MultiblockProcessor.InMachineProcessor<BasicChemicalRecipe> processor;


        public State(IInitialMultiblockContext<State> ctx){
            final Supplier<@Nullable Level> getLevel = ctx.levelSupplier();
            final Runnable markDirty = ctx.getMarkDirtyRunnable();

            this.energyCap = new StoredCapability<>(this.energy);
            this.inventory = new SlotwiseItemHandler(List.of(
                    new IOConstraint(true, i -> BasicChemicalRecipe.acceptableCatalyst(getLevel.get(), i)),
                    IOConstraint.OUTPUT,
                    new IOConstraint(false, i -> ChemicalRepairRecipe.isValidRepairItem(getLevel.get(), i))
            ), ctx.getMarkDirtyRunnable());
            this.input_output = ctx.getCapabilityAt(ForgeCapabilities.ITEM_HANDLER, ITEM_INPUT_OUTPUT);

            this.outputHandler = new StoredCapability<>(new WrappingItemHandler(
                    inventory, false, true, new IntRange(1, 2)
            ));

            this.fluidOutput = ctx.getCapabilityAt(ForgeCapabilities.FLUID_HANDLER, new MultiblockFace(FLUID_OUTPUT_CAP.side(), FLUID_OUTPUT_CAP.posInMultiblock().south()));
            this.processor = new InMachineProcessor<>(4, 0, 4, ctx.getMarkDirtyRunnable(), BasicChemicalRecipe.RECIPES::getById);

            this.inputCapBack = new StoredCapability<>(new ArrayFluidHandler(true, true, markDirty, this.tanks.leftInput));
            this.inputCapFront = new StoredCapability<>(new ArrayFluidHandler(true, true, markDirty, this.tanks.rightInput));
            this.outputCap = new StoredCapability<>(ArrayFluidHandler.drainOnly(this.tanks.output, markDirty));
            this.itemInputCap = new StoredCapability<>(this.inventory);
        }

        @Override
        public void readSaveNBT(CompoundTag nbt){
            this.energy.deserializeNBT(nbt.get("energy"));
            this.tanks.readNBT(nbt.getCompound("tanks"));
            this.inventory.deserializeNBT(nbt.getCompound("inventory"));
            this.processor.fromNBT(nbt.get("processor"), MultiblockProcessInMachine::new);
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt){
            nbt.put("energy", this.energy.serializeNBT());
            nbt.put("tanks", this.tanks.toNBT());
            nbt.put("processor", this.processor.toNBT());
            nbt.put("inventory", this.inventory.serializeNBT());
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