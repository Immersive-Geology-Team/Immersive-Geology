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
import blusunrize.immersiveengineering.common.util.EnergyHelper;
import com.google.common.collect.ImmutableList;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.ISkinnableMultiblockLogic;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.AlternatorShape;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AlternatorLogic implements ISkinnableMultiblockLogic<AlternatorLogic.State>, IServerTickableComponent<AlternatorLogic.State>, IClientTickableComponent<AlternatorLogic.State> {
    public static final BlockPos REDSTONE_IN = new BlockPos(6,1,0);
    private static final List<BlockPos> ENERGY_OUTPUTS = IntStream.range(2, 5).mapToObj((i) -> {
        return new BlockPos(5, i, 0);
    }).toList();

    public static final BlockPos ROTATION_IN = new BlockPos(3, 3, 4);

    private static final int MAX_ENERGY_OUTPUT = 24576;
    private static final float MAX_TURBINE_SPEED = 36f;

    @Override
    public void tickClient(IMultiblockContext<State> ctx) {
        State state = ctx.getState();
        state.render_rotation += state.rotation_speed;
        state.render_rotation %= 360;
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
        boolean generating_energy = provideFlux(state);
        if(state.target_rotation > 0)
        {
            state.target_rotation = Mth.lerp(0.05f, state.target_rotation, 0);
            if(state.target_rotation < 0.1f) state.target_rotation = 0;
        }

        if(state.request_sync)
        {
            state.request_sync = false;
            ctx.requestMasterBESync();
        }
        ctx.markDirtyAndSync();
    }
    /*
			IMultiblockLevel mbLevel = ctx.getLevel();
			Level level = mbLevel.getRawLevel();
			BlockPos steam_engine_check = mbLevel.toAbsolute(new BlockPos(3, 3, 4)).south();
			BlockState engine_state = level.getBlockState(steam_engine_check);
			if(engine_state.getBlock() instanceof SteamTurbinePart)
			{
				BlockEntity entity = level.getBlockEntity(steam_engine_check);
				if(entity instanceof IMultiblockBE<?> mbe)
				{
					BlockPos posInMB = mbe.getHelper().getPositionInMB();
					BlockPos masterPos = mbe.getHelper().getMultiblock().masterPosInMB();
					BlockPos masterAbsPos = entity.getBlockPos().subtract(posInMB).offset(masterPos.getX(), masterPos.getY(), masterPos.getZ());
					BlockEntity masterBE = level.getBlockEntity(masterAbsPos);
					if(masterBE instanceof IMultiblockBE<?> master)
					{
						IMultiblockState turbine = master.getHelper().getState();
						if(turbine instanceof SteamTurbineLogic.State turbine_state)
						{
							state.target_rotation = turbine_state.getRotationSpeed();
						}
					}
				}
			} else
			{
				state.target_rotation = 0f;
			}
	*/
    public boolean provideFlux(AlternatorLogic.State state)
    {
        List<IEnergyStorage> presentOutputs = state.energyOutputs.stream().map(CapabilityReference::getNullable).filter(Objects::nonNull).collect(Collectors.toList());
        if(!presentOutputs.isEmpty())
        {
            int output = Math.round(MAX_ENERGY_OUTPUT*(state.rotation_speed/MAX_TURBINE_SPEED));
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
            return cap != ForgeCapabilities.ENERGY || position.side() != null && (position.side() != RelativeBlockFace.UP || !ENERGY_OUTPUTS.contains(position.posInMultiblock())) ? LazyOptional.empty() : ((AlternatorLogic.State)ctx.getState()).energyView.cast(ctx);
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

    public static class State implements IMultiblockState {
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

        private class RotationAcceptor implements IRotationAcceptor
        {
            private RotationAcceptor() {}
            public void inputRotation(double rotation) {
                setTargetRotation(Math.min(rotation, MAX_TURBINE_SPEED));
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

        public float getRender_rotation()
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