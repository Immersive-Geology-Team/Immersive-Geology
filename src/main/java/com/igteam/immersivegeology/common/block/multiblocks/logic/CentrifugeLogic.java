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
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.*;
import blusunrize.immersiveengineering.client.utils.TextUtils;
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
import com.igteam.immersivegeology.common.block.helper.IGReceiveOnlyEnergy;
import com.igteam.immersivegeology.common.block.multiblocks.logic.CentrifugeLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGMultiblockState;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CentrifugeRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.CentrifugeShape;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage;
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
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
        RecipeHolder<CentrifugeRecipe> recipe = CentrifugeRecipe.findRecipe(level, input);
        if(recipe == null) return;
        MultiblockProcessInMachine<CentrifugeRecipe> process = new MultiblockProcessInMachine<>(recipe);
        if(input.isEmpty()) process.setInputTanks(0);

        if(state.processor.addProcessToQueue(process, level, true))
        {
            state.tank.drain(recipe.value().fluidIn.amount(), FluidAction.EXECUTE);
            state.processor.addProcessToQueue(process, level, false);
        }
    }

    private void drainOutputTank(IMultiblockContext<CentrifugeLogic.State> context, Supplier<@Nullable IFluidHandler> outputRef, FluidTank tank)
    {
        int outSize = Math.min(FluidType.BUCKET_VOLUME, tank.getFluidAmount());
        FluidStack out = Utils.copyFluidStackWithAmount(tank.getFluid(), outSize, false);
        IFluidHandler output = outputRef.get();

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
    public void registerCapabilities(CapabilityRegistrar<State> register)
    {
        register.register(EnergyStorage.BLOCK, (state, position) ->
                position.side()==null||ENERGY_INPUTS.contains(position)?state.energyCap:null);
        register.register(FluidHandler.BLOCK, (state, position) -> {
            if(FLUID_INPUT_CAP.equals(position)) return state.fInputCap;
            if(FLUID_PRIMARY_OUTPUT_CAP.equals(position)) return state.fPrimaryOutput;
            if(FLUID_SECONDARY_OUTPUT_CAP.equals(position)) return state.fSecondaryOutput;
            return null;
        });
        register.registerAt(ItemHandler.BLOCK, ITEM_OUTPUT_CAP, state -> state.itemOutputCap);
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return CentrifugeShape.GETTER;
    }

    @Nullable
    @Override
    public List<Component> getOverlayText(State state, BlockPos posInMultiblock, BlockHitResult absoluteHit, Player player, boolean b)
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
        private final IFluidHandler fInputCap;
        private final IEnergyStorage energyCap;
        private final DroppingMultiblockOutput output;
        private final IItemHandler itemOutputCap;

        private final Supplier<@Nullable IFluidHandler> fluidOutputPrimary, fluidOutputSecondary;
        private final IFluidHandler fPrimaryOutput, fSecondaryOutput;

        public final FluidTank primary_output_tank = new FluidTank(TANK_VOLUME);
        public final FluidTank secondary_output_tank = new FluidTank(TANK_VOLUME);

        private Supplier<IMultiblockLevel> mbLevelGetter;
        public float rotation;
        public boolean isActive;

        public State(IInitialMultiblockContext<State> ctx)
        {
            final Supplier<@Nullable Level> getLevel = ctx.levelSupplier();
            this.rotation = 0;
            this.energyCap = IGReceiveOnlyEnergy.of(this.energy);
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
            this.itemOutputCap = new WrappingItemHandler(
                    inventory, false, true, new IntRange(0, 1)
            );
            this.fInputCap = new ArrayFluidHandler(tank, true, true, changedAndSync);

            this.fPrimaryOutput = new ArrayFluidHandler(primary_output_tank, true, false, changedAndSync);
            this.fSecondaryOutput = new ArrayFluidHandler(secondary_output_tank, true, false, changedAndSync);

            this.fluidOutputPrimary = ctx.getCapabilityAt(Capabilities.FluidHandler.BLOCK, new MultiblockFace(FLUID_PRIMARY_OUTPUT_CAP.side(), FLUID_PRIMARY_OUTPUT_CAP.posInMultiblock().above()));
            this.fluidOutputSecondary = ctx.getCapabilityAt(Capabilities.FluidHandler.BLOCK, new MultiblockFace(FLUID_SECONDARY_OUTPUT_CAP.side(), FLUID_SECONDARY_OUTPUT_CAP.posInMultiblock().above()));

            this.isActive = false;
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt, HolderLookup.Provider provider) {
            nbt.put("energy", energy.serializeNBT(provider));
            nbt.put("processor", processor.toNBT(provider));
            nbt.put("tank", tank.writeToNBT(provider, new CompoundTag()));
            nbt.put("primary_output_tank", primary_output_tank.writeToNBT(provider, new CompoundTag()));
            nbt.put("secondary_output_tank", secondary_output_tank.writeToNBT(provider, new CompoundTag()));
            nbt.put("inventory", inventory.serializeNBT(provider));
            nbt.putBoolean("isActive", isActive);
        }

        @Override
        public void readSaveNBT(CompoundTag nbt, HolderLookup.Provider provider){
            energy.deserializeNBT(provider, nbt.get("energy"));
            processor.fromNBT(nbt.get("processor"), (getRecipe, data, p) -> new MultiblockProcessInMachine<>(getRecipe, data), provider);
            tank.readFromNBT(provider, nbt.getCompound("tank"));
            primary_output_tank.readFromNBT(provider, nbt.getCompound("primary_output_tank"));
            secondary_output_tank.readFromNBT(provider, nbt.getCompound("secondary_output_tank"));
            inventory.deserializeNBT(provider, nbt.getCompound("inventory"));
            isActive = nbt.getBoolean("isActive");
        }

        @Override
        public void writeSyncNBT(CompoundTag nbt, HolderLookup.Provider provider)
        {
            writeSaveNBT(nbt, provider);
        }

        @Override
        public void readSyncNBT(CompoundTag nbt, HolderLookup.Provider provider)
        {
            readSaveNBT(nbt, provider);
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
        }
    }

}