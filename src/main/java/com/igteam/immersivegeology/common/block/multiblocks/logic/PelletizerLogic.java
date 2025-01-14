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
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.PelletizerShape;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.TrommelShape;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Function;

public class PelletizerLogic implements IMultiblockLogic<PelletizerLogic.State>, IServerTickableComponent<PelletizerLogic.State>, IClientTickableComponent<PelletizerLogic.State> {
    public static final BlockPos REDSTONE_IN = new BlockPos(0,0,1);
    public static final int ENERGY_CAPACITY = 12000;

    @Override
    public void tickClient(IMultiblockContext<State> context) {
        final PelletizerLogic.State state = context.getState();
        float rot = state.rotation;
        state.rotation = (float)((rot-2.5)%360);
    }

    @Override
    public void tickServer(IMultiblockContext<State> iMultiblockContext) {

    }

    @Override
    public State createInitialState(IInitialMultiblockContext<State> capability) {
        return new PelletizerLogic.State(capability);
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return PelletizerShape.GETTER;
    }

    public static class State implements IMultiblockState {
        public final AveragingEnergyStorage energy = new AveragingEnergyStorage(ENERGY_CAPACITY);
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();

        private float rotation;

        public State(IInitialMultiblockContext<State> context){
            rotation = 0;
        }

        @Override
        public void readSaveNBT(CompoundTag nbt){

        }

        @Override
        public void writeSaveNBT(CompoundTag nbt){

        }

        public float getRotation()
        {
            return rotation;
        }

        public boolean shouldRenderActive()
        {
            return true;
        }
    }

}