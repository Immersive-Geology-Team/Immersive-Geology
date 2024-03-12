package com.igteam.immersivegeology.common.blocks.multiblocks.logic;

import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import com.igteam.immersivegeology.common.blocks.multiblocks.shapes.CrystallizerShape;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Function;

public class CrystallizerLogic implements IMultiblockLogic<CrystallizerLogic.State>, IServerTickableComponent<CrystallizerLogic.State>, IClientTickableComponent<CrystallizerLogic.State> {

    @Override
    public void tickClient(IMultiblockContext<State> iMultiblockContext) {

    }

    @Override
    public void tickServer(IMultiblockContext<State> iMultiblockContext) {

    }

    @Override
    public State createInitialState(IInitialMultiblockContext<State> capability) {
        return null;
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return CrystallizerShape.GETTER;
    }

    public static class State implements IMultiblockState {
        public final AveragingEnergyStorage energy = new AveragingEnergyStorage(12000);
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