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
import com.igteam.immersivegeology.common.block.multiblocks.logic.ChemicalReactorLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGFurnaceHandler;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BloomeryRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.BloomeryShape;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.GenericShape;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.IndSluiceShape;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class BloomeryLogic implements IMultiblockLogic<BloomeryLogic.State>, IServerTickableComponent<BloomeryLogic.State>, IClientTickableComponent<BloomeryLogic.State> {
    public static final BlockPos REDSTONE_IN = new BlockPos(6,1,4);

    @Override
    public void tickClient(IMultiblockContext<State> iMultiblockContext) {

    }

    @Override
    public void tickServer(IMultiblockContext<State> iMultiblockContext) {

    }

    @Override
    public State createInitialState(IInitialMultiblockContext<State> capability) {
        return new BloomeryLogic.State(capability);
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return BloomeryShape.GETTER;
    }

    public static class State implements IMultiblockState, IGFurnaceHandler.IFurnaceEnvironment<BloomeryRecipe>
	{

        public State(IInitialMultiblockContext<BloomeryLogic.State> ctx)
        {

        }

        @Override
        public void readSaveNBT(CompoundTag nbt){

        }

        @Override
        public void writeSaveNBT(CompoundTag nbt){

        }

        @Override
        public IItemHandlerModifiable getInventory(int furnaceIndex)
        {
            return null;
        }

        @Override
        public @Nullable BloomeryRecipe getRecipeForInput(int furnaceIndex)
        {
            return null;
        }

        @Override
        public int getBurnTimeOf(Level level, ItemStack fuel)
        {
            return 0;
        }
    }

}