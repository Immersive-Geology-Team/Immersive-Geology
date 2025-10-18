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
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.CapabilityPosition;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.StoredCapability;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.interfaces.MBOverlayText;
import blusunrize.immersiveengineering.common.util.EnergyHelper;
import com.google.common.collect.ImmutableList;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGMultiblockState;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.ISkinnableMultiblockLogic;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.AlternatorShape;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AlternatorLogic implements ISkinnableMultiblockLogic<AlternatorLogic.State>, MBOverlayText<AlternatorLogic.State>, IServerTickableComponent<AlternatorLogic.State>, IClientTickableComponent<AlternatorLogic.State> {
    public static final BlockPos REDSTONE_IN = new BlockPos(6,1,0);
    private static final List<BlockPos> ENERGY_OUTPUTS = IntStream.range(2, 5).mapToObj((i) -> {
        return new BlockPos(5, i, 0);
    }).toList();

    public static final BlockPos ROTATION_IN = new BlockPos(3, 3, 4);

    private static final int MAX_ENERGY_OUTPUT = 32768;
    private static final float MAX_TURBINE_SPEED = 0.21f; //20% turn per tick, or 4 turns a second.

    @Override
    public void tickClient(IMultiblockContext<State> ctx) {
        State state = ctx.getState();
        state.render_rotation += state.rotation_speed;
        state.render_rotation %= 1.0f;
        if(state.render_rotation < 0) state.render_rotation += 1.0f;

        if(state.request_sync)
        {
            state.request_sync = false;
            ctx.requestMasterBESync();
        }
    }

    @Override
    public void tickServer(IMultiblockContext<State> ctx) {
        State state = ctx.getState();
        state.rotation_speed = Mth.lerp(0.1f, state.rotation_speed, state.target_rotation);
        //if(state.rotation_speed < 0.0005f) state.rotation_speed = Math.round(state.rotation_speed);
        if(state.rotation_speed > 0) provideFlux(state);
        if(state.target_rotation > 0)
        {
            state.target_rotation = Mth.lerp(0.005f, state.target_rotation, 0);
            if(state.target_rotation < 0.001f) state.target_rotation = 0;
        }

        if(state.request_sync)
        {
            state.request_sync = false;
            ctx.requestMasterBESync();
        }
        ctx.markDirtyAndSync();
    }

    public boolean provideFlux(AlternatorLogic.State state)
    {
        List<IEnergyStorage> presentOutputs = state.energyOutputs.stream().map(CapabilityReference::getNullable).filter(Objects::nonNull).collect(Collectors.toList());
        if(!presentOutputs.isEmpty())
        {
            int output = Math.round(MAX_ENERGY_OUTPUT*(state.rotation_speed/(MAX_TURBINE_SPEED - 0.05f)));
            if(EnergyHelper.distributeFlux(presentOutputs, output, true) < output)
            {
                return EnergyHelper.distributeFlux(presentOutputs, output, false) < output;
            }
        }
        return false;
    }

    @Override
    public State createInitialState(IInitialMultiblockContext<State> capability) {
        return new AlternatorLogic.State(capability);
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return AlternatorShape.GETTER;
    }

    @Override
    public <T> LazyOptional<T> getCapability(IMultiblockContext<State> ctx, CapabilityPosition position, Capability<T> cap)
    {
        if (cap != ForgeCapabilities.FLUID_HANDLER && cap != IRotationAcceptor.CAPABILITY) {
            if(cap != ForgeCapabilities.ENERGY) return LazyOptional.empty();
            if(position.side()!=null && (position.side()!=RelativeBlockFace.RIGHT || !ENERGY_OUTPUTS.contains(position.posInMultiblock())))
            {
                return LazyOptional.empty();
            }
            return ctx.getState().energyView.cast(ctx);
        }
        if(cap == IRotationAcceptor.CAPABILITY)
        {
            if(ROTATION_IN.equals(position.posInMultiblock()))
            {
                return ctx.getState().rotationCap.cast(ctx);
            }
        }
        return LazyOptional.empty();
    }

    @Nullable
    @Override
    public List<Component> getOverlayText(State state, Player player, boolean b)
    {
        if(state == null) return List.of();
        int rpm = Math.round(state.rotation_speed * 1200.0f);
        return List.of(Component.translatable("immersivegeology.alternator.rpm").append(String.valueOf(rpm)));
    }

    public static class State implements IGMultiblockState
    {
        private final StoredCapability<IEnergyStorage> energyView;
        private final StoredCapability<IRotationAcceptor> rotationCap;
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();
        private final List<CapabilityReference<IEnergyStorage>> energyOutputs;
        public float render_rotation = 0f;
        public float target_rotation = 0f;
        public float rotation_speed = 0f;
        private boolean request_sync = false;

        public State(IInitialMultiblockContext<State> ctx){
            ImmutableList.Builder<CapabilityReference<IEnergyStorage>> outputs = ImmutableList.builder();
            for(BlockPos pos : AlternatorLogic.ENERGY_OUTPUTS)
            {
                outputs.add(ctx.getCapabilityAt(ForgeCapabilities.ENERGY, pos, RelativeBlockFace.RIGHT));
            }

            this.energyOutputs = outputs.build();
            this.rotationCap = new StoredCapability<>(new RotationAcceptor());
            this.energyView = new StoredCapability<>(NullEnergyStorage.INSTANCE);
        }

        @Override
        public void invalidate(@NotNull IMultiblockContext<?> context)
        {
            this.energyView.get(context).invalidate();
            this.rotationCap.get(context).invalidate();
        }

        private class RotationAcceptor implements IRotationAcceptor
        {
            private RotationAcceptor() {}
            @Override
            public void inputRotation(double inputValue) {
                double normalizedRPT = convertToRPT(inputValue);
                setTargetRotation(Math.min(normalizedRPT, MAX_TURBINE_SPEED));
            }

            private double convertToRPT(double raw) {
                if (raw >= 9) {
                    // Windmill: rotation * 800
                    return raw / 0.75 / 2880;
                } else if (raw >= 2) {
                    // Watermill: power * 0.75 rough estimate (power max ~1.5 - 2.0)
                    return  raw / 1600; // from watermill logic: 1/1440 * power
                }
                // Fallback: assume it's already close to RPT (from other sources)
                return raw;
            }
        }

        @Override
        public void readSaveNBT(CompoundTag nbt){
            readSyncNBT(nbt);
            render_rotation = nbt.getFloat("rotation");
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt){
            writeSyncNBT(nbt);
            nbt.putFloat("rotation", render_rotation);
        }

        @Override
        public void writeSyncNBT(CompoundTag nbt)
        {
            nbt.putFloat("target_rotation", target_rotation);
            nbt.putFloat("rotation_speed", rotation_speed);
            nbt.putBoolean("request_sync", request_sync);
        }

        @Override
        public void readSyncNBT(CompoundTag nbt)
        {
            target_rotation = nbt.getFloat("target_rotation");
            rotation_speed = nbt.getFloat("rotation_speed");
            request_sync = nbt.getBoolean("request_sync");
        }

        public float getRenderRotation()
        {
            return render_rotation;
        }

        public void setTargetRotation(double rotation)
        {
            this.target_rotation = (float)rotation;
            request_sync = true;
        }
    }

}