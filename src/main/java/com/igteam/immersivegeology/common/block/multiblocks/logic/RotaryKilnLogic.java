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
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor.InMachineProcessor;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext.ProcessContextInMachine;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler.IOConstraint;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler.IntRange;
import com.igteam.immersivegeology.common.block.multiblocks.logic.RotaryKilnLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGMultiblockState;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.ISkinnableMultiblockLogic;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.RotaryKilnHeatState;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RotaryKilnRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.process.RotaryKilnProcess;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.RotaryKilnShape;
import com.igteam.immersivegeology.core.lib.IGLib;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RotaryKilnLogic implements ISkinnableMultiblockLogic<State>, IServerTickableComponent<RotaryKilnLogic.State>, IClientTickableComponent<RotaryKilnLogic.State> {
    public static final BlockPos REDSTONE_IN = new BlockPos(3, 1, 2);
    public static final int ENERGY_CAPACITY = 16000;
    private static final CapabilityPosition ENERGY_LEFT = new CapabilityPosition(5,2,0, RelativeBlockFace.UP);
    private static final CapabilityPosition ENERGY_MID = new CapabilityPosition(5,2,1, RelativeBlockFace.UP);
    private static final CapabilityPosition ENERGY_RIGHT = new CapabilityPosition(5,2,2, RelativeBlockFace.UP);
    private static final MultiblockFace OUTPUT_POS = new MultiblockFace(7,1,1, RelativeBlockFace.LEFT);
    private static final MultiblockFace INPUT_POS = new MultiblockFace(0,2,1, RelativeBlockFace.UP);
    private static final CapabilityPosition ITEM_OUTPUT_CAP = new CapabilityPosition(7,1,1, RelativeBlockFace.LEFT);
    private static final CapabilityPosition ITEM_INPUT_CAP = new CapabilityPosition(0,2,1, RelativeBlockFace.UP);
    public static final int NUM_SLOTS = 15;

    public static final int LV_HEAT_CAP  =  30;
    public static final int MV_HEAT_CAP  =  75;
    public static final int HV_HEAT_CAP  = 120;
    public static final int EHV_HEAT_CAP = 165;

    private static final int BASE_LV_ENERGY  =  50;
    private static final int BASE_MV_ENERGY  = 750;
    private static final int BASE_HV_ENERGY  = 3000;
    private static final int BASE_EHV_ENERGY = 12000;

    @Override
    public void tickClient(IMultiblockContext<State> iMultiblockContext) {
        State state = iMultiblockContext.getState();
    }

    @Override
    public void dropExtraItems(State state, Consumer<ItemStack> drop)
    {
        MBInventoryUtils.dropItems(state.getInventory(), drop);
    }

    private int processIndex = 0;
    @Override
    public void tickServer(IMultiblockContext<State> context) {
        balanceEnergy(context);
        final RotaryKilnLogic.State state = context.getState();
        final Level level = context.getLevel().getRawLevel();
        int qSize = state.getProcessorQueue().size();
        if(qSize > 0 && state.rsState.isEnabled(context))
        {
            state.tube_rotation+=0.5f % 360;
            state.isActive = true;
            context.requestMasterBESync();
        }
        if(state.isActive && qSize == 0)
        {
            state.isActive = false;
            context.requestMasterBESync();
        }

        if(state.tube_rotation % 90 != 0 &! state.isActive)
        {
            state.tube_rotation+=0.5f % 360;
            context.requestMasterBESync();
        }

        state.processor.tickServer(state, context.getLevel(), state.rsState.isEnabled(context));

        provideHeat(context);

        if(state.processor.getQueueSize() > 7) return;
        ItemStack inputSlot = state.inventory.getStackInSlot(0).copy();
        if(!inputSlot.isEmpty())
        {
            RotaryKilnRecipe recipe = RotaryKilnRecipe.findRecipe(level, inputSlot);
            if(recipe!=null)
            {
                for(int i = 1; i < 8; i++)
                {
                    if(state.inventory.getStackInSlot(i).isEmpty())
                    {
                        processIndex = i;
                        break;
                    }
                }
                RotaryKilnProcess process = new RotaryKilnProcess(recipe, processIndex);
                int rCount = recipe.itemIn.getCount();
                process.setInputAmounts(rCount);
                if(state.processor.addProcessToQueue(process, level, true) && state.inventory.getStackInSlot(processIndex).isEmpty())
                {
                    state.processor.addProcessToQueue(process, level, false);
                    state.inventory.setStackInSlot(processIndex, inputSlot.copyWithCount(rCount));
                    inputSlot.shrink(rCount);
                    state.inventory.setStackInSlot(0, inputSlot);
                    context.markMasterDirty();
                }
            }
        }
    }
    public void provideHeat(IMultiblockContext<State> context) {

        State state = context.getState();
        state.heatState = determineHeatState(context);
        state.heatState.execute(context);
        if(state.heatLevel < 0) state.heatLevel = 0;
        context.markDirtyAndSync();
    }

    private RotaryKilnHeatState determineHeatState(IMultiblockContext<State> context)
    {
        State state = context.getState();
        Level level = context.getLevel().getRawLevel();
        boolean isActive = state.rsState.isEnabled(context);

        if(!isActive) return RotaryKilnHeatState.MACHINE_OFF;

        int storedEnergy = state.total_energy.getEnergyStored();
        int avgInput = state.getAveragePower();
        float currentHeat = state.getHeat();
        float target_heat = state.getTargetHeat();

        if(avgInput > 0)
        {

			int recipeHeatTarget = convertAvePowerToHeat(avgInput);
            List<MultiblockProcess<RotaryKilnRecipe, ProcessContextInMachine<RotaryKilnRecipe>>> queue = state.getProcessorQueue();
            if(queue != null && !queue.isEmpty())
            {
                List<RotaryKilnRecipe> recipes = queue.stream().map(p -> p.getRecipe(level)).toList();
                int recipeHeat = 0;
                for(RotaryKilnRecipe r : recipes)
                {
                    if(r == null) continue;
                    int h = r.getHeatRequired();
                    if(recipeHeat < h) recipeHeat = h;
                }

                if(recipeHeatTarget < recipeHeat) recipeHeatTarget = recipeHeat;
            }

            if(recipeHeatTarget!=target_heat) state.targetHeat = recipeHeatTarget;
            if(currentHeat < recipeHeatTarget) return RotaryKilnHeatState.HEATING_UP;
            if(currentHeat > (recipeHeatTarget+7)) return RotaryKilnHeatState.COOLING_DOWN;
            if(!state.getProcessorQueue().isEmpty()) return RotaryKilnHeatState.RUNNING_RECIPE;
        }
        return RotaryKilnHeatState.MAINTAINING_HEAT;
    }

    private static int convertAvePowerToHeat(int avgInput)
    {
        if(avgInput > 0 && avgInput < BASE_MV_ENERGY) return RotaryKilnLogic.LV_HEAT_CAP;
        if(avgInput > BASE_MV_ENERGY && avgInput < BASE_HV_ENERGY) return RotaryKilnLogic.MV_HEAT_CAP;
        if(avgInput > BASE_HV_ENERGY && avgInput < BASE_EHV_ENERGY) return RotaryKilnLogic.HV_HEAT_CAP;
        if(avgInput > BASE_EHV_ENERGY) return RotaryKilnLogic.EHV_HEAT_CAP;
        return 0;
    }

    private static List<RotaryKilnRecipe> getActiveRecipes(State state, Level level) {
        return state.processor.getQueue().stream()
                .map(q -> q.getRecipe(level))
                .filter(Objects::nonNull)
                .toList();
    }

    // Constants for heating and cooling rates
    private static final float PASSIVE_COOL_RATE = 0.05f;
    private static final float BASE_HEAT_RATE = 0.2f;

    int nextPacketIndex = 0;
    private void balanceEnergy(IMultiblockContext<State> context)
    {
        final State state = context.getState();
        int totalEnergy = state.getEnergy().getEnergyStored();
        int capLV = ENERGY_CAPACITY;
        int capMV = ENERGY_CAPACITY * 2;
        int capHV = ENERGY_CAPACITY * 4;
        int energyLV = Math.min(totalEnergy, capLV);
        int remaining = totalEnergy - energyLV;
        int energyMV = Math.min(remaining, capMV);
        remaining -= energyMV;
        int energyHV = Math.min(remaining, capHV);

        state.energy_lv.setStoredEnergy(energyLV);
        state.energy_mv.setStoredEnergy(energyMV);
        state.energy_hv.setStoredEnergy(energyHV);

        int averageInsertion = state.total_energy.getAverageInsertion();
        if(nextPacketIndex >= state.lastEnergyPackets.size())
        {
            state.lastEnergyPackets.add(Math.max(0, averageInsertion));
        }
        else
        {
            state.lastEnergyPackets.set(nextPacketIndex, Math.max(0, averageInsertion));
        }
        nextPacketIndex = (nextPacketIndex+1)%20;
    }

    @Override
    public State createInitialState(IInitialMultiblockContext<State> capability) {
        return new RotaryKilnLogic.State(capability);
    }

    @Override
    public <T> LazyOptional<T> getCapability(IMultiblockContext<State> ctx, CapabilityPosition position, Capability<T> cap)
    {
        final RotaryKilnLogic.State state = ctx.getState();
        if(cap == ForgeCapabilities.ENERGY)
        {
            if(ENERGY_LEFT.equals(position) || ENERGY_MID.equals(position) || ENERGY_RIGHT.equals(position))
            {
                return state.energyCap.cast(ctx);
            }
        }

        if(cap == ForgeCapabilities.ITEM_HANDLER)
        {
            if(ITEM_INPUT_CAP.equals(position)) return state.itemInputCap.cast(ctx);
            if(ITEM_OUTPUT_CAP.equals(position)) return state.outputHandler.cast(ctx);
        }

        return LazyOptional.empty();
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return RotaryKilnShape.GETTER;
    }

    public static class State implements IGMultiblockState, ProcessContextInMachine<RotaryKilnRecipe>
    {
        public final AveragingEnergyStorage total_energy = new AveragingEnergyStorage(ENERGY_CAPACITY * 7);
        public final AveragingEnergyStorage energy_lv = new AveragingEnergyStorage(ENERGY_CAPACITY);
        public final AveragingEnergyStorage energy_mv = new AveragingEnergyStorage(ENERGY_CAPACITY * 2);
        public final AveragingEnergyStorage energy_hv = new AveragingEnergyStorage(ENERGY_CAPACITY * 4);
        public final DoubleList lastEnergyPackets = new DoubleArrayList(20);
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();
        public final SlotwiseItemHandler inventory;
        private final CapabilityReference<IItemHandler> output;
        private final StoredCapability<IItemHandler> outputHandler;
        private final StoredCapability<IItemHandler> itemInputCap;
        private float tube_rotation;
        private boolean isActive;
        private final StoredCapability<IEnergyStorage> energyCap;
        private float heatLevel = 0;
        private float targetHeat = 0;
        private RotaryKilnHeatState heatState;

        private final MultiblockProcessor.InMachineProcessor<RotaryKilnRecipe> processor;
        Runnable markDirty;
        public State(IInitialMultiblockContext<State> ctx) {
            this.energyCap = new StoredCapability<>(this.total_energy);
            this.processor = new InMachineProcessor<>(7, 0, 7, ctx.getMarkDirtyRunnable(), RotaryKilnRecipe.RECIPES::getById);
            this.tube_rotation = 0.0f;
            this.isActive = false;
            this.heatState = RotaryKilnHeatState.MACHINE_OFF;
            final Supplier<@Nullable Level> levelGetter = ctx.levelSupplier();
            final Runnable markDirty = ctx.getMarkDirtyRunnable();
            this.markDirty = markDirty;
            this.inventory = new SlotwiseItemHandler(List.of(
                    // Main Input
                    new IOConstraint(true, i -> RotaryKilnRecipe.findRecipe(levelGetter.get(), i) != null),

                    // In Process
                    new IOConstraint(false, i -> RotaryKilnRecipe.findRecipe(levelGetter.get(), i) != null),
                    new IOConstraint(false, i -> RotaryKilnRecipe.findRecipe(levelGetter.get(), i) != null),
                    new IOConstraint(false, i -> RotaryKilnRecipe.findRecipe(levelGetter.get(), i) != null),
                    new IOConstraint(false, i -> RotaryKilnRecipe.findRecipe(levelGetter.get(), i) != null),
                    new IOConstraint(false, i -> RotaryKilnRecipe.findRecipe(levelGetter.get(), i) != null),
                    new IOConstraint(false, i -> RotaryKilnRecipe.findRecipe(levelGetter.get(), i) != null),
                    new IOConstraint(false, i -> RotaryKilnRecipe.findRecipe(levelGetter.get(), i) != null),

                    //Output Holders
                    IOConstraint.OUTPUT,
                    IOConstraint.OUTPUT,
                    IOConstraint.OUTPUT,
                    IOConstraint.OUTPUT,
                    IOConstraint.OUTPUT,
                    IOConstraint.OUTPUT,
                    IOConstraint.OUTPUT
            ), markDirty);

            this.output = ctx.getCapabilityAt(ForgeCapabilities.ITEM_HANDLER, OUTPUT_POS);
            this.outputHandler = new StoredCapability<>(new WrappingItemHandler(
                    inventory, false, true, new IntRange(8,14)
            ));
            this.itemInputCap = new StoredCapability<>(new WrappingItemHandler(inventory, true, false, new IntRange(0,1)));
        }

        @Override
        public SlotwiseItemHandler getInventory()
        {
            return inventory;
        }

        @Override
        public int[] getOutputSlots()
        {
            return new int[]{8,9,10,11,12,13,14};
        }

        @Override
        public void onProcessFinish(MultiblockProcess<RotaryKilnRecipe, ?> process, Level level)
        {
            if(process instanceof RotaryKilnProcess rotaryKilnProcess)
            {
                int index = rotaryKilnProcess.getSlot();
                inventory.setStackInSlot(index, ItemStack.EMPTY);
                markDirty.run();
            }
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt){
            nbt.put("energy_lv", energy_lv.serializeNBT());
            nbt.put("energy_mv", energy_mv.serializeNBT());
            nbt.put("energy_hv", energy_hv.serializeNBT());
            nbt.put("energy", total_energy.serializeNBT());
            nbt.put("processor", processor.toNBT());
            nbt.putFloat("tube_rotation", tube_rotation);
            nbt.put("inventory", inventory.serializeNBT());
            nbt.putBoolean("is_active", isActive);

            nbt.putFloat("target_heat", targetHeat);
            nbt.putFloat("heat", heatLevel);

            nbt.putInt("heat_state", heatState.ordinal());


            CompoundTag data = new CompoundTag();
            int i = 0;
            for(Double d : lastEnergyPackets) data.putDouble("i"+i++, d);
            nbt.put("energy_input_packets", data);

        }

        @Override
        public void readSaveNBT(CompoundTag nbt){
            energy_lv.deserializeNBT(nbt.get("energy_lv"));
            energy_mv.deserializeNBT(nbt.get("energy_mv"));
            energy_hv.deserializeNBT(nbt.get("energy_hv"));
            total_energy.deserializeNBT(nbt.get("energy"));
            this.tube_rotation = nbt.getFloat("tube_rotation");
            this.inventory.deserializeNBT(nbt.getCompound("inventory"));
            this.processor.fromNBT(nbt.get("processor"), RotaryKilnProcess::new);
            this.isActive = nbt.getBoolean("is_active");
            this.targetHeat = nbt.getFloat("target_heat");
            this.heatLevel = nbt.getFloat("heat");
            this.heatState = RotaryKilnHeatState.values()[nbt.getInt("heat_state")];

            CompoundTag data = nbt.getCompound("energy_input_packets");
            int size = data.size();
            lastEnergyPackets.clear();
            for(int i = 0; i < size; i++)
            {
                lastEnergyPackets.add(i, data.getDouble("i"+i));
            }
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

        public float getHeat()
        {
            return this.heatLevel;
        }

        public int getAveragePower()
        {
            if(lastEnergyPackets.isEmpty()) return 0;
            double sum = 0;
            synchronized(lastEnergyPackets)
            {
                for(double transfer : lastEnergyPackets) sum += transfer;
            }
            return Math.max(0,(int) Math.round(sum/lastEnergyPackets.size()) - 1);
        }

        @Override
        public AveragingEnergyStorage getEnergy()
        {
			return total_energy;
        }

        public AveragingEnergyStorage getEnergyHV()
        {
            return energy_hv;
        }

        public AveragingEnergyStorage getEnergyMV()
        {
            return energy_mv;
        }

        public AveragingEnergyStorage getEnergyLV()
        {
            return energy_lv;
        }

        public float getRotation()
        {
            return tube_rotation;
        }

        public List<MultiblockProcess<RotaryKilnRecipe, ProcessContextInMachine<RotaryKilnRecipe>>> getProcessorQueue()
        {
            return processor.getQueue();
        }

        public boolean isActive()
        {
            return isActive;
        }

        public boolean hasRequiredHeat(int recipeHeat)
        {
			int lowerBound = recipeHeat-7;
            int upperBound = recipeHeat+7;
            boolean flag = heatLevel >= lowerBound && heatLevel <= upperBound && heatState.equals(RotaryKilnHeatState.RUNNING_RECIPE);
            if(flag)
            {
                heatLevel -= 0.1f;
            }
			return flag;
		}

        public float getTargetHeat()
        {
            return targetHeat;
        }

        public void modifyHeat(float v)
        {
            float newHeat = this.heatLevel + v;
            boolean invalid = newHeat < 0 || newHeat > 170;
            if(!invalid) this.heatLevel = newHeat;
        }

        public void setHeat(float v)
        {
            this.heatLevel = v;
        }

        @Override
        public void invalidate(@NotNull IMultiblockContext<?> ctx)
        {
            this.energyCap.get(ctx).invalidate();
            this.outputHandler.get(ctx).invalidate();
            this.itemInputCap.get(ctx).invalidate();
        }
    }
}