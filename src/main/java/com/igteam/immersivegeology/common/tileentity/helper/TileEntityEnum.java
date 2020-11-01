package com.igteam.immersivegeology.common.tileentity.helper;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.client.menu.helper.ItemSubGroup;
import com.igteam.immersivegeology.common.IGContent;
import com.igteam.immersivegeology.common.blocks.IGBaseBlock;
import com.igteam.immersivegeology.common.blocks.IGTileBlock;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.tileentity.entities.IGCrudeForge;
import com.igteam.immersivegeology.common.tileentity.entities.IGTileEntity;
import com.igteam.immersivegeology.common.tileentity.entities.IGToolForge;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

public enum TileEntityEnum {
    TOOL_FORGE(IGToolForge.class, new IGTileBlock("tool_forge", MaterialUseType.ROCK, EnumMaterials.Marble.material).setSubGroup(ItemSubGroup.machines)),
    CRUDE_FORGE(IGCrudeForge.class, new IGTileBlock("crude_forge", MaterialUseType.ROCK, EnumMaterials.Marble.material).setSubGroup(ItemSubGroup.machines));

    final Class<? extends TileEntity> linked_entity;
    final IGBaseBlock linked_block;

    TileEntityEnum(Class<? extends TileEntity> linkedEntity,IGBaseBlock linkedBlock) {
        this.linked_entity = linkedEntity;
        this.linked_block = linkedBlock;
    }

    public Class<? extends TileEntity> getTile() {
        return linked_entity;
    }

    public IGBaseBlock getBlock() {
        return linked_block;
    }
}
