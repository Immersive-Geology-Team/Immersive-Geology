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
import com.igteam.immersivegeology.common.block.multiblocks.shapes.SmallChemicalReactorShape;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Function;

public class SmallChemicalReactorLogic implements IMultiblockLogic<SmallChemicalReactorLogic.State>, IServerTickableComponent<SmallChemicalReactorLogic.State>, IClientTickableComponent<SmallChemicalReactorLogic.State> {
    public static final BlockPos REDSTONE_IN = new BlockPos(6,1,0);
    public static final int ENERGY_CAPACITY = 24000;

    @Override
    public void tickClient(IMultiblockContext<State> iMultiblockContext) {

    }

    @Override
    public void tickServer(IMultiblockContext<State> iMultiblockContext) {

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


        public State(IInitialMultiblockContext<State> context){

        }

        @Override
        public void readSaveNBT(CompoundTag nbt){

        }

        @Override
        public void writeSaveNBT(CompoundTag nbt){

        }
    }

}