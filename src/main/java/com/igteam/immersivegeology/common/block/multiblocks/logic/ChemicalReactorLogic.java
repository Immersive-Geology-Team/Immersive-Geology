/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.logic;

import blusunrize.immersiveengineering.api.crafting.RefineryRecipe;
import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.*;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInMachine;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor.InMachineProcessor;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import com.igteam.immersivegeology.common.block.multiblocks.logic.CoreDrillLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.ChemicalRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CrystallizerRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.ChemicalReactorShape;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.IndSluiceShape;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChemicalReactorLogic implements IMultiblockLogic<ChemicalReactorLogic.State>, IServerTickableComponent<ChemicalReactorLogic.State>, IClientTickableComponent<ChemicalReactorLogic.State> {
    public static final BlockPos REDSTONE_IN = new BlockPos(5,1,6);
    private static final Set<CapabilityPosition> ENERGY_POS;
    private static final MultiblockFace FLUID_OUTPUT;
    private static final CapabilityPosition FLUID_OUTPUT_CAP;
    private static final Set<CapabilityPosition> FLUID_INPUT_CAPS;
    private static final Set<BlockPos> FLUID_INPUTS;
    private static final BlockPos ITEM_INPUT;

    static {
        ITEM_INPUT = new BlockPos(4,5,4);
        ENERGY_POS = Set.of(new CapabilityPosition(6, 0, 5, RelativeBlockFace.FRONT), new CapabilityPosition(6, 1, 5, RelativeBlockFace.FRONT));
        FLUID_OUTPUT = new MultiblockFace(5, 0, 8, RelativeBlockFace.BACK);
        FLUID_OUTPUT_CAP =new CapabilityPosition(5, 0, 8, RelativeBlockFace.BACK);
        FLUID_INPUT_CAPS = Set.of(new CapabilityPosition(0, 0, 5, RelativeBlockFace.LEFT),
                                  new CapabilityPosition(8, 0, 3, RelativeBlockFace.RIGHT),
                                  new CapabilityPosition(3, 0, 0, RelativeBlockFace.BACK));
        FLUID_INPUTS = FLUID_INPUT_CAPS.stream().map(CapabilityPosition::posInMultiblock).collect(Collectors.toSet());
    }

    @Override
    public void tickClient(IMultiblockContext<State> ctx) {

    }

    @Override
    public void tickServer(IMultiblockContext<State> ctx) {
        State state = ctx.getState();
        state.processor.tickServer(state, ctx.getLevel(), true);
    }

    @Override
    public State createInitialState(IInitialMultiblockContext<State> capability) {
        return new ChemicalReactorLogic.State(capability);
    }

    @Override
    public <T> LazyOptional<T> getCapability(IMultiblockContext<State> ctx, CapabilityPosition position, Capability<T> cap)
    {
        final State state = ctx.getState();
        if(cap ==ForgeCapabilities.ENERGY && ENERGY_POS.contains(position))
        {
            return state.energyCap.cast(ctx);
        }

        if(cap == ForgeCapabilities.FLUID_HANDLER)
        {
            if(FLUID_INPUTS.contains(position.posInMultiblock())) return state.inputCap.cast(ctx);

            if(FLUID_OUTPUT_CAP.equals(position))
            {
                return state.outputCap.cast(ctx);
            }
        }

        return LazyOptional.empty();
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return ChemicalReactorShape.GETTER;
    }

    public static class State implements IMultiblockState, ProcessContext.ProcessContextInMachine<ChemicalRecipe> {
        public final AveragingEnergyStorage energy = new AveragingEnergyStorage(8192);
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();
        public final ChemicalReactorTanks tanks = new ChemicalReactorTanks();
        private final MultiblockProcessor.InMachineProcessor<ChemicalRecipe> processor;
        private final StoredCapability<IEnergyStorage> energyCap;
        private final StoredCapability<IFluidHandler> inputCap;
        private final StoredCapability<IFluidHandler> outputCap;

        public State(IInitialMultiblockContext<State> ctx){
            Runnable markDirty = ctx.getMarkDirtyRunnable();

            this.processor = new InMachineProcessor<>(1, 0, 1, ctx.getMarkDirtyRunnable(), ChemicalRecipe.RECIPES::getById);
            this.energyCap = new StoredCapability<>(this.energy);
            this.inputCap = new StoredCapability<>(new ArrayFluidHandler(true, true, markDirty, this.tanks.leftInput, this.tanks.backInput, this.tanks.rightInput));
            this.outputCap = new StoredCapability<>(ArrayFluidHandler.drainOnly(this.tanks.output, markDirty));
        }

        @Override
        public void readSaveNBT(CompoundTag nbt){
            this.energy.deserializeNBT(nbt.get("energy"));
            this.tanks.readNBT(nbt.getCompound("tanks"));
            this.processor.fromNBT(nbt.get("processor"), MultiblockProcessInMachine::new);
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt){
            nbt.put("energy", this.energy.serializeNBT());
            nbt.put("tanks", this.tanks.toNBT());
            nbt.put("processor", this.processor.toNBT());
        }

        @Override
        public AveragingEnergyStorage getEnergy()
        {
            return energy;
        }
    }

    public record ChemicalReactorTanks(FluidTank leftInput, FluidTank rightInput, FluidTank backInput, FluidTank output) {
        private static final int TANK_BUFFER_CAPACITY = FluidType.BUCKET_VOLUME * 32;

        public ChemicalReactorTanks() {
            this(new FluidTank(TANK_BUFFER_CAPACITY), new FluidTank(TANK_BUFFER_CAPACITY), new FluidTank(TANK_BUFFER_CAPACITY), new FluidTank(TANK_BUFFER_CAPACITY));
        }

        public ChemicalReactorTanks(FluidTank leftInput, FluidTank rightInput, FluidTank backInput, FluidTank output) {
            this.leftInput = leftInput;
            this.rightInput = rightInput;
            this.backInput = backInput;
            this.output = output;
        }

        public Tag toNBT() {
            CompoundTag tag = new CompoundTag();
            tag.put("leftIn", this.leftInput.writeToNBT(new CompoundTag()));
            tag.put("rightIn", this.rightInput.writeToNBT(new CompoundTag()));
            tag.put("backIn", this.backInput.writeToNBT(new CompoundTag()));
            tag.put("out", this.output.writeToNBT(new CompoundTag()));
            return tag;
        }

        public void readNBT(CompoundTag tag) {
            this.leftInput.readFromNBT(tag.getCompound("leftIn"));
            this.rightInput.readFromNBT(tag.getCompound("rightIn"));
            this.backInput.readFromNBT(tag.getCompound("backIn"));
            this.output.readFromNBT(tag.getCompound("out"));
        }

        public FluidTank leftInput() {
            return this.leftInput;
        }

        public FluidTank rightInput() {
            return this.rightInput;
        }

        public FluidTank backInput() {
            return this.backInput;
        }

        public FluidTank output() {
            return this.output;
        }
    }

}