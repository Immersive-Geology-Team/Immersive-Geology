/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.logic;

import blusunrize.immersiveengineering.api.crafting.CrusherRecipe;
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
import blusunrize.immersiveengineering.client.utils.TextUtils;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.CrusherLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.interfaces.MBOverlayText;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.*;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext.ProcessContextInWorld;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import blusunrize.immersiveengineering.common.util.DroppingMultiblockOutput;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.InsertOnlyInventory;
import com.igteam.immersivegeology.common.block.multiblocks.logic.CoreDrillLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BallmillRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.PelletizerRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.PelletizerShape;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.TrommelShape;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PelletizerLogic implements IMultiblockLogic<PelletizerLogic.State>, IServerTickableComponent<PelletizerLogic.State>, IClientTickableComponent<PelletizerLogic.State>, MBOverlayText<PelletizerLogic.State>
{
    public static final BlockPos REDSTONE_IN = new BlockPos(2,0,0);
    public static final int ENERGY_CAPACITY = 12000;

    private static final MultiblockFace OUTPUT_POS = new MultiblockFace(1,0,4, RelativeBlockFace.FRONT);
    private static final Set<CapabilityPosition> ENERGY_INPUTS = Set.of(new CapabilityPosition(0,1, 3, RelativeBlockFace.UP));
    public static final int TANK_VOLUME = 4 * FluidType.BUCKET_VOLUME;
    private static final CapabilityPosition FLUID_INPUT_CAP = new CapabilityPosition(1,0,0, RelativeBlockFace.FRONT);
    private static final CapabilityPosition ITEM_OUTPUT_CAP = CapabilityPosition.opposing(OUTPUT_POS);

    public static final int ENERGY_CONSUMPTION_RATE = 20; // Per tick

    @Override
    public void tickClient(IMultiblockContext<State> context) {
        final PelletizerLogic.State state = context.getState();
        float rot = state.rotation;
        if(state.shouldRenderActive()) state.rotation = (float)((rot-3.5)%360);
    }

    @Override
    public void dropExtraItems(State state, Consumer<ItemStack> drop)
    {
        MBInventoryUtils.dropItems(state.getInventory(), drop);
    }

    @Override
    public void tickServer(IMultiblockContext<State> context) {
        final PelletizerLogic.State state = context.getState();
        final boolean isEnabled = state.rsState.isEnabled(context);
        final boolean wasActive = state.renderAsActive;
        state.renderAsActive = isEnabled && (!state.tank.isEmpty()) && state.processor.tickServer(state, context.getLevel(), state.rsState.isEnabled(context));
        if((wasActive != state.renderAsActive))
        {
            context.requestMasterBESync();
        }

        if(state.processor.getQueueSize() > 0 && !state.tank.isEmpty() && isEnabled)
        {
            state.tank.drain(2, FluidAction.EXECUTE);
            context.requestMasterBESync();
        }
    }

    @Override
    public State createInitialState(IInitialMultiblockContext<State> capability) {
        return new PelletizerLogic.State(capability);
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return PelletizerShape.GETTER;
    }

    @Override
    public void onEntityCollision(IMultiblockContext<State> ctx, BlockPos posInMultiblock, Entity collided)
    {
        if(collided.level().isClientSide)
            return;
        final PelletizerLogic.State state = ctx.getState();
        final IMultiblockLevel level = ctx.getLevel();
        final AABB internalBB = new AABB(0, 1f, 0, 3, 3f, 6);
        final AABB pelletizerHopper = level.toAbsolute(internalBB);
        if(collided instanceof ItemEntity itemEntity)
        {
            if (collided.getBoundingBox().intersects(pelletizerHopper)) {
                ItemStack stack = itemEntity.getItem();
                if(stack.isEmpty())
                    return;
                stack = stack.copy();
                if(insertItemToProcess(stack, itemEntity, true, state, level.getRawLevel()))
                {
                    if(insertItemToProcess(stack, itemEntity, false, state, level.getRawLevel()))
                        ctx.markDirtyAndSync();
                    if(stack.getCount() <= 0)
                        itemEntity.discard();
                    else
                    {
                        stack.shrink(1);
                        itemEntity.setItem(stack);
                    }
                }
            }
        }
    }

    private static boolean isInInput(BlockPos posInMultiblock, boolean allowMiddleLayer) {
        if (posInMultiblock.getY() != 2 && (!allowMiddleLayer || posInMultiblock.getY() != 1)) {
            return false;
        } else {
            return posInMultiblock.getX() > 0 && posInMultiblock.getX() < 4;
        }
    }

    private static boolean insertItemToProcess(ItemStack stack, ItemEntity itemEntity, boolean simulate, State state, Level rawLevel)
    {
        ItemStack remaining = state.insertionHandler.getValue().insertItem(0, new ItemStack(stack.getItem()) , simulate);
        return remaining.isEmpty();
    }

    @Override
    public <T> LazyOptional<T> getCapability(IMultiblockContext<State> ctx, CapabilityPosition position, Capability<T> cap)
    {
        final PelletizerLogic.State state = ctx.getState();
        if(cap == ForgeCapabilities.ENERGY)
        {
            if((position.side()==null || ENERGY_INPUTS.contains(position))) return state.energyCap.cast(ctx);
        }
        if(cap == ForgeCapabilities.FLUID_HANDLER)
        {
            if (cap == ForgeCapabilities.ITEM_HANDLER) {
                return state.insertionHandler.cast(ctx);
            }
            if(FLUID_INPUT_CAP.equals(position))
            {
                return state.fInputCap.cast(ctx);
            }
        }
        return LazyOptional.empty();
    }

    @Nullable
    @Override
    public List<Component> getOverlayText(State state, Player player, boolean b)
    {
        if(Utils.isFluidRelatedItemStack(player.getItemInHand(InteractionHand.MAIN_HAND)))
            return List.of(TextUtils.formatFluidStack(state.tank.getFluid()));
        return null;
    }

    public static class State implements IMultiblockState, ProcessContextInWorld<PelletizerRecipe>
    {
        public final AveragingEnergyStorage energy = new AveragingEnergyStorage(ENERGY_CAPACITY);
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();

        public final FluidTank tank = new FluidTank(TANK_VOLUME);
        private final StoredCapability<IFluidHandler> fInputCap;

        private final StoredCapability<IEnergyStorage> energyCap;
        private final StoredCapability<IItemHandler> insertionHandler;
        private final DroppingMultiblockOutput output;
        private final MultiblockProcessor<PelletizerRecipe, ProcessContextInWorld<PelletizerRecipe>> processor;
        private float rotation;
        private boolean renderAsActive;
        public State(IInitialMultiblockContext<State> ctx){
            final Supplier<@Nullable Level> levelGetter = ctx.levelSupplier();
            final Runnable markDirty = ctx.getMarkDirtyRunnable();
            final Runnable sync = ctx.getSyncRunnable();
            Runnable changedAndSync = () -> {
                markDirty.run();
                sync.run();
            };

            this.energyCap = new StoredCapability<>(this.energy);
            this.output = new DroppingMultiblockOutput(OUTPUT_POS, ctx);
            this.processor = new MultiblockProcessor<>(128, 0, 8, ctx.getMarkDirtyRunnable(), PelletizerRecipe.RECIPES::getById);

            DirectProcessingItemHandler<PelletizerRecipe> insertionHandler = (new DirectProcessingItemHandler<>(ctx.levelSupplier(), this.processor, PelletizerRecipe::findRecipe));
            this.insertionHandler = new StoredCapability<>(insertionHandler);

            this.rotation = 0;
            this.fInputCap = new StoredCapability<>(new ArrayFluidHandler(tank, true, true, changedAndSync));
        }

        @Override
        public void readSaveNBT(CompoundTag nbt){
            this.tank.readFromNBT(nbt.getCompound("tank"));
            this.energy.deserializeNBT(nbt.get("energy"));
            this.processor.fromNBT(nbt.get("processor"), MultiblockProcessInWorld::new);
        }

        @Override
        public void doProcessOutput(ItemStack result, IMultiblockLevel level)
        {
            output.insertOrDrop(result, level);
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt){
            nbt.put("tank", this.tank.writeToNBT(new CompoundTag()));
            nbt.put("energy", energy.serializeNBT());
            nbt.put("processor", processor.toNBT());
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

        public float getRotation()
        {
            return rotation;
        }

        public boolean shouldRenderActive()
        {
            return renderAsActive;
        }

        @Override
        public AveragingEnergyStorage getEnergy()
        {
            return energy;
        }

        public List<MultiblockProcess<PelletizerRecipe, ProcessContextInWorld<PelletizerRecipe>>> getProcessQueue() {
            return this.processor.getQueue();
        }
    }

}