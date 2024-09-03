package com.igteam.immersivegeology.common.block.multiblocks.logic;

import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.*;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.interfaces.MBOverlayText;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext.ProcessContextInWorld;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import com.igteam.immersivegeology.common.block.multiblocks.logic.CrystallizerLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CoreDrillRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.CrystallizerShape;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class CrystallizerLogic implements IMultiblockLogic<CrystallizerLogic.State>, IServerTickableComponent<CrystallizerLogic.State>, MBOverlayText<State>, IClientTickableComponent<CrystallizerLogic.State> {
    public static final BlockPos REDSTONE_IN = new BlockPos(0, 1, 1);

    private static final int ENERGY_CAPACITY = 16000;
    private static final CapabilityPosition ENERGY_INPUT = new CapabilityPosition(1,2,1, RelativeBlockFace.UP);

    private static final CapabilityPosition FLUID_INPUT_CAP = new CapabilityPosition(1,1,2, RelativeBlockFace.BACK);

    public static final int TANK_VOLUME = 4 *FluidType.BUCKET_VOLUME;
    public static final int ENERGY_CONSUMPTION_RATE =  2048;

    @Override
    public void tickClient(IMultiblockContext<State> iMultiblockContext) {

    }

    @Override
    public void tickServer(IMultiblockContext<State> iMultiblockContext) {

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
        return List.of();
    }

    public static class State implements IMultiblockState, ProcessContextInWorld<CoreDrillRecipe>
    {
        public final AveragingEnergyStorage energy = new AveragingEnergyStorage(ENERGY_CAPACITY);
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();

        public final FluidTank tank = new FluidTank(TANK_VOLUME);
        private final StoredCapability<IFluidHandler> fInputCap;
        private final StoredCapability<IEnergyStorage> energyCap;

        public State(IInitialMultiblockContext<State> ctx)
        {
            this.energyCap = new StoredCapability<>(this.energy);
            Runnable changedAndSync = () -> {
                ctx.getSyncRunnable().run();
                ctx.getMarkDirtyRunnable().run();
            };
            this.fInputCap = new StoredCapability<>(new ArrayFluidHandler(tank, true, true, changedAndSync));
        }

        @Override
        public void readSaveNBT(CompoundTag nbt){
            nbt.put("energy", energy.serializeNBT());
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt){
            energy.deserializeNBT(nbt.get("energy"));
        }

        @Override
        public AveragingEnergyStorage getEnergy()
        {
            return energy;
        }
    }

}