package com.igteam.immersive_geology.common.block;

import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.common.block.helpers.BlockMaterialType;
import com.igteam.immersive_geology.common.block.helpers.IGBlockType;
import com.igteam.immersive_geology.common.item.IGBlockItem;
import javafx.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;

public class BlockBase extends Block implements IGBlockType {

    protected final Item itemBlock;
    protected final MaterialUseType blockUseType;
    protected final String holder_name;
    protected Map<BlockMaterialType, Material> blockMaterialData = new HashMap<>();

    public BlockBase(String registryName, Material material, MaterialUseType useType) {
        this(registryName, material, useType, material.getMaterialBlockProperties());
    }

    public BlockBase(String registryName, Material material, MaterialUseType useType, Properties properties) {
        super(properties);
        this.setRegistryName(registryName.toLowerCase());
        this.itemBlock = new IGBlockItem(this, useType.getSubgroup(), material);
        itemBlock.setRegistryName(registryName.toLowerCase());
        blockMaterialData.put(BlockMaterialType.BASE_MATERIAL, material);
        blockUseType = useType;
        holder_name = registryName.toLowerCase();
    }

    @Override
    public Item asItem() {
        return itemBlock;
    }

    @Override
    public Block getSelf() {
        return this;
    }

    @Override
    public String getHolderName(){
        return holder_name;
    }

    @Override
    public MaterialUseType getBlockUseType() {
        return blockUseType;
    }

    @Override
    public Material getMaterial(BlockMaterialType type) {
        return blockMaterialData.get(type);
    }
}
