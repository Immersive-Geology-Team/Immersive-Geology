package com.igteam.immersivegeology.common.tileentity.entities;

import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.materials.ToolUseType;
import com.igteam.immersivegeology.api.toolsystem.Tooltypes;
import com.igteam.immersivegeology.api.util.IGRegistryGrabber;
import com.igteam.immersivegeology.common.IGContent;
import com.igteam.immersivegeology.common.items.IGBaseItem;
import com.igteam.immersivegeology.common.items.IGMaterialItem;
import com.igteam.immersivegeology.common.items.IGMaterialResourceItem;
import com.igteam.immersivegeology.common.items.tools.IGToolPickaxe;
import com.igteam.immersivegeology.common.tileentity.IGRegisterTileEntityTypes;
import net.minecraft.command.arguments.NBTCompoundTagArgument;
import net.minecraft.inventory.IClearable;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class ToolForgeTileEntity extends IGTileEntity implements IClearable, ITickableTileEntity {
    //Required for successful registry
    public static TileEntityType<CrudeForgeTileEntity> TYPE;

    public ToolForgeTileEntity() {
        super(TYPE);
    }

    @Override
    public void readCustomNBT(CompoundNBT compoundNBT, boolean b) {

    }

    @Override
    public void writeCustomNBT(CompoundNBT compoundNBT, boolean b) {

    }

    @Override
    public void clear() {

    }

    @Override
    public void tick() {
    }
}
