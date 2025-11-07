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
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.*;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.client.utils.TextUtils;
import blusunrize.immersiveengineering.common.blocks.multiblocks.blockimpl.MultiblockLevel;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.interfaces.MBOverlayText;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInMachine;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext.ProcessContextInMachine;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import blusunrize.immersiveengineering.common.util.DroppingMultiblockOutput;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler.IOConstraint;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler.IOConstraintGroup;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler.IntRange;
import com.igteam.immersivegeology.common.block.multiblocks.logic.CentrifugeLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGMultiblockState;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CentrifugeRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.ChemicalRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.CentrifugeShape;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CentrifugeLogic implements IMultiblockLogic<State>, IServerTickableComponent<State>, IClientTickableComponent<State>, MBOverlayText<State> {
    public static final BlockPos REDSTONE_IN = new BlockPos(2, 1, 1);

    private static final int ENERGY_CAPACITY = 32000;
    private static final Set<CapabilityPosition> ENERGY_INPUTS = Set.of(
            new CapabilityPosition(2,2,0, RelativeBlockFace.FRONT),
            new CapabilityPosition(2,1,0, RelativeBlockFace.FRONT));
    private static final CapabilityPosition FLUID_INPUT_CAP = new CapabilityPosition(4,0,4, RelativeBlockFace.BACK);
    private static final MultiblockFace OUTPUT_POS = new MultiblockFace(2,0,-1, RelativeBlockFace.BACK);
    private static final CapabilityPosition ITEM_OUTPUT_CAP = CapabilityPosition.opposing(OUTPUT_POS);

    private static final CapabilityPosition FLUID_PRIMARY_OUTPUT_CAP = new CapabilityPosition(0,2,0, RelativeBlockFace.UP);
    private static final CapabilityPosition FLUID_SECONDARY_OUTPUT_CAP = new CapabilityPosition(4,2,0, RelativeBlockFace.UP);

    public static final int TANK_VOLUME = 4 *FluidType.BUCKET_VOLUME;

    @Override
    public void tickServer(IMultiblockContext<State> context) {
        final State state = context.getState();
        if(state.mbLevelGetter == null) state.mbLevelGetter = context::getLevel;

        if(!state.tank.isEmpty()) tryRunRecipe(state, context.getLevel().getRawLevel());
        final boolean wasActive = state.isActive;
        state.isActive = state.processor.tickServer(state, context.getLevel(), state.rsState.isEnabled(context));
        if(state.processor.getQueueSize() > 0) context.requestMasterBESync();

        if((wasActive != state.isActive))
        {
            context.requestMasterBESync();
        }

        if(!state.primary_output_tank.isEmpty())
        {
            drainOutputTank(context, state.fluidOutputPrimary, state.primary_output_tank);
            context.requestMasterBESync();
        }

        if(!state.secondary_output_tank.isEmpty())
        {
            drainOutputTank(context, state.fluidOutputSecondary, state.secondary_output_tank);
            context.requestMasterBESync();
        }
    }

    @Override
    public void dropExtraItems(State state, Consumer<ItemStack> drop)
    {
        MBInventoryUtils.dropItems(state.getInventory(), drop);
    }

    private void tryRunRecipe(State state, Level level)
    {
        if(state.energy.getEnergyStored() <= 0 || state.processor.getQueueSize() >= state.processor.getMaxQueueSize()) return;

        final FluidStack input = state.tank.getFluid();
        if(input.isEmpty()) return;
        CentrifugeRecipe recipe = CentrifugeRecipe.findRecipe(level, input);
        if(recipe == null) return;
        MultiblockProcessInMachine<CentrifugeRecipe> process = new MultiblockProcessInMachine<>(recipe);
        if(input.isEmpty()) process.setInputTanks(0);

        if(state.processor.addProcessToQueue(process, level, true))
        {
            state.tank.drain(recipe.fluidIn.getAmount(), FluidAction.EXECUTE);
            state.processor.addProcessToQueue(process, level, false);
        }
    }

    private void drainOutputTank(IMultiblockContext<CentrifugeLogic.State> context, CapabilityReference<IFluidHandler> outputRef, FluidTank tank)
    {
        int outSize = Math.min(FluidType.BUCKET_VOLUME, tank.getFluidAmount());
        FluidStack out = Utils.copyFluidStackWithAmount(tank.getFluid(), outSize, false);
        IFluidHandler output = outputRef.getNullable();

        if(output==null)
            return;

        int accepted = output.fill(out, FluidAction.SIMULATE);
        if(accepted > 0)
        {
            int drained = output.fill(Utils.copyFluidStackWithAmount(out, Math.min(out.getAmount(), accepted), false), FluidAction.EXECUTE);
            tank.drain(drained, FluidAction.EXECUTE);
            context.markMasterDirty();
            context.requestMasterBESync();
        }
    }

    @Override
    public State createInitialState(IInitialMultiblockContext<State> capability) {
        return new CentrifugeLogic.State(capability);
    }

    @Override
    public <T> LazyOptional<T> getCapability(IMultiblockContext<CentrifugeLogic.State> ctx, CapabilityPosition position, Capability<T> cap)
    {
        final CentrifugeLogic.State state = ctx.getState();
        if(cap == ForgeCapabilities.ENERGY && (position.side()==null || ENERGY_INPUTS.contains(position)))
        {
            return state.energyCap.cast(ctx);
        }

        if(cap == ForgeCapabilities.FLUID_HANDLER)
        {
            if(FLUID_INPUT_CAP.equals(position))
            {
                return state.fInputCap.cast(ctx);
            }

            if(FLUID_PRIMARY_OUTPUT_CAP.equals(position))
            {
                return state.fPrimaryOutput.cast(ctx);
            }
            if(FLUID_SECONDARY_OUTPUT_CAP.equals(position))
            {
                return state.fSecondaryOutput.cast(ctx);
            }
        }

        if(cap==ForgeCapabilities.ITEM_HANDLER)
        {
            if(ITEM_OUTPUT_CAP.equals(position))
                return state.itemOutputCap.cast(ctx);
        }

        return LazyOptional.empty();
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return CentrifugeShape.GETTER;
    }

    @Nullable
    @Override
    public List<Component> getOverlayText(State state, Player player, boolean b)
    {
        if(Utils.isFluidRelatedItemStack(player.getItemInHand(InteractionHand.MAIN_HAND)))
            return List.of(TextUtils.formatFluidStack(state.tank.getFluid()), TextUtils.formatFluidStack(state.primary_output_tank.getFluid()), TextUtils.formatFluidStack(state.secondary_output_tank.getFluid()), Component.literal("Processes: " + state.processor.getQueueSize()));
        return null;
    }

    @Override
    public void tickClient(IMultiblockContext<State> context)
    {
        final State state = context.getState();
        float rot = state.rotation;
        if(state.shouldRenderActive()) state.rotation = (float)((rot-3.5)%360);
    }

    public static class State implements IGMultiblockState, ProcessContextInMachine<CentrifugeRecipe>
    {
        public final AveragingEnergyStorage energy = new AveragingEnergyStorage(ENERGY_CAPACITY);
        private final MultiblockProcessor<CentrifugeRecipe, ProcessContextInMachine<CentrifugeRecipe>> processor;
        public final SlotwiseItemHandler inventory;

        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();

        public final FluidTank tank = new FluidTank(TANK_VOLUME);
        private final StoredCapability<IFluidHandler> fInputCap;
        private final StoredCapability<IEnergyStorage> energyCap;
        private final DroppingMultiblockOutput output;
        private final StoredCapability<IItemHandler> itemOutputCap;

        private final CapabilityReference<IFluidHandler> fluidOutputPrimary, fluidOutputSecondary;
        private final StoredCapability<IFluidHandler> fPrimaryOutput, fSecondaryOutput;

        public final FluidTank primary_output_tank = new FluidTank(TANK_VOLUME);
        public final FluidTank secondary_output_tank = new FluidTank(TANK_VOLUME);

        private Supplier<IMultiblockLevel> mbLevelGetter;
        public float rotation;
        public boolean isActive;

        public State(IInitialMultiblockContext<State> ctx)
        {
            final Supplier<@Nullable Level> getLevel = ctx.levelSupplier();
            this.rotation = 0;
            this.energyCap = new StoredCapability<>(this.energy);
            this.output = new DroppingMultiblockOutput(OUTPUT_POS, ctx);
            this.processor = new MultiblockProcessor<>(
                16, 0, 8, ctx.getMarkDirtyRunnable(), CentrifugeRecipe.RECIPES::getById
            );
            this.inventory = SlotwiseItemHandler.makeWithGroups(
                List.of(new IOConstraintGroup(IOConstraint.NO_CONSTRAINT, 1)), ctx.getMarkDirtyRunnable()
            );
            Runnable changedAndSync = () -> {
                ctx.getSyncRunnable().run();
                ctx.getMarkDirtyRunnable().run();
            };
            this.itemOutputCap = new StoredCapability<>(new WrappingItemHandler(
                    inventory, false, true, new IntRange(0, 1)
            ));
            this.fInputCap = new StoredCapability<>(new ArrayFluidHandler(tank, true, true, changedAndSync));

            this.fPrimaryOutput = new StoredCapability<>(new ArrayFluidHandler(primary_output_tank, true, false, changedAndSync));
            this.fSecondaryOutput = new StoredCapability<>(new ArrayFluidHandler(secondary_output_tank, true, false, changedAndSync));

            this.fluidOutputPrimary = ctx.getCapabilityAt(ForgeCapabilities.FLUID_HANDLER, new MultiblockFace(FLUID_PRIMARY_OUTPUT_CAP.side(), FLUID_PRIMARY_OUTPUT_CAP.posInMultiblock().above()));
            this.fluidOutputSecondary = ctx.getCapabilityAt(ForgeCapabilities.FLUID_HANDLER, new MultiblockFace(FLUID_SECONDARY_OUTPUT_CAP.side(), FLUID_SECONDARY_OUTPUT_CAP.posInMultiblock().above()));

            this.isActive = false;
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt) {
            nbt.put("energy", energy.serializeNBT());
            nbt.put("processor", processor.toNBT());
            nbt.put("tank", tank.writeToNBT(new CompoundTag()));
            nbt.put("primary_output_tank", primary_output_tank.writeToNBT(new CompoundTag()));
            nbt.put("secondary_output_tank", secondary_output_tank.writeToNBT(new CompoundTag()));
            nbt.put("inventory", inventory.serializeNBT());
            nbt.putBoolean("isActive", isActive);
        }

        @Override
        public void readSaveNBT(CompoundTag nbt){
            energy.deserializeNBT(nbt.get("energy"));
            processor.fromNBT(nbt.get("processor"), MultiblockProcessInMachine::new);
            tank.readFromNBT(nbt.getCompound("tank"));
            primary_output_tank.readFromNBT(nbt.getCompound("primary_output_tank"));
            secondary_output_tank.readFromNBT(nbt.getCompound("secondary_output_tank"));
            inventory.deserializeNBT(nbt.getCompound("inventory"));
            isActive = nbt.getBoolean("isActive");
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

        @Override
        public void onProcessFinish(MultiblockProcess<CentrifugeRecipe, ?> process, Level level)
        {
            try {
                CentrifugeRecipe recipe = process.getRecipe(level);
                primary_output_tank.fill(recipe.primaryFluidOutput.get(), FluidAction.EXECUTE);
                secondary_output_tank.fill(recipe.secondaryFluidOutput.get(), FluidAction.EXECUTE);
            } catch(Exception error)
            {
                IGLib.IG_LOGGER.error("Error: {}", error.getMessage());
            }
        }

        @Override
        public int[] getOutputSlots()
        {
            return new int[]{0};
        }

        @Override
        public int[] getOutputTanks()
        {
            return new int[]{1,2};
        }

        @Override
        public IFluidTank[] getInternalTanks()
        {
            return new FluidTank[]{tank, primary_output_tank, secondary_output_tank};
        }

        @Override
        public IItemHandlerModifiable getInventory()
        {
            return inventory.getRawHandler();
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

        public boolean shouldRenderActive()
        {
            return isActive;
        }

        @Override
        public void invalidate(@NotNull IMultiblockContext<?> context)
        {
            this.fPrimaryOutput.get(context).invalidate();
            this.fSecondaryOutput.get(context).invalidate();
            this.itemOutputCap.get(context).invalidate();
            this.fInputCap.get(context).invalidate();
            this.energyCap.get(context).invalidate();
            this.itemOutputCap.get(context).invalidate();
        }
    }

}