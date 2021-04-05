package com.igteam.immersive_geology.common.block;

import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.client.menu.helper.ItemSubGroup;
import com.igteam.immersive_geology.common.item.IGBlockItem;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class BlockBase extends Block {

    protected Item itemBlock;

    protected Material blockMaterial;
    protected MaterialUseType blockUseType;

    protected String holder_name;

    public BlockBase(String registryName, Material material, MaterialUseType useType) {
        super(material.getMaterialBlockProperties());
        this.setRegistryName(registryName.toLowerCase());
        this.itemBlock = new IGBlockItem(this, useType.getSubgroup(), material);
        itemBlock.setRegistryName(registryName.toLowerCase());
        blockMaterial = material;
        blockUseType = useType;
        holder_name = registryName;
    }

    @Override
    public Item asItem() {
        return itemBlock;
    }

    public String getHolderName(){
        return holder_name;
    }
}
