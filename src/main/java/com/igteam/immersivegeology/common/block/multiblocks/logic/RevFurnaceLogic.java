package com.igteam.immersivegeology.common.block.multiblocks.logic;

import blusunrize.immersiveengineering.api.crafting.BlastFurnaceFuel;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.*;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import blusunrize.immersiveengineering.common.util.CachedRecipe;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler.IOConstraint;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler.IntRange;
import com.igteam.immersivegeology.common.block.multiblocks.logic.RevFurnaceLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGFurnaceHandler;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGFurnaceHandler.InputSlot;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGFurnaceHandler.OutputSlot;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RevFurnaceRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.RevFurnaceShape;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RevFurnaceLogic implements IMultiblockLogic<RevFurnaceLogic.State>, IServerTickableComponent<State>
{
    public static final BlockPos REDSTONE_IN = new BlockPos(0, 0, 0);
    static final int TANK_CAPACITY = 32*FluidType.BUCKET_VOLUME;

    public static final int INPUT_SLOT_1 = 0;
    public static final int OUTPUT_SLOT_1 = 1;
    public static final int INPUT_SLOT_2 = 0;
    public static final int OUTPUT_SLOT_2 = 1;
    public static final int NUM_SLOTS = 2;


    CapabilityPosition SLOT_1_INPUT_POSITION = new CapabilityPosition(1,1,1, RelativeBlockFace.UP);
    CapabilityPosition SLOT_2_INPUT_POSITION = new CapabilityPosition(1,1,4, RelativeBlockFace.UP);

    public static final MultiblockFace SLOT_1_OUTPUT_POSITION = new MultiblockFace(0,0,1, RelativeBlockFace.LEFT);
    public static final MultiblockFace SLOT_2_OUTPUT_POSITION = new MultiblockFace(0,0,4, RelativeBlockFace.RIGHT);

    CapabilityPosition SLOT_1_OUTPUT_FLUID = new CapabilityPosition(4,11,1, RelativeBlockFace.UP);
    CapabilityPosition SLOT_2_OUTPUT_FLUID = new CapabilityPosition(4,11,4, RelativeBlockFace.UP);

    @Override
    public void tickServer(IMultiblockContext<RevFurnaceLogic.State> context) {
        context.getState().furnace1.tickServer(context, 0);
        context.getState().furnace2.tickServer(context, 1);
        outputItems(context.getState());
    }
    private static final int[] OUTPUT_SLOTS = Util.make(new int[1], slots -> {slots[0] = 2;});

    private void outputItems(RevFurnaceLogic.State state)
    {
        IItemHandler outputHandler = state.output1.getNullable();
        if(outputHandler!=null)
            for(int j : OUTPUT_SLOTS)
            {
                final ItemStack nextStack = state.inventory1.getStackInSlot(j);
                if(nextStack.isEmpty())
                    continue;
                ItemStack stack = ItemHandlerHelper.copyStackWithSize(nextStack, 1);
                stack = ItemHandlerHelper.insertItem(outputHandler, stack, false);
                if(stack.isEmpty())
                    nextStack.shrink(1);
            }

        outputHandler = state.output2.getNullable();
        if(outputHandler!=null)
            for(int j : OUTPUT_SLOTS)
            {
                final ItemStack nextStack = state.inventory2.getStackInSlot(j);
                if(nextStack.isEmpty())
                    continue;
                ItemStack stack = ItemHandlerHelper.copyStackWithSize(nextStack, 1);
                stack = ItemHandlerHelper.insertItem(outputHandler, stack, false);
                if(stack.isEmpty())
                    nextStack.shrink(1);
            }
    }

    @Override
    public <T> LazyOptional<T> getCapability(IMultiblockContext<State> ctx, CapabilityPosition position, Capability<T> cap)
    {
        final State state = ctx.getState();
        if(cap==ForgeCapabilities.ITEM_HANDLER)
        {
            if(SLOT_1_INPUT_POSITION.equals(position)) return state.invCap1.cast(ctx);
            if(SLOT_2_INPUT_POSITION.equals(position)) return state.invCap2.cast(ctx);
            if(SLOT_1_OUTPUT_POSITION.posInMultiblock().equals(position.posInMultiblock())) {
                return state.outputHandler1.cast(ctx);
            }
            if(SLOT_2_OUTPUT_POSITION.posInMultiblock().equals(position.posInMultiblock())) {
                return state.outputHandler2.cast(ctx);
            }
        }
        else if(cap==ForgeCapabilities.FLUID_HANDLER)
            if(SLOT_1_OUTPUT_FLUID.equals(position) || SLOT_2_OUTPUT_FLUID.equals(position)) return state.fluidCap.cast(ctx);

        return LazyOptional.empty();
    }

    @Override
    public void dropExtraItems(State state, Consumer<ItemStack> drop)
    {
        MBInventoryUtils.dropItems(state.inventory1, drop);
        MBInventoryUtils.dropItems(state.inventory2, drop);
    }

    @Override
    public State createInitialState(IInitialMultiblockContext<State> capability) {
        return new RevFurnaceLogic.State(capability);
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return RevFurnaceShape.GETTER;
    }

    public static class State implements IMultiblockState, IGFurnaceHandler.IFurnaceEnvironment<RevFurnaceRecipe>
    {
        private final SlotwiseItemHandler inventory1;
        private final SlotwiseItemHandler inventory2;
        final IGFurnaceHandler<RevFurnaceRecipe> furnace1;
        final IGFurnaceHandler<RevFurnaceRecipe> furnace2;

        private final Supplier<RevFurnaceRecipe> cachedRecipe1;
        private final Supplier<RevFurnaceRecipe> cachedRecipe2;

        private final FluidTank tank = new FluidTank(TANK_CAPACITY);
        private final StoredCapability<IItemHandler> invCap1;
        private final StoredCapability<IItemHandler> invCap2;

        private final StoredCapability<IItemHandler> outputHandler1;
        private final StoredCapability<IItemHandler> outputHandler2;

        private final CapabilityReference<IItemHandler> output1;
        private final CapabilityReference<IItemHandler> output2;

        private final StoredCapability<IFluidHandler> fluidCap;

        public State(IInitialMultiblockContext<?> ctx)
        {
            final Supplier<@Nullable Level> getLevel = ctx.levelSupplier();
            inventory1 = new SlotwiseItemHandler(List.of(
                    new IOConstraint(true, i -> RevFurnaceRecipe.findRecipe(getLevel.get(), i, null)!=null),
                    new IOConstraint(true, i -> BlastFurnaceFuel.isValidBlastFuel(getLevel.get(), i)),
                    IOConstraint.OUTPUT
            ), ctx.getMarkDirtyRunnable());

            inventory2 = new SlotwiseItemHandler(List.of(
                    new IOConstraint(true, i -> RevFurnaceRecipe.findRecipe(getLevel.get(), i, null)!=null),
                    new IOConstraint(true, i -> BlastFurnaceFuel.isValidBlastFuel(getLevel.get(), i)),
                    IOConstraint.OUTPUT
            ), ctx.getMarkDirtyRunnable());


            this.output1 = ctx.getCapabilityAt(ForgeCapabilities.ITEM_HANDLER, SLOT_1_OUTPUT_POSITION);
            this.output2 = ctx.getCapabilityAt(ForgeCapabilities.ITEM_HANDLER, SLOT_2_OUTPUT_POSITION);

            this.outputHandler1 = new StoredCapability<>(new WrappingItemHandler(
                    inventory1, false, true, new IntRange(2,3)
            ));

            this.outputHandler2 = new StoredCapability<>(new WrappingItemHandler(
                    inventory2, false, true, new IntRange(2,3)
            ));

            furnace1 = new IGFurnaceHandler<>(
                    1,
                    List.of(new InputSlot<>(r -> r.input, 0)),
                    List.of(new OutputSlot<>(r -> r.result, 2)),
                    r -> r.time,
                    ctx.getMarkDirtyRunnable()
            );

            furnace2 = new IGFurnaceHandler<>(
                    1,
                    List.of(new InputSlot<>(r -> r.input, 0)),
                    List.of(new OutputSlot<>(r -> r.result, 2)),
                    r -> r.time,
                    ctx.getMarkDirtyRunnable()
            );

            cachedRecipe1 = CachedRecipe.cached(
                    RevFurnaceRecipe::findRecipe, getLevel, () -> inventory1.getStackInSlot(0)
            );

            cachedRecipe2 = CachedRecipe.cached(
                    RevFurnaceRecipe::findRecipe, getLevel, () -> inventory2.getStackInSlot(0)
            );

            this.invCap1 = new StoredCapability<>(this.inventory1);
            this.invCap2 = new StoredCapability<>(this.inventory2);
            this.fluidCap = new StoredCapability<>(
                    new ArrayFluidHandler(new IFluidTank[]{tank}, true, false, ctx.getMarkDirtyRunnable())
            );
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt)
        {
            nbt.put("inventory1", inventory1.serializeNBT());
            nbt.put("inventory2", inventory2.serializeNBT());
            nbt.put("furnace1", furnace1.toNBT());
            nbt.put("furnace2", furnace2.toNBT());
        }

        @Override
        public void readSaveNBT(CompoundTag nbt)
        {
            inventory1.deserializeNBT(nbt.getCompound("inventory1"));
            inventory2.deserializeNBT(nbt.getCompound("inventory2"));
            furnace1.readNBT(nbt.get("furnace1"));
            furnace2.readNBT(nbt.get("furnace2"));
        }

        @Override
        public IItemHandlerModifiable getInventory(int furnaceIndex)
        {
            return furnaceIndex == 0 ? inventory1 : inventory2;
        }

        @Override
        public @Nullable RevFurnaceRecipe getRecipeForInput(int furnaceIndex)
        {
            return furnaceIndex == 0 ? cachedRecipe1.get() : cachedRecipe2.get();
        }

        @Override
        public int getBurnTimeOf(Level level, ItemStack fuel)
        {
            return BlastFurnaceFuel.getBlastFuelTime(level, fuel);
        }

        public ContainerData getStateView1()
        {
            return furnace1.stateView;
        }

        public ContainerData getStateView2()
        {
            return furnace2.stateView;
        }
    }
}