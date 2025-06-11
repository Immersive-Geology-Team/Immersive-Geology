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
import blusunrize.immersiveengineering.client.utils.TextUtils;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.interfaces.MBOverlayText;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.*;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext.ProcessContextInWorld;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import blusunrize.immersiveengineering.common.util.DroppingMultiblockOutput;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler.IOConstraint;
import com.igteam.immersivegeology.common.block.multiblocks.logic.PelletizerLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.ISkinnableMultiblockLogic;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.PelletizerRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RotaryKilnRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.PelletizerShape;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PelletizerLogic implements ISkinnableMultiblockLogic<State>, IServerTickableComponent<PelletizerLogic.State>, IClientTickableComponent<PelletizerLogic.State>, MBOverlayText<PelletizerLogic.State>
{
    public static final BlockPos REDSTONE_IN = new BlockPos(2,0,0);
    public static final int ENERGY_CAPACITY = 12000;

    private static final MultiblockFace OUTPUT_POS = new MultiblockFace(1,0,4, RelativeBlockFace.FRONT);
    private static final Set<CapabilityPosition> ENERGY_INPUTS = Set.of(new CapabilityPosition(0,1, 3, RelativeBlockFace.UP));
    public static final int TANK_VOLUME = 4 * FluidType.BUCKET_VOLUME;
    private static final CapabilityPosition FLUID_INPUT_CAP = new CapabilityPosition(1,0,0, RelativeBlockFace.FRONT);
    private static final CapabilityPosition ITEM_OUTPUT_CAP = CapabilityPosition.opposing(OUTPUT_POS);

    public static final int ENERGY_CONSUMPTION_RATE = 20; // Per ticke
    private static final Random rand = new Random();

    @Override
    public void tickClient(IMultiblockContext<State> context) {
        final PelletizerLogic.State state = context.getState();
        float rot = state.rotation;
        if(state.shouldRenderActive())
        {
            state.rotation = (float)((rot-3.5)%360);
            Level level = context.getLevel().getRawLevel();
            Vec3 absoluteSmokePosition = context.getLevel().toAbsolute(new Vec3(1.5,1.5f,2.125f));
            float red   = 0.8235f;
            float green = 0.7059f;
            float blue  = 0.5490f;
            float scale = rand.nextFloat(0.25f,1.25f);

            DustParticleOptions dust = new DustParticleOptions(new Vector3f(red, green, blue), scale);

            level.addParticle(
                    dust,
                    absoluteSmokePosition.x + rand.nextFloat(-.5f,.5f),
                    absoluteSmokePosition.y + rand.nextFloat(-.125f,.125f),
                    absoluteSmokePosition.z +  rand.nextFloat(-.5f,.5f),
                    0, 0.1, 0
            );

            level.addParticle(
                    ParticleTypes.SPLASH,
                    absoluteSmokePosition.x,
                    absoluteSmokePosition.y+0.5,
                    absoluteSmokePosition.z-0.5,
                    0, 0.1, 0
            );
        }
    }

    @Override
    public void dropExtraItems(State state, Consumer<ItemStack> drop)
    {
        MBInventoryUtils.dropItems(state.getInventory(), drop);
    }

    @Override
    public void tickServer(IMultiblockContext<State> context) {
        final PelletizerLogic.State state = context.getState();
        final boolean isEnabled = state.rsState.isEnabled(context);
        final boolean wasActive = state.renderAsActive;
        final Level level = context.getLevel().getRawLevel();
        state.renderAsActive = isEnabled && (!state.tank.isEmpty()) && state.processor.tickServer(state, context.getLevel(), state.rsState.isEnabled(context));
        if((wasActive != state.renderAsActive))
        {
            context.requestMasterBESync();
        }
        ItemStack inputStack = state.inventory.getStackInSlot(0);
        if(!inputStack.isEmpty())
        {
            bindingAgent = ChemicalEnum.BindingAgent.getFluid(BlockCategoryFlags.FLUID);
            if(bindingAgent.isSame(state.tank.getFluid().getFluid()))
            {
                PelletizerRecipe recipe = PelletizerRecipe.findRecipe(level, inputStack);
                if(recipe == null) return;
                MultiblockProcessInWorld<PelletizerRecipe> process = new MultiblockProcessInWorld<>(recipe, inputStack);
                if(state.processor.addProcessToQueue(process, level, true))
                {
                    state.processor.addProcessToQueue(process, level, false);
                    inputStack.shrink(recipe.itemIn.getCount());
                }
                return;
            }
        }

        if(state.processor.getQueueSize() > 0 && !state.tank.isEmpty() && isEnabled)
        {
            state.tank.drain(2, FluidAction.EXECUTE);
            context.requestMasterBESync();
        }
    }

    @Override
    public State createInitialState(IInitialMultiblockContext<State> capability) {
        return new PelletizerLogic.State(capability);
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return PelletizerShape.GETTER;
    }
    private static Fluid bindingAgent = ChemicalEnum.BindingAgent.getFluid(BlockCategoryFlags.FLUID);
    @Override
    public void onEntityCollision(IMultiblockContext<State> ctx, BlockPos posInMultiblock, Entity collided)
    {
        if(collided.level().isClientSide)
            return;
        final PelletizerLogic.State state = ctx.getState();
        final IMultiblockLevel level = ctx.getLevel();
        final AABB internalBB = new AABB(0.5F, 0f, 0.5f, 2.5f, 3f, 2.5f);
        final AABB pelletizerHopper = level.toAbsolute(internalBB);
        if(collided instanceof ItemEntity itemEntity)
        {
            if (collided.getBoundingBox().intersects(pelletizerHopper)) {
                ItemStack stack = itemEntity.getItem();
                if(stack.isEmpty())
                    return;

                stack = stack.copy();
                if(insertItemToInventory(stack, state, level.getRawLevel(), true))
                {
                    if(insertItemToInventory(stack,state, level.getRawLevel(), false))
                        ctx.markDirtyAndSync();
                    if(stack.getCount() <= 0)
                        itemEntity.discard();
                    else
                    {
                        stack.shrink(1);
                        itemEntity.setItem(stack);
                    }
                }
            }
        }
    }

    private static boolean isInInput(BlockPos posInMultiblock, boolean allowMiddleLayer) {
        if (posInMultiblock.getY() != 2 && (!allowMiddleLayer || posInMultiblock.getY() != 1)) {
            return false;
        } else {
            return posInMultiblock.getX() > 0 && posInMultiblock.getX() < 4;
        }
    }

    private static boolean insertItemToInventory(ItemStack stack, State state, Level level, boolean simulate)
    {
        if(PelletizerRecipe.findRecipe(level, stack) == null) return false;
        ItemStack remaining = state.insertionHandler.getValue().insertItem(0, new ItemStack(stack.getItem()) , simulate);
        return remaining.isEmpty();
    }

    @Override
    public <T> LazyOptional<T> getCapability(IMultiblockContext<State> ctx, CapabilityPosition position, Capability<T> cap)
    {
        final PelletizerLogic.State state = ctx.getState();
        if(cap == ForgeCapabilities.ENERGY)
        {
            if((position.side()==null || ENERGY_INPUTS.contains(position))) return state.energyCap.cast(ctx);
        }
        if(cap == ForgeCapabilities.FLUID_HANDLER)
        {
            if(FLUID_INPUT_CAP.equals(position))
            {
                return state.fInputCap.cast(ctx);
            }
        }
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return state.insertionHandler.cast(ctx);
        }
        return LazyOptional.empty();
    }

    @Nullable
    @Override
    public List<Component> getOverlayText(State state, Player player, boolean b)
    {
        if(state == null) return null;
        if(!state.tank.getFluid().getFluid().equals(ChemicalEnum.BindingAgent.getFluid(BlockCategoryFlags.FLUID)))
        {
            ItemStack stack = state.inventory.getStackInSlot(0);
            Component component = stack.isEmpty() ? Component.empty() : Component.literal(stack.getHoverName().getString() + "x" + stack.getCount());
            return List.of(Component.literal("No Binding Agent Available").withStyle(ChatFormatting.RED), component);
        }
        if(Utils.isFluidRelatedItemStack(player.getItemInHand(InteractionHand.MAIN_HAND)))
            return List.of(TextUtils.formatFluidStack(state.tank.getFluid()));
        return null;
    }

    public static class State implements IMultiblockState, ProcessContextInWorld<PelletizerRecipe>
    {
        public final AveragingEnergyStorage energy = new AveragingEnergyStorage(ENERGY_CAPACITY);
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();

        public final FluidTank tank = new FluidTank(TANK_VOLUME);
        private final StoredCapability<IFluidHandler> fInputCap;

        private final StoredCapability<IEnergyStorage> energyCap;
        private final StoredCapability<IItemHandler> insertionHandler;
        public final SlotwiseItemHandler inventory;
        private final DroppingMultiblockOutput output;
        private final MultiblockProcessor<PelletizerRecipe, ProcessContextInWorld<PelletizerRecipe>> processor;
        private float rotation;
        private boolean renderAsActive;
        public State(IInitialMultiblockContext<State> ctx){
            final Supplier<@Nullable Level> levelGetter = ctx.levelSupplier();
            final Runnable markDirty = ctx.getMarkDirtyRunnable();
            final Runnable sync = ctx.getSyncRunnable();
            Runnable changedAndSync = () -> {
                markDirty.run();
                sync.run();
            };
            this.inventory = new SlotwiseItemHandler(List.of(
                    new IOConstraint(true, i -> PelletizerRecipe.findRecipe(levelGetter.get(), i) != null),
                    IOConstraint.OUTPUT
            ), markDirty);

            this.energyCap = new StoredCapability<>(this.energy);
            this.output = new DroppingMultiblockOutput(OUTPUT_POS, ctx);
            this.processor = new MultiblockProcessor<>(64, 0, 8, ctx.getMarkDirtyRunnable(), PelletizerRecipe.RECIPES::getById);

            this.insertionHandler = new StoredCapability<>(inventory);
            this.rotation = 0;
            this.fInputCap = new StoredCapability<>(new ArrayFluidHandler(tank, true, true, changedAndSync));
        }

        @Override
        public SlotwiseItemHandler getInventory()
        {
            return inventory;
        }

        @Override
        public void readSaveNBT(CompoundTag nbt){
            this.tank.readFromNBT(nbt.getCompound("tank"));
            this.energy.deserializeNBT(nbt.get("energy"));
            this.processor.fromNBT(nbt.get("processor"), MultiblockProcessInWorld::new);
            this.inventory.deserializeNBT(nbt.getCompound("inventory"));
        }

        @Override
        public void doProcessOutput(ItemStack result, IMultiblockLevel level)
        {
            output.insertOrDrop(result, level);
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt){
            nbt.put("tank", this.tank.writeToNBT(new CompoundTag()));
            nbt.put("energy", energy.serializeNBT());
            nbt.put("processor", processor.toNBT());
            nbt.put("inventory", inventory.serializeNBT());
        }

        @Override
        public void writeSyncNBT(CompoundTag nbt)
        {
            writeSaveNBT(nbt);
            nbt.putBoolean("renderActive", renderAsActive);
        }

        @Override
        public void readSyncNBT(CompoundTag nbt)
        {
            readSaveNBT(nbt);
            renderAsActive = nbt.getBoolean("renderActive");
        }

        public float getRotation()
        {
            return rotation;
        }

        public boolean shouldRenderActive()
        {
            return renderAsActive;
        }

        @Override
        public AveragingEnergyStorage getEnergy()
        {
            return energy;
        }

        public List<MultiblockProcess<PelletizerRecipe, ProcessContextInWorld<PelletizerRecipe>>> getProcessQueue() {
            return this.processor.getQueue();
        }
    }

}