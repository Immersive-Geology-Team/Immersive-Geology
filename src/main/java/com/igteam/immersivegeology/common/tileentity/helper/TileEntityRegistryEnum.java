package com.igteam.immersivegeology.common.tileentity.helper;

import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.blocks.IGBaseBlock;
import com.igteam.immersivegeology.common.blocks.IGTileBlock;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.tileentity.entities.CrudeForgeTileEntity;
import com.igteam.immersivegeology.common.tileentity.entities.ToolForgeTileEntity;
import net.minecraft.tileentity.TileEntity;

public enum TileEntityRegistryEnum {
    TOOL_FORGE(ToolForgeTileEntity.class, new IGTileBlock("tool_forge", MaterialUseType.ROCK, EnumMaterials.Marble.material)),
    CRUDE_FORGE(CrudeForgeTileEntity.class, new IGTileBlock("crude_forge", MaterialUseType.ROCK, EnumMaterials.Marble.material));

    final Class<? extends TileEntity> linked_entity;
    final IGTileBlock linked_block;

    TileEntityRegistryEnum(Class<? extends TileEntity> linkedEntity, IGTileBlock linkedBlock) {
        this.linked_entity = linkedEntity;
        this.linked_block = linkedBlock;

        this.linked_block.setLinkedEntity(linked_entity);
    }

    public Class<? extends TileEntity> getTile() {
        return linked_entity;
    }

    public IGBaseBlock getBlock() {
        return linked_block;
    }
}
