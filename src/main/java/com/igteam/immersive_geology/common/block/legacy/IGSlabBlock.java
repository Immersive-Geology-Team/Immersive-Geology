package com.igteam.immersive_geology.common.block.legacy;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import com.igteam.immersive_geology.legacy_api.materials.Material;
import com.igteam.immersive_geology.legacy_api.materials.MaterialUseType;
import com.igteam.immersive_geology.common.block.helpers.BlockMaterialType;
import com.igteam.immersive_geology.common.block.helpers.IGBlockType;
import com.igteam.immersive_geology.common.item.legacy.IGBlockItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import java.util.HashMap;
import java.util.Map;

public class IGSlabBlock extends SlabBlock implements IGBlockType, IEBlockInterfaces.IColouredBlock {

    protected final String holder_name;
    protected final MaterialUseType blockUseType;
    protected final MaterialUseType itemDrop;
    protected final Item itemBlock;

    private String sideTexturePath;
    private String coverTexturePath;

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
        this.itemBlock = new IGBlockItem(this, this, blockUseType.getSubgroup(), material);
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

    public  IGSlabBlock setSideTexturePath(String path)
    {
        this.sideTexturePath = path.toLowerCase();
        return this;
    }

    public  IGSlabBlock setCoverTexturePath(String path)
    {
        this.coverTexturePath = path.toLowerCase();
        return this;
    }

    public String getSideTexturePath() {
        return sideTexturePath;
    }

    public String getCoverTexturePath() {
        return coverTexturePath;
    }

    @Override
    public boolean hasCustomBlockColours() {
        return true;
    }

    @Override
    public int getRenderColour(BlockState state, IBlockReader worldIn, BlockPos pos, int tintIndex) {
        return getMaterial(BlockMaterialType.BASE_MATERIAL).getColor(0);
    }
}
