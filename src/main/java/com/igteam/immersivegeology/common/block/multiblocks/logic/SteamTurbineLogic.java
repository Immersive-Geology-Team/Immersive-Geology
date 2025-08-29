/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.logic;

import blusunrize.immersiveengineering.api.energy.IRotationAcceptor;
import blusunrize.immersiveengineering.api.energy.NullEnergyStorage;
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
import blusunrize.immersiveengineering.common.util.CachedRecipe;
import blusunrize.immersiveengineering.common.util.EnergyHelper;
import blusunrize.immersiveengineering.common.util.Utils;
import com.google.common.collect.ImmutableList;
import com.igteam.immersivegeology.common.block.multiblocks.logic.SteamTurbineLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.ISkinnableMultiblockLogic;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.TurbineFuel;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.SteamTurbineShape;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
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
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SteamTurbineLogic implements ISkinnableMultiblockLogic<State>, MBOverlayText<SteamTurbineLogic.State>, IServerTickableComponent<SteamTurbineLogic.State>, IClientTickableComponent<SteamTurbineLogic.State> {
    public static final BlockPos REDSTONE_IN = new BlockPos(0,1,11);

    public static final int STEAM_CAPACITY = 500;
    public static final int WATER_CAPACITY = 250;
    private static final CapabilityPosition FLUID_INPUT;
    private static final CapabilityPosition FLUID_OUTPUT_A;
    private static final CapabilityPosition FLUID_OUTPUT_B;
    private static final CapabilityPosition ROTATION_OUTPUT;


    @Override
    public void tickClient(IMultiblockContext<State> context) {
        SteamTurbineLogic.State state = context.getState();
        state.rotation += state.rotation_speed;
        state.rotation = state.rotation % 360;
    }

    @Override
    public void tickServer(IMultiblockContext<State> context)
    {
        SteamTurbineLogic.State state = (SteamTurbineLogic.State)context.getState();
        boolean active = state.active;
        state.rotation_speed = Mth.lerp(0.01f, state.rotation_speed, state.target_rotation);
        if(active)
        {
            final int tank_amount = state.steam_tank.getFluidAmount();
            if(tank_amount!=state.steam_tank.getFluidAmount()) context.requestMasterBESync();
        }
        if (state.rsState.isEnabled(context) && !state.steam_tank.getFluid().isEmpty() && state.water_tank.getSpace() > 0) {
            TurbineFuel recipe = (TurbineFuel) state.recipeGetter.apply(context.getLevel().getRawLevel(), state.steam_tank.getFluid().getFluid());

            if (recipe != null) {
                int fluidConsumed = recipe.getConsumed();
                int burnTime = recipe.getBurnTime();
                float outputRatio = recipe.getOutputRatio();

                if (state.consumeTick <= 0) {
                    // Only try to consume fluid if we're "ready" for a new burn cycle
                    if (state.steam_tank.getFluidAmount() >= fluidConsumed) {
                        // Consume steam and start new burn cycle
                        state.steam_tank.drain(fluidConsumed, FluidAction.EXECUTE);
                        state.consumeTick = burnTime;

                        int waterOutput = (int) (fluidConsumed * outputRatio);
                        state.water_tank.fill(new FluidStack(Fluids.WATER, waterOutput), FluidAction.EXECUTE);

                        active = true;
                    } else {
                        // Not enough steam to start a new cycle
                        active = false;
                    }
                } else {
                    // Still in a burn cycle — produce water but don't consume more steam
                    state.consumeTick--;
                    active = true;
                }
            } else {
                active = false;
            }

            state.target_rotation = active ? 36f : 0f;
        } else {
            // Not enabled or can't operate
            if (active) {
                active = false;
                state.target_rotation = 0f;
            }
        }

        if(active!=state.active)
        {
            state.active = active;
        }

        if(state.water_tank.getFluid().getAmount() > 0 && context.getLevel().getRawLevel().getGameTime() % 2 == 0)
        {
            int amount = state.water_tank.getFluid().getAmount();
            int half = amount / 2;

            int outputLeftAmount = (amount == 1) ? 1 : (amount % 2 == 0 ? half : half - 1);
            int outputRightAmount = (amount == 1) ? 1 : half;

            drainOutputTank(state, outputLeftAmount, state.fluidOutputs.get(0));
            drainOutputTank(state, outputRightAmount, state.fluidOutputs.get(1));
        }

        if(state.target_rotation == 0 && state.rotation_speed < 0.5f) state.rotation_speed = Math.round(state.rotation);

        if(state.rotation_speed > 0)
        {
            IRotationAcceptor alternator = (IRotationAcceptor)state.outputCap.getNullable();
            if(alternator != null)
            {
                alternator.inputRotation(state.rotation_speed);
            }
        }

        context.requestMasterBESync();
    }

    private void drainOutputTank(SteamTurbineLogic.State state, int amount, CapabilityReference<IFluidHandler> output_reference)
    {
		FluidStack out = Utils.copyFluidStackWithAmount(state.water_tank.getFluid(), amount, false);
        IFluidHandler output = output_reference.getNullable();

        if(output==null)
            return;

        int accepted = output.fill(out, FluidAction.SIMULATE);
        if(accepted > 0)
        {
            int drained = output.fill(Utils.copyFluidStackWithAmount(out, Math.min(out.getAmount(), accepted), false), FluidAction.EXECUTE);
            state.water_tank.drain(drained, FluidAction.EXECUTE);
        }
    }

    @Override
    public State createInitialState(IInitialMultiblockContext<State> capability) {
        return new SteamTurbineLogic.State(capability);
    }

    @Nullable
    @Override
    public List<Component> getOverlayText(State state, Player player, boolean b)
    {
        if(state != null && state.water_tank.getSpace() < 50) return List.of(Component.translatable("immersivegeology.steam_turbine.water_warning").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.RED));
        if(Utils.isFluidRelatedItemStack(player.getItemInHand(InteractionHand.MAIN_HAND)))
            return List.of(TextUtils.formatFluidStack(state.steam_tank.getFluid()),TextUtils.formatFluidStack(state.water_tank.getFluid()));
        return List.of();
    }

    @Override
    public <T> LazyOptional<T> getCapability(IMultiblockContext<State> ctx, CapabilityPosition position, Capability<T> cap)
    {
        if (cap != ForgeCapabilities.FLUID_HANDLER || !FLUID_INPUT.equalsOrNullFace(position) && !FLUID_OUTPUT_A.equalsOrNullFace(position) && !FLUID_OUTPUT_B.equalsOrNullFace(position)) {
            return LazyOptional.empty();
        } else {
            if(position.equals(FLUID_OUTPUT_A) || position.equals(FLUID_OUTPUT_B)) return ((State)ctx.getState()).waterFluidCap.cast(ctx);
            return ((SteamTurbineLogic.State)ctx.getState()).steamFluidCap.cast(ctx);
        }
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return SteamTurbineShape.GETTER;
    }

    static {
        FLUID_INPUT = new CapabilityPosition(1, 1, 11, RelativeBlockFace.BACK);
        FLUID_OUTPUT_A = new CapabilityPosition(2, 0, 3, RelativeBlockFace.LEFT);
        FLUID_OUTPUT_B = new CapabilityPosition(0, 0, 3, RelativeBlockFace.RIGHT);
        ROTATION_OUTPUT = new CapabilityPosition(1, 1, 0, RelativeBlockFace.BACK);
    }

    public static class State implements IMultiblockState {
        private final CapabilityReference<IRotationAcceptor> outputCap;
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();
        public final FluidTank steam_tank = new FluidTank(STEAM_CAPACITY);
        public final FluidTank water_tank = new FluidTank(WATER_CAPACITY);
        private boolean active = false;
        private int consumeTick = 0;
        private float rotation = 0;
        private float target_rotation = 0;
        private float rotation_speed = 0;
        private final BiFunction<Level, Fluid, TurbineFuel> recipeGetter = CachedRecipe.cached(TurbineFuel::getRecipeFor);
        private final StoredCapability<IFluidHandler> steamFluidCap;
        private final StoredCapability<IFluidHandler> waterFluidCap;
        private final List<CapabilityReference<IFluidHandler>> fluidOutputs;


        public State(IInitialMultiblockContext<State> ctx){
            Runnable changedAndSync = () -> {
                ctx.getSyncRunnable().run();
                ctx.getMarkDirtyRunnable().run();
            };

            this.steamFluidCap = new StoredCapability<>(new ArrayFluidHandler(steam_tank, true, true, changedAndSync));
            this.waterFluidCap = new StoredCapability<>(new ArrayFluidHandler(water_tank, true, false, changedAndSync));
            ImmutableList.Builder<CapabilityReference<IFluidHandler>> outputs = ImmutableList.builder();
            outputs.add(ctx.getCapabilityAt(ForgeCapabilities.FLUID_HANDLER, FLUID_OUTPUT_A.posInMultiblock().east(), FLUID_OUTPUT_A.side()));
            outputs.add(ctx.getCapabilityAt(ForgeCapabilities.FLUID_HANDLER, FLUID_OUTPUT_B.posInMultiblock().west(), FLUID_OUTPUT_B.side()));

            this.outputCap = ctx.getCapabilityAt(IRotationAcceptor.CAPABILITY, ROTATION_OUTPUT.posInMultiblock().offset(0,0,-1), ROTATION_OUTPUT.side());

            this.fluidOutputs = outputs.build();


        }

        @Override
        public void readSaveNBT(CompoundTag nbt){
            readSyncNBT(nbt);
            this.active = nbt.getBoolean("active");
            this.consumeTick = nbt.getInt("consumeTick");
            this.rotation = nbt.getFloat("rotation");
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt){
            writeSyncNBT(nbt);
            nbt.putBoolean("active", this.active);
            nbt.putInt("consumeTick", this.consumeTick);
            nbt.putFloat("rotation", this.rotation);
        }

        public void writeSyncNBT(CompoundTag nbt) {
            nbt.put("steam_tank", this.steam_tank.writeToNBT(new CompoundTag()));
            nbt.put("water_tank", this.water_tank.writeToNBT(new CompoundTag()));
            nbt.putBoolean("active", this.active);
            nbt.putFloat("target_rotation", this.target_rotation);
            nbt.putFloat("rotation_speed", this.rotation_speed);
        }

        public void readSyncNBT(CompoundTag nbt) {
            this.steam_tank.readFromNBT(nbt.getCompound("steam_tank"));
            this.water_tank.readFromNBT(nbt.getCompound("water_tank"));
            this.active = nbt.getBoolean("active");
            this.target_rotation = nbt.getFloat("target_rotation");
            this.rotation_speed = nbt.getFloat("rotation_speed");
        }

		public float getRotation()
		{
            return this.rotation;
		}

        public float getTargetRotation()
        {
            return this.target_rotation;
        }

        public float getRotationSpeed()
        {
            return this.rotation_speed;
        }
    }

}