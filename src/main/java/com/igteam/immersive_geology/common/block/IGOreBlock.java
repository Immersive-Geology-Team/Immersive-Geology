package com.igteam.immersive_geology.common.block;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialStoneBase;
import com.igteam.immersive_geology.client.render.RenderLayerHandler;
import com.igteam.immersive_geology.common.block.helpers.BlockMaterialType;
import com.igteam.immersive_geology.common.block.helpers.IGBlockType;
import com.igteam.immersive_geology.common.item.IGBlockItem;
import net.minecraft.block.*;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeBlock;

import java.util.HashMap;
import java.util.Map;

public class IGOreBlock extends OreBlock implements IGBlockType, IForgeBlock, IEBlockInterfaces.IColouredBlock {

    protected final Item itemBlock;
    protected MaterialUseType blockUseType;
    protected String holder_name;
    protected Map<BlockMaterialType, Material> blockMaterialData = new HashMap<>();

    public IGOreBlock(String registryName, Material[] material, MaterialUseType useType) {
        super(Properties.create(net.minecraft.block.material.Material.ROCK).setRequiresTool().hardnessAndResistance(3.0F, 3.0F));
        this.setRegistryName(registryName.toLowerCase());
        blockUseType = useType;
        holder_name = registryName.toLowerCase();

        blockMaterialData.put(BlockMaterialType.BASE_MATERIAL, material[0]);
        blockMaterialData.put(BlockMaterialType.ORE_MATERIAL, material[1]);

        this.itemBlock = new IGBlockItem(this, useType.getSubgroup(), material[0]);
        itemBlock.setRegistryName(registryName.toLowerCase());

        RenderLayerHandler.setRenderType(this, RenderLayerHandler.RenderTypeSkeleton.TRANSLUCENT);
    }

    @Override
    public Item asItem() {
        return itemBlock;
    }

    @Override
    public boolean hasCustomBlockColours() {
        return true;
    }

    @Override
    public int getRenderColour(BlockState blockState, IBlockReader iBlockReader, BlockPos blockPos, int pass)
    {
        Material[] materials = new Material[2];
        materials[0] = getMaterial(BlockMaterialType.BASE_MATERIAL);
        materials[1] = getMaterial(BlockMaterialType.ORE_MATERIAL);

        return materials[MathHelper.clamp(pass,0,materials.length-1)].getColor(1);
     }

    @Override
    public int getHarvestLevel(BlockState state) {
        return getMaterial(BlockMaterialType.BASE_MATERIAL).getBlockHarvestLevel();
    }

    @Override
    public ToolType getHarvestTool(BlockState state) {
        return ToolType.PICKAXE;
    }

    @Override
    public Block getSelf() {
        return this;
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
        return MaterialUseType.CHUNK;
    }

    @Override
    public float maxDrops() {
        return 1;
    }

    @Override
    public float minDrops() {
        return 1;
    }
}
