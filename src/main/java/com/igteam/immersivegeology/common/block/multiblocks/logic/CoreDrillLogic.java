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
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext.ProcessContextInWorld;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import blusunrize.immersiveengineering.common.fluids.IEFluid;
import blusunrize.immersiveengineering.common.register.IEFluids;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.MultiFluidTank;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CoreDrillRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.CoreDrillShape;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
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
import org.stringtemplate.v4.ST;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class CoreDrillLogic implements IMultiblockLogic<CoreDrillLogic.State>, IServerTickableComponent<CoreDrillLogic.State>, IClientTickableComponent<CoreDrillLogic.State>, MBOverlayText<CoreDrillLogic.State>
{
    public static final BlockPos REDSTONE_IN = new BlockPos(3, 1, 8);

    private static final int ENERGY_CAPACITY = 64000;
    private static final Set<CapabilityPosition> ENERGY_INPUTS = Set.of(
            new CapabilityPosition(4,0,8, RelativeBlockFace.BACK),
            new CapabilityPosition(4,1,8, RelativeBlockFace.BACK),
            new CapabilityPosition(4,2,8, RelativeBlockFace.BACK));

    private static final MultiblockFace FLUID_OUTPUT = new MultiblockFace(8,0,3, RelativeBlockFace.LEFT);
    private static final CapabilityPosition FLUID_OUTPUT_CAP = new CapabilityPosition(8,0,3, RelativeBlockFace.LEFT);
    private static final CapabilityPosition FLUID_INPUT_CAP = new CapabilityPosition(8,0,5, RelativeBlockFace.LEFT);

    public static final int TANK_VOLUME = 8*FluidType.BUCKET_VOLUME;

    public static final int ENERGY_CONSUMPTION_RATE = 8192; // Per tick

    @Override
    public void tickClient(IMultiblockContext<State> context) {
        final State state = context.getState();
        Random rand = new Random();
        if(state.renderAsActive)
        {
            if(state.spinDown && state.spinWait == 1)
            {
                state.drill_angle = (state.drill_angle+state.drill_spin_rate)%360;
                state.gear_clockwise_angle = ((state.drill_height) / 4f) * 260;
                state.gear_counter_clockwise_angle = ((state.drill_height * -1) / 4f) * 260;
                state.drill_height = adjustHeight(state.drill_height, -4f, 0, 0.03125f, state) + (rand.nextInt(0, 100) > 15 ? rand.nextFloat() * 0.05f: 0f);
                state.drill_shake = rand.nextInt(0, 100) > 15 ? rand.nextFloat(0, 0.01f) : 0;
            }
            else
            {
                if(state.spinWait - 1 > 0)
                {
                    state.spinWait--;
                    state.drill_spin_rate = 24f * ((float) (state.spinWaitReset - state.spinWait) / state.spinWaitReset );
                    state.drill_angle = (state.drill_angle+state.drill_spin_rate) %360;
                    state.drill_shake = rand.nextFloat(-((float) state.spinWait/ state.spinWaitReset),(float) state.spinWait/ state.spinWaitReset) * 0.0075f;
                } else {
                    state.spinDown = true;
                }
            }
        } else {
            if(state.drill_spin_rate != 0)
            {
                state.spinWait += 1;
                state.drill_spin_rate = 24f * (1 - ((float) state.spinWait / state.spinWaitReset));
                state.drill_angle = (state.drill_angle+state.drill_spin_rate)%360;
                state.drill_direction = true;
                if(state.drill_height < -0.03125f)
                {
                    state.drill_height = adjustHeight(state.drill_height, -4f, 0, 0.03125f, state);
                    state.gear_clockwise_angle = ((state.drill_height)/4f)*260;
                    state.gear_counter_clockwise_angle = ((state.drill_height*-1)/4f)*260;
                }
            }
        }
    }

    private float adjustHeight(float current, float min, float max, float difference, State state)
    {
        boolean increase = state.drill_direction;
        if(increase)
        {
            if((current+difference) < max)
            {
                return current + difference;
            }
            state.drill_direction = false;
        }

        if(current-difference > min)
        {
            return current-difference;
        }
        state.drill_direction = true;

        return current;
    }

    @Override
    public void tickServer(IMultiblockContext<State> context) {
        // Now to force the multiblock to output fluid in the tank
        final State state = context.getState();

        final boolean wasActive = state.renderAsActive;
        state.renderAsActive = (!state.rsState.isEnabled(context)) && state.getEnergy().getEnergyStored() > ENERGY_CONSUMPTION_RATE;// state.processor.tickServer(state, context.getLevel(), state.rsState.isEnabled(context));
        if(wasActive != state.renderAsActive)
        {
            context.requestMasterBESync();
        }

        if(state.renderAsActive)
        {
            //Spawn particles here I suppose
        }

        if(state.output_tank.getFluidAmount() > 0){
            drainOutputTank(state, context);
        }

        if(!state.rsState.isEnabled(context))
        {
            if(state.energy.getEnergyStored() > ENERGY_CONSUMPTION_RATE)
            {
                if(state.energy.extractEnergy(ENERGY_CONSUMPTION_RATE, true) > 0)
                {
                    state.energy.extractEnergy(ENERGY_CONSUMPTION_RATE, false);
                }
            }
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

    public static class State implements IMultiblockState, ProcessContextInWorld<CoreDrillRecipe> {
        public final AveragingEnergyStorage energy = new AveragingEnergyStorage(ENERGY_CAPACITY);
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();
        public final FluidTank acid_tank = new FluidTank(TANK_VOLUME);
        public final FluidTank output_tank = new FluidTank(TANK_VOLUME);

        private final MultiblockProcessor<CoreDrillRecipe, ProcessContextInWorld<CoreDrillRecipe>> processor;

        private final CapabilityReference<IFluidHandler> fluidOutput;
        private final StoredCapability<IFluidHandler> fInputCap;
        private final StoredCapability<IFluidHandler> fOutputCap;

        private final StoredCapability<IEnergyStorage> energyCap;

        private float drill_angle;
        private float gear_clockwise_angle;
        private float gear_counter_clockwise_angle;
        private float drill_height;
        private boolean drill_direction = false;
        private boolean renderAsActive;
        private float drill_shake;
        private float drill_spin_rate;
        private boolean spinDown = false;
        private int spinWaitReset = 260;
        private int spinWait = spinWaitReset;

        public State(IInitialMultiblockContext<State> ctx){
            // This is selected the Block connected to the output side
            // Allows us to 'fill' it
            this.fluidOutput = ctx.getCapabilityAt(ForgeCapabilities.FLUID_HANDLER, FLUID_OUTPUT.face().offsetRelative(FLUID_OUTPUT.posInMultiblock(), 1), FLUID_OUTPUT.face());
            this.processor = new MultiblockProcessor<>(2048, 0, 1, ctx.getMarkDirtyRunnable(), CoreDrillRecipe.RECIPES::getById);
            this.energyCap = new StoredCapability<>(this.energy);
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
            nbt.putBoolean("renderActive", renderAsActive);
        }

        @Override
        public void readSyncNBT(CompoundTag nbt)
        {
            readSaveNBT(nbt);
            renderAsActive = nbt.getBoolean("renderActive");
        }

        public boolean shouldRenderActive()
        {
            return renderAsActive;
        }

        public float getDrillAngle()
        {
            return drill_angle;
        }

        public float getDrillSpeed()
        {
            return drill_spin_rate;
        }

        public float getDrillHeight()
        {
            return drill_height;
        }

        public float getGearClockwiseAngle()
        {
            return gear_clockwise_angle;
        }

        public float getGearCounterClockwiseAngle()
        {
            return gear_counter_clockwise_angle;
        }

        public float getDrillShake()
        {
            return drill_shake;
        }

        @Override
        public AveragingEnergyStorage getEnergy()
        {
            return energy;
        }
    }

}