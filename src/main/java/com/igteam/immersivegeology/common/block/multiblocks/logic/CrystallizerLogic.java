/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.logic;

import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.*;
import blusunrize.immersiveengineering.client.utils.TextUtils;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.interfaces.MBOverlayText;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInMachine;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext.ProcessContextInMachine;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler.IOConstraint;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler.IOConstraintGroup;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler.IntRange;
import com.igteam.immersivegeology.common.block.helper.IGReceiveOnlyEnergy;
import com.igteam.immersivegeology.common.block.multiblocks.logic.CrystallizerLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGMultiblockState;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CrystallizerRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.CrystallizerShape;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage;
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CrystallizerLogic implements IMultiblockLogic<CrystallizerLogic.State>, IServerTickableComponent<CrystallizerLogic.State>, MBOverlayText<State> {
    public static final BlockPos REDSTONE_IN = new BlockPos(0, 0, 1);

    public static final int ENERGY_CAPACITY = 48000;
    private static final CapabilityPosition ENERGY_INPUT = new CapabilityPosition(2,0,1, RelativeBlockFace.LEFT);

    private static final CapabilityPosition FLUID_INPUT_CAP = new CapabilityPosition(1,1,2, RelativeBlockFace.BACK);
    private static final CapabilityPosition FLUID_OUTPUT_CAP = new CapabilityPosition(1,0,1, RelativeBlockFace.DOWN);
    private static final MultiblockFace OUTPUT_POS = new MultiblockFace(1,1,-1, RelativeBlockFace.BACK);
    private static final CapabilityPosition ITEM_OUTPUT_CAP = CapabilityPosition.opposing(OUTPUT_POS);

    public static final int TANK_VOLUME = 4 * FluidType.BUCKET_VOLUME;

    @Override
    public void tickServer(IMultiblockContext<State> context) {
        final State state = context.getState();
        boolean isActive = state.rsState.isEnabled(context);
        if(isActive)
        {
            final int tank_amount = state.tank.getFluidAmount();
            state.processor.tickServer(state, context.getLevel(), state.rsState.isEnabled(context));
            tryRunRecipe(state, context.getLevel().getRawLevel());
            if(tank_amount!=state.tank.getFluidAmount()) context.requestMasterBESync();
        }
        if(state.output_tank.getFluid().getAmount() > 0)
        {
            drainOutputTank(state, context, state.fluidOutput);
            context.requestMasterBESync();
        }
    }

    @Override
    public void dropExtraItems(State state, Consumer<ItemStack> drop)
    {
        MBInventoryUtils.dropItems(state.getInventory(), drop);
    }

    private void drainOutputTank(State state, IMultiblockContext<State> context, Supplier<@Nullable IFluidHandler> output_reference)
    {
        int outSize = Math.min(FluidType.BUCKET_VOLUME, state.output_tank.getFluidAmount());
        FluidStack out = Utils.copyFluidStackWithAmount(state.output_tank.getFluid(), outSize, false);
        IFluidHandler output = output_reference.get();

        if(output==null)
            return;

        int accepted = output.fill(out, FluidAction.SIMULATE);
        if(accepted > 0)
        {
            int drained = output.fill(Utils.copyFluidStackWithAmount(out, Math.min(out.getAmount(), accepted), false), FluidAction.EXECUTE);
            state.output_tank.drain(drained, FluidAction.EXECUTE);
            context.markMasterDirty();
            context.requestMasterBESync();
        }
    }

    private void tryRunRecipe(State state, Level level)
    {
        if(state.energy.getEnergyStored() <= 0 || state.processor.getQueueSize() >= state.processor.getMaxQueueSize()) return;

        final FluidStack input = state.tank.getFluid();
        if(input.isEmpty()) return;
        RecipeHolder<CrystallizerRecipe> recipe = CrystallizerRecipe.findRecipe(level, input);
        if(recipe == null) return;
        MultiblockProcessInMachine<CrystallizerRecipe> process = new MultiblockProcessInMachine<>(recipe);
        if(input.isEmpty()) process.setInputTanks(1);
        int drainSimulation = state.tank.drain(recipe.value().fluidIn.amount(), FluidAction.SIMULATE).getAmount();
        int drainAmount = recipe.value().fluidIn.amount();
        if(state.processor.addProcessToQueue(process, level, true) && drainSimulation == drainAmount)
        {
            state.processor.addProcessToQueue(process, level, false);
            state.tank.drain(recipe.value().fluidIn.amount(), FluidAction.EXECUTE).getAmount();
        }
    }

    @Override
    public State createInitialState(IInitialMultiblockContext<State> capability) {
        return new CrystallizerLogic.State(capability);
    }

    @Override
    public void registerCapabilities(CapabilityRegistrar<State> register)
    {
        register.register(EnergyStorage.BLOCK, (state, position) ->
                position.side()==null||ENERGY_INPUT.equals(position)?state.energyCap:null);
        register.register(FluidHandler.BLOCK, (state, position) -> {
            if(FLUID_INPUT_CAP.equals(position)) return state.fInputCap;
            if(FLUID_OUTPUT_CAP.equals(position)) return state.fOutputCap;
            return null;
        });
        register.registerAt(ItemHandler.BLOCK, ITEM_OUTPUT_CAP, state -> state.itemOutputCap);
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return CrystallizerShape.GETTER;
    }

    @Nullable
    @Override
    public List<Component> getOverlayText(State state, BlockPos posInMultiblock, BlockHitResult absoluteHit, Player player, boolean b)
    {
        if(Utils.isFluidRelatedItemStack(player.getItemInHand(InteractionHand.MAIN_HAND)))
            return List.of(TextUtils.formatFluidStack(state.tank.getFluid()), TextUtils.formatFluidStack(state.output_tank.getFluid()));
        return null;
    }

    public static class State implements IGMultiblockState, ProcessContextInMachine<CrystallizerRecipe>
    {
        public final AveragingEnergyStorage energy = new AveragingEnergyStorage(ENERGY_CAPACITY);
        private final MultiblockProcessor<CrystallizerRecipe, ProcessContextInMachine<CrystallizerRecipe>> processor;
        public final SlotwiseItemHandler inventory;

        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();

        public final FluidTank tank = new FluidTank(TANK_VOLUME);
        public final FluidTank output_tank = new FluidTank(TANK_VOLUME);
        private final IFluidHandler fInputCap;
        private final IFluidHandler fOutputCap;
        private final IEnergyStorage energyCap;
        private final Supplier<@Nullable IItemHandler> output;
        private final IItemHandler itemOutputCap;
        private final Supplier<@Nullable IFluidHandler> fluidOutput;

        public State(IInitialMultiblockContext<State> ctx)
        {
            this.energyCap = IGReceiveOnlyEnergy.of(this.energy);
            this.output = ctx.getCapabilityAt(Capabilities.ItemHandler.BLOCK, OUTPUT_POS);
            this.processor = new MultiblockProcessor<>(
                1, 0, 1, ctx.getMarkDirtyRunnable(), CrystallizerRecipe.RECIPES::getById
            );
            this.inventory = SlotwiseItemHandler.makeWithGroups(
                List.of(new IOConstraintGroup(IOConstraint.NO_CONSTRAINT, 1)), ctx.getMarkDirtyRunnable()
            );
            Runnable changedAndSync = () -> {
                ctx.getSyncRunnable().run();
                ctx.getMarkDirtyRunnable().run();
            };
            this.itemOutputCap = new WrappingItemHandler(
                    inventory, false, true, new IntRange(0, 1)
            );
            this.fInputCap = new ArrayFluidHandler(tank, true, true, changedAndSync);
            this.fOutputCap = new ArrayFluidHandler(output_tank, true, false, changedAndSync);

            this.fluidOutput = ctx.getCapabilityAt(Capabilities.FluidHandler.BLOCK, new MultiblockFace(FLUID_OUTPUT_CAP.side().getOpposite(), FLUID_OUTPUT_CAP.posInMultiblock().below()));
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt, HolderLookup.Provider provider){
            nbt.put("energy", energy.serializeNBT(provider));
            nbt.put("processor", processor.toNBT(provider));
            nbt.put("tank", tank.writeToNBT(provider, new CompoundTag()));
            nbt.put("output_tank", output_tank.writeToNBT(provider, new CompoundTag()));
            nbt.put("inventory", inventory.serializeNBT(provider));
        }

        @Override
        public void readSaveNBT(CompoundTag nbt, HolderLookup.Provider provider){
            energy.deserializeNBT(provider, nbt.get("energy"));
            tank.readFromNBT(provider, nbt.getCompound("tank"));
            output_tank.readFromNBT(provider, nbt.getCompound("output_tank"));
            inventory.deserializeNBT(provider, nbt.getCompound("inventory"));
            processor.fromNBT(nbt.get("processor"), (getRecipe, data, p) -> new MultiblockProcessInMachine<>(getRecipe, data), provider);
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

        public void invalidate(@NotNull IMultiblockContext<?> ctx)
        {
        }

        @Override
        public void onProcessFinish(MultiblockProcess<CrystallizerRecipe, ?> process, Level level)
        {
            try {
                CrystallizerRecipe recipe = process.getRecipe(level);
                output_tank.fill(recipe.fluidOutput.get(), FluidAction.EXECUTE);
            } catch(Exception error)
            {
                IGLib.IG_LOGGER.error("Error: {}", error.getMessage());
            }
        }

        @Override
        public int[] getOutputSlots()
        {
            return new int[]{0};
        }

        @Override
        public FluidTank[] getInternalTanks()
        {
            return new FluidTank[]{tank, output_tank};
        }

        @Override
        public int[] getOutputTanks()
        {
            return new int[]{1};
        }

        public float getPercentComplete(Level level)
        {
            if(this.processor.getQueue().isEmpty()) return 0;
            MultiblockProcess<CrystallizerRecipe, ProcessContextInMachine<CrystallizerRecipe>> process = this.processor.getQueue().get(0);
            return (float)process.processTick/ process.getMaxTicks(level);
        }

        @Override
        public IItemHandlerModifiable getInventory()
        {
            return inventory.getRawHandler();
        }

        @Override
        public AveragingEnergyStorage getEnergy()
        {
            return energy;
        }
    }
}