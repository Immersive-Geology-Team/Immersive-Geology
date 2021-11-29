package com.igteam.immersive_geology.common.block.tileentity;

import blusunrize.immersiveengineering.api.IEEnums;
import blusunrize.immersiveengineering.api.client.IModelOffsetProvider;
import blusunrize.immersiveengineering.api.energy.immersiveflux.FluxStorage;
import blusunrize.immersiveengineering.client.models.IOBJModelCallback;
import blusunrize.immersiveengineering.common.blocks.IEBaseTileEntity;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.util.EnergyHelper;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.Property;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3i;

import javax.annotation.Nonnull;

/**
 * Another Apology to Immersive Engineering, Using their internal Classes isn't a good thing to do... But it does make some things easier.
 */
public class BloomeryTileEntity extends IEBaseTileEntity implements ITickableTileEntity, IEBlockInterfaces.IStateBasedDirectional, IEBlockInterfaces.IBlockBounds, IEBlockInterfaces.IHasDummyBlocks,
        IIEInventory, EnergyHelper.IIEInternalFluxHandler, IEBlockInterfaces.IInteractionObjectIE, IOBJModelCallback<BlockState>, IModelOffsetProvider {

    public BloomeryTileEntity(TileEntityType<? extends TileEntity> type) {
        super(type);
    }

    @Override
    public void readCustomNBT(CompoundNBT compoundNBT, boolean b) {

    }

    @Override
    public void writeCustomNBT(CompoundNBT compoundNBT, boolean b) {

    }

    @Override
    public BlockPos getModelOffset(BlockState blockState, Vector3i vector3i) {
        return null;
    }

    @Nonnull
    @Override
    public VoxelShape getBlockBounds(ISelectionContext iSelectionContext) {
        return null;
    }

    @Override
    public Property<Direction> getFacingProperty() {
        return null;
    }

    @Override
    public PlacementLimitation getFacingLimitation() {
        return null;
    }

    @Override
    public void placeDummies(BlockItemUseContext blockItemUseContext, BlockState blockState) {

    }

    @Override
    public void breakDummies(BlockPos blockPos, BlockState blockState) {

    }

    @Override
    public IEBlockInterfaces.IGeneralMultiblock master() {
        return null;
    }

    @Override
    public IEBlockInterfaces.IInteractionObjectIE getGuiMaster() {
        return null;
    }

    @Override
    public boolean canUseGui(PlayerEntity playerEntity) {
        return false;
    }

    @Nonnull
    @Override
    public FluxStorage getFluxStorage() {
        return null;
    }

    @Nonnull
    @Override
    public IEEnums.IOSideConfig getEnergySideConfig(Direction direction) {
        return null;
    }

    @Override
    public EnergyHelper.IEForgeEnergyWrapper getCapabilityWrapper(Direction direction) {
        return null;
    }

    @Override
    public NonNullList<ItemStack> getInventory() {
        return null;
    }

    @Override
    public boolean isStackValid(int i, ItemStack itemStack) {
        return false;
    }

    @Override
    public int getSlotLimit(int i) {
        return 0;
    }

    @Override
    public void doGraphicalUpdates() {

    }

    @Override
    public void tick() {

    }
}
