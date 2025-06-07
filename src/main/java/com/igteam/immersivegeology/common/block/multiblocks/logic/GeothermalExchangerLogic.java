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
import blusunrize.immersiveengineering.common.blocks.multiblocks.blockimpl.MultiblockLevel;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInMachine;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor.InMachineProcessor;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext.ProcessContextInMachine;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import blusunrize.immersiveengineering.common.util.CachedRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.IGGeothermalExchangerMultiblock;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.GeothermalHeatHelper;
import com.igteam.immersivegeology.common.block.multiblocks.part.GeothermalPart;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.*;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.GeothermalExchangerShape;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
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

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GeothermalExchangerLogic implements IMultiblockLogic<GeothermalExchangerLogic.State>, IServerTickableComponent<GeothermalExchangerLogic.State>, IClientTickableComponent<GeothermalExchangerLogic.State> {
    public static final BlockPos REDSTONE_IN = new BlockPos(2,5,1);
    public static final int ENERGY_CAPACITY = 16000;
    private static final CapabilityPosition FLUID_INPUT_CAP = new CapabilityPosition(3,4,1, RelativeBlockFace.UP);
    private static final CapabilityPosition FLUID_OUTPUT_CAP = new CapabilityPosition(1,4,1, RelativeBlockFace.UP);
    private static final CapabilityPosition ENERGY_INPUT = new CapabilityPosition(3,5,0, RelativeBlockFace.UP);
    public static final int TANK_VOLUME = 8 * FluidType.BUCKET_VOLUME;

    @Override
    public void tickClient(IMultiblockContext<State> context) {

    }

    @Override
    public InteractionResult click(IMultiblockContext<State> ctx, BlockPos posInMultiblock, Player player, InteractionHand hand, BlockHitResult absoluteHit, boolean isClient)
    {
        return IMultiblockLogic.super.click(ctx, posInMultiblock, player, hand, absoluteHit, isClient);
    }

    public static double convertToDisplayHeat(int input) {
        if (input < 300) {
            return -0.5 + ((double)(input - 1) / 299.0) * 0.5;
        } else if (input == 300) {
            return 0.0;
        } else if (input < 1000) {
            return ((double)(input - 300) / 700.0) * 0.25;
        } else if (input == 1000) {
            return 0.25;
        } else {
            return 0.25 + ((double)(input - 1000) / 2200.0) * 0.25;
        }
    }

    int tickCounter = 0;
    int slowestCoolrate = 6546;
    int currentCoolrate = slowestCoolrate;
    @Override
    public void tickServer(IMultiblockContext<State> context) {
        final State state = context.getState();
        IMultiblockLevel multiblockLevel = context.getLevel();
        Level rawLevel = multiblockLevel.getRawLevel();
        Vec3i size = IGGeothermalExchangerMultiblock.INSTANCE.getSize(rawLevel);
        boolean wasActive = state.isActive;
        boolean isActive = state.rsState.isEnabled(context);
        state.isActive = isActive;

        if(transitionPlanes.isEmpty() || wasActive != isActive) {
            state.display_heat = (float) convertToDisplayHeat(state.heat);
        }

        updateMultiblockHeatStates(state, size, multiblockLevel);
        setupTransitionPlanes(multiblockLevel);
        GeothermalHeatHelper.HeatData heatData = state.heatData;

        state.processor.tickServer(state, context.getLevel(), state.rsState.isEnabled(context));
        boolean newRecipe = tryRunRecipe(state, context.getLevel().getRawLevel());
        if(newRecipe) updateMultiblockHeatStates(state, size, multiblockLevel);
        boolean runningRecipe = false;
        if(state.processor.getQueueSize() > 0 &&
                state.cachedRecipe.get() != null &&
                state.additionalCanProcessCheck(state.processor.getQueue().get(0), multiblockLevel.getRawLevel()))
        {
            heatData = GeothermalHeatHelper.processRecipeHeatEffects(state.cachedRecipe.get(), heatData, transitionPlanes, multiblockLevel);
            runningRecipe = true;
        }

        if (isActive) {
            tickCounter++;
            int refresh = tickCounter % currentCoolrate;
            if(runningRecipe)
            {
                float refreshPercent = (float)tickCounter/ currentCoolrate;
                state.cooling_rate = ((Mth.lerp(0.1f, state.cooling_rate, refreshPercent)));
                state.cooling_rate = Math.min(1, state.cooling_rate);
                state.cooling_rate = Math.max(state.cooling_rate, 0);
            }
            int entropyScore = getEntropyScore(rawLevel, multiblockLevel, heatData.machineHeat);
            boolean isCooling = false;
            if(state.cachedRecipe.get() != null) isCooling = state.cachedRecipe.get().isCooling();
            heatData = GeothermalHeatHelper.updateMachineTemperature(transitionPlanes, multiblockLevel, heatData, isCooling);
            if(refresh == 0) {
                heatData = GeothermalHeatHelper.processBlockConversions(transitionPlanes, multiblockLevel, heatData);
                updateMultiblockHeatStates(state, size, multiblockLevel);

                tickCounter = 0;
                float t = ((float)(12 + entropyScore) / 148);
                currentCoolrate = (int)(t * slowestCoolrate);

                context.markMasterDirty();
                context.requestMasterBESync();
            }
        }

        state.heatData = GeothermalHeatHelper.syncToMachineState(heatData, state);
        state.currentY = currentY;
        state.display_heat = (float) Mth.lerp(0.1f, state.display_heat, convertToDisplayHeat(state.heat));
        context.markDirtyAndSync();
    }

    private double calculateTemperatureScore(double temperature) {
        // Clamp temperature to valid range
        temperature = Math.max(1.0, Math.min(3200.0, temperature));

        if (temperature <= 300.0) {
            // For cold temperatures, use logarithmic scale to heavily favor extreme cold
            // Score approaches 1.0 as temperature approaches 1K
            double coldScore = Math.log(300.0 / temperature) / Math.log(300.0);
            return Math.min(1.0, coldScore);
        } else {
            // For hot temperatures, use linear scale
            // Score approaches 1.0 as temperature approaches 3200K
            double hotScore = (temperature - 300.0) / (3200.0 - 300.0);
            return Math.min(1.0, hotScore);
        }
    }

    private int getEntropyScore(Level level, IMultiblockLevel multiblockLevel, int machineHeatKelvin)
    {
        double totalBlockHeat = 0;
        int validBlocks = 0;

        for (Map.Entry<Integer, List<BlockPos>> entry : transitionPlanes.entrySet()) {
            List<BlockPos> plane = entry.getValue();
            for (BlockPos relPos : plane) {
                BlockPos absPos = multiblockLevel.toAbsolute(relPos);
                BlockState state = level.getBlockState(absPos);
                Block block = state.getBlock();

                GeothermalConversionRecipe recipe = GeothermalConversionRecipe.RECIPES.getRecipes(level).stream()
                        .filter(r -> r.transitionBlock.get().equals(block))
                        .findFirst()
                        .orElse(null);

                if (recipe != null) {
                    totalBlockHeat += recipe.blockHeat;
                    validBlocks++;
                }
            }
        }

        if (validBlocks == 0) {
            return 0;
        }

        double avgBlockTemp = totalBlockHeat / validBlocks;

        double machineTempScore = calculateTemperatureScore(machineHeatKelvin);
        double avgBlockTempScore = calculateTemperatureScore(avgBlockTemp);

        double combinedTempScore = (machineTempScore + avgBlockTempScore) / 2.0;

        double blockCountMultiplier = Math.min(1.0, validBlocks / 66.0);

        int score = (int) Math.round(combinedTempScore * blockCountMultiplier * 148.0);

        return Math.max(0, Math.min(148, score));
    }

    private boolean tryRunRecipe(State state, Level rawLevel)
    {
        if(transitionPlanes.isEmpty()) return false;
        if(state.energy_storage.getEnergyStored() <= 0 || state.processor.getQueueSize() >= state.processor.getMaxQueueSize()) return false;

        final FluidStack input = state.water_tank.getFluid();
        if(input.isEmpty()) return false;
        GeothermalExchangerRecipe recipe = GeothermalExchangerRecipe.findRecipe(rawLevel, input);
        if(recipe == null) return false;
        MultiblockProcessInMachine<GeothermalExchangerRecipe> process = new MultiblockProcessInMachine<>(recipe);
        if(input.isEmpty()) process.setInputTanks(1);
        int drainSimulation = state.water_tank.drain(recipe.fluidIn.getAmount(), FluidAction.SIMULATE).getAmount();
        int drainAmount = recipe.fluidIn.getAmount();
        if(state.processor.addProcessToQueue(process, rawLevel, true) && drainSimulation == drainAmount)
        {
            state.heatData.currentY = Collections.max(transitionPlanes.keySet());
            state.processor.addProcessToQueue(process, rawLevel, false);
            state.water_tank.drain(recipe.fluidIn.getAmount(), FluidAction.EXECUTE).getAmount();
            return true;
        }
        return false;
    }

    private void updateMultiblockHeatStates(State state, Vec3i size, IMultiblockLevel multiblockLevel)
    {
        int structureHeight = size.getY()-1;
        int structureLength = size.getX();
        int structureWidth = size.getZ();
        Set<Block> blockSet = new HashSet<>();
        MutableBlockPos cursor = new MutableBlockPos();
        Level rawLevel = multiblockLevel.getRawLevel();
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
                        Block block = relativeState.getBlock();
                        blockSet.add(block);

                        GeothermalConversionRecipe recipe = GeothermalConversionRecipe.findRecipe(rawLevel, block);
                        int heatBlockIndex = -1;
                        if(recipe != null)
                        {
                            List<GeothermalConversionRecipe> recipeList = GeothermalConversionRecipe.RECIPES.getRecipes(rawLevel).stream().toList();
                            heatBlockIndex = recipeList.indexOf(recipe);
                        }

                        state.setHeatStateAtIndex(index, heatBlockIndex);
                        index++;
                    }
                }
            }
        }
    }

    Map<Integer, List<BlockPos>> transitionPlanes = new TreeMap<>(Comparator.reverseOrder());
    int currentY = -256;
    private void setupTransitionPlanes(IMultiblockLevel multiblockLevel)
    {
        transitionPlanes.clear();
        Level rawLevel = multiblockLevel.getRawLevel();
        Vec3i size = IGGeothermalExchangerMultiblock.INSTANCE.getSize(null);
        int structureHeight = size.getY()-2;
        int structureLength = size.getX();
        int structureWidth = size.getZ();
        MutableBlockPos cursor = new MutableBlockPos();
        List<Block> validBlocks = GeothermalConversionRecipe.RECIPES.getRecipes(rawLevel).stream().map(r -> r.transitionBlock.get()).toList();

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
                        relativeState = rawLevel.getBlockState(multiblockLevel.toAbsolute(cursor));
                        if(validBlocks.contains(relativeState.getBlock()))
                        {
                            int y = cursor.getY();
                            transitionPlanes.computeIfAbsent(y, k -> new ArrayList<>()).add(cursor.immutable());
                        }
                    }
                }
            }
        }
        if (!transitionPlanes.isEmpty()) {
            currentY = transitionPlanes.keySet().iterator().next();
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
        private final MultiblockProcessor<GeothermalExchangerRecipe, ProcessContextInMachine<GeothermalExchangerRecipe>> processor;
        private final FluidTank water_tank = new FluidTank(TANK_VOLUME);
        private final FluidTank steam_tank = new FluidTank(TANK_VOLUME);
        private final AveragingEnergyStorage energy_storage = new AveragingEnergyStorage(ENERGY_CAPACITY);
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();
        private byte[] heating_states = new byte[66];
        private boolean isActive;
        private float cooling_rate;
        private float display_heat;
        private int heat;
        private int accumulatedHeatExchange;
        private int currentY;
        private final StoredCapability<IFluidHandler> fInputCap;
        private final StoredCapability<IFluidHandler> fOutputCap;
        private final CapabilityReference<IFluidHandler> fluidOutput;
        private final StoredCapability<IEnergyStorage> energyCap;
        private final Supplier<GeothermalExchangerRecipe> cachedRecipe;

        private final MultiblockProcessor.InMachineProcessor<GeothermalExchangerRecipe> dummy;
        private GeothermalHeatHelper.HeatData heatData;

        public State(IInitialMultiblockContext<State> context) {
            this.energyCap = new StoredCapability<>(this.energy_storage);
            this.heatData = GeothermalHeatHelper.fromMachineState(this, currentY);
            this.isActive = false;
            this.accumulatedHeatExchange = 0;
            this.currentY = 0;
            this.display_heat = 0;
            this.heat = 300;
            this.cooling_rate = 0;
            Runnable changedAndSync = () -> {
                context.getSyncRunnable().run();
                context.getMarkDirtyRunnable().run();
            };

            this.fInputCap = new StoredCapability<>(new ArrayFluidHandler(water_tank, true, true, changedAndSync));
            this.fOutputCap = new StoredCapability<>(new ArrayFluidHandler(steam_tank, true, false, changedAndSync));

            Supplier<Level> getLevel = context.levelSupplier();
            this.cachedRecipe = CachedRecipe.cached(GeothermalExchangerRecipe::findRecipe, getLevel, this.water_tank::getFluid);
            this.processor = new MultiblockProcessor<>(
                    1, 0, 1, context.getMarkDirtyRunnable(), GeothermalExchangerRecipe.RECIPES::getById
            );
			assert FLUID_OUTPUT_CAP.side()!=null;
			this.fluidOutput = context.getCapabilityAt(ForgeCapabilities.FLUID_HANDLER, new MultiblockFace(FLUID_OUTPUT_CAP.side().getOpposite(), FLUID_OUTPUT_CAP.posInMultiblock().above()));
            this.dummy = new InMachineProcessor<>(1, 0, 1, context.getMarkDirtyRunnable(), GeothermalExchangerRecipe.RECIPES::getById);
        }

        public void clearProcessor()
        {
            this.processor.fromNBT(dummy.toNBT(), MultiblockProcessInMachine::new);
        }

        @Override
        public boolean additionalCanProcessCheck(MultiblockProcess<GeothermalExchangerRecipe, ?> process, Level level)
        {
            GeothermalExchangerRecipe recipe = process.getRecipe(level);
            if(cachedRecipe != null && cachedRecipe.get() != null && !cachedRecipe.get().equals(recipe)) clearProcessor();
			return recipe != null && (steam_tank.getFluid().isFluidEqual(recipe.fluidOutput.get()) || steam_tank.isEmpty()) && steam_tank.getSpace() >= recipe.fluidOutput.get().getAmount() && GeothermalHeatHelper.canProcessRecipe(recipe, heat);
        }

        @Override
        public void readSyncNBT(CompoundTag nbt)
        {
            readSaveNBT(nbt);
            heating_states = nbt.getByteArray("heating_states");
            currentY = nbt.getInt("current_y");
        }

        @Override
        public void writeSyncNBT(CompoundTag nbt)
        {
            writeSaveNBT(nbt);
            nbt.putByteArray("heating_states", heating_states);
            nbt.putInt("current_y", currentY);
        }

        @Override
        public FluidTank[] getInternalTanks()
        {
            return new FluidTank[]{water_tank, steam_tank};
        }

        @Override
        public int[] getOutputTanks()
        {
            return new int[]{1};
        }

        @Override
        public void readSaveNBT(CompoundTag nbt){
            isActive = nbt.getBoolean("is_active");
            heat = nbt.getInt("heat");
            display_heat = nbt.getFloat("display_heat");
            cooling_rate = nbt.getFloat("cooling");
            water_tank.readFromNBT(nbt.getCompound("water_tank"));
            steam_tank.readFromNBT(nbt.getCompound("steam_tank"));
            processor.fromNBT(nbt.get("processor"), MultiblockProcessInMachine::new);
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt){
            nbt.putBoolean("is_active", isActive);
            nbt.putInt("heat", heat);
            nbt.putFloat("display_heat", display_heat);
            nbt.putFloat("cooling", cooling_rate);
            nbt.put("water_tank", water_tank.writeToNBT(new CompoundTag()));
            nbt.put("steam_tank", steam_tank.writeToNBT(new CompoundTag()));
            nbt.put("processor", processor.toNBT());
        }

        @Override
        public void onProcessFinish(MultiblockProcess<GeothermalExchangerRecipe, ?> process, Level level)
        {
            try {
                GeothermalExchangerRecipe recipe = process.getRecipe(level);
                if(recipe != null)
                {
                    heatData.machineHeat = recipe.fluidIn.getRandomizedExampleStack(0).getFluid().getFluidType().getTemperature();
                    steam_tank.fill(recipe.fluidOutput.get(), FluidAction.EXECUTE);
                }
            } catch(Exception error)
            {
                IGLib.IG_LOGGER.error("Error: {}", error.getMessage());
            }
        }

        public int getCurrentHeat()
		{
            return heat;
		}

        public float getCoolingRate()
        {
            return cooling_rate;
        }

        @Override
        public AveragingEnergyStorage getEnergy()
        {
            return energy_storage;
        }

        public void setHeatStateAtIndex(int index, int value) {
            this.heating_states[index] = (byte) value;
        }

        public byte[] getHeatingStates()
        {
            return heating_states;
        }

        public void setHeat(int machineHeat)
        {
            this.heat = machineHeat;
        }

        public void setAccumulatedHeatExchange(int accumulatedHeatExchange)
        {
            this.accumulatedHeatExchange = accumulatedHeatExchange;
        }

        public int getAccumulatedHeatExchange()
        {
            return this.accumulatedHeatExchange;
        }

        public float getDisplayHeat()
        {
            return this.display_heat;
        }
    }

}