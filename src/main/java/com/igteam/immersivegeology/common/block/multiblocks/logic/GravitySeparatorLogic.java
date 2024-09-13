package com.igteam.immersivegeology.common.block.multiblocks.logic;

import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
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
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.interfaces.MBOverlayText;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.*;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor.InWorldProcessLoader;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor.InWorldProcessor;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext.ProcessContextInWorld;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import blusunrize.immersiveengineering.common.util.DroppingMultiblockOutput;
import blusunrize.immersiveengineering.common.util.inventory.InsertOnlyInventory;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.SeparatorProcess;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GravitySeparatorRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.GravitySeparatorShape;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntToDoubleFunction;
import java.util.function.Supplier;

public class GravitySeparatorLogic implements IMultiblockLogic<GravitySeparatorLogic.State>, IServerTickableComponent<GravitySeparatorLogic.State>, MBOverlayText<GravitySeparatorLogic.State>, IClientTickableComponent<GravitySeparatorLogic.State> {
    public static final BlockPos REDSTONE_IN = new BlockPos(1, 6, 1);

    private static final CapabilityPosition FLUID_INPUT_CAP = new CapabilityPosition(1,6,1, RelativeBlockFace.UP);

    private static final int MAX_PROCESSES = 64;
    private static final CapabilityPosition INPUT_POS = new CapabilityPosition(0, 1, 0, RelativeBlockFace.RIGHT);
    private static final MultiblockFace OUTPUT_POS = new MultiblockFace(1,0,3, RelativeBlockFace.FRONT);
    private static final MultiblockFace SECONDARY_OUTPUT_POS = new MultiblockFace(1,0,0, RelativeBlockFace.BACK);
    private static final CapabilityPosition ITEM_OUTPUT_CAP = CapabilityPosition.opposing(OUTPUT_POS);

    public static final int TANK_VOLUME = 8 * FluidType.BUCKET_VOLUME;

    @Override
    public void tickClient(IMultiblockContext<State> iMultiblockContext) {

    }

    @Override
    public void tickServer(IMultiblockContext<State> context) {
        final State state = context.getState();
        final IMultiblockLevel level = context.getLevel();
        final Level rawLevel = level.getRawLevel();
        final boolean wasActive = state.renderAsActive;

        if(state.tank.getFluidAmount() > 0)
        {
            // Removal List for Gathering To Remove Items, because Java is being a baby and throws a Concurrent Modification Tantrum when using Iterators
            ArrayList<SeparatorProcess> removalList = new ArrayList<>();
            int size = state.separatorProcessesQueue.size();
            ArrayList<SeparatorProcess> processList = new ArrayList<>(List.of(state.separatorProcessesQueue.toArray(new SeparatorProcess[size])));
			for(SeparatorProcess process : processList)
			{
				if(process.processStep(rawLevel, state.tank))
				{
					context.markMasterDirty();
				}

				if(process.isProcessFinished())
				{
					state.output.insertOrDrop(process.getCurrentOutput(), level);
                    if(process.outputByproduct()) state.secondary.insertOrDrop(process.getCurrentByproduct(), level);
					removalList.add(process);
					context.markDirtyAndSync();
				}
			}
            state.separatorProcessesQueue.removeAll(removalList);

            if(wasActive != state.renderAsActive) context.requestMasterBESync();
        }
    }

    @Override
    public void onEntityCollision(IMultiblockContext<State> ctx, BlockPos posInMultiblock, Entity collided)
    {
        if(collided.level().isClientSide)
            return;
        final State state = ctx.getState();
        final IMultiblockLevel level = ctx.getLevel();
        final AABB internalBB = new AABB(-2, 4, -2, 5, 7, 5);
        final AABB separatorInternal = level.toAbsolute(internalBB);
        if(collided instanceof ItemEntity itemEntity)
        {
            ItemStack stack = itemEntity.getItem();
            if(stack.isEmpty())
                return;
            stack = stack.copy();
            if(insertItemToProcess(stack, false, state, level.getRawLevel()))
                ctx.markDirtyAndSync();
            if(stack.getCount() <= 0)
                itemEntity.discard();
            else
                itemEntity.setItem(stack);
        }
    }

    @Override
    public GravitySeparatorLogic.State createInitialState(IInitialMultiblockContext<State> capability) {
        return new GravitySeparatorLogic.State(capability);
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return GravitySeparatorShape.GETTER;
    }

    @Override
    public <T> LazyOptional<T> getCapability(IMultiblockContext<State> ctx, CapabilityPosition position, Capability<T> cap)
    {
        final State state = ctx.getState();
        if(cap == ForgeCapabilities.ITEM_HANDLER)
        {
            return state.insertionHandler.cast(ctx);
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

    private static boolean isInInput(BlockPos posInMultiblock, boolean allowMiddleLayer)
    {
        return true;
    }

    private static boolean insertItemToProcess(ItemStack stack, boolean simulate, State state, Level rawLevel)
    {
        if(state.separatorProcessesQueue.size() >= MAX_PROCESSES) return false;
        SeparatorProcess p;
        GravitySeparatorRecipe recipe = GravitySeparatorRecipe.findRecipe(rawLevel, stack);
        if(recipe == null) return false;
        if(!simulate)
        {
            p = new SeparatorProcess(ItemHandlerHelper.copyStackWithSize(stack, 1));
            state.separatorProcessesQueue.add(p);
            stack.shrink(1);
        }
        return true;
    }

    @Nullable
    @Override
    public List<Component> getOverlayText(State state, Player player, boolean b)
    {
        return List.of();
    }

    public static class State implements IMultiblockState
    {
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.disabledByDefault();
        public final ArrayList<SeparatorProcess> separatorProcessesQueue = new ArrayList<>();
        private final StoredCapability<IItemHandler> insertionHandler;
        private final DroppingMultiblockOutput output;
        private final DroppingMultiblockOutput secondary;
        private final StoredCapability<IFluidHandler> fInputCap;
        public final FluidTank tank = new FluidTank(TANK_VOLUME);
        private boolean renderAsActive;

        public State(IInitialMultiblockContext<State> ctx){
            final Supplier<@Nullable Level> levelGetter = ctx.levelSupplier();
            final Runnable markDirty = ctx.getMarkDirtyRunnable();
            final Runnable sync = ctx.getSyncRunnable();

            this.output = new DroppingMultiblockOutput(OUTPUT_POS, ctx);
            this.secondary = new DroppingMultiblockOutput(SECONDARY_OUTPUT_POS, ctx);
            Runnable changedAndSync = () -> {
                markDirty.run();
                sync.run();
            };

            this.fInputCap = new StoredCapability<>(new ArrayFluidHandler(tank, true, true, changedAndSync));

            this.insertionHandler = new StoredCapability<>(new InsertOnlyInventory()
            {
                @Override
                protected ItemStack insert(ItemStack toInsert, boolean simulate)
                {
                    toInsert = toInsert.copy();
                    if(insertItemToProcess(toInsert, simulate, State.this, levelGetter.get()))
                    {
                        changedAndSync.run();
                    }
                    return toInsert;
                }
            });
        }

        public boolean shouldRenderActive()
        {
            return renderAsActive;
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt){
            writeCommonNBT(nbt);
            nbt.put("tank", this.tank.writeToNBT(new CompoundTag()));
        }

        @Override
        public void readSaveNBT(CompoundTag nbt){
            readCommonNBT(nbt);
            tank.readFromNBT(nbt.getCompound("tank"));
        }

        @Override
        public void writeSyncNBT(CompoundTag nbt)
        {
            nbt.putBoolean("renderActive", renderAsActive);
            writeSaveNBT(nbt);
        }

        @Override
        public void readSyncNBT(CompoundTag nbt)
        {
            renderAsActive = nbt.getBoolean("renderActive");
            readSaveNBT(nbt);
        }

        private void writeCommonNBT(CompoundTag nbt)
        {
            ListTag processes = new ListTag();
            for(final SeparatorProcess process : separatorProcessesQueue)
                processes.add(process.writeToNBT());
            nbt.put("processes", processes);
        }

        private void readCommonNBT(CompoundTag nbt)
        {
            ListTag processes = nbt.getList("processes", Tag.TAG_COMPOUND);
            separatorProcessesQueue.clear();
            for(int i = 0; i < processes.size(); ++i)
                separatorProcessesQueue.add(SeparatorProcess.readFromNBT(processes.getCompound(i)));
        }
    }
}