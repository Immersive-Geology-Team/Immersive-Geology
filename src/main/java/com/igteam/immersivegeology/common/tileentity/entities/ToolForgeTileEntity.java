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
import net.minecraft.inventory.IInventory;
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
import net.minecraftforge.common.ToolType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class ToolForgeTileEntity extends IGTileEntity implements IClearable {
    //Required for successful registry
    public static TileEntityType<ToolForgeTileEntity> TYPE;

    public ToolForgeTileEntity() {
        super(TYPE);
        this.head_slot = ItemStack.EMPTY;
        this.binding_slot = ItemStack.EMPTY;
        this.handle_slot = ItemStack.EMPTY;
        this.tip_slot = ItemStack.EMPTY;
    }

    private ItemStack head_slot;
    private ItemStack binding_slot;
    private ItemStack handle_slot;
    private ItemStack tip_slot;

    @Override
    public void readCustomNBT(CompoundNBT compoundNBT, boolean b) {

    }

    @Override
    public void writeCustomNBT(CompoundNBT compoundNBT, boolean b) {

    }

    @Override
    public void read(CompoundNBT nbt) {
        super.read(nbt);
        for(int i = 0; i < 4; i ++) {
            if (nbt.contains("ToolItem"+getPartName(i), 10)) {
                this.setSlot(ItemStack.read(nbt.getCompound("ToolItem"+getPartName(i))), i);
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        super.write(nbt);
        for(int i = 0; i < 4; i ++) {
            if (!this.getItem(i).isEmpty()) {
                nbt.put("ToolItem"+getPartName(i), this.getItem(i).write(new CompoundNBT()));
            }
        }
        return nbt;
    }

    private String getPartName(int index) {
        switch (index){
            case 1:
                return "Handle";
            case 2:
                return "Binding";
            case 3:
                return "Tip";
            default:
            case 0:
                return "Head";
        }
    }

    @Override
    public void clear() {
        for(int i = 0; i < 4; i ++) {
            this.setSlot(ItemStack.EMPTY, i);
        }
    }

    public ItemStack getItem(int index){
        switch (index){
            case 1:
                return this.handle_slot;
            case 2:
                return this.binding_slot;
            case 3:
                return this.tip_slot;
            default:
            case 0:
                return this.head_slot;
        }
    }

    public void setSlot(ItemStack item, int index){
        switch (index){
            case 1:
                this.handle_slot = item;
                break;
            case 2:
                this.binding_slot = item;
                break;
            case 3:
                this.tip_slot = item;
                break;
            default:
            case 0:
                this.head_slot = item;
                break;
        }
        this.markDirty();
    }

    public boolean canForge(){
        return !this.head_slot.isEmpty() && !this.handle_slot.isEmpty() && !this.binding_slot.isEmpty();
    }

    public boolean hasTipMaterial(){
        return !this.tip_slot.isEmpty();
    }
}
