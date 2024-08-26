package com.igteam.immersivegeology.common.block.multiblocks.logic;

import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import blusunrize.immersiveengineering.api.fluid.FluidUtils;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.*;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.client.utils.TextUtils;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.interfaces.MBOverlayText;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import blusunrize.immersiveengineering.common.fluids.IEFluid;
import blusunrize.immersiveengineering.common.register.IEFluids;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.MultiFluidTank;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.CoreDrillShape;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class CoreDrillLogic implements IMultiblockLogic<CoreDrillLogic.State>, IServerTickableComponent<CoreDrillLogic.State>, IClientTickableComponent<CoreDrillLogic.State>, MBOverlayText<CoreDrillLogic.State>
{
    public static final BlockPos REDSTONE_IN = new BlockPos(0, 0, 0);

    private static final int ENERGY_CAPACITY = 64000;
    private static final Set<CapabilityPosition> ENERGY_INPUTS = Set.of(
            new CapabilityPosition(4,0,8, RelativeBlockFace.FRONT),
            new CapabilityPosition(4,1,8, RelativeBlockFace.FRONT),
            new CapabilityPosition(4,2,8, RelativeBlockFace.FRONT));

    private static final MultiblockFace FLUID_OUTPUT = new MultiblockFace(8,0,3, RelativeBlockFace.LEFT);
    private static final CapabilityPosition FLUID_OUTPUT_CAP = new CapabilityPosition(8,0,3, RelativeBlockFace.LEFT);
    private static final CapabilityPosition FLUID_INPUT_CAP = new CapabilityPosition(8,0,5, RelativeBlockFace.LEFT);

    public static final int TANK_VOLUME = 8*FluidType.BUCKET_VOLUME;

    @Override
    public void tickClient(IMultiblockContext<State> iMultiblockContext) {

    }

    @Override
    public void tickServer(IMultiblockContext<State> context) {
        // Now to force the multiblock to output fluid in the tank
        final State state = context.getState();
        if(state.output_tank.getFluidAmount() > 0){
            drainOutputTank(state, context);
        }
    }

    private void drainOutputTank(State state, IMultiblockContext<State> context)
    {
        int outSize = Math.min(FluidType.BUCKET_VOLUME, state.output_tank.getFluidAmount());
        CapabilityReference<IFluidHandler> outputRef = state.fluidOutput;
        FluidStack out = Utils.copyFluidStackWithAmount(state.output_tank.getFluid(), outSize, false);
        IFluidHandler output = outputRef.getNullable();

        if(output==null)
            return;

        int accepted = output.fill(out, FluidAction.SIMULATE);
        if(accepted > 0)
        {
            int drained = output.fill(Utils.copyFluidStackWithAmount(out, Math.min(out.getAmount(), accepted), false), FluidAction.EXECUTE);
            state.output_tank.drain(drained, FluidAction.EXECUTE);
            context.markMasterDirty();
            context.requestMasterBESync();
        }
    }

    private void drainInputTank(State state, IMultiblockContext<State> context)
    {
        int outSize = Math.min(FluidType.BUCKET_VOLUME, state.acid_tank.getFluidAmount());
        CapabilityReference<IFluidHandler> outputRef = state.fluidOutput;
        FluidStack out = Utils.copyFluidStackWithAmount(state.acid_tank.getFluid(), outSize, false);
        IFluidHandler output = outputRef.getNullable();

        if(output==null)
            return;

        int accepted = output.fill(out, FluidAction.SIMULATE);
        if(accepted > 0)
        {
            int drained = output.fill(Utils.copyFluidStackWithAmount(out, Math.min(out.getAmount(), accepted), false), FluidAction.EXECUTE);
            state.acid_tank.drain(drained, FluidAction.EXECUTE);
            context.markMasterDirty();
            context.requestMasterBESync();
        }
    }

    @Override
    public State createInitialState(IInitialMultiblockContext<State> capability) {
        return new State(capability);
    }

    @Override
    public <T> LazyOptional<T> getCapability(IMultiblockContext<State> ctx, CapabilityPosition position, Capability<T> cap)
    {
        final State state = ctx.getState();
        if(cap == ForgeCapabilities.ENERGY && (position.side()==null || ENERGY_INPUTS.contains(position)))
        {
            return state.energyCap.cast(ctx);
        }

        if(cap == ForgeCapabilities.FLUID_HANDLER)
        {
            if(FLUID_OUTPUT_CAP.equals(position))
            {
                return state.fOutputCap.cast(ctx);
            }

            if(FLUID_INPUT_CAP.equals(position))
            {
                return state.fInputCap.cast(ctx);
            }
        }

        return LazyOptional.empty();
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return CoreDrillShape.GETTER;
    }

    @Nullable
    @Override
    public List<Component> getOverlayText(State state, Player player, boolean b)
    {
        if(Utils.isFluidRelatedItemStack(player.getItemInHand(InteractionHand.MAIN_HAND)))
            return List.of(TextUtils.formatFluidStack(state.acid_tank.getFluid()), TextUtils.formatFluidStack(state.output_tank.getFluid()));
        return null;
    }

    public static class State implements IMultiblockState {
        public final AveragingEnergyStorage energy = new AveragingEnergyStorage(ENERGY_CAPACITY);
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();
        public final FluidTank acid_tank = new FluidTank(TANK_VOLUME);
        public final FluidTank output_tank = new FluidTank(TANK_VOLUME);

        private final CapabilityReference<IFluidHandler> fluidOutput;
        private final StoredCapability<IFluidHandler> fInputCap;
        private final StoredCapability<IFluidHandler> fOutputCap;

        private final StoredCapability<IEnergyStorage> energyCap;

        public State(IInitialMultiblockContext<State> ctx){
            // This is selected the Block connected to the output side
            // Allows us to 'fill' it
            this.fluidOutput = ctx.getCapabilityAt(ForgeCapabilities.FLUID_HANDLER, FLUID_OUTPUT.face().offsetRelative(FLUID_OUTPUT.posInMultiblock(), 1), FLUID_OUTPUT.face());

            this.energyCap = new StoredCapability<>(energy);
            Runnable changedAndSync = () -> {
                ctx.getSyncRunnable().run();
                ctx.getMarkDirtyRunnable().run();
            };
            this.fInputCap = new StoredCapability<>(new ArrayFluidHandler(acid_tank, true, true, changedAndSync));
            this.fOutputCap = new StoredCapability<>(new ArrayFluidHandler(output_tank, true, true, changedAndSync));
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt){
            nbt.put("energy", energy.serializeNBT());
            nbt.put("acid_tank", acid_tank.writeToNBT(new CompoundTag()));
            nbt.put("output_tank", output_tank.writeToNBT(new CompoundTag()));
        }


        @Override
        public void readSaveNBT(CompoundTag nbt){
            energy.deserializeNBT(nbt.get("energy"));
            acid_tank.readFromNBT(nbt.getCompound("acid_tank"));
            output_tank.readFromNBT(nbt.getCompound("output_tank"));
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
    }

}