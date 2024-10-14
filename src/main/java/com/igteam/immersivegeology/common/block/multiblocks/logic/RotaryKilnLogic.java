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
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.*;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInMachine;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInWorld;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext.ProcessContextInMachine;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext.ProcessContextInWorld;
import blusunrize.immersiveengineering.common.util.DroppingMultiblockOutput;
import blusunrize.immersiveengineering.common.util.inventory.InsertOnlyInventory;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler.IOConstraint;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler.IOConstraintGroup;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler.IntRange;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RotaryKilnRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.RotaryKilnShape;
import com.igteam.immersivegeology.core.lib.IGLib;
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
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class RotaryKilnLogic implements IMultiblockLogic<RotaryKilnLogic.State>, IServerTickableComponent<RotaryKilnLogic.State>, IClientTickableComponent<RotaryKilnLogic.State> {
    public static final BlockPos REDSTONE_IN = new BlockPos(2, 1, 2);
    private static final int ENERGY_CAPACITY = 256000;
    private static final Set<CapabilityPosition> ENERGY_INPUTS = Set.of(new CapabilityPosition(4,0, 0, RelativeBlockFace.FRONT), new CapabilityPosition(4,1, 0, RelativeBlockFace.FRONT));
    private static final MultiblockFace OUTPUT_POS = new MultiblockFace(-1,0,1, RelativeBlockFace.LEFT);
    private static final MultiblockFace INPUT_POS = new MultiblockFace(7,2,1, RelativeBlockFace.UP);
    private static final CapabilityPosition ITEM_OUTPUT_CAP = CapabilityPosition.opposing(OUTPUT_POS);
    private static final CapabilityPosition ITEM_INPUT_CAP = new CapabilityPosition(7,2,1, RelativeBlockFace.UP);


    @Override
    public void tickClient(IMultiblockContext<State> iMultiblockContext) {

    }

    @Override
    public void tickServer(IMultiblockContext<State> context) {
        final RotaryKilnLogic.State state = context.getState();
        state.processor.tickServer(state, context.getLevel(), state.rsState.isEnabled(context));

    }

    private static boolean tryRunRecipe(ItemStack input, State state, Level level)
    {
        if(state.energy.getEnergyStored() <= 0 || state.processor.getQueueSize() >= state.processor.getMaxQueueSize())
            return false;

        if(input.isEmpty()) return false;

        RotaryKilnRecipe recipe = RotaryKilnRecipe.findRecipe(level, input);

        if(recipe == null) return false;

        MultiblockProcessInWorld<RotaryKilnRecipe> process = new MultiblockProcessInWorld<>(recipe, input);
        input.shrink(1);
        return state.processor.addProcessToQueue(process, level, false);
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
            if((position.side()==null || ENERGY_INPUTS.contains(position))) return state.energyCap.cast(ctx);
        }

        if(cap == ForgeCapabilities.ITEM_HANDLER && ITEM_INPUT_CAP.equals(position))
        {
            return state.itemInputCap.cast(ctx);
        }

        return LazyOptional.empty();
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return RotaryKilnShape.GETTER;
    }

    public static class State implements IMultiblockState, ProcessContextInWorld<RotaryKilnRecipe>
    {
        public final AveragingEnergyStorage energy = new AveragingEnergyStorage(ENERGY_CAPACITY);
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();

        private final DroppingMultiblockOutput output;
        private final StoredCapability<IItemHandler> itemInputCap;

        private final StoredCapability<IEnergyStorage> energyCap;
        private final MultiblockProcessor<RotaryKilnRecipe, ProcessContextInWorld<RotaryKilnRecipe>> processor;
        Supplier<@Nullable Level> levelGetter;
        public State(IInitialMultiblockContext<State> ctx){
            this.energyCap = new StoredCapability<>(this.energy);
            this.output = new DroppingMultiblockOutput(OUTPUT_POS, ctx);
            this.processor = new MultiblockProcessor<>(16, 0, 1, ctx.getMarkDirtyRunnable(), RotaryKilnRecipe.RECIPES::getById);
            final Supplier<@Nullable Level> levelGetter = ctx.levelSupplier();
            final Runnable markDirty = ctx.getMarkDirtyRunnable();
            final Runnable sync = ctx.getSyncRunnable();
            Runnable changedAndSync = () -> {
                markDirty.run();
                sync.run();
            };

            this.itemInputCap = new StoredCapability<>(new InsertOnlyInventory()
            {
                @Override
                protected ItemStack insert(ItemStack toInsert, boolean simulate)
                {
                    toInsert = toInsert.copy();
                    if(tryRunRecipe(toInsert, RotaryKilnLogic.State.this, levelGetter.get()))
                    {
                        changedAndSync.run();
                    }
                    return toInsert;
                }
            });
        }

        @Override
        public void doProcessOutput(ItemStack result, IMultiblockLevel level)
        {
            output.insertOrDrop(result, level);
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt){
            nbt.put("energy", energy.serializeNBT());
            nbt.put("processor", processor.toNBT());
        }

        @Override
        public void readSaveNBT(CompoundTag nbt){
            energy.deserializeNBT(nbt.get("energy"));
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
    }

}