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
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MBInventoryUtils;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import blusunrize.immersiveengineering.common.util.CachedRecipe;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler.IOConstraint;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BloomeryFuel;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGFurnaceHandler;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGFurnaceHandler.InputSlot;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGFurnaceHandler.OutputSlot;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BloomeryRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.BloomeryShape;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class BloomeryLogic implements IMultiblockLogic<BloomeryLogic.State>, IServerTickableComponent<BloomeryLogic.State>, IClientTickableComponent<BloomeryLogic.State> {
    public static final int NUM_SLOTS = 3;

    @Override
    public void tickClient(IMultiblockContext<State> iMultiblockContext) {

    }

    @Override
    public void tickServer(IMultiblockContext<State> context) {
        final State state = context.getState();
        state.furnace.tickServer(context, 0);
    }

    @Override
    public void dropExtraItems(State state, Consumer<ItemStack> drop)
    {
        IGLib.IG_LOGGER.info("Drop?");
        MBInventoryUtils.dropItems(state.inventory, drop);
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
        private final SlotwiseItemHandler inventory;
        final IGFurnaceHandler<BloomeryRecipe> furnace;
        private final Supplier<BloomeryRecipe> cachedRecipe;

        public State(IInitialMultiblockContext<BloomeryLogic.State> ctx)
        {
            Supplier<Level> getLevel = ctx.levelSupplier();
            this.inventory = new SlotwiseItemHandler(List.of(new SlotwiseItemHandler.IOConstraint(true, (i) -> {
                return BloomeryRecipe.findRecipe((Level)getLevel.get(), i, (BloomeryRecipe)null) != null;
            }), new SlotwiseItemHandler.IOConstraint(true, (i) -> {
                return BloomeryFuel.isValidBloomeryFuel((Level)getLevel.get(), i);
            }), IOConstraint.OUTPUT, IOConstraint.OUTPUT), ctx.getMarkDirtyRunnable());

            this.furnace = new IGFurnaceHandler<>(
                    1,
                    List.of(new InputSlot<>(r -> r.input, 0)),
                    List.of(new OutputSlot<>(r -> r.result, 2)),
                    r -> r.time,
                    ctx.getMarkDirtyRunnable()
            );

            this.cachedRecipe = CachedRecipe.cached(BloomeryRecipe::findRecipe, getLevel, () -> {
                return this.inventory.getStackInSlot(0);
            });
        }

        @Override
        public void readSaveNBT(CompoundTag nbt){
            inventory.deserializeNBT(nbt.getCompound("inventory"));
            furnace.readNBT(nbt.getCompound("furnace"), 0);
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt){
            nbt.put("inventory", inventory.serializeNBT());
            nbt.put("furnace", furnace.toNBT(0));
        }

        @Override
        public void readSyncNBT(CompoundTag nbt)
        {
            readSaveNBT(nbt);
        }

        @Override
        public void writeSyncNBT(CompoundTag nbt)
        {
            writeSaveNBT(nbt);
        }

        @Override
        public IItemHandlerModifiable getInventory(int furnaceIndex)
        {
            return inventory;
        }

        @Override
        public @Nullable BloomeryRecipe getRecipeForInput(int furnaceIndex)
        {
            return cachedRecipe.get();
        }

        @Override
        public int getBurnTimeOf(Level level, ItemStack fuel)
        {
            return BloomeryFuel.getBloomeryFuelTime(level, fuel);
        }

        public ContainerData getStateView() {
            return this.furnace.stateView;
        }
    }

}