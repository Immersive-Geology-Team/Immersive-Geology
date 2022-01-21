package com.igteam.immersive_geology.common.block;

import com.igteam.immersive_geology.common.item.IGGenericBlockItem;
import com.igteam.immersive_geology.common.item.IGGenericItem;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.helper.MaterialTexture;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.menu.helper.IGItemGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.apache.logging.log4j.Level;

import java.util.*;

public class IGGenericBlock extends Block {

    private final Map<MaterialTexture, MaterialInterface> materialMap = new HashMap<>();

    private final BlockPattern pattern;
    private final IGGenericBlockItem itemBlock;

    public IGGenericBlock(MaterialInterface m, BlockPattern p) {
        super(Properties.of(Material.STONE, MaterialColor.STONE));
        this.pattern = p;
        this.materialMap.put(MaterialTexture.base, m);
        this.itemBlock = new IGGenericBlockItem(this, m, ItemPattern.block_item);
    }

    @Override
    public Item asItem() {
        return itemBlock;
    }

    public BlockPattern getPattern(){
        return this.pattern;
    }

    public void addMaterial(MaterialInterface material, MaterialTexture t){
        materialMap.put(t, material);
        itemBlock.addMaterial(t, material);
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

        return this.pattern + data.toString();
    }

    public void finalizeData(){
        itemBlock.finalizeData();
        setRegistryName(IGRegistrationHolder.getRegistryKey(this));
    }

    public MaterialInterface getMaterial(MaterialTexture t){
        return materialMap.get(t);
    }
}
