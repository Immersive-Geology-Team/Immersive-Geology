package com.igteam.immersivegeology.common.tileentity.entities;

import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.materials.ToolUseType;
import com.igteam.immersivegeology.common.IGContent;
import com.igteam.immersivegeology.common.items.IGMaterialItem;
import com.igteam.immersivegeology.common.tileentity.IGRegisterTileEntityTypes;
import net.minecraft.inventory.IClearable;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;

public class CrudeForgeTileEntity extends IGTileEntity implements IClearable, ITickableTileEntity {
    //THIS IS REQUIRED FOR IT TO REGISTER,
    public static TileEntityType<CrudeForgeTileEntity> TYPE;

    public CrudeForgeTileEntity() {
        super(TYPE);
    }

    @Override
    public void clear() {

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
}
