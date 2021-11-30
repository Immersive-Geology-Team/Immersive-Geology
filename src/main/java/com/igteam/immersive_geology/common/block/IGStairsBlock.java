package com.igteam.immersive_geology.common.block;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.common.block.helpers.BlockMaterialType;
import com.igteam.immersive_geology.common.block.helpers.IGBlockType;
import com.igteam.immersive_geology.common.item.IGBlockItem;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import java.util.HashMap;
import java.util.Map;

public class IGStairsBlock extends StairsBlock implements IGBlockType,IEBlockInterfaces.IColouredBlock {

    protected final String holder_name;
    protected final MaterialUseType blockUseType;
    protected final MaterialUseType itemDrop;
    protected final Item itemBlock;
    protected Map<BlockMaterialType, Material> blockMaterialData = new HashMap<>();

    public IGStairsBlock(String registryName, Material material, MaterialUseType useType) {
        this(registryName, material, useType, material.getMaterialBlockProperties());
    }
    public IGStairsBlock(String registryName, Material material, MaterialUseType itemDropType, Properties properties) {

        super(() -> IGRegistrationHolder.getBlockByMaterial(MaterialUseType.SHEETMETAL, material).getDefaultState(),
                properties);
        blockMaterialData.put(BlockMaterialType.BASE_MATERIAL, material);
        this.setRegistryName(registryName.toLowerCase());
        holder_name = registryName.toLowerCase();
        blockUseType = MaterialUseType.SHEETMETAL_STAIRS;
        this.itemDrop = itemDropType;
        this.itemBlock = new IGBlockItem(this, this, MaterialUseType.SHEETMETAL_STAIRS.getSubgroup(), material);
        itemBlock.setRegistryName(registryName.toLowerCase());

    }



    public String getHolderName(){
        return holder_name;
    }

    public Material getMaterial(BlockMaterialType type) {
        return blockMaterialData.get(type);
    }

    public MaterialUseType getBlockUseType() {
        return blockUseType;
    }

    @Override
    public boolean hasCustomBlockColours() {
        return true;
    }

    @Override
    public int getRenderColour(BlockState state, IBlockReader worldIn, BlockPos pos, int tintIndex) {
        return getMaterial(BlockMaterialType.BASE_MATERIAL).getColor(0);
    }

    public MaterialUseType getDropUseType() {
        return MaterialUseType.SHEETMETAL_STAIRS;
    }

    public float maxDrops () { return  1f; }
    public float minDrops () { return  1f; }

    @Override
    public Item asItem() {
        return itemBlock;
    }
}
