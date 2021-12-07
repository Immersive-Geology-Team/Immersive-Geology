package com.igteam.immersive_geology.common.block;

import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.common.block.helpers.BlockMaterialType;
import com.igteam.immersive_geology.common.block.helpers.IGBlockType;
import com.igteam.immersive_geology.common.item.IGBlockItem;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.block.SlabBlock;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;

public class IGSlabBlock extends SlabBlock implements IGBlockType {

    protected final String holder_name;
    protected final MaterialUseType blockUseType;
    protected final MaterialUseType itemDrop;
    protected final Item itemBlock;
    protected Map<BlockMaterialType, Material> blockMaterialData = new HashMap<>();

    public IGSlabBlock(String registryName, Material material, MaterialUseType useType) {
        this(registryName, material, useType, material.getMaterialBlockProperties());
    }

    public IGSlabBlock(String registryName, Material material, MaterialUseType itemDropType, Properties properties) {
        super(properties);

        blockMaterialData.put(BlockMaterialType.BASE_MATERIAL, material);
        this.setRegistryName(registryName.toLowerCase());
        holder_name = registryName.toLowerCase();
        blockUseType = MaterialUseType.SLAB;
        this.itemDrop = itemDropType;
        this.itemBlock = new IGBlockItem(this, this, MaterialUseType.SLAB.getSubgroup(), material);
        itemBlock.setRegistryName(registryName.toLowerCase());

    }

    @Override
    public String getHolderName() {
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

    @Override
    public MaterialUseType getDropUseType() {
        return MaterialUseType.SLAB;
    }

    @Override
    public float maxDrops() {
        return 1f;
    }

    @Override
    public float minDrops() {
        return 1f;
    }

    @Override
    public Item asItem() {
        return itemBlock;
    }
}
