package com.igteam.immersive_geology.common.block.blocks;

import com.igteam.immersive_geology.common.item.IGGenericBlockItem;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import igteam.immersive_geology.block.IGBlockType;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.helper.MaterialTexture;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class IGStairsBlock extends StairsBlock implements IGBlockType {

    private final Map<MaterialTexture, MaterialInterface> materialMap = new HashMap<>();
    private final IGGenericBlockItem itemBlock;
    private String parentTexture = "";


    public IGStairsBlock(MaterialInterface<?> m) {
        //Not sure, if works fine for sheetmetal
        super( () ->m.getBlock(BlockPattern.storage).getDefaultState(),
                Properties.create(Material.ROCK, MaterialColor.STONE));
        this.materialMap.put(MaterialTexture.base, m);
        this.itemBlock = new IGGenericBlockItem(this, m, ItemPattern.block_item);
    }

    @Override
    public int getColourForIGBlock(int pass) {
        return materialMap.get(MaterialTexture.values()[pass]).getColor(BlockPattern.stairs);
    }

    @Override
    public Item asItem() {
        return itemBlock;
    }


    @Nonnull
    @Override
    public MaterialPattern getPattern() {
        return BlockPattern.stairs;
    }

    @Override
    public String getHolderKey() {
        StringBuilder data = new StringBuilder();

        for (MaterialTexture t : MaterialTexture.values()) {
            if (materialMap.containsKey(t)) {
                data.append("_").append(materialMap.get(t).getName());
            }
        }

        return getPattern() + data.toString();
    }

    @Override
    public Collection<MaterialInterface> getMaterials() {
        return materialMap.values();
    }

    @Override
    public Block getBlock() {
        return this;
    }

    public MaterialInterface getMaterial(MaterialTexture t){
        return materialMap.get(t);
    }

    public void finalizeData(){
        itemBlock.finalizeData();
        setRegistryName(IGRegistrationHolder.getRegistryKey(this));
    }
}

