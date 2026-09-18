/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.logic;

import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
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
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler.IOConstraint;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler.IntRange;
import com.igteam.immersivegeology.common.block.helper.IGReceiveOnlyEnergy;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.FoundryRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.FoundryShape;
import com.igteam.immersivegeology.common.item.helper.IGFlagItem;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class FoundryLogic implements IMultiblockLogic<FoundryLogic.State>, IServerTickableComponent<FoundryLogic.State>, MBOverlayText<FoundryLogic.State>
{
    public static final BlockPos REDSTONE_IN = new BlockPos(0, 1, 1);

    public static final int MOLD_SLOT = 0;
    public static final int FIRST_OUTPUT_SLOT = 1;
    public static final int OUTPUT_SLOTS = 4;
    public static final int SLOT_COUNT = FIRST_OUTPUT_SLOT+OUTPUT_SLOTS;

    public static final int MOLD_DURABILITY = 2048;
    public static final int ENERGY_CAPACITY = 48000;
    public static final int TANK_VOLUME = 16*FluidType.BUCKET_VOLUME;
    public static final int TANK_COUNT = 8;

    private static final CapabilityPosition ENERGY_INPUT = new CapabilityPosition(3, 1, 0, RelativeBlockFace.FRONT);
    private static final CapabilityPosition FLUID_INPUT_CAP = new CapabilityPosition(1, 2, 0, RelativeBlockFace.FRONT);
    private static final CapabilityPosition MOLD_INPUT_CAP = new CapabilityPosition(1, 4, 1, RelativeBlockFace.UP);
    private static final MultiblockFace OUTPUT_POS = new MultiblockFace(-1, 1, 3, RelativeBlockFace.LEFT);
    private static final CapabilityPosition ITEM_OUTPUT_CAP = CapabilityPosition.opposing(OUTPUT_POS);

    @Override
    public void tickServer(IMultiblockContext<State> context)
    {
        final State state = context.getState();
        final int before = state.storedFluid();

        if(state.rsState.isEnabled(context))
        {
            if(state.castTotalTicks > 0) tickCast(state);
            if(state.castTotalTicks <= 0) startCast(state, context.getLevel().getRawLevel());
        }
        ejectOutput(state);

        if(before!=state.storedFluid()) context.requestMasterBESync();
    }

    private void tickCast(State state)
    {
        if(state.castTicks >= state.castTotalTicks)
        {
            finishCast(state);
            return;
        }
        if(state.energy.extractEnergy(state.castEnergyPerTick, true) < state.castEnergyPerTick) return;
        state.energy.extractEnergy(state.castEnergyPerTick, false);
        state.castTicks++;
        if(state.castTicks >= state.castTotalTicks) finishCast(state);
    }

    private void finishCast(State state)
    {
        ItemStack remainder = insertOutput(state, state.castResult.copy());
        state.castResult = remainder;
        if(!remainder.isEmpty()) return;
        state.castTicks = 0;
        state.castTotalTicks = 0;
        state.castEnergyPerTick = 0;
    }

    private void damageMold(State state, ItemStack mold, int uses)
    {
        if(!mold.isDamageableItem()) return;
        int damage = mold.getDamageValue()+uses;
        if(damage >= mold.getMaxDamage()) state.inventory.setStackInSlot(MOLD_SLOT, ItemStack.EMPTY);
        else
        {
            mold.setDamageValue(damage);
            state.inventory.setStackInSlot(MOLD_SLOT, mold);
        }
    }

    private void startCast(State state, Level level)
    {
        final ItemStack mold = state.inventory.getStackInSlot(MOLD_SLOT);
        if(mold.isEmpty()) return;
        for(FluidTank tank : state.tanks)
            if(startCastFrom(state, level, mold, tank)) return;
    }

    private boolean startCastFrom(State state, Level level, ItemStack mold, FluidTank tank)
    {
        final FluidStack input = tank.getFluid();
        if(input.isEmpty()) return false;
        final FoundryRecipe recipe = FoundryRecipe.findRecipe(level, input, mold);
        if(recipe==null) return false;

        final ItemStack single = recipe.itemOutput.get();
        final int perCast = single.getCount();
        final int perCastFluid = recipe.fluidIn.getAmount();
        if(single.isEmpty()||perCast <= 0||perCastFluid <= 0) return false;

        if(input.getAmount() < perCastFluid) return false;
        if(outputSpaceFor(state, single) < perCast) return false;
        if(mold.isDamageableItem()&&mold.getMaxDamage()-mold.getDamageValue() < perCast) return false;

        tank.drain(perCastFluid, FluidAction.EXECUTE);
        damageMold(state, mold, perCast);
        state.castResult = single.copy();
        state.castTicks = 0;
        state.castTotalTicks = Math.max(1, recipe.getTotalProcessTime());
        state.castEnergyPerTick = Math.max(1, energyPerTick(recipe.getTotalProcessEnergy(), recipe.getTotalProcessTime()));
        return true;
    }

    private static int energyPerTick(int energy, int time)
    {
        return time <= 0?energy: (energy+time-1)/time;
    }

    private int outputSpaceFor(State state, ItemStack stack)
    {
        IItemHandlerModifiable inventory = state.getInventory();
        int space = 0;
        for(int slot = FIRST_OUTPUT_SLOT; slot < SLOT_COUNT; slot++)
        {
            ItemStack present = inventory.getStackInSlot(slot);
            if(present.isEmpty()) space += stack.getMaxStackSize();
            else if(ItemStack.isSameItemSameTags(present, stack)) space += present.getMaxStackSize()-present.getCount();
        }
        return space;
    }

    private static ItemStack withCount(ItemStack stack, int count)
    {
        ItemStack copy = stack.copy();
        copy.setCount(count);
        return copy;
    }

    private ItemStack insertOutput(State state, ItemStack stack)
    {
        IItemHandlerModifiable inventory = state.getInventory();
        ItemStack remaining = stack.copy();
        for(int slot = FIRST_OUTPUT_SLOT; slot < SLOT_COUNT&&!remaining.isEmpty(); slot++)
        {
            ItemStack present = inventory.getStackInSlot(slot);
            int moved;
            if(present.isEmpty()) moved = Math.min(remaining.getCount(), remaining.getMaxStackSize());
            else if(ItemStack.isSameItemSameTags(present, remaining))
                moved = Math.min(remaining.getCount(), present.getMaxStackSize()-present.getCount());
            else continue;
            if(moved <= 0) continue;
            inventory.setStackInSlot(slot, withCount(remaining, present.getCount()+moved));
            remaining.shrink(moved);
        }
        return remaining;
    }

    private void ejectOutput(State state)
    {
        if(state.output.getNullable()==null) return;
        IItemHandlerModifiable inventory = state.getInventory();
        for(int slot = FIRST_OUTPUT_SLOT; slot < SLOT_COUNT; slot++)
        {
            ItemStack stored = inventory.getStackInSlot(slot);
            if(stored.isEmpty()) continue;
            inventory.setStackInSlot(slot, Utils.insertStackIntoInventory(state.output, stored, false));
        }
    }

    @Override
    public State createInitialState(IInitialMultiblockContext<State> capability)
    {
        return new FoundryLogic.State(capability);
    }

    @Override
    public void dropExtraItems(State state, Consumer<ItemStack> drop)
    {
        MBInventoryUtils.dropItems(state.getInventory(), drop);
        if(!state.castResult.isEmpty()) drop.accept(state.castResult.copy());
    }

    @Override
    public <T> LazyOptional<T> getCapability(IMultiblockContext<State> ctx, CapabilityPosition position, Capability<T> cap)
    {
        final State state = ctx.getState();
        if(cap==ForgeCapabilities.ENERGY&&(position.side()==null||ENERGY_INPUT.equals(position)))
            return state.energyCap.cast(ctx);

        if(cap==ForgeCapabilities.FLUID_HANDLER&&FLUID_INPUT_CAP.equals(position))
            return state.fInputCap.cast(ctx);

        if(cap==ForgeCapabilities.ITEM_HANDLER)
        {
            if(MOLD_INPUT_CAP.equals(position)) return state.moldInputCap.cast(ctx);
            if(ITEM_OUTPUT_CAP.equals(position)) return state.itemOutputCap.cast(ctx);
        }

        return LazyOptional.empty();
    }

    @Nullable
    @Override
    public List<Component> getOverlayText(FoundryLogic.State state, Player player, boolean b)
    {
        if(!Utils.isFluidRelatedItemStack(player.getItemInHand(InteractionHand.MAIN_HAND))) return null;
        List<Component> lines = new ArrayList<>();
        for(FluidTank tank : state.tanks)
            if(!tank.getFluid().isEmpty()) lines.add(TextUtils.formatFluidStack(tank.getFluid()));
        return lines.isEmpty()?List.of(TextUtils.formatFluidStack(FluidStack.EMPTY)): lines;
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType)
    {
        return FoundryShape.GETTER;
    }

    public static class SharedTank extends FluidTank
    {
        private SharedTank[] group = new SharedTank[0];

        private SharedTank()
        {
            super(TANK_VOLUME);
        }

        public static SharedTank[] group(int count)
        {
            SharedTank[] group = new SharedTank[count];
            for(int i = 0; i < count; i++) group[i] = new SharedTank();
            for(SharedTank tank : group) tank.group = group;
            return group;
        }

        private void refreshCapacity()
        {
            int used = 0;
            for(SharedTank other : group)
                if(other!=this) used += other.getFluidAmount();
            capacity = Math.max(0, TANK_VOLUME-used);
        }

        @Override
        public int getCapacity()
        {
            refreshCapacity();
            return capacity;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action)
        {
            refreshCapacity();
            return super.fill(resource, action);
        }
    }

    public static boolean isMold(ItemStack stack)
    {
        return stack.getItem() instanceof IGFlagItem flagItem
                &&flagItem.getFlag().getValue() instanceof ItemCategoryFlags flag
                &&flag.isMold();
    }

    public static class State implements IMultiblockState
    {
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();
        public final AveragingEnergyStorage energy = new AveragingEnergyStorage(ENERGY_CAPACITY);
        public final SlotwiseItemHandler inventory;
        public final SharedTank[] tanks = SharedTank.group(TANK_COUNT);

        private final StoredCapability<IFluidHandler> fInputCap;
        private final StoredCapability<IItemHandler> moldInputCap;
        private final StoredCapability<IItemHandler> itemOutputCap;
        private final StoredCapability<IEnergyStorage> energyCap;
        private final CapabilityReference<IItemHandler> output;

        private final Runnable changedAndSync;
        private ItemStack castResult = ItemStack.EMPTY;
        private int castTicks;
        private int castTotalTicks;
        private int castEnergyPerTick;

        public State(IInitialMultiblockContext<State> ctx)
        {
            this.energyCap = new StoredCapability<>(IGReceiveOnlyEnergy.of(this.energy));
            this.output = ctx.getCapabilityAt(ForgeCapabilities.ITEM_HANDLER, OUTPUT_POS);
            this.inventory = SlotwiseItemHandler.makeWithGroups(
                    List.of(
                            new SlotwiseItemHandler.IOConstraintGroup(new IOConstraint(true, FoundryLogic::isMold), 1),
                            new SlotwiseItemHandler.IOConstraintGroup(IOConstraint.OUTPUT, OUTPUT_SLOTS)
                    ), ctx.getMarkDirtyRunnable()
            );
            this.changedAndSync = () -> {
                ctx.getSyncRunnable().run();
                ctx.getMarkDirtyRunnable().run();
            };
            this.moldInputCap = new StoredCapability<>(new WrappingItemHandler(
                    inventory, true, true, new IntRange(MOLD_SLOT, FIRST_OUTPUT_SLOT)
            ));
            this.itemOutputCap = new StoredCapability<>(new WrappingItemHandler(
                    inventory, false, true, new IntRange(FIRST_OUTPUT_SLOT, SLOT_COUNT)
            ));
            this.fInputCap = new StoredCapability<>(new ArrayFluidHandler(tanks, true, true, changedAndSync));
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt)
        {
            nbt.put("energy", energy.serializeNBT());
            ListTag tankList = new ListTag();
            for(FluidTank tank : tanks) tankList.add(tank.writeToNBT(new CompoundTag()));
            nbt.put("tanks", tankList);
            nbt.put("inventory", inventory.serializeNBT());
            nbt.put("castResult", castResult.save(new CompoundTag()));
            nbt.putInt("castTicks", castTicks);
            nbt.putInt("castTotalTicks", castTotalTicks);
            nbt.putInt("castEnergyPerTick", castEnergyPerTick);
        }

        @Override
        public void readSaveNBT(CompoundTag nbt)
        {
            energy.deserializeNBT(nbt.get("energy"));
            readTanks(nbt);
            readInventory(nbt.getCompound("inventory"));
            castResult = ItemStack.of(nbt.getCompound("castResult"));
            castTicks = nbt.getInt("castTicks");
            castTotalTicks = nbt.getInt("castTotalTicks");
            castEnergyPerTick = nbt.getInt("castEnergyPerTick");
        }

        @Override
        public void writeSyncNBT(CompoundTag nbt)
        {
            writeSaveNBT(nbt);
        }

        @Override
        public void readSyncNBT(CompoundTag nbt)
        {
            readSaveNBT(nbt);
        }

        private void readInventory(CompoundTag nbt)
        {
            IItemHandlerModifiable handler = inventory.getRawHandler();
            for(int slot = 0; slot < handler.getSlots(); slot++) handler.setStackInSlot(slot, ItemStack.EMPTY);
            ListTag items = nbt.getList("Items", Tag.TAG_COMPOUND);
            for(int i = 0; i < items.size(); i++)
            {
                CompoundTag entry = items.getCompound(i);
                int slot = entry.getInt("Slot");
                if(slot >= 0&&slot < handler.getSlots()) handler.setStackInSlot(slot, ItemStack.of(entry));
            }
        }

        public float getProgress()
        {
            return castTotalTicks > 0?Math.min(1f, castTicks/(float)castTotalTicks): 0f;
        }

        private void readTanks(CompoundTag nbt)
        {
            for(FluidTank tank : tanks) tank.setFluid(FluidStack.EMPTY);
            if(nbt.contains("tank"))
            {
                tanks[0].readFromNBT(nbt.getCompound("tank"));
                return;
            }
            ListTag tankList = nbt.getList("tanks", Tag.TAG_COMPOUND);
            for(int i = 0; i < Math.min(tankList.size(), tanks.length); i++)
                tanks[i].readFromNBT(tankList.getCompound(i));
        }

        public void purgeTank(int index)
        {
            if(index < 0||index >= tanks.length||tanks[index].isEmpty()) return;
            tanks[index].setFluid(FluidStack.EMPTY);
            changedAndSync.run();
        }

        public SharedTank[] getTanks()
        {
            return tanks;
        }

        public int storedFluid()
        {
            int total = 0;
            for(FluidTank tank : tanks) total += tank.getFluidAmount();
            return total;
        }

        public IItemHandlerModifiable getInventory()
        {
            return inventory.getRawHandler();
        }

        public AveragingEnergyStorage getEnergy()
        {
            return energy;
        }
    }
}
