package com.igteam.immersive_geology.common.block.legacy;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import com.igteam.immersive_geology.legacy_api.materials.Material;
import com.igteam.immersive_geology.legacy_api.materials.MaterialUseType;
import com.igteam.immersive_geology.common.block.helpers.BlockMaterialType;
import com.igteam.immersive_geology.common.block.helpers.IGBlockType;
import com.igteam.immersive_geology.common.item.legacy.IGBlockItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import java.util.HashMap;
import java.util.Map;

public class IGBaseBlock extends Block implements IGBlockType, IEBlockInterfaces.IColouredBlock {

    protected final Item itemBlock;
    protected final MaterialUseType blockUseType;
    protected final String holder_name;
    protected Map<BlockMaterialType, Material> blockMaterialData = new HashMap<>();

    protected final MaterialUseType itemDrop;
    protected final Float[] drops;

    public IGBaseBlock(String registryName, Material material, MaterialUseType useType) {
        this(registryName, material, useType, material.getMaterialBlockProperties(), useType, 1f, 1f);
    }

    public IGBaseBlock(String registryName, Material material, MaterialUseType useType, MaterialUseType dropType, float min_drop, float max_drop) {
        this(registryName, material, useType, material.getMaterialBlockProperties(), dropType, min_drop, max_drop);
    }

    public IGBaseBlock(String registryName, Material material, MaterialUseType useType, Properties properties, MaterialUseType itemDropType, float min_drop, float max_drop) {
        super(properties);
        this.setRegistryName(registryName.toLowerCase());
        blockMaterialData.put(BlockMaterialType.BASE_MATERIAL, material);
        blockUseType = useType;
        holder_name = registryName.toLowerCase();
        this.itemDrop = itemDropType;
        this.drops = new Float[]{min_drop, max_drop};
        this.itemBlock = new IGBlockItem(this, this, useType.getSubgroup(), material);
        itemBlock.setRegistryName(registryName.toLowerCase());
    }

    @Override
    public Item asItem() {
        return itemBlock;
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

    @Override
    public MaterialUseType getDropUseType() {
        return itemDrop;
    }

    @Override
    public float maxDrops() {
        return drops[1];
    }

    @Override
    public float minDrops() {
        return drops[0];
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
