package com.igteam.immersivegeology.common.blocks.machines;

import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.util.IGRegistryGrabber;
import com.igteam.immersivegeology.common.IGContent;
import com.igteam.immersivegeology.common.blocks.IGTileBlock;
import com.igteam.immersivegeology.common.items.IGMaterialResourceItem;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.tileentity.entities.ToolForgeTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class IGToolForge extends IGTileBlock {
    public IGToolForge() {
        super("tool_forge", MaterialUseType.ROCK, EnumMaterials.Marble.material);
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
       TileEntity entity = world.getTileEntity(pos);
       if(entity instanceof ToolForgeTileEntity){
           ToolForgeTileEntity tool_forge = ((ToolForgeTileEntity) entity);
           ItemStack heldItem = player.getHeldItem(Hand.MAIN_HAND);

           if(!tool_forge.canForge()) {
               boolean successful_input = false;

               if(!heldItem.isEmpty()) {
                   Item input_item = heldItem.getItem();
                   if(input_item instanceof IGMaterialResourceItem) {
                       IGMaterialResourceItem ig_input_item = (IGMaterialResourceItem) input_item;
                       MaterialUseType useType = ig_input_item.subtype;
                       int chosen_index = 0;
                       if(useType == MaterialUseType.INGOT) {
                           chosen_index = 0;
                       }
                       if(useType == MaterialUseType.ROD){
                           chosen_index = 1;
                       }
                       if(useType == MaterialUseType.WIRE || useType == MaterialUseType.ROUGH_WIRE){
                           chosen_index = 2;
                       }

                       tool_forge.setSlot(new ItemStack(ig_input_item, 1), chosen_index);
                       successful_input = true;
                   }
               }


               if(successful_input) {
                   player.setHeldItem(Hand.MAIN_HAND, new ItemStack(heldItem.getItem(), heldItem.getCount() - 1));
               }
               return successful_input;
           } else {
              return createPickaxe(world, pos, player);
           }
       }
       return false;
    }

    public boolean createPickaxe(World world, BlockPos pos, PlayerEntity player) {
        TileEntity entity = world.getTileEntity(pos);
        if(entity instanceof ToolForgeTileEntity) {
            ToolForgeTileEntity tool_forge = ((ToolForgeTileEntity) entity);

            int head_index = 0;
            int handle_index = 1;
            int binding_index = 2;
            int tip_index = 3;

            ItemStack head_stack = tool_forge.getItem(head_index);
            Item item_head = head_stack.getItem();

            IGMaterialResourceItem head_material_item = (IGMaterialResourceItem) item_head;
            Material head_material = head_material_item.materials[0];
            MaterialUseType head_type = head_material_item.subtype;

            ItemStack handle_stack = tool_forge.getItem(handle_index);
            Item item_handle = handle_stack.getItem();

            IGMaterialResourceItem handle_material_item = (IGMaterialResourceItem) item_handle;
            Material handle_material = handle_material_item.materials[0];
            MaterialUseType handle_type = handle_material_item.subtype;

            ItemStack binding_stack = tool_forge.getItem(binding_index);
            Item item_binding = binding_stack.getItem();

            IGMaterialResourceItem binding_material_item = (IGMaterialResourceItem) item_binding;
            Material binding_material = binding_material_item.materials[0];
            MaterialUseType binding_type = binding_material_item.subtype;


            ItemStack pick = new ItemStack(IGContent.itemPickaxe);
            ItemNBTHelper.putString(pick, "head_material", head_material.getName());
            ItemNBTHelper.putString(pick, "handle_material", handle_material.getName());
            ItemNBTHelper.putString(pick, "binding_material", binding_material.getName());

            world.addEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), pick));
            tool_forge.clear();
            return true;
        } else {
            return false;
        }
    }
}
