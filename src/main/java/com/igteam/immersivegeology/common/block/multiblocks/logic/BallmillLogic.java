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
import com.igteam.immersivegeology.common.block.helper.IGReceiveOnlyEnergy;
import com.igteam.immersivegeology.common.block.multiblocks.IGBallmillMultiblock;
import com.igteam.immersivegeology.common.block.multiblocks.logic.BloomeryLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGMultiblockState;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BallmillRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.PelletizerRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RotaryKilnRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.BallmillShape;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.RotaryKilnShape;
import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
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
    public void registerCapabilities(CapabilityRegistrar<State> register)
    {
        register.register(EnergyStorage.BLOCK, (state, position) ->
                position.side()==null||ENERGY_INPUTS.contains(position)?state.energyCap:null);
        register.registerAt(ItemHandler.BLOCK, ITEM_INPUT_CAP, state -> state.insertionHandler);
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return BallmillShape.GETTER;
    }

    public static class State implements IGMultiblockState, ProcessContextInWorld<BallmillRecipe>
    {
        public final AveragingEnergyStorage energy = new AveragingEnergyStorage(ENERGY_CAPACITY);
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();

        private final DroppingMultiblockOutput output;
        private final IItemHandler insertionHandler;
        private float rotation;
        private boolean renderAsActive;
        private final IEnergyStorage energyCap;
        private final MultiblockProcessor<BallmillRecipe, ProcessContextInWorld<BallmillRecipe>> processor;
        Supplier<@Nullable Level> levelGetter;
        public State(IInitialMultiblockContext<State> ctx){
            this.rotation = 0;
            this.energyCap = IGReceiveOnlyEnergy.of(this.energy);
            this.output = new DroppingMultiblockOutput(OUTPUT_POS, ctx);
            this.processor = new MultiblockProcessor<>(64, 0, 8, ctx.getMarkDirtyRunnable(), BallmillRecipe.RECIPES::getById);
            final Supplier<@Nullable Level> levelGetter = ctx.levelSupplier();
            final Runnable markDirty = ctx.getMarkDirtyRunnable();
            final Runnable sync = ctx.getSyncRunnable();
            Runnable changedAndSync = () -> {
                markDirty.run();
                sync.run();
            };

            this.insertionHandler = new InsertOnlyInventory()
            {
                @Override
                protected ItemStack insert(ItemStack toInsert, boolean simulate)
                {
                    ItemStack stack = toInsert.copy();
                    RecipeHolder<BallmillRecipe> recipe = BallmillRecipe.findRecipe(levelGetter.get(), stack);
                    if (recipe == null) {
                        return stack;
                    } else {
                        MultiblockProcessInWorld<BallmillRecipe> process = new MultiblockProcessInWorld<>(recipe, stack.copyWithCount(recipe.value().itemIn.getCount()));

                        if (processor.addProcessToQueue(process, levelGetter.get(), simulate)) {
                            stack.shrink(recipe.value().itemIn.getCount());
                        }

                        return stack;
                    }
                }
            };
        }


        @Override
        public void doProcessOutput(ItemStack result, IMultiblockLevel level)
        {
            output.insertOrDrop(result, level);
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt, HolderLookup.Provider provider){
            nbt.put("energy", energy.serializeNBT(provider));
            nbt.put("processor", processor.toNBT(provider));
        }

        public boolean shouldRenderActive()
        {
            return renderAsActive;
        }

        @Override
        public void readSaveNBT(CompoundTag nbt, HolderLookup.Provider provider){
            this.energy.deserializeNBT(provider, nbt.get("energy"));
            this.processor.fromNBT(nbt.get("processor"), MultiblockProcessInWorld::new, provider);
        }

        @Override
        public void writeSyncNBT(CompoundTag nbt, HolderLookup.Provider provider)
        {
            writeSaveNBT(nbt, provider);
            nbt.putBoolean("renderActive", renderAsActive);
        }

        @Override
        public void readSyncNBT(CompoundTag nbt, HolderLookup.Provider provider)
        {
            readSaveNBT(nbt, provider);
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

        @Override
        public void invalidate(@NotNull IMultiblockContext<?> context)
        {
        }
    }

}