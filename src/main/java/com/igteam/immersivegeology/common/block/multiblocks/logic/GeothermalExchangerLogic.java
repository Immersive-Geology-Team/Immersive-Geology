/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.logic;

import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockPartBlock;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import blusunrize.immersiveengineering.common.blocks.multiblocks.blockimpl.MultiblockLevel;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext.ProcessContextInMachine;
import com.igteam.immersivegeology.common.block.multiblocks.IGGeothermalExchangerMultiblock;
import com.igteam.immersivegeology.common.block.multiblocks.part.GeothermalPart;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GeothermalExchangerRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.GeothermalExchangerShape;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.function.Function;

public class GeothermalExchangerLogic implements IMultiblockLogic<GeothermalExchangerLogic.State>, IServerTickableComponent<GeothermalExchangerLogic.State>, IClientTickableComponent<GeothermalExchangerLogic.State> {
    public static final BlockPos REDSTONE_IN = new BlockPos(2,5,1);
    public static final int ENERGY_CAPACITY = 16000;

    @Override
    public void tickClient(IMultiblockContext<State> iMultiblockContext) {

    }

    @Override
    public InteractionResult click(IMultiblockContext<State> ctx, BlockPos posInMultiblock, Player player, InteractionHand hand, BlockHitResult absoluteHit, boolean isClient)
    {
        return IMultiblockLogic.super.click(ctx, posInMultiblock, player, hand, absoluteHit, isClient);
    }

    @Override
    public void tickServer(IMultiblockContext<State> contex) {
        final State state = contex.getState();
        IMultiblockLevel multiblockLevel = contex.getLevel();
        Level rawLevel = multiblockLevel.getRawLevel();
        Vec3i size = IGGeothermalExchangerMultiblock.INSTANCE.getSize(rawLevel);

        updateMultiblockHeatStates(state, size, multiblockLevel);
    }

    private void updateMultiblockHeatStates(State state, Vec3i size, IMultiblockLevel multiblockLevel)
    {
        Level rawLevel = multiblockLevel.getRawLevel();
        List<StructureBlockInfo> structure = IGGeothermalExchangerMultiblock.INSTANCE.getStructure(rawLevel);
        int structureHeight = size.getY()-1;
        int structureLength = size.getX();
        int structureWidth = size.getZ();
        MutableBlockPos cursor = new MutableBlockPos();
        int index = 0;
        for(int h = -1; h < structureHeight; ++h)
        {
            for(int l = 0; l < structureLength; ++l)
            {
                for(int w = 0; w < structureWidth; ++w)
                {
                    cursor.set(l, h, w);
                    BlockState relativeState = multiblockLevel.getBlockState(cursor);
                    if(index < 66 && !(relativeState.getBlock() instanceof GeothermalPart))
                    {
                        int heatLevel = 0;
                        if(relativeState.is(Blocks.LAVA)) heatLevel = 3;
                        if(relativeState.is(Blocks.MAGMA_BLOCK)) heatLevel = 2;
                        if(relativeState.is(Blocks.OBSIDIAN)) heatLevel = 1;

                        state.setHeatStateAtIndex(index, heatLevel);
                        index++;
                    }
                }
            }
        }
    }

    @Override
    public State createInitialState(IInitialMultiblockContext<State> capability) {
        return new GeothermalExchangerLogic.State(capability);
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return GeothermalExchangerShape.GETTER;
    }

    public static class State implements IMultiblockState, ProcessContextInMachine<GeothermalExchangerRecipe>
    {
        private final AveragingEnergyStorage energy_storage = new AveragingEnergyStorage(ENERGY_CAPACITY);
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();
        private byte[] heating_states = new byte[17];
        private int cooling_rate;
        private int heat;

        public State(IInitialMultiblockContext<State> context){
            this.heat = 0;
            this.cooling_rate = 0;
        }

        @Override
        public void readSyncNBT(CompoundTag nbt)
        {
            IMultiblockState.super.readSyncNBT(nbt);
            heat = nbt.getInt("heat");
            cooling_rate = nbt.getInt("cooling");
            heating_states = nbt.getByteArray("heating_states");
        }

        @Override
        public void writeSyncNBT(CompoundTag nbt)
        {
            IMultiblockState.super.writeSyncNBT(nbt);
            nbt.putInt("heat", heat);
            nbt.putInt("cooling", cooling_rate);
            nbt.putByteArray("heating_states", heating_states);
        }

        @Override
        public void readSaveNBT(CompoundTag nbt){
            readSyncNBT(nbt);
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt){
            writeSyncNBT(nbt);
        }

		public int getCurrentHeat()
		{
            return heat;
		}

        public int getCoolingRate()
        {
            return cooling_rate;
        }

        @Override
        public AveragingEnergyStorage getEnergy()
        {
            return energy_storage;
        }

        public void setHeatStateAtIndex(int index, int state) {
            int bitIndex = index * 2;
            int byteIndex = bitIndex / 8;
            int offset = bitIndex % 8;

            int cleared = this.heating_states[byteIndex] & ~(0b11 << offset);
            int set = (state & 0b11) << offset;
            this.heating_states[byteIndex] = (byte)(cleared | set);
        }

        public int getHeatStateAtIndex(int index) {
            int bitIndex = index * 2;
            int byteIndex = bitIndex / 8;
            int offset = bitIndex % 8;

            return (heating_states[byteIndex] >> offset) & 0b11;
        }

        public byte[] getHeatingStates()
        {
            return heating_states;
        }
    }

}