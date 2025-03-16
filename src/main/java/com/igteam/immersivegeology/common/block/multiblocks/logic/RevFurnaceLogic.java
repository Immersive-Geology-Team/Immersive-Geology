/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.logic;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.BlastFurnaceFuel;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.*;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.client.utils.TextUtils;
import blusunrize.immersiveengineering.common.blocks.metal.BlastFurnacePreheaterBlockEntity;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.AdvBlastFurnaceLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.interfaces.MBOverlayText;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import blusunrize.immersiveengineering.common.gui.sync.GetterAndSetter;
import blusunrize.immersiveengineering.common.util.CachedRecipe;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler.IOConstraint;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler.IntRange;
import com.igteam.immersivegeology.common.block.multiblocks.logic.CoreDrillLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGFurnaceHandler;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGFurnaceHandler.IFurnaceEnvironment;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGFurnaceHandler.InputSlot;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGFurnaceHandler.OutputSlot;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGRevFurnaceHandler;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGRevFurnaceHandler.IRevFurnaceEnvironment;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGRevFurnaceHandler.RevInputSlot;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGRevFurnaceHandler.RevOutputSlot;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RevFurnaceRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.RevFurnaceShape;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ParticleUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RevFurnaceLogic implements IMultiblockLogic<RevFurnaceLogic.State>, MBOverlayText<RevFurnaceLogic.State>, IServerTickableComponent<RevFurnaceLogic.State>, IClientTickableComponent<RevFurnaceLogic.State>
{
    public static final int TANK_CAPACITY = 24*FluidType.BUCKET_VOLUME;
    public static final int NUM_SLOTS = 6;
    CapabilityPosition SLOT_1_INPUT_POSITION = new CapabilityPosition(1,1,1, RelativeBlockFace.UP);
    CapabilityPosition SLOT_2_INPUT_POSITION = new CapabilityPosition(1,1,4, RelativeBlockFace.UP);

    private static final BlockPos[] HEATER_OFFSETS = new BlockPos[]{new BlockPos(1, 0, -1), new BlockPos(1, 0, 6)};

    public static final MultiblockFace SLOT_1_OUTPUT_POSITION = new MultiblockFace(0,0,1, RelativeBlockFace.LEFT);
    public static final MultiblockFace SLOT_2_OUTPUT_POSITION = new MultiblockFace(0,0,4, RelativeBlockFace.RIGHT);

    public static final CapabilityPosition SLOT_1_OUTPUT_FLUID = new CapabilityPosition(4,11,1, RelativeBlockFace.UP);
    static CapabilityPosition SLOT_2_OUTPUT_FLUID = new CapabilityPosition(4,11,4, RelativeBlockFace.UP);

    @Override
    public void tickServer(IMultiblockContext<RevFurnaceLogic.State> context) {
        final State state = context.getState();
        state.active_left = state.furnace.tickServerLeft(context);
        state.active_right = state.furnace.tickServerRight(context);
        outputItems(state);
        if(state.tank.getFluidAmount() > 0)
        {
            drainOutputTank(state, context, state.fluidOutput1);
            drainOutputTank(state, context, state.fluidOutput2);
        }

        // Not the most optimal way to solve this issue.
        // But a sync request should be alright for this purpose for now.
        context.requestMasterBESync();
    }

    private static final int[] OUTPUT_SLOTS_LEFT = Util.make(new int[1], slots -> {slots[0] = 2;});
    private static final int[] OUTPUT_SLOTS_RIGHT = Util.make(new int[1], slots -> {slots[0] = 4;});

    private boolean leftChimneyUsed = false;
    private boolean rightChimneyUsed = false;

    private void outputItems(RevFurnaceLogic.State state)
    {
        IItemHandler outputHandlerLeft = state.outputLeft.getNullable();
        if(outputHandlerLeft!=null)
            for(int j : OUTPUT_SLOTS_LEFT)
            {
                final ItemStack nextStack = state.inventory.getStackInSlot(j);
                if(nextStack.isEmpty())
                    continue;
                ItemStack stack = ItemHandlerHelper.copyStackWithSize(nextStack, 1);
                stack = ItemHandlerHelper.insertItem(outputHandlerLeft, stack, false);
                if(stack.isEmpty())
                    nextStack.shrink(1);
            }

        IItemHandler outputHandlerRight = state.outputRight.getNullable();
        if(outputHandlerRight!=null)
            for(int j : OUTPUT_SLOTS_RIGHT)
            {
                final ItemStack nextStack = state.inventory.getStackInSlot(j);
                if(nextStack.isEmpty())
                    continue;
                ItemStack stack = ItemHandlerHelper.copyStackWithSize(nextStack, 1);
                stack = ItemHandlerHelper.insertItem(outputHandlerRight, stack, false);
                if(stack.isEmpty())
                    nextStack.shrink(1);
            }
    }

    private void drainOutputTank(RevFurnaceLogic.State state, IMultiblockContext<RevFurnaceLogic.State> context, CapabilityReference<IFluidHandler> outputRef)
    {
        int outSize = Math.min(FluidType.BUCKET_VOLUME, state.tank.getFluidAmount());
        FluidStack out = Utils.copyFluidStackWithAmount(state.tank.getFluid(), outSize, false);
        IFluidHandler output = outputRef.getNullable();

        if(output==null)
            return;

        int accepted = output.fill(out, FluidAction.SIMULATE);
        if(accepted > 0)
        {
            int drained = output.fill(Utils.copyFluidStackWithAmount(out, Math.min(out.getAmount(), accepted), false), FluidAction.EXECUTE);
            state.tank.drain(drained, FluidAction.EXECUTE);
            context.markMasterDirty();
            context.requestMasterBESync();
        }
    }


    @Override
    public <T> LazyOptional<T> getCapability(IMultiblockContext<State> ctx, CapabilityPosition position, Capability<T> cap)
    {
        final State state = ctx.getState();
        final boolean isMirrored = ctx.getLevel().getOrientation().mirrored();
        if(cap==ForgeCapabilities.ITEM_HANDLER)
        {
            if(SLOT_1_INPUT_POSITION.equals(position)) return (isMirrored ? state.invCapRight.cast(ctx) : state.invCapLeft.cast(ctx));
            if(SLOT_2_INPUT_POSITION.equals(position)) return (isMirrored ? state.invCapLeft.cast(ctx) : state.invCapRight.cast(ctx) );
            if(SLOT_1_OUTPUT_POSITION.posInMultiblock().equals(position.posInMultiblock()))
            {
                return isMirrored ? state.outputHandlerRight.cast(ctx) : state.outputHandlerLeft.cast(ctx);
            }
            if(SLOT_2_OUTPUT_POSITION.posInMultiblock().equals(position.posInMultiblock()))
            {
                return isMirrored ? state.outputHandlerLeft.cast(ctx) : state.outputHandlerRight.cast(ctx);
            }
        }
        else if(cap==ForgeCapabilities.FLUID_HANDLER)
        {
            if(SLOT_1_OUTPUT_FLUID.equals(position) || SLOT_2_OUTPUT_FLUID.equals(position)) return state.fluidCap.cast(ctx);
        }
        return LazyOptional.empty();
    }

    @Override
    public void dropExtraItems(State state, Consumer<ItemStack> drop)
    {
        MBInventoryUtils.dropItems(state.inventory, drop);
    }

    @Override
    public State createInitialState(IInitialMultiblockContext<State> capability) {
        return new RevFurnaceLogic.State(capability);
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return RevFurnaceShape.GETTER;
    }

    @Nullable
    @Override
    public List<Component> getOverlayText(State state, Player player, boolean b)
    {
        if(Utils.isFluidRelatedItemStack(player.getItemInHand(InteractionHand.MAIN_HAND)))
            return List.of(TextUtils.formatFluidStack(state.tank.getFluid()));
        return List.of();
    }
    private double particleXZSpeed()
    {
        return ApiUtils.RANDOM.nextDouble(-0.015625, 0.015625);
    }
    private static final Vec3 SMOKE_POSITION_LEFT = new Vec3(4.5,12,1.5);
    private static final Vec3 SMOKE_POSITION_RIGHT = new Vec3(4.5,12,4.5);
    @Override
    public void tickClient(IMultiblockContext<State> context)
    {
        final State state = context.getState();
        final boolean isMirrored = context.getLevel().getOrientation().mirrored();
        if(context.getLevel().shouldTickModulo(2))
        {
            if(((state.active_left &! state.fluidOutput1.isPresent())))
            {
                spawnSmoke(context, isMirrored ? SMOKE_POSITION_RIGHT : SMOKE_POSITION_LEFT);
            }
            if(((state.active_right &! state.fluidOutput2.isPresent())))
            {
                spawnSmoke(context, isMirrored ? SMOKE_POSITION_LEFT : SMOKE_POSITION_RIGHT);
            }
        }
    }

    private void spawnSmoke(IMultiblockContext<State> context, Vec3 position)
    {
        final Vec3 absoluteSmokePosition = context.getLevel().toAbsolute(position);
        context.getLevel().getRawLevel().addAlwaysVisibleParticle(
                ParticleTypes.CAMPFIRE_COSY_SMOKE,
                absoluteSmokePosition.x, absoluteSmokePosition.y, absoluteSmokePosition.z,
                particleXZSpeed(), 0.0625, particleXZSpeed()
        );
    }

    public static class State implements IMultiblockState, IGRevFurnaceHandler.IRevFurnaceEnvironment<RevFurnaceRecipe>
    {
        private final SlotwiseItemHandler inventory;
        final IGRevFurnaceHandler<RevFurnaceRecipe> furnace;
        private boolean active_left = false;
        private boolean active_right = false;

        private final Supplier<RevFurnaceRecipe> cachedRecipeLeft;
        private final Supplier<RevFurnaceRecipe> cachedRecipeRight;

        private final FluidTank tank = new FluidTank(TANK_CAPACITY);
        private final StoredCapability<IItemHandler> invCapLeft;
        private final StoredCapability<IItemHandler> invCapRight;
        private final StoredCapability<IItemHandler> outputHandlerLeft;
        private final StoredCapability<IItemHandler> outputHandlerRight;

        private final CapabilityReference<IItemHandler> outputLeft;
        private final CapabilityReference<IItemHandler> outputRight;

        private final CapabilityReference<IFluidHandler> fluidOutput1;
        private final CapabilityReference<IFluidHandler> fluidOutput2;
        private final StoredCapability<IFluidHandler> fluidCap;

        public State(IInitialMultiblockContext<?> ctx)
        {
            final Supplier<@Nullable Level> getLevel = ctx.levelSupplier();
            inventory = new SlotwiseItemHandler(List.of(
                    // Left
                    new IOConstraint(true, i -> RevFurnaceRecipe.findRecipe(getLevel.get(), i, null)!=null),
                    new IOConstraint(true, i -> BlastFurnaceFuel.isValidBlastFuel(getLevel.get(), i)),
                    IOConstraint.OUTPUT,
                    // Right
                    new IOConstraint(true, i -> RevFurnaceRecipe.findRecipe(getLevel.get(), i, null)!=null),
                    new IOConstraint(true, i -> BlastFurnaceFuel.isValidBlastFuel(getLevel.get(), i)),
                    IOConstraint.OUTPUT
            ), ctx.getMarkDirtyRunnable());

            this.outputLeft = ctx.getCapabilityAt(ForgeCapabilities.ITEM_HANDLER, SLOT_1_OUTPUT_POSITION);
            this.outputRight = ctx.getCapabilityAt(ForgeCapabilities.ITEM_HANDLER, SLOT_2_OUTPUT_POSITION);

            this.fluidOutput1 = ctx.getCapabilityAt(ForgeCapabilities.FLUID_HANDLER, new MultiblockFace(SLOT_1_OUTPUT_FLUID.side(), SLOT_1_OUTPUT_FLUID.posInMultiblock().above()));
            this.fluidOutput2 = ctx.getCapabilityAt(ForgeCapabilities.FLUID_HANDLER, new MultiblockFace(SLOT_2_OUTPUT_FLUID.side(), SLOT_2_OUTPUT_FLUID.posInMultiblock().above()));

            this.outputHandlerLeft = new StoredCapability<>(new WrappingItemHandler(
                    inventory, false, true, new IntRange(2,3)
            ));

            this.outputHandlerRight = new StoredCapability<>(new WrappingItemHandler(
                    inventory, false, true, new IntRange(5,6)
            ));

            cachedRecipeLeft = CachedRecipe.cached(
                    RevFurnaceRecipe::findRecipe, getLevel, () -> inventory.getStackInSlot(0)
            );

            cachedRecipeRight = CachedRecipe.cached(
                    RevFurnaceRecipe::findRecipe, getLevel, () -> inventory.getStackInSlot(3)
            );

            furnace = new IGRevFurnaceHandler<>(
                    1,
                    List.of(new RevInputSlot<>(r -> r.input, 0)),
                    List.of(new RevOutputSlot<>(r -> r.result, 2)),
                    r -> r.time,
                    4,
                    List.of(new RevInputSlot<>(r -> r.input, 3)),
                    List.of(new RevOutputSlot<>(r -> r.result, 5)),
                    r -> r.time,
                    ctx.getMarkDirtyRunnable()
            );

            this.invCapLeft = new StoredCapability<>(new WrappingItemHandler(
                    inventory, true, true, new IntRange(0,2)
            ));

            this.invCapRight = new StoredCapability<>(new WrappingItemHandler(
                    inventory, true, true, new IntRange(3,5)
            ));
            this.fluidCap = new StoredCapability<>(
                    new ArrayFluidHandler(new IFluidTank[]{tank}, true, false, ctx.getMarkDirtyRunnable())
            );
        }

        public void addToTank(int amount)
        {
            if(amount > 0)
            {
                FluidStack resource = new FluidStack(IGRegistrationHolder.getFluid.apply(BlockCategoryFlags.FLUID.getRegistryKey(ChemicalEnum.SulfurDioxde)), amount);
                tank.fill(resource, FluidAction.EXECUTE);
            }
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt)
        {
            nbt.put("tank", tank.writeToNBT(new CompoundTag()));
            nbt.put("inventory", inventory.serializeNBT());
            nbt.put("furnace", furnace.toNBT());
            nbt.putBoolean("active_left", active_left);
            nbt.putBoolean("active_right", active_right);
        }

        @Override
        public void readSaveNBT(CompoundTag nbt)
        {
            inventory.deserializeNBT(nbt.getCompound("inventory"));
            furnace.readNBT(nbt.getCompound("furnace"));
            tank.readFromNBT(nbt.getCompound("tank"));
            active_left = nbt.getBoolean("active_left");
            active_right = nbt.getBoolean("active_right");
        }

        @Override
        public void readSyncNBT(CompoundTag nbt)
        {
            readSaveNBT(nbt);
        }

        @Override
        public void writeSyncNBT(CompoundTag nbt)
        {
            writeSaveNBT(nbt);
        }

        public boolean isActive(int furnaceIndex)
        {
            return furnaceIndex == 0 ? active_left : active_right;
        }

        @Override
        public IItemHandlerModifiable getInventory()
        {
            return inventory;
        }

        @Override
        public @Nullable RevFurnaceRecipe getRecipeForInput(boolean isLeft)
        {
            return isLeft ? cachedRecipeLeft.get() : cachedRecipeRight.get();
        }

        @Override
        public int getProcessSpeed(IMultiblockLevel level)
        {
            int i = 1;
            BlockPos[] var3 = RevFurnaceLogic.HEATER_OFFSETS;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                BlockPos offset = var3[var5];
                BlastFurnacePreheaterBlockEntity preheater = this.getPreheater(level, offset);
                if (preheater != null) {
                    i += preheater.doSpeedup();
                }
            }

            return i;
        }

        public @Nullable BlastFurnacePreheaterBlockEntity getPreheater(IMultiblockLevel level, BlockPos pos) {
            BlockEntity te = level.getBlockEntity(pos);
            BlastFurnacePreheaterBlockEntity var10000;
            if (te instanceof BlastFurnacePreheaterBlockEntity heater) {
                var10000 = heater;
            } else {
                var10000 = null;
            }

            return var10000;
        }

        public GetterAndSetter<Boolean> preheaterActive(IMultiblockLevel level, int index) {
            return GetterAndSetter.getterOnly(() -> {
                BlastFurnacePreheaterBlockEntity heater = this.getPreheater(level, RevFurnaceLogic.HEATER_OFFSETS[index]);
                return heater != null && heater.active;
            });
        }

        @Override
        public int getBurnTimeOf(Level level, ItemStack fuel)
        {
            return BlastFurnaceFuel.getBlastFuelTime(level, fuel);
        }

        @Override
        public void turnOff(IMultiblockLevel level, boolean isLeft)
        {
            BlockPos[] var2 = RevFurnaceLogic.HEATER_OFFSETS;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                BlockPos offset = var2[var4];
                BlastFurnacePreheaterBlockEntity preheater = this.getPreheater(level, offset);
                if (preheater != null) {
                    preheater.turnOff();
                }
            }

            if(isLeft)
            {
                active_left = false;
                return;
            }
            active_right = false;
        }

        public ContainerData getStateView() {
            return this.furnace.stateView;
        }

		public FluidTank getTank()
		{
            return this.tank;
		}
	}
}