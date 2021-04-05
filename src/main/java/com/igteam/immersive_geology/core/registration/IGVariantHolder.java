package com.igteam.immersive_geology.core.registration;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.StoneEnum;
import com.igteam.immersive_geology.common.block.BlockBase;
import com.igteam.immersive_geology.common.block.blocks.IGOreBlock;
import com.igteam.immersive_geology.common.item.ItemBase;
import org.apache.logging.log4j.Logger;

public class IGVariantHolder {

    private static Logger log = ImmersiveGeology.getNewLogger();

    public static void createVariants(MaterialUseType type) {
        log.info("Creating " +  type.getName()+ " Variants");
        for(MaterialEnum material : MaterialEnum.values()) {
            Material mat = material.getMaterial();
            if(mat.hasSubtype(type)) {
                log.info("Variant " + type.getName() + ", Registering " + mat.getName());
                if(type.isBlock()) {
                    if (mat.hasSubtype(MaterialUseType.ORE_STONE)) {
                        for (StoneEnum stone : StoneEnum.values()) {
                            createBlockVariants(mat, type, stone);
                        }
                    } else {
                        createBlockVariants(mat, type);
                    }
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

    private static void createBlockVariants(Material material, MaterialUseType type, StoneEnum stonetype){
        if(stonetype != null){
            String holder_key = type.getName() + "_" + stonetype.name() + "_" + material.getName();
            IGOreBlock block = new IGOreBlock(holder_key, material, type, stonetype);
            IGRegistrationHolder.registeredIGBlocks.put(holder_key, block);
            IGRegistrationHolder.registeredIGItems.put(holder_key, block.asItem());
        } else {
            String holder_key = type.getName() + "_" + material.getName();
            BlockBase block = new BlockBase(holder_key, material, type);
            IGRegistrationHolder.registeredIGBlocks.put(holder_key, block);
            IGRegistrationHolder.registeredIGItems.put(holder_key, block.asItem());
        }

    }
    private static void createBlockVariants(Material material, MaterialUseType type){
        createBlockVariants(material, type, null);
    }

}
