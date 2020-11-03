package com.igteam.immersivegeology.common.tileentity.entities;

import blusunrize.immersiveengineering.api.IEEnums;
import blusunrize.immersiveengineering.api.energy.immersiveflux.FluxStorage;
import blusunrize.immersiveengineering.client.models.IOBJModelCallback;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.util.EnergyHelper;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.materials.ToolUseType;
import com.igteam.immersivegeology.common.IGContent;
import com.igteam.immersivegeology.common.items.IGMaterialItem;
import com.igteam.immersivegeology.common.tileentity.IGRegisterTileEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IClearable;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.EnumProperty;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;
import java.util.HashMap;

public class CrudeForgeTileEntity extends IGTileEntity implements ITickableTileEntity, IEBlockInterfaces.IStateBasedDirectional, IEBlockInterfaces.IBlockBounds, IEBlockInterfaces.IHasDummyBlocks,
        IIEInventory, IEBlockInterfaces.IInteractionObjectIE, IOBJModelCallback<BlockState> {
    //THIS IS REQUIRED FOR IT TO REGISTER,
    public static TileEntityType<CrudeForgeTileEntity> TYPE;

    public static final int SLOT_FUEL = 0;
    public static final int SLOT_INPUT = 1;

    public final FluidTank tank = new FluidTank(4000)
    {
        @Override
        protected void onContentsChanged()
        {
            CrudeForgeTileEntity.this.sendSyncPacket(2);
        }

        @Override
        public boolean isFluidValid(FluidStack fluid)
        {
            return true;
        }
    };
    public CrudeForgeTileEntity() {
        super(TYPE);
    }

    @Override
    public void tick() {

    }

    @Override
    public void readCustomNBT(CompoundNBT compoundNBT, boolean b) {

    }

    @Override
    public void writeCustomNBT(CompoundNBT compoundNBT, boolean b) {

    }

    @Override
    public EnumProperty<Direction> getFacingProperty() {
        return null;
    }

    @Override
    public PlacementLimitation getFacingLimitation() {
        return null;
    }

    @Override
    public VoxelShape getBlockBounds(@Nullable ISelectionContext iSelectionContext) {
        return null;
    }

    @Override
    public void placeDummies(BlockItemUseContext blockItemUseContext, BlockState blockState) {

    }

    @Override
    public void breakDummies(BlockPos blockPos, BlockState blockState) {

    }

    @Nullable
    @Override
    public IEBlockInterfaces.IGeneralMultiblock master() {
        return null;
    }

    @Nullable
    @Override
    public IEBlockInterfaces.IInteractionObjectIE getGuiMaster() {
        return null;
    }

    @Override
    public boolean canUseGui(PlayerEntity playerEntity) {
        return false;
    }

    @Nullable
    @Override
    public NonNullList<ItemStack> getInventory() {
        return null;
    }

    @Override
    public boolean isStackValid(int i, ItemStack itemStack) {
        return true;
    }

    @Override
    public int getSlotLimit(int i) {
        return 0;
    }

    @Override
    public void doGraphicalUpdates(int i) {

    }

    protected void sendSyncPacket(int type) {

    }
}
