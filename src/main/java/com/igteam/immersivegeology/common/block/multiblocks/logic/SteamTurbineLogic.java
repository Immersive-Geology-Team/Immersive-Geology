/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.logic;

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
    public static final BlockPos REDSTONE_IN = new BlockPos(0,1,5);
    private static final List<BlockPos> ENERGY_OUTPUTS = IntStream.range(2, 5).mapToObj((i) -> {
        return new BlockPos(13, 3, i);
    }).toList();

    public static final int STEAM_CAPACITY = 500;
    public static final int WATER_CAPACITY = 250;
    private static final CapabilityPosition FLUID_INPUT_A;
    private static final CapabilityPosition FLUID_INPUT_B;
    private static final CapabilityPosition FLUID_OUTPUT;

    @Override
    public void tickClient(IMultiblockContext<State> context) {

    }

    @Override
    public void tickServer(IMultiblockContext<State> context) {
        SteamTurbineLogic.State state = (SteamTurbineLogic.State)context.getState();
        boolean active = ((SteamTurbineLogic.State)context.getState()).active;
        if(active)
        {
            final int tank_amount = state.steam_tank.getFluidAmount();
            if(tank_amount!=state.steam_tank.getFluidAmount()) context.requestMasterBESync();
        }

        if (state.rsState.isEnabled(context) && !state.steam_tank.getFluid().isEmpty() && state.water_tank.getSpace() > 0) {
            int output = 12288;
            List<IEnergyStorage> presentOutputs = state.energyOutputs.stream().map(CapabilityReference::getNullable).filter(Objects::nonNull).collect(Collectors.toList());
            TurbineFuel recipe = (TurbineFuel)state.recipeGetter.apply(context.getLevel().getRawLevel(), state.steam_tank.getFluid().getFluid());
            if (recipe != null && !presentOutputs.isEmpty() && EnergyHelper.distributeFlux(presentOutputs, output, false) < output) {
                int fluidConsumed = recipe.getBurnTime();
                float outputRatio = recipe.getOutputRatio();
                if (state.steam_tank.getFluidAmount() >= fluidConsumed) {
                    if (!active) {
                        active = true;
                    }

                    state.steam_tank.drain(fluidConsumed, FluidAction.EXECUTE);

                    int waterOutput = Math.round(fluidConsumed * outputRatio);
                    state.water_tank.fill(new FluidStack(Fluids.WATER,waterOutput), FluidAction.EXECUTE);
                } else if (active) {
                    state.steam_tank.drain(state.steam_tank.getFluidAmount(), FluidAction.EXECUTE);
                    active = false;
                }

            }
        } else if (active) {
            active = false;
        }

        if (active != state.active) {
            state.active = active;
        }

        if(state.water_tank.getFluid().getAmount() > 0)
        {
            drainOutputTank(state, context, state.fluidOutput);
            context.requestMasterBESync();
        }
    }

    private void drainOutputTank(SteamTurbineLogic.State state, IMultiblockContext<SteamTurbineLogic.State> context, CapabilityReference<IFluidHandler> output_reference)
    {
        int outSize = Math.min(FluidType.BUCKET_VOLUME, state.water_tank.getFluidAmount());
        FluidStack out = Utils.copyFluidStackWithAmount(state.water_tank.getFluid(), outSize, false);
        IFluidHandler output = output_reference.getNullable();

        if(output==null)
            return;

        int accepted = output.fill(out, FluidAction.SIMULATE);
        if(accepted > 0)
        {
            int drained = output.fill(Utils.copyFluidStackWithAmount(out, Math.min(out.getAmount(), accepted), false), FluidAction.EXECUTE);
            state.water_tank.drain(drained, FluidAction.EXECUTE);
            context.markMasterDirty();
            context.requestMasterBESync();
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
        if (cap != ForgeCapabilities.FLUID_HANDLER || !FLUID_INPUT_A.equalsOrNullFace(position) && !FLUID_INPUT_B.equalsOrNullFace(position) && !FLUID_OUTPUT.equalsOrNullFace(position)) {
            return cap != ForgeCapabilities.ENERGY || position.side() != null && (position.side() != RelativeBlockFace.UP || !ENERGY_OUTPUTS.contains(position.posInMultiblock())) ? LazyOptional.empty() : ((SteamTurbineLogic.State)ctx.getState()).energyView.cast(ctx);
        } else {
            if(position.equals(FLUID_OUTPUT)) return ((State)ctx.getState()).waterFluidCap.cast(ctx);
            return ((SteamTurbineLogic.State)ctx.getState()).steamFluidCap.cast(ctx);
        }
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return SteamTurbineShape.GETTER;
    }

    static {
        FLUID_INPUT_A = new CapabilityPosition(0, 3, 2, RelativeBlockFace.RIGHT);
        FLUID_INPUT_B = new CapabilityPosition(0, 3, 4, RelativeBlockFace.RIGHT);
        FLUID_OUTPUT = new CapabilityPosition(11, 7, 3, RelativeBlockFace.LEFT);
    }

    public static class State implements IMultiblockState {
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();
        public final FluidTank steam_tank = new FluidTank(STEAM_CAPACITY);
        public final FluidTank water_tank = new FluidTank(WATER_CAPACITY);
        private boolean active = false;
        private int consumeTick = 0;
        private final BiFunction<Level, Fluid, TurbineFuel> recipeGetter = CachedRecipe.cached(TurbineFuel::getRecipeFor);
        private final List<CapabilityReference<IEnergyStorage>> energyOutputs;
        private final StoredCapability<IFluidHandler> steamFluidCap;
        private final StoredCapability<IFluidHandler> waterFluidCap;
        private final CapabilityReference<IFluidHandler> fluidOutput;

        private final StoredCapability<IEnergyStorage> energyView;

        public State(IInitialMultiblockContext<State> ctx){
            Runnable changedAndSync = () -> {
                ctx.getSyncRunnable().run();
                ctx.getMarkDirtyRunnable().run();
            };

            this.steamFluidCap = new StoredCapability<>(new ArrayFluidHandler(steam_tank, true, true, changedAndSync));
            this.waterFluidCap = new StoredCapability<>(new ArrayFluidHandler(water_tank, true, false, changedAndSync));
            ImmutableList.Builder<CapabilityReference<IEnergyStorage>> outputs = ImmutableList.builder();

			for(BlockPos pos : SteamTurbineLogic.ENERGY_OUTPUTS)
			{
				outputs.add(ctx.getCapabilityAt(ForgeCapabilities.ENERGY, pos, RelativeBlockFace.RIGHT));
			}

            this.energyOutputs = outputs.build();
            this.energyView = new StoredCapability<>(NullEnergyStorage.INSTANCE);

            this.fluidOutput = ctx.getCapabilityAt(ForgeCapabilities.FLUID_HANDLER, new MultiblockFace(FLUID_OUTPUT.side().getOpposite(), FLUID_OUTPUT.posInMultiblock().east()));
        }

        @Override
        public void readSaveNBT(CompoundTag nbt){
            this.steam_tank.readFromNBT(nbt.getCompound("steam_tank"));
            this.water_tank.readFromNBT(nbt.getCompound("water_tank"));
            this.active = nbt.getBoolean("active");
            this.consumeTick = nbt.getInt("consumeTick");
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt){
            nbt.put("steam_tank", this.steam_tank.writeToNBT(new CompoundTag()));
            nbt.put("water_tank", this.water_tank.writeToNBT(new CompoundTag()));
            nbt.putBoolean("active", this.active);
            nbt.putInt("consumeTick", this.consumeTick);
        }

        public void writeSyncNBT(CompoundTag nbt) {
            writeSaveNBT(nbt);
            nbt.putBoolean("active", this.active);
        }

        public void readSyncNBT(CompoundTag nbt) {
            readSaveNBT(nbt);
            this.active = nbt.getBoolean("active");
        }
    }

}