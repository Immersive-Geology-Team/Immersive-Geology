package com.igteam.immersivegeology.common.blocks;


import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.client.menu.helper.ItemSubGroup;
import com.igteam.immersivegeology.common.util.IGLogger;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class IGTileBlock extends IGBaseBlock {

    protected Class<? extends TileEntity> linked_entity;

    public IGTileBlock(String name, MaterialUseType subtype, Material... materials) {
        super("tile_"+name, Properties.create(subtype.getMaterial()), null, ItemSubGroup.machines);
        this.itemBlock = new IGBlockItem(this, new Item.Properties().group(ImmersiveGeology.IG_ITEM_GROUP), ItemSubGroup.machines);
        this.itemBlock.setRegistryName(this.name);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        try {
            return linked_entity.newInstance();
        } catch (Exception e){
            IGLogger.error("Failed to link TileEntity("+linked_entity.getName()+") to " + this.name + " due to " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    public IGTileBlock setLinkedEntity(Class<? extends TileEntity> entity){
        this.linked_entity = entity;
        return this;
    }
}
