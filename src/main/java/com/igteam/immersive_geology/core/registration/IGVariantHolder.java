package com.igteam.immersive_geology.core.registration;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialStoneBase;
import com.igteam.immersive_geology.common.block.BlockBase;
import com.igteam.immersive_geology.common.block.blocks.IGOreBlock;
import com.igteam.immersive_geology.common.item.IGOreItem;
import com.igteam.immersive_geology.common.item.ItemBase;
import org.apache.logging.log4j.Logger;

public class IGVariantHolder {

    private static Logger log = ImmersiveGeology.getNewLogger();

    public static void createVariants(Material mat) {
        log.info("Creating " +  mat.getName().toUpperCase() + " Variants");
        for(MaterialUseType type : MaterialUseType.values()) {
            if(shouldGenerate(type)) {
                if (mat.hasSubtype(type)) {
                    log.info("Registering " + type.getName().toUpperCase());
                    if (type.isBlock()) {
                        createBlockVariants(mat, type);
                    } else {
                        createItemVariants(mat, type);
                    }
                }
            }
        }
    }

    private static boolean shouldGenerate(MaterialUseType type){
        return (type != MaterialUseType.ORE_STONE) && (type != MaterialUseType.ORE_BIT) && (type != MaterialUseType.ORE_CHUNK);
    }

    private static void createItemVariants(Material material, MaterialUseType type){
        switch (type){
            case ROCK_BIT:
            case CHUNK:
                registerOreItem(material, type);
                registerBasicItem(material, type);
            default:
                registerBasicItem(material, type);
        }
    }

    private static void registerOreItem(Material materialBase, MaterialUseType type){
        Material materialOre = null;
        for(MaterialEnum container : MaterialEnum.values()){
            materialOre = container.getMaterial();
            if(materialOre != null){
                if(materialOre.hasSubtype(MaterialUseType.ORE_STONE)){
                    String holder_key = type.getName() + "_" + materialBase.getName() + "_" + materialOre.getName();
                    log.info("Registering special type: " + holder_key);
                    IGOreItem item = new IGOreItem(holder_key, new Material[]{materialBase, materialOre}, type);
                    IGRegistrationHolder.registeredIGItems.put(holder_key, item);
                }
            }
        }
    }

    private static void registerBasicItem(Material material, MaterialUseType type){
        String holder_key = type.getName()+"_"+material.getName();
        ItemBase item = new ItemBase(holder_key, material, type);
        IGRegistrationHolder.registeredIGItems.put(holder_key, item);
    }

    private static void registerOreBlock(Material materialBase){
        Material materialOre = null;
        MaterialUseType type = MaterialUseType.ORE_STONE;
        for(MaterialEnum container : MaterialEnum.values()) {
            materialOre = container.getMaterial();
            if (materialOre != null) {
                if (materialOre.hasSubtype(MaterialUseType.ORE_STONE)) {
                    String holder_key = type.getName() + "_" + materialBase.getName() + "_" + materialOre.getName();
                    log.info("Registering special type: " + holder_key);
                    IGOreBlock block = new IGOreBlock(holder_key, new Material[]{materialBase, materialOre}, type);
                    IGRegistrationHolder.registeredIGBlocks.put(holder_key, block);
                    IGRegistrationHolder.registeredIGItems.put(holder_key, block.asItem());
                }
            }
        }
    }

    private static void createBlockVariants(Material material, MaterialUseType type){
        switch(type){
            case STONE:
               registerOreBlock(material);
               registerBasicBlock(material, type);
            default:
                registerBasicBlock(material, type);
        }
    }

    private static void registerBasicBlock(Material material, MaterialUseType type){
        String holder_key = type.getName() + "_" + material.getName();
        BlockBase block = new BlockBase(holder_key, material, type);
        IGRegistrationHolder.registeredIGBlocks.put(holder_key, block);
        IGRegistrationHolder.registeredIGItems.put(holder_key, block.asItem());
    }

}
