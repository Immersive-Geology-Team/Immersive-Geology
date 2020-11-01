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
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;

public class IGToolForge extends IGTileEntity implements IClearable, ITickableTileEntity {
    private final NonNullList<ItemStack> inventory;

    public IGToolForge(TileEntityType<?> type) {
        super(type);
        this.inventory = NonNullList.withSize(4, ItemStack.EMPTY);
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }


    @Override
    public void tick() {
        boolean isWorldRemote = this.world.isRemote;
        if (isWorldRemote) {
            //particle effect go here
        } else {
            forgeItem();
        }
    }

    private void forgeItem(){
        HashMap<ToolUseType, IGMaterialItem> craftMap = new HashMap<ToolUseType, IGMaterialItem>();
        for(int slot_index = 0; slot_index < this.inventory.size(); ++slot_index) {
            ItemStack item = this.inventory.get(slot_index);
            if(!item.isEmpty()){
                if(item.getItem() instanceof IGMaterialItem){
                    IGMaterialItem matItem = (IGMaterialItem) item.getItem();
                    if(matItem.subtype.equals(MaterialUseType.BINDING)){
                        craftMap.put(ToolUseType.BINDING, matItem);
                    }
                    if(matItem.subtype.equals(MaterialUseType.HANDLE)){
                        craftMap.put(ToolUseType.HANDLE, matItem);
                    }
                }
            }
        }

        if(craftMap.containsKey(ToolUseType.HANDLE)){
            if(craftMap.containsKey(ToolUseType.BINDING)){
                ItemStack craftedItem = new ItemStack(IGContent.itemPickaxe);

                CompoundNBT data = new CompoundNBT();
                data.putString("handle_material", craftMap.get(ToolUseType.HANDLE).getMaterial().getName());
                data.putString("binding_material", craftMap.get(ToolUseType.BINDING).getMaterial().getName());
                craftedItem.setTag(data);

                BlockPos blockPos = this.getPos();
                InventoryHelper.spawnItemStack(this.world, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), craftedItem);
                this.clear();
                this.markState();
            }
        }

    }

    public NonNullList<ItemStack> getInventory() {
        return this.inventory;
    }

    public void read(CompoundNBT p_145839_1_) {
        super.read(p_145839_1_);
        this.inventory.clear();
        ItemStackHelper.loadAllItems(p_145839_1_, this.inventory);
    }

    public CompoundNBT write(CompoundNBT p_189515_1_) {
        this.writeItems(p_189515_1_);
        return p_189515_1_;
    }

    private CompoundNBT writeItems(CompoundNBT p_213983_1_) {
        super.write(p_213983_1_);
        ItemStackHelper.saveAllItems(p_213983_1_, this.inventory, true);
        return p_213983_1_;
    }

    public boolean addItem(ItemStack item) {
        for(int lvt_3_1_ = 0; lvt_3_1_ < this.inventory.size(); ++lvt_3_1_) {
            ItemStack lvt_4_1_ = (ItemStack) this.inventory.get(lvt_3_1_);
            if (lvt_4_1_.isEmpty()) {
                this.inventory.set(lvt_3_1_, item.split(1));
                this.markState();
                return true;
            }
        }
        return false;
    }

    private void markState() {
        this.markDirty();
        this.getWorld().notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public void func_213986_d() {
        if (!this.getWorld().isRemote) {
            InventoryHelper.dropItems(this.getWorld(), this.getPos(), this.getInventory());
        }

        this.markState();
    }
}
