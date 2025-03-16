/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.logic;

import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
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
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.CrusherLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.DirectProcessingItemHandler;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInWorld;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext.ProcessContextInWorld;
import blusunrize.immersiveengineering.common.util.DroppingMultiblockOutput;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.InsertOnlyInventory;
import com.igteam.immersivegeology.common.block.multiblocks.IGBallmillMultiblock;
import com.igteam.immersivegeology.common.block.multiblocks.logic.BloomeryLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BallmillRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.PelletizerRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RotaryKilnRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.BallmillShape;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.RotaryKilnShape;
import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class BallmillLogic implements IMultiblockLogic<BallmillLogic.State>, IServerTickableComponent<BallmillLogic.State>, IClientTickableComponent<BallmillLogic.State> {
    public static final BlockPos REDSTONE_IN = new BlockPos(4,1,3);
    private static final int ENERGY_CAPACITY = 64000;
    private static final Set<CapabilityPosition> ENERGY_INPUTS = Set.of(new CapabilityPosition(0,1, 3, RelativeBlockFace.UP));
    private static final MultiblockFace OUTPUT_POS = new MultiblockFace(2,0,4, RelativeBlockFace.FRONT);
    private static final MultiblockFace INPUT_POS = new MultiblockFace(0,0,1, RelativeBlockFace.FRONT);
    private static final CapabilityPosition ITEM_OUTPUT_CAP = CapabilityPosition.opposing(OUTPUT_POS);
    private static final CapabilityPosition ITEM_INPUT_CAP = new CapabilityPosition(0,0,1, RelativeBlockFace.RIGHT);

    public static final int ENERGY_CONSUMPTION_RATE = 80; // Per tick


    @Override
    public void tickClient(IMultiblockContext<State> context) {
        final BallmillLogic.State state = context.getState();
        if(state.renderAsActive)
        {
            float rot = state.rotation;
            state.rotation = (float)((rot+2.5)%360);
        }
    }

    @Override
    public void tickServer(IMultiblockContext<State> context) {
        final BallmillLogic.State state = context.getState();

        final boolean wasActive = state.renderAsActive;
        state.renderAsActive = state.processor.tickServer(state, context.getLevel(), state.rsState.isEnabled(context));

        if(wasActive != state.renderAsActive)
        {
            context.requestMasterBESync();
        }
    }

    @Override
    public State createInitialState(IInitialMultiblockContext<State> capability) {
        return new BallmillLogic.State(capability);
    }


    @Override
    public void dropExtraItems(State state, Consumer<ItemStack> drop)
    {
        MBInventoryUtils.dropItems(state.getInventory(), drop);
    }

    @Override
    public <T> LazyOptional<T> getCapability(IMultiblockContext<State> ctx, CapabilityPosition position, Capability<T> cap)
    {
        final BallmillLogic.State state = ctx.getState();
        if(cap == ForgeCapabilities.ENERGY)
        {
            if((position.side()==null || ENERGY_INPUTS.contains(position))) return state.energyCap.cast(ctx);
        }

        if(cap == ForgeCapabilities.ITEM_HANDLER && ITEM_INPUT_CAP.equals(position))
        {
            return state.insertionHandler.cast(ctx);
        }

        return LazyOptional.empty();
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return BallmillShape.GETTER;
    }

    public static class State implements IMultiblockState, ProcessContextInWorld<BallmillRecipe>
    {
        public final AveragingEnergyStorage energy = new AveragingEnergyStorage(ENERGY_CAPACITY);
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();

        private final DroppingMultiblockOutput output;
        private final StoredCapability<IItemHandler> insertionHandler;
        private float rotation;
        private boolean renderAsActive;
        private final StoredCapability<IEnergyStorage> energyCap;
        private final MultiblockProcessor<BallmillRecipe, ProcessContextInWorld<BallmillRecipe>> processor;
        Supplier<@Nullable Level> levelGetter;
        public State(IInitialMultiblockContext<State> ctx){
            this.rotation = 0;
            this.energyCap = new StoredCapability<>(this.energy);
            this.output = new DroppingMultiblockOutput(OUTPUT_POS, ctx);
            this.processor = new MultiblockProcessor<>(64, 0, 8, ctx.getMarkDirtyRunnable(), BallmillRecipe.RECIPES::getById);
            final Supplier<@Nullable Level> levelGetter = ctx.levelSupplier();
            final Runnable markDirty = ctx.getMarkDirtyRunnable();
            final Runnable sync = ctx.getSyncRunnable();
            Runnable changedAndSync = () -> {
                markDirty.run();
                sync.run();
            };

            this.insertionHandler = new StoredCapability<>(new InsertOnlyInventory()
            {
                @Override
                protected ItemStack insert(ItemStack toInsert, boolean simulate)
                {
                    ItemStack stack = toInsert.copy();
                    BallmillRecipe recipe = BallmillRecipe.findRecipe(levelGetter.get(), stack);
                    if (recipe == null) {
                        return stack;
                    } else {
                        MultiblockProcessInWorld<BallmillRecipe> process = new MultiblockProcessInWorld<>(recipe, stack);

                        if (processor.addProcessToQueue(process, levelGetter.get(), simulate)) {
                            stack.shrink(recipe.itemIn.getCount());
                        }

                        return stack;
                    }
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

        public boolean shouldRenderActive()
        {
            return renderAsActive;
        }

        @Override
        public void readSaveNBT(CompoundTag nbt){
            this.energy.deserializeNBT(nbt.get("energy"));
            this.processor.fromNBT(nbt.get("processor"), MultiblockProcessInWorld::new);
        }

        @Override
        public void writeSyncNBT(CompoundTag nbt)
        {
            writeSaveNBT(nbt);
            nbt.putBoolean("renderActive", renderAsActive);
        }

        @Override
        public void readSyncNBT(CompoundTag nbt)
        {
            readSaveNBT(nbt);
            renderAsActive = nbt.getBoolean("renderActive");
        }

        @Override
        public AveragingEnergyStorage getEnergy()
        {
            return energy;
        }

        public float getRotation()
        {
            return rotation;
        }
    }

}