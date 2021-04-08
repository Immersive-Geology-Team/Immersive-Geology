package com.igteam.immersive_geology.core.registration;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialStoneBase;
import com.igteam.immersive_geology.common.block.BlockBase;
import com.igteam.immersive_geology.common.block.blocks.IGOreBlock;
import com.igteam.immersive_geology.common.item.ItemBase;
import org.apache.logging.log4j.Logger;

public class IGVariantHolder {

    private static Logger log = ImmersiveGeology.getNewLogger();

    public static void createVariants(Material mat) {
        log.info("Creating " +  mat.getName().toUpperCase() + " Variants");
        for(MaterialUseType type : MaterialUseType.values()) {
            if(mat.hasSubtype(type)) {
                log.info("Registering " + type.getName().toUpperCase());
                if(type.isBlock()) {
                    if(mat.hasSubtype(MaterialUseType.ORE_STONE) && !mat.hasSubtype(MaterialUseType.STONE)){
                        for(MaterialEnum base : MaterialEnum.values()) {
                            if(base.getMaterial().hasSubtype(MaterialUseType.STONE)) {
                                createBlockVariants(base.getMaterial(), mat, type);
                            }
                        }
                    }
                    createBlockVariants(mat, type);
                } else {
                    createItemVariants(mat, type);
                }
            }
        }
    }

    private static void createItemVariants(Material material, MaterialUseType type){
        String holder_key = type.getName()+"_"+material.getName();
        ItemBase item = new ItemBase(holder_key, material, type);
        IGRegistrationHolder.registeredIGItems.put(holder_key, item);
    }

    private static void createBlockVariants(Material materialBase, Material materialOre, MaterialUseType type){
            String holder_key = type.getName() + "_" + materialOre.getName() + "_" + materialBase.getName();
            log.info("Registering special type: " + holder_key);
            IGOreBlock block = new IGOreBlock(holder_key, new Material[]{materialBase, materialOre}, type);
            IGRegistrationHolder.registeredIGBlocks.put(holder_key, block);
            IGRegistrationHolder.registeredIGItems.put(holder_key, block.asItem());
    }
    private static void createBlockVariants(Material material, MaterialUseType type){
        String holder_key = type.getName() + "_" + material.getName();
        BlockBase block = new BlockBase(holder_key, material, type);
        IGRegistrationHolder.registeredIGBlocks.put(holder_key, block);
        IGRegistrationHolder.registeredIGItems.put(holder_key, block.asItem());
    }

}
