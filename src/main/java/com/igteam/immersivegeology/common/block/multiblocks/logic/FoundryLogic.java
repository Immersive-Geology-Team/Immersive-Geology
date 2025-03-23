/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.logic;

import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.*;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.FoundryShape;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import java.util.function.Consumer;
import java.util.function.Function;

public class FoundryLogic implements IMultiblockLogic<FoundryLogic.State>, IServerTickableComponent<FoundryLogic.State>, IClientTickableComponent<FoundryLogic.State> {
    public static final BlockPos REDSTONE_IN = new BlockPos(0,1,1);

    @Override
    public void tickClient(IMultiblockContext<State> context) {

    }

    @Override
    public void tickServer(IMultiblockContext<State> context) {
    }

    @Override
    public State createInitialState(IInitialMultiblockContext<State> capability) {
        return new FoundryLogic.State(capability);
    }


    @Override
    public void dropExtraItems(State state, Consumer<ItemStack> drop)
    {

    }

    @Override
    public <T> LazyOptional<T> getCapability(IMultiblockContext<State> ctx, CapabilityPosition position, Capability<T> cap)
    {
        return LazyOptional.empty();
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return FoundryShape.GETTER;
    }

    public static class State implements IMultiblockState
    {
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();
        public State(IInitialMultiblockContext<State> ctx){

        }


        @Override
        public void writeSaveNBT(CompoundTag nbt){
        }

        @Override
        public void readSaveNBT(CompoundTag nbt){
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
    }

}