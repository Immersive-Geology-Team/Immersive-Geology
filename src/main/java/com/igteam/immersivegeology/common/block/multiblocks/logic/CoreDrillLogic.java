/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.logic;

import blusunrize.immersiveengineering.api.ApiUtils;
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
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInWorld;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext.ProcessContextInWorld;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import blusunrize.immersiveengineering.common.fluids.IEFluid;
import blusunrize.immersiveengineering.common.register.IEFluids;
import blusunrize.immersiveengineering.common.register.IEParticles;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.MultiFluidTank;
import com.igteam.immersivegeology.common.block.helper.IGReceiveOnlyEnergy;
import com.igteam.immersivegeology.common.block.multiblocks.logic.CoreDrillLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGMultiblockState;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.ISkinnableMultiblockLogic;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CoreDrillRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.CoreDrillShape;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stringtemplate.v4.ST;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class CoreDrillLogic implements ISkinnableMultiblockLogic<State>, IServerTickableComponent<CoreDrillLogic.State>, IClientTickableComponent<CoreDrillLogic.State>, MBOverlayText<CoreDrillLogic.State>
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
    //TODO implement sister system to the Excavator, all 'core fluids' show as the same until processed.
    public static final int ENERGY_CONSUMPTION_RATE = 4092; // Per tick

    
    @Override
	public void dropExtraItems(State state, Consumer<ItemStack> drop)
	{
		MBInventoryUtils.dropItems(state.getInventory(), drop);
	}

    @Override
    public void tickClient(IMultiblockContext<State> context) {
        final State state = context.getState();
        // Particles
        if(state.renderAsActive)
        {
            if(state.spinDown && state.spinWait == 1)
            {
                Vec3 position = new Vec3(4.5f, 0, 4.5f);

                double xSpeed = ApiUtils.RANDOM.nextDouble(-0.25f, 0.25f);
                double zSpeed = ApiUtils.RANDOM.nextDouble(-0.25f, 0.25f);

                if(!state.drill_direction)
                {
                    final Vec3 absoluteSmokePosition = context.getLevel().toAbsolute(position);
                    context.getLevel().getRawLevel().addParticle(
                            ParticleTypes.POOF,
                            absoluteSmokePosition.x, absoluteSmokePosition.y, absoluteSmokePosition.z,
                            xSpeed, 0.0625, zSpeed);

                }

                for(int i = 0; i < 4; i++)
                {
                    Vec3 exhaust = context.getLevel().toAbsolute(new Vec3(2.75f, (8.25f+state.drill_height)-(i*0.6f), 4.5f));
                    zSpeed = ApiUtils.RANDOM.nextDouble(-0.2, -0.015625);
                    xSpeed = ApiUtils.RANDOM.nextDouble(-0.015625, 0.005625);
                    context.getLevel().getRawLevel().addAlwaysVisibleParticle(
                            ParticleTypes.SMOKE,
                            exhaust.x, exhaust.y, exhaust.z,
                            zSpeed, 0.0625, xSpeed);
                }
            }
        }

        animateDrill(context);
    }

    // Constants to improve readability and maintenance
    private static final float GEAR_RATIO = 260f / 4f;
    private static final float MAX_DRILL_SHAKE = 0.01f;
    private static final float DRILL_HEIGHT_INCREMENT = 0.03125f;
    private static final float MAX_DRILL_HEIGHT = -4f;
    private static final int SHAKE_PROBABILITY = 85;  // 100 - 15
    private void animateDrill(IMultiblockContext<State> context)
    {
        final State state = context.getState();
        Random rand = null;
        if(state.renderAsActive)
        {
            if(state.spinDown && state.spinWait == 1)
            {
                // Initialize Random only when needed
                rand = new Random();

                state.drill_angle = (state.drill_angle + state.drill_spin_rate) % 360;

                // Combine drill height calculations
                float newDrillHeight = state.drill_height;
                if (rand.nextInt(100) > SHAKE_PROBABILITY) {
                    newDrillHeight += rand.nextFloat() * 0.025f;
                }
                state.drill_height = newDrillHeight;

                // Calculate gear angles once
                float gearAngle = state.drill_height * GEAR_RATIO;
                state.gear_clockwise_angle = gearAngle;
                state.gear_counter_clockwise_angle = -gearAngle;

                state.drill_shake = rand.nextInt(100) > SHAKE_PROBABILITY ?
                        rand.nextFloat(0, MAX_DRILL_SHAKE) : 0;
            }
            else
            {
                if (state.spinWait - 1 > 0) {
                    state.drill_angle = (state.drill_angle + state.drill_spin_rate) % 360;

                    rand = new Random();
                    float shakeRange = (float)state.spinWait / state.spinWaitReset * 0.0075f;
                    state.drill_shake = rand.nextFloat(-shakeRange, shakeRange);
                }
            }
        } else {
            if(state.drill_spin_rate != 0)
            {
                state.drill_angle = (state.drill_angle + state.drill_spin_rate) % 360;

                if (state.drill_height < -DRILL_HEIGHT_INCREMENT) {
                    float gearAngle = state.drill_height * GEAR_RATIO;
                    state.gear_clockwise_angle = gearAngle;
                    state.gear_counter_clockwise_angle = -gearAngle;
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
        state.renderAsActive = (!state.rsState.isEnabled(context)) && state.getEnergy().getEnergyStored() > ENERGY_CONSUMPTION_RATE;
        if(wasActive != state.renderAsActive)
        {
            context.requestMasterBESync();
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
                    CoreDrillRecipe recipe = CoreDrillRecipe.get(context.getLevel().getRawLevel(), state.acid_tank.getFluid());

                    if(recipe != null && !state.drill_direction && state.spinDown)
                    {
                        int amount = recipe.getInput().getAmount();
                        if(!state.acid_tank.drain(amount, FluidAction.SIMULATE).isEmpty())
                        {
                            FluidStack toFill = new FluidStack(recipe.getOutput(), 1);
                            if(state.output_tank.fill(toFill, FluidAction.SIMULATE) > 0)
                            {
                                state.acid_tank.drain(amount, FluidAction.EXECUTE);
                                state.output_tank.fill(toFill, FluidAction.EXECUTE);
                            }
                        }
                    }
                }
            }
        }
        //move to func later
        Random rand = null;
        if(state.renderAsActive)
        {
            if(state.spinDown && state.spinWait == 1)
            {
                // Initialize Random only when needed
                rand = new Random();

                // Combine drill height calculations
                float newDrillHeight = adjustHeight(state.drill_height, MAX_DRILL_HEIGHT, 0, DRILL_HEIGHT_INCREMENT, state);
                if (rand.nextInt(100) > SHAKE_PROBABILITY) {
                    newDrillHeight += rand.nextFloat() * 0.025f;
                }
                state.drill_height = newDrillHeight;
            }
            else
            {
                if (state.spinWait - 1 > 0) {
                    state.spinWait--;
                    float spinProgress = (float)(state.spinWaitReset - state.spinWait) / state.spinWaitReset;
                    state.drill_spin_rate = 12F * spinProgress;  // Simplified 6F * progress * 2f
                } else {
                    state.spinDown = true;
                }
            }
        } else {
            if(state.drill_spin_rate != 0)
            {
                state.spinWait += 1;
                state.drill_spin_rate = 24f * (1 - ((float)state.spinWait / state.spinWaitReset));
                state.drill_direction = true;

                if (state.drill_height < -DRILL_HEIGHT_INCREMENT) {
                    state.drill_height = adjustHeight(state.drill_height, MAX_DRILL_HEIGHT, 0, DRILL_HEIGHT_INCREMENT, state);
                }
            }
        }
        context.requestMasterBESync();
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

    public static class State implements IGMultiblockState, ProcessContextInWorld<CoreDrillRecipe> {
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
            this.energyCap = new StoredCapability<>(IGReceiveOnlyEnergy.of(this.energy));
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
            nbt.put("processor", this.processor.toNBT());
        }


        @Override
        public void readSaveNBT(CompoundTag nbt){
            energy.deserializeNBT(nbt.get("energy"));
            acid_tank.readFromNBT(nbt.getCompound("acid_tank"));
            output_tank.readFromNBT(nbt.getCompound("output_tank"));
            processor.fromNBT(nbt.get("processor"), MultiblockProcessInWorld::new);
        }

        @Override
        public void writeSyncNBT(CompoundTag nbt)
        {
            writeSaveNBT(nbt);
            // Animation Data
            nbt.putBoolean("renderActive", renderAsActive);
            nbt.putBoolean("drillDirection", drill_direction);
            nbt.putBoolean("spinDown", spinDown);
//            nbt.putFloat("clockwiseAngle", gear_clockwise_angle);
//            nbt.putFloat("counterAngle", gear_counter_clockwise_angle);
             nbt.putFloat("drillHeight", drill_height);
//            nbt.putFloat("drillAngle", drill_angle);
            nbt.putFloat("drillSpinRate", drill_spin_rate);
//            nbt.putFloat("drillShake", drill_shake);
            nbt.putInt("spinWaitReset", spinWaitReset);
            nbt.putInt("spinWait", spinWait);
        }

        @Override
        public void readSyncNBT(CompoundTag nbt)
        {
            readSaveNBT(nbt);
            renderAsActive = nbt.getBoolean("renderActive");
            drill_direction = nbt.getBoolean("drillDirection");
            spinDown = nbt.getBoolean("spinDown");
//            gear_clockwise_angle = nbt.getFloat("clockwiseAngle");
//            gear_counter_clockwise_angle = nbt.getFloat("counterAngle");
            drill_height = nbt.getFloat("drillHeight");
//            drill_angle = nbt.getFloat("drillAngle");
            drill_spin_rate = nbt.getFloat("drillSpinRate");
//            drill_shake = nbt.getFloat("drillShake");
            spinWaitReset = nbt.getInt("spinWaitReset");
            spinWait = nbt.getInt("spinWait");
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

        public boolean getDrillDirection()
        {
            return drill_direction;
        }

        @Override
        public void invalidate(@NotNull IMultiblockContext<?> context)
        {
            this.energyCap.get(context).invalidate();
            this.fOutputCap.get(context).invalidate();
            this.fInputCap.get(context).invalidate();
        }
    }

}