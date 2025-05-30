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
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.*;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext.ProcessContextInMachine;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import com.igteam.immersivegeology.common.block.multiblocks.IGGeothermalExchangerMultiblock;
import com.igteam.immersivegeology.common.block.multiblocks.part.GeothermalPart;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GeothermalExchangerRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.GeothermalExchangerShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GeothermalExchangerLogic implements IMultiblockLogic<GeothermalExchangerLogic.State>, IServerTickableComponent<GeothermalExchangerLogic.State>, IClientTickableComponent<GeothermalExchangerLogic.State> {
    public static final BlockPos REDSTONE_IN = new BlockPos(2,5,1);
    public static final int ENERGY_CAPACITY = 16000;
    private static final CapabilityPosition FLUID_INPUT_CAP = new CapabilityPosition(3,4,1, RelativeBlockFace.UP);
    private static final CapabilityPosition FLUID_OUTPUT_CAP = new CapabilityPosition(1,4,1, RelativeBlockFace.UP);
    private static final CapabilityPosition ENERGY_INPUT = new CapabilityPosition(3,5,0, RelativeBlockFace.UP);

    @Override
    public void tickClient(IMultiblockContext<State> context) {

    }

    @Override
    public InteractionResult click(IMultiblockContext<State> ctx, BlockPos posInMultiblock, Player player, InteractionHand hand, BlockHitResult absoluteHit, boolean isClient)
    {
        return IMultiblockLogic.super.click(ctx, posInMultiblock, player, hand, absoluteHit, isClient);
    }

    int tickCounter = 0;
    int maxAttemptsPerTick = 3;
    Random random = new Random();
    int slowestCoolrate = 6546;
    int currentCoolrate = slowestCoolrate;

    @Override
    public void tickServer(IMultiblockContext<State> context) {
        tickCounter++;
        final State state = context.getState();
        IMultiblockLevel multiblockLevel = context.getLevel();
        Level rawLevel = multiblockLevel.getRawLevel();
        Vec3i size = IGGeothermalExchangerMultiblock.INSTANCE.getSize(rawLevel);
        boolean wasActive = state.isActive;
        boolean isActive = state.rsState.isEnabled(context);
        state.isActive = isActive;
        updateMultiblockHeatStates(state, size, multiblockLevel);

        float cooling_rate = (float) tickCounter / currentCoolrate;
        state.cooling_rate = (int)(cooling_rate * 160f);

        if(coolingPlanes.isEmpty())
        {
            setupCoolingMap(multiblockLevel);
            return;
        }
        if(wasActive != isActive)
        {
            setupCoolingMap(multiblockLevel);
        }
        if (isActive)
        {
            if(getCoolingScore(rawLevel, multiblockLevel) > 0 && tickCounter % 10 == 0 && state.heat < 160)
            {
                state.heat+=1;
            }
            if(tickCounter >= currentCoolrate)
            {
                coolFromCurrentPlane(multiblockLevel);
                setupCoolingMap(multiblockLevel);
                float t =  ((float)(12+getCoolingScore(rawLevel, multiblockLevel))/ 148);
                currentCoolrate = (int)(t * slowestCoolrate);
                tickCounter = 0;
                context.markMasterDirty();
                context.requestMasterBESync();
            }
        }
    }

    private void coolFromCurrentPlane(IMultiblockLevel multiblockLevel) {
        Level level = multiblockLevel.getRawLevel();

        if (!coolingPlanes.containsKey(currentY)) return;

        List<BlockPos> currentPlane = getAbsolutePlane(multiblockLevel, currentY);
        for (BlockPos pos : currentPlane) {
            if (level.getBlockState(pos).is(Blocks.LAVA)) {
                coolState(pos, level);
                return;
            }
        }

        // Step 2: If mostly Magma, try cooling above and below in parallel
        if (isMostlyMagma(currentPlane, level, 0.5)) {
            boolean cooled = false;

            // Try plane above: Magma → Obsidian
            int aboveY = currentY + 1;
            List<BlockPos> abovePlane = getAbsolutePlane(multiblockLevel, aboveY);
            if (!abovePlane.isEmpty()) {
                for (BlockPos pos : abovePlane) {
                    if (level.getBlockState(pos).is(Blocks.MAGMA_BLOCK) && random.nextInt(6) == 0) {
                        coolState(pos, level);
                        cooled = true;
                        break;
                    }
                }
            }

            // Try plane below: Lava → Magma (more likely than above)
            int belowY = currentY - 1;
            List<BlockPos> belowPlane = getAbsolutePlane(multiblockLevel, belowY);
            if (!belowPlane.isEmpty()) {
                for (BlockPos pos : belowPlane) {
                    if (level.getBlockState(pos).is(Blocks.LAVA) && random.nextInt(3) == 0) {
                        coolState(pos, level);
                        cooled = true;
                        break;
                    }
                }
            }

            if(!cooled)
            {
                for(BlockPos pos : currentPlane)
                {
                    if(level.getBlockState(pos).is(Blocks.MAGMA_BLOCK))
                    {
                        coolState(pos, level);
                        return;
                    }
                }
            }

            // Optional: Advance if current is nearly fully Magma
            if (!cooled && isMostlyMagma(currentPlane, level, 0.95)) {
                advanceToNextPlane();
            }
        }
    }

    private int getCoolingScore(Level level, IMultiblockLevel multiblockLevel) {
        int score = 0;

        for (Map.Entry<Integer, List<BlockPos>> entry : coolingPlanes.entrySet()) {
            List<BlockPos> plane = entry.getValue();
            for (BlockPos relPos : plane) {
                BlockPos absPos = multiblockLevel.toAbsolute(relPos);
                BlockState state = level.getBlockState(absPos);

                if (state.is(Blocks.LAVA)) {
                    score += 2;
                } else if (state.is(Blocks.MAGMA_BLOCK)) {
                    score += 1;
                }
            }
        }

        return score;
    }

    private ArrayList<BlockPos> getAbsolutePlane(IMultiblockLevel multiblockLevel, int y) {
        ArrayList<BlockPos> plane = coolingPlanes.getOrDefault(y, List.of()).stream()
                .map(multiblockLevel::toAbsolute)
                .collect(Collectors.toCollection(ArrayList::new));
        if(!plane.isEmpty()) Collections.shuffle(plane, random);
        return plane;
    }

    private void advanceToNextPlane() {
        NavigableMap<Integer, List<BlockPos>> tailMap = ((TreeMap<Integer, List<BlockPos>>) coolingPlanes).tailMap(currentY, false);
        if (!tailMap.isEmpty()) {
            currentY = tailMap.firstKey();
        }
    }

    private boolean isMostlyMagma(List<BlockPos> plane, Level level, double requiredRatio) {
        int magmaCount = 0;
        int total = plane.size();

        if (total == 0) return false;
        for (BlockPos pos : plane) {
            BlockState state = level.getBlockState(pos);
            if (state.is(Blocks.MAGMA_BLOCK)) {
                magmaCount++;
            } else if (state.isAir() || !(state.is(Blocks.LAVA) || state.is(Blocks.MAGMA_BLOCK))) {
                magmaCount++;
            }
        }

        return (magmaCount / (double) total) >= requiredRatio;
    }

    private void coolState(BlockPos pos, Level level) {
        BlockState state = level.getBlockState(pos);

        if (state.is(Blocks.LAVA)) {
            level.setBlock(pos, Blocks.MAGMA_BLOCK.defaultBlockState(), 3);
        } else if (state.is(Blocks.MAGMA_BLOCK)) {
            level.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 3);
        }
    }

    private void updateMultiblockHeatStates(State state, Vec3i size, IMultiblockLevel multiblockLevel)
    {
        int structureHeight = size.getY()-1;
        int structureLength = size.getX();
        int structureWidth = size.getZ();
        MutableBlockPos cursor = new MutableBlockPos();
        int index = 0;
        for(int h = -1; h < structureHeight; ++h)
        {
            for(int l = 0; l < structureLength; ++l)
            {
                for(int w = 0; w < structureWidth; ++w)
                {
                    cursor.set(l, h, w);
                    BlockState relativeState = multiblockLevel.getBlockState(cursor);
                    if(index < 66 && !(relativeState.getBlock() instanceof GeothermalPart))
                    {
                        int heatLevel = 0;
                        if(relativeState.is(Blocks.LAVA) && relativeState.getFluidState().isSource()) heatLevel = 3;
                        if(relativeState.is(Blocks.MAGMA_BLOCK)) heatLevel = 2;
                        if(relativeState.is(Blocks.OBSIDIAN)) heatLevel = 1;

                        state.setHeatStateAtIndex(index, heatLevel);
                        index++;
                    }
                }
            }
        }
    }

    Map<Integer, List<BlockPos>> coolingPlanes = new TreeMap<>(Comparator.reverseOrder());
    int currentY = -256;
    private void setupCoolingMap(IMultiblockLevel multiblockLevel)
    {
        coolingPlanes.clear();
        Vec3i size = IGGeothermalExchangerMultiblock.INSTANCE.getSize(null);
        int structureHeight = size.getY()-2;
        int structureLength = size.getX();
        int structureWidth = size.getZ();
        MutableBlockPos cursor = new MutableBlockPos();
        int index = 0;
        for(int h = -1; h < structureHeight; ++h)
        {
            for(int l = 0; l < structureLength; ++l)
            {
                for(int w = 0; w < structureWidth; ++w)
                {
                    cursor.set(l, h, w);
                    BlockState relativeState = multiblockLevel.getBlockState(cursor);
                    if(!(relativeState.getBlock() instanceof GeothermalPart))
                    {
                        relativeState = multiblockLevel.getRawLevel().getBlockState(multiblockLevel.toAbsolute(cursor));
                        if(relativeState.is(Blocks.LAVA) || relativeState.is(Blocks.MAGMA_BLOCK))
                        {
                            int y = cursor.getY();
                            coolingPlanes.computeIfAbsent(y, k -> new ArrayList<>()).add(cursor.immutable());
                        }
                        index++;
                    }
                }
            }
        }
        if (!coolingPlanes.isEmpty()) {
            currentY = coolingPlanes.keySet().iterator().next(); // Highest Y
        }
    }

    @Override
    public State createInitialState(IInitialMultiblockContext<State> capability) {
        return new GeothermalExchangerLogic.State(capability);
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return GeothermalExchangerShape.GETTER;
    }

    @Override
    public <T> LazyOptional<T> getCapability(IMultiblockContext<State> ctx, CapabilityPosition position, Capability<T> cap)
    {
        final GeothermalExchangerLogic.State state = ctx.getState();
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
            if(FLUID_OUTPUT_CAP.equals(position))
            {
                return state.fOutputCap.cast(ctx);
            }
        }
        return LazyOptional.empty();
    }

    public static class State implements IMultiblockState, ProcessContextInMachine<GeothermalExchangerRecipe>
    {
        private final FluidTank water_tank = new FluidTank(8000);
        private final FluidTank steam_tank = new FluidTank(8000);
        private final AveragingEnergyStorage energy_storage = new AveragingEnergyStorage(ENERGY_CAPACITY);
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();
        private byte[] heating_states = new byte[17];
        private boolean isActive;
        private int cooling_rate;
        private int heat;

        private final StoredCapability<IFluidHandler> fInputCap;
        private final StoredCapability<IFluidHandler> fOutputCap;
        private final CapabilityReference<IFluidHandler> fluidOutput;
        private final StoredCapability<IEnergyStorage> energyCap;

        public State(IInitialMultiblockContext<State> context){
            this.energyCap = new StoredCapability<>(this.energy_storage);
            this.water_tank.setValidator(f -> f.getRawFluid().equals(Fluids.WATER));
            this.isActive = false;
            this.heat = 0;
            this.cooling_rate = 0;
            Runnable changedAndSync = () -> {
                context.getSyncRunnable().run();
                context.getMarkDirtyRunnable().run();
            };

            this.fInputCap = new StoredCapability<>(new ArrayFluidHandler(water_tank, true, true, changedAndSync));
            this.fOutputCap = new StoredCapability<>(new ArrayFluidHandler(steam_tank, true, false, changedAndSync));

			assert FLUID_OUTPUT_CAP.side()!=null;
			this.fluidOutput = context.getCapabilityAt(ForgeCapabilities.FLUID_HANDLER, new MultiblockFace(FLUID_OUTPUT_CAP.side().getOpposite(), FLUID_OUTPUT_CAP.posInMultiblock().above()));
        }

        @Override
        public void readSyncNBT(CompoundTag nbt)
        {
            IMultiblockState.super.readSyncNBT(nbt);
            heat = nbt.getInt("heat");
            cooling_rate = nbt.getInt("cooling");
            heating_states = nbt.getByteArray("heating_states");
            isActive = nbt.getBoolean("is_active");
            nbt.put("water_tank", water_tank.writeToNBT(new CompoundTag()));
            nbt.put("steam_tank", steam_tank.writeToNBT(new CompoundTag()));
        }

        @Override
        public void writeSyncNBT(CompoundTag nbt)
        {
            IMultiblockState.super.writeSyncNBT(nbt);
            nbt.putInt("heat", heat);
            nbt.putInt("cooling", cooling_rate);
            nbt.putByteArray("heating_states", heating_states);
            nbt.putBoolean("is_active", isActive);
            water_tank.writeToNBT(nbt.getCompound("water_tank"));
            steam_tank.writeToNBT(nbt.getCompound("steam_tank"));
        }

        @Override
        public IFluidTank[] getInternalTanks()
        {
            return new IFluidTank[]{water_tank, steam_tank};
        }

        @Override
        public int[] getOutputTanks()
        {
            return new int[]{1};
        }

        @Override
        public void readSaveNBT(CompoundTag nbt){
            readSyncNBT(nbt);
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt){
            writeSyncNBT(nbt);
        }

		public int getCurrentHeat()
		{
            return heat;
		}

        public int getCoolingRate()
        {
            return cooling_rate;
        }

        @Override
        public AveragingEnergyStorage getEnergy()
        {
            return energy_storage;
        }

        public void setHeatStateAtIndex(int index, int state) {
            int bitIndex = index * 2;
            int byteIndex = bitIndex / 8;
            int offset = bitIndex % 8;

            int cleared = this.heating_states[byteIndex] & ~(0b11 << offset);
            int set = (state & 0b11) << offset;
            this.heating_states[byteIndex] = (byte)(cleared | set);
        }

        public int getHeatStateAtIndex(int index) {
            int bitIndex = index * 2;
            int byteIndex = bitIndex / 8;
            int offset = bitIndex % 8;

            return (heating_states[byteIndex] >> offset) & 0b11;
        }

        public byte[] getHeatingStates()
        {
            return heating_states;
        }
    }

}