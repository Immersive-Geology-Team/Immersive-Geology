/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.logic;

import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
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
import com.igteam.immersivegeology.common.block.helper.IGReceiveOnlyEnergy;
import com.igteam.immersivegeology.common.block.multiblocks.logic.RotaryKilnLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGMultiblockState;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.ISkinnableMultiblockLogic;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.RotaryKilnHeatState;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RotaryKilnRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.process.RotaryKilnProcess;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.RotaryKilnShape;
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

import java.util.List;
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

    /** How many recipes the kiln can have in flight at once. */
    public static final int MAX_PROCESSES = 7;
    /** One input slot, then a holding slot and an output slot per concurrent process. */
    public static final int NUM_SLOTS = 1+(MAX_PROCESSES*2);

    public static final int LV_HEAT_CAP  =  30;
    public static final int MV_HEAT_CAP  =  75;
    public static final int HV_HEAT_CAP  = 120;
    public static final int EHV_HEAT_CAP = 165;

    /**
     * Averaged input power (FE/t) at which each heat tier is reached. These line up with the
     * upkeep steps in RotaryKilnHeatState: holding a tier's cap costs exactly the power that
     * tier is defined by.
     */
    private static final int BASE_MV_ENERGY  = 750;
    private static final int BASE_HV_ENERGY  = 3000;
    private static final int BASE_EHV_ENERGY = 12000;

    /** Ticks of input power averaged together to pick the heat tier. */
    private static final int ENERGY_SAMPLE_WINDOW = 20;

    /** Degrees the tube turns per tick, purely cosmetic. */
    private static final float TUBE_ROTATION_STEP = 0.5f;
    /** Once stopped the tube keeps turning until it parks on a multiple of this. */
    private static final float TUBE_REST_ANGLE = 90f;

    @Override
    public void tickClient(IMultiblockContext<State> context) {
        final State state = context.getState();
        // The tube is animation only, so the client drives it and the server never has to sync
        // a float every tick. It spins while running, then coasts to the nearest quarter turn.
        if(state.isActive||state.tube_rotation%TUBE_REST_ANGLE!=0)
            state.tube_rotation = (state.tube_rotation+TUBE_ROTATION_STEP)%360f;
    }

    @Override
    public void dropExtraItems(State state, Consumer<ItemStack> drop)
    {
        MBInventoryUtils.dropItems(state.getInventory(), drop);
    }

    @Override
    public void tickServer(IMultiblockContext<State> context) {
        balanceEnergy(context);
        final RotaryKilnLogic.State state = context.getState();
        final Level level = context.getLevel().getRawLevel();
        final boolean enabled = state.rsState.isEnabled(context);

        final boolean wasActive = state.isActive;
        state.isActive = enabled&&!state.getProcessorQueue().isEmpty();
        if(wasActive!=state.isActive) context.requestMasterBESync();

        state.processor.tickServer(state, context.getLevel(), enabled);

        provideHeat(context);

        if(state.processor.getQueueSize() >= state.processor.getMaxQueueSize()) return;
        ItemStack inputSlot = state.inventory.getStackInSlot(0).copy();
        if(inputSlot.isEmpty()) return;

        RotaryKilnRecipe recipe = RotaryKilnRecipe.findRecipe(level, inputSlot);
        if(recipe==null) return;

        int rCount = recipe.itemIn.getCount();
        // Never hand a process more than the input slot actually holds, or the copy below
        // would mint the difference out of nothing
        if(inputSlot.getCount() < rCount) return;

        int processIndex = -1;
        for(int i = 1; i <= MAX_PROCESSES; i++)
        {
            if(state.inventory.getStackInSlot(i).isEmpty())
            {
                processIndex = i;
                break;
            }
        }
        if(processIndex < 0) return;

        RotaryKilnProcess process = new RotaryKilnProcess(recipe, processIndex);
        process.setInputAmounts(rCount);
        if(state.processor.addProcessToQueue(process, level, true))
        {
            state.processor.addProcessToQueue(process, level, false);
            state.inventory.setStackInSlot(processIndex, inputSlot.copyWithCount(rCount));
            inputSlot.shrink(rCount);
            state.inventory.setStackInSlot(0, inputSlot);
            context.markMasterDirty();
        }
    }

    public void provideHeat(IMultiblockContext<State> context) {

        State state = context.getState();
        state.heatState = determineHeatState(context);
        state.heatState.execute(context);
        if(state.heatLevel < 0) state.heatLevel = 0;
        // Heat is server-side only - the GUI reads it through the menu's synced data - so this
        // needs saving but never a sync packet
        context.markMasterDirty();
    }

    private RotaryKilnHeatState determineHeatState(IMultiblockContext<State> context)
    {
        State state = context.getState();
        Level level = context.getLevel().getRawLevel();

        if(!state.rsState.isEnabled(context)) return RotaryKilnHeatState.MACHINE_OFF;

        int avgInput = state.getAveragePower();
        if(avgInput <= 0) return RotaryKilnHeatState.MAINTAINING_HEAT;

        // Power decides the tier the kiln settles at, but a queued recipe can pull the target
        // above it: power is what creates the heat, the recipe is what consumes it.
        int targetHeat = convertAvePowerToHeat(avgInput);
        List<MultiblockProcess<RotaryKilnRecipe, ProcessContextInMachine<RotaryKilnRecipe>>> queue = state.getProcessorQueue();
        for(MultiblockProcess<RotaryKilnRecipe, ProcessContextInMachine<RotaryKilnRecipe>> queued : queue)
        {
            RotaryKilnRecipe queuedRecipe = queued.getRecipe(level);
            if(queuedRecipe!=null) targetHeat = Math.max(targetHeat, queuedRecipe.getHeatRequired());
        }
        state.targetHeat = targetHeat;

        float currentHeat = state.getHeat();
        if(currentHeat < targetHeat) return RotaryKilnHeatState.HEATING_UP;
        if(currentHeat > targetHeat+State.HEAT_TOLERANCE) return RotaryKilnHeatState.COOLING_DOWN;
        if(!queue.isEmpty()) return RotaryKilnHeatState.RUNNING_RECIPE;
        return RotaryKilnHeatState.MAINTAINING_HEAT;
    }

    /**
     * The heat tier a given averaged input holds the kiln at. The bounds are inclusive at the
     * bottom and exclusive at the top so that every input maps to exactly one tier - supplying
     * precisely a tier's power reaches that tier rather than falling between two of them.
     */
    private static int convertAvePowerToHeat(int avgInput)
    {
        if(avgInput <= 0) return 0;
        if(avgInput < BASE_MV_ENERGY) return RotaryKilnLogic.LV_HEAT_CAP;
        if(avgInput < BASE_HV_ENERGY) return RotaryKilnLogic.MV_HEAT_CAP;
        if(avgInput < BASE_EHV_ENERGY) return RotaryKilnLogic.HV_HEAT_CAP;
        return RotaryKilnLogic.EHV_HEAT_CAP;
    }

    private void balanceEnergy(IMultiblockContext<State> context)
    {
        final State state = context.getState();
        // The three bars in the GUI are a display split of the one real buffer, recomputed
        // every tick rather than stored
        int totalEnergy = state.getEnergy().getEnergyStored();
        int energyLV = Math.min(totalEnergy, ENERGY_CAPACITY);
        int remaining = totalEnergy-energyLV;
        int energyMV = Math.min(remaining, ENERGY_CAPACITY*2);
        remaining -= energyMV;
        int energyHV = Math.min(remaining, ENERGY_CAPACITY*4);

        state.energy_lv.setStoredEnergy(energyLV);
        state.energy_mv.setStoredEnergy(energyMV);
        state.energy_hv.setStoredEnergy(energyHV);

        int averageInsertion = Math.max(0, state.total_energy.getAverageInsertion());
        if(state.nextPacketIndex >= state.lastEnergyPackets.size())
            state.lastEnergyPackets.add(averageInsertion);
        else
            state.lastEnergyPackets.set(state.nextPacketIndex, averageInsertion);
        state.nextPacketIndex = (state.nextPacketIndex+1)%ENERGY_SAMPLE_WINDOW;
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
        /** How far either side of a recipe's heat the kiln may sit and still run it. */
        public static final float HEAT_TOLERANCE = 7;
        /** Heat drawn out of the kiln by each recipe on every tick it makes progress. */
        public static final float HEAT_PER_PROCESS_TICK = 0.1f;
        private static final float MAX_HEAT = 170;

        public final AveragingEnergyStorage total_energy = new AveragingEnergyStorage(ENERGY_CAPACITY * 7);
        // Display-only views of total_energy, refilled by balanceEnergy each tick and synced to
        // the GUI as container data, so they are neither averaged nor saved
        public final MutableEnergyStorage energy_lv = new MutableEnergyStorage(ENERGY_CAPACITY);
        public final MutableEnergyStorage energy_mv = new MutableEnergyStorage(ENERGY_CAPACITY * 2);
        public final MutableEnergyStorage energy_hv = new MutableEnergyStorage(ENERGY_CAPACITY * 4);
        public final DoubleList lastEnergyPackets = new DoubleArrayList(ENERGY_SAMPLE_WINDOW);
        private int nextPacketIndex = 0;
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
            this.energyCap = new StoredCapability<>(IGReceiveOnlyEnergy.of(this.total_energy));
            this.processor = new InMachineProcessor<>(MAX_PROCESSES, 0, MAX_PROCESSES, ctx.getMarkDirtyRunnable(), RotaryKilnRecipe.RECIPES::getById);
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
                    inventory, false, true, new IntRange(MAX_PROCESSES+1, NUM_SLOTS)
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
            int[] slots = new int[MAX_PROCESSES];
            for(int i = 0; i < MAX_PROCESSES; i++) slots[i] = MAX_PROCESSES+1+i;
            return slots;
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
            nbt.put("energy", total_energy.serializeNBT());
            nbt.put("processor", processor.toNBT());
            nbt.put("inventory", inventory.serializeNBT());

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
            total_energy.deserializeNBT(nbt.get("energy"));
            this.inventory.deserializeNBT(nbt.getCompound("inventory"));
            this.processor.fromNBT(nbt.get("processor"), RotaryKilnProcess::new);
            this.targetHeat = nbt.getFloat("target_heat");
            this.heatLevel = nbt.getFloat("heat");

            RotaryKilnHeatState[] states = RotaryKilnHeatState.values();
            int stateIndex = nbt.getInt("heat_state");
            this.heatState = stateIndex >= 0&&stateIndex < states.length? states[stateIndex]: RotaryKilnHeatState.MACHINE_OFF;

            CompoundTag data = nbt.getCompound("energy_input_packets");
            int size = data.size();
            lastEnergyPackets.clear();
            for(int i = 0; i < size; i++)
            {
                lastEnergyPackets.add(i, data.getDouble("i"+i));
            }
            this.nextPacketIndex = 0;
        }

        @Override
        public void writeSyncNBT(CompoundTag nbt)
        {
            writeSaveNBT(nbt);
            // All the client needs is whether to spin the tube; the GUI gets heat, energy and
            // process progress through the menu's own synced data
            nbt.putBoolean("is_active", isActive);
        }

        @Override
        public void readSyncNBT(CompoundTag nbt)
        {
            readSaveNBT(nbt);
            this.isActive = nbt.getBoolean("is_active");
        }

        public float getHeat()
        {
            return this.heatLevel;
        }

        /**
         * Power being offered to the kiln in FE/t, averaged over the last
         * {@link RotaryKilnLogic#ENERGY_SAMPLE_WINDOW} ticks. This both picks the heat tier and
         * is what the GUI reads out.
         * <p>
         * It measures what the supply pushes at the kiln, not what the kiln burns: a connector
         * offers its full rate every tick whether or not the buffer has room, so a full kiln
         * still reads the supply's rate rather than its own draw.
         */
        public int getAveragePower()
        {
            if(lastEnergyPackets.isEmpty()) return 0;
            double sum = 0;
            for(double transfer : lastEnergyPackets) sum += transfer;
            return Math.max(0, (int)Math.round(sum/lastEnergyPackets.size()));
        }

        @Override
        public AveragingEnergyStorage getEnergy()
        {
			return total_energy;
        }

        public MutableEnergyStorage getEnergyHV()
        {
            return energy_hv;
        }

        public MutableEnergyStorage getEnergyMV()
        {
            return energy_mv;
        }

        public MutableEnergyStorage getEnergyLV()
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

        /**
         * Whether the kiln is sitting close enough to {@code recipeHeat} to run that recipe.
         * A pure query - the heat a running recipe draws is taken in {@link #consumeProcessHeat()}.
         */
        public boolean hasRequiredHeat(int recipeHeat)
        {
            if(heatState!=RotaryKilnHeatState.RUNNING_RECIPE) return false;
            return heatLevel >= recipeHeat-HEAT_TOLERANCE&&heatLevel <= recipeHeat+HEAT_TOLERANCE;
        }

        /** Takes one process tick's worth of heat out of the kiln. */
        public void consumeProcessHeat()
        {
            modifyHeat(-HEAT_PER_PROCESS_TICK);
        }

        public float getTargetHeat()
        {
            return targetHeat;
        }

        public void modifyHeat(float v)
        {
            float newHeat = this.heatLevel + v;
            boolean invalid = newHeat < 0 || newHeat > MAX_HEAT;
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
