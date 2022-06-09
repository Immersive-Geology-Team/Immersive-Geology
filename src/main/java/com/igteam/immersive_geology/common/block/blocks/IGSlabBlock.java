package com.igteam.immersive_geology.common.block.blocks;

import com.igteam.immersive_geology.client.render.RenderLayerHandler;
import com.igteam.immersive_geology.common.item.IGGenericBlockItem;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import igteam.immersive_geology.block.IGBlockType;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.helper.MaterialTexture;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class IGSlabBlock extends SlabBlock implements IGBlockType {
    private final Map<MaterialTexture, MaterialInterface> materialMap = new HashMap<>();
    private final IGGenericBlockItem itemBlock;

    public IGSlabBlock(MaterialInterface<?> m) {
        super(Properties.create(Material.ROCK, MaterialColor.STONE));
        this.materialMap.put(MaterialTexture.base, m);
        this.itemBlock = new IGGenericBlockItem(this, m, ItemPattern.block_item);

        RenderLayerHandler.setRenderType(this, RenderLayerHandler.RenderTypeSkeleton.TRANSLUCENT);
    }

    @Override
    public Item asItem() {
        return itemBlock;
    }

    public BlockPattern getPattern(){
        return BlockPattern.slab;
    }

    public void addMaterial(MaterialInterface material, MaterialTexture t){
        materialMap.put(t, material);
        itemBlock.addMaterial(t, material);
    }

    @Override
    public int getColourForIGBlock(int pass) {
        return materialMap.get(MaterialTexture.values()[pass]).getColor(BlockPattern.slab);
    }

    public Collection<MaterialInterface> getMaterials() {
        return materialMap.values();
    }

    public String getHolderKey(){
        StringBuilder data = new StringBuilder();

        for(MaterialTexture t : MaterialTexture.values()){
            if (materialMap.containsKey(t)) {
                data.append("_").append(materialMap.get(t).getName());
            }
        }

        return getPattern() + data.toString();
    }

    @Override
    public Block getBlock() {
        return this;
    }

    @Override
    public boolean hasCustomBlockColours() {
        return true;
    }

    public void finalizeData(){
        itemBlock.finalizeData();
        setRegistryName(IGRegistrationHolder.getRegistryKey(this));
    }

    public MaterialInterface getMaterial(MaterialTexture t){
        return materialMap.get(t);
    }
}
