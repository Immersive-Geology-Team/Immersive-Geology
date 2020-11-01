package com.igteam.immersivegeology.common.blocks;


import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.client.menu.helper.ItemSubGroup;
import com.igteam.immersivegeology.common.IGContent;
import com.igteam.immersivegeology.common.tileentity.entities.IGToolForge;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.state.IProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class IGTileBlock extends IGBaseBlock {

    TileEntity linked_entity;

    public IGTileBlock(String name, MaterialUseType subtype, Material... materials) {
        super("tile_"+name, Properties.create(subtype.getMaterial()), null, subtype.getSubGroup());
        this.itemBlock = new IGBlockItem(this, new Item.Properties().group(ImmersiveGeology.IG_ITEM_GROUP), ItemSubGroup.machines);
        this.itemBlock.setRegistryName(this.name);
    }


    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return linked_entity;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
}
