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
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.interfaces.MBOverlayText;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInMachine;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInWorld;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext.ProcessContextInMachine;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext.ProcessContextInWorld;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import blusunrize.immersiveengineering.common.util.DroppingMultiblockOutput;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler.IOConstraint;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler.IOConstraintGroup;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler.IntRange;
import com.igteam.immersivegeology.common.block.multiblocks.logic.CrystallizerLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CoreDrillRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CrystallizerRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.CrystallizerShape;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.core.BlockPos;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class CrystallizerLogic implements IMultiblockLogic<CrystallizerLogic.State>, IServerTickableComponent<CrystallizerLogic.State>, MBOverlayText<State> {
    public static final BlockPos REDSTONE_IN = new BlockPos(2, 1, 1);

    private static final int ENERGY_CAPACITY = 16000;
    private static final CapabilityPosition ENERGY_INPUT = new CapabilityPosition(1,2,1, RelativeBlockFace.UP);

    private static final CapabilityPosition FLUID_INPUT_CAP = new CapabilityPosition(1,1,2, RelativeBlockFace.BACK);
    private static final MultiblockFace OUTPUT_POS = new MultiblockFace(1,1,-1, RelativeBlockFace.BACK);
    private static final CapabilityPosition ITEM_OUTPUT_CAP = CapabilityPosition.opposing(OUTPUT_POS);

    public static final int TANK_VOLUME = 4 *FluidType.BUCKET_VOLUME;

    @Override
    public void tickServer(IMultiblockContext<State> context) {
        final State state = context.getState();
        final int tank_amount = state.tank.getFluidAmount();
        state.processor.tickServer(state, context.getLevel(), state.rsState.isEnabled(context));
        tryRunRecipe(state, context.getLevel().getRawLevel());
        if(tank_amount != state.tank.getFluidAmount()) context.requestMasterBESync();
    }

    private void tryRunRecipe(State state, Level level)
    {
        if(state.energy.getEnergyStored() <= 0 || state.processor.getQueueSize() >= state.processor.getMaxQueueSize()) return;

        final FluidStack input = state.tank.getFluid();
        if(input.isEmpty()) return;
        CrystallizerRecipe recipe = CrystallizerRecipe.findRecipe(level, input);
        if(recipe == null) return;
        MultiblockProcessInMachine<CrystallizerRecipe> process = new MultiblockProcessInMachine<>(recipe);
        if(input.isEmpty()) process.setInputTanks(1);
        state.processor.addProcessToQueue(process, level, false);
    }

    @Override
    public State createInitialState(IInitialMultiblockContext<State> capability) {
        return new CrystallizerLogic.State(capability);
    }

    @Override
    public <T> LazyOptional<T> getCapability(IMultiblockContext<CrystallizerLogic.State> ctx, CapabilityPosition position, Capability<T> cap)
    {
        final CrystallizerLogic.State state = ctx.getState();
        if(cap == ForgeCapabilities.ENERGY && (position.side()==null || ENERGY_INPUT.equals(position)))
        {
            return state.energyCap.cast(ctx);
        }

        if(cap == ForgeCapabilities.FLUID_HANDLER)
        {
            if(FLUID_INPUT_CAP.equals(position))
            {
                return state.fInputCap.cast(ctx);
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
        return CrystallizerShape.GETTER;
    }

    @Nullable
    @Override
    public List<Component> getOverlayText(State state, Player player, boolean b)
    {
        if(Utils.isFluidRelatedItemStack(player.getItemInHand(InteractionHand.MAIN_HAND)))
            return List.of(TextUtils.formatFluidStack(state.tank.getFluid()));
        return null;
    }

    public static class State implements IMultiblockState, ProcessContextInMachine<CrystallizerRecipe>
    {
        public final AveragingEnergyStorage energy = new AveragingEnergyStorage(ENERGY_CAPACITY);
        private final MultiblockProcessor<CrystallizerRecipe, ProcessContextInMachine<CrystallizerRecipe>> processor;
        public final SlotwiseItemHandler inventory;

        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();

        public final FluidTank tank = new FluidTank(TANK_VOLUME);
        private final StoredCapability<IFluidHandler> fInputCap;
        private final StoredCapability<IEnergyStorage> energyCap;
        private final CapabilityReference<IItemHandler> output;
        private final StoredCapability<IItemHandler> itemOutputCap;

        public State(IInitialMultiblockContext<State> ctx)
        {
            this.energyCap = new StoredCapability<>(this.energy);
            this.output = ctx.getCapabilityAt(ForgeCapabilities.ITEM_HANDLER, OUTPUT_POS);
            this.processor = new MultiblockProcessor<>(
                1, 0, 1, ctx.getMarkDirtyRunnable(), CrystallizerRecipe.RECIPES::getById
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
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt){
            nbt.put("energy", energy.serializeNBT());
            nbt.put("processor", processor.toNBT());
            nbt.put("tank", tank.writeToNBT(new CompoundTag()));
            nbt.put("inventory", inventory.serializeNBT());
        }

        @Override
        public void readSaveNBT(CompoundTag nbt){
            energy.deserializeNBT(nbt.get("energy"));
            tank.readFromNBT(nbt.getCompound("tank"));
            inventory.deserializeNBT(nbt.getCompound("inventory"));
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
        public void onProcessFinish(MultiblockProcess<CrystallizerRecipe, ?> process, Level level)
        {
            try {
                CrystallizerRecipe recipe = process.getRecipe(level);
                tank.drain(recipe.fluidIn.getAmount(), FluidAction.EXECUTE);
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
        public IFluidTank[] getInternalTanks()
        {
            return new FluidTank[]{tank};
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
    }

}