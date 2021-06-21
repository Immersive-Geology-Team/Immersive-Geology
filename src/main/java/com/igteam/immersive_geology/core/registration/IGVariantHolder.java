package com.igteam.immersive_geology.core.registration;

import blusunrize.immersiveengineering.common.util.fluids.IEFluid;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialFluidBase;
import com.igteam.immersive_geology.api.util.IGRegistryGrabber;
import com.igteam.immersive_geology.common.block.BlockBase;
import com.igteam.immersive_geology.common.block.IGOreBlock;
import com.igteam.immersive_geology.common.block.IGStairsBlock;
import com.igteam.immersive_geology.common.fluid.IGFluid;
import com.igteam.immersive_geology.common.item.IGOreItem;
import com.igteam.immersive_geology.common.item.ItemBase;
import com.igteam.immersive_geology.core.data.generators.helpers.IGTags;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.potion.Effects;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

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

                        if(type == MaterialUseType.FLUIDS) {
                            registerFluidType(mat);
                        }

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
                break;
            case FLUIDS:
                break;
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
                    String holder_key = getOreType(type).getName() + "_" + materialBase.getName() + "_" + materialOre.getName();
                    log.info("Registering special type: " + holder_key);
                    IGOreItem item = new IGOreItem(holder_key, new Material[]{materialBase, materialOre}, getOreType(type));
                    IGRegistrationHolder.registeredIGItems.put(holder_key, item);
                }
            }
        }
    }

    public static ArrayList<Fluid> fluidlist = new ArrayList<Fluid>();

    private static void registerFluidType(Material material){
        if(material instanceof MaterialFluidBase) {
            MaterialFluidBase fluid_material = (MaterialFluidBase) material;
            String fluid_name = fluid_material.getName();

            IGFluid fluid;
            fluid = new IGFluid(fluid_material,IGFluid.createBuilder((int) fluid_material.getDensity(), fluid_material.getViscosity()));
            if(fluid_material.getContactEffect() != null) {
                fluid.block.setEffect(fluid_material.getContactEffect(), fluid_material.getContactEffectDuration(), fluid_material.getContactEffectLevel());
            }

            fluidlist.add(fluid);

            log.info("Registering Fluid Type: " + fluid_name);
        }
    }

    private static MaterialUseType getOreType(MaterialUseType type){
        return type == MaterialUseType.ROCK_BIT ? MaterialUseType.ORE_BIT : MaterialUseType.ORE_CHUNK;
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
            case GEODE:
                registerGeodeBlock(material);
                break;
            case STONE:
               registerOreBlock(material);
               registerBasicBlock(material, type);
                break;
            case SHEETMETAL_STAIRS:
                registerStairsBlock(material);
                break;
            case FLUIDS:
                break;
            default:
                registerBasicBlock(material, type);
        }
    }

    private static void registerGeodeBlock(Material material){
        MaterialUseType type = MaterialUseType.GEODE;
        String holder_key = type.getName() + "_" + material.getName();
        BlockBase block = new BlockBase(holder_key, material, type, MaterialUseType.RAW_CRYSTAL, material.getMinDrops(), material.getMaxDrops());
        IGRegistrationHolder.registeredIGBlocks.put(holder_key, block);
        IGRegistrationHolder.registeredIGItems.put(holder_key, block.asItem());
    }

    private static void registerStairsBlock(Material material)
    {
        MaterialUseType type = MaterialUseType.SHEETMETAL_STAIRS;
        String holder_key = type.getName() + "_" + material.getName();
        IGStairsBlock block = new IGStairsBlock(holder_key, material,type);
        IGRegistrationHolder.registeredIGBlocks.put(holder_key, block);
        IGRegistrationHolder.registeredIGItems.put(holder_key, block.asItem());
    }

    private static void registerBasicBlock(Material material, MaterialUseType type){
        String holder_key = type.getName() + "_" + material.getName();
        BlockBase block = new BlockBase(holder_key, material, type);
        IGRegistrationHolder.registeredIGBlocks.put(holder_key, block);
        IGRegistrationHolder.registeredIGItems.put(holder_key, block.asItem());
    }

}
