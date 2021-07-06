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
import com.igteam.immersive_geology.common.item.IGBucketItem;
import com.igteam.immersive_geology.common.item.IGOreItem;
import com.igteam.immersive_geology.common.item.ItemBase;
import com.igteam.immersive_geology.core.data.generators.helpers.IGTags;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Effects;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class IGVariantHolder {

    private static Logger log = ImmersiveGeology.getNewLogger();

    public static void createVariants(Material mat) {
        log.debug("Creating " +  mat.getName().toUpperCase() + " Variants");
        for(MaterialUseType type : MaterialUseType.values()) {
            if(shouldGenerate(type)) {
                if (mat.hasSubtype(type)) {
                    log.debug("Registering " + type.getName().toUpperCase());
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
            case FLASK:
                registerBucketItem(material, type);
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
                    log.debug("Registering special type: " + holder_key);
                    IGOreItem item = new IGOreItem(holder_key, new Material[]{materialBase, materialOre}, getOreType(type));
                    IGRegistrationHolder.registeredIGItems.put(holder_key, item);
                }
            }
        }
    }

    private static void registerFluidType(Material material){
        if(material instanceof MaterialFluidBase) {
            MaterialFluidBase fluid_material = (MaterialFluidBase) material;
            String fluid_name = fluid_material.getName();

            IGFluid fluid;
            fluid = new IGFluid(fluid_material,IGFluid.createBuilder((int) fluid_material.getDensity(), fluid_material.getViscosity(), fluid_material.getRarity(), fluid_material.getColor(0), fluid_material.getFluidType().isGas()));
            if(fluid_material.getContactEffect() != null) {
                fluid.block.setEffect(fluid_material.getContactEffect(), fluid_material.getContactEffectDuration(), fluid_material.getContactEffectLevel());
            }
            IGRegistrationHolder.registeredIGFluids.put(IGRegistrationHolder.getRegistryKey(fluid_material, MaterialUseType.FLUIDS), fluid);
            IGRegistrationHolder.registeredIGFluids.put(IGRegistrationHolder.getRegistryKey(fluid_material, MaterialUseType.FLUIDS) + "_flowing", fluid.getFlowingFluid());
            IGRegistrationHolder.registeredIGBlocks.put(IGRegistrationHolder.getRegistryKey(fluid_material, MaterialUseType.FLUIDS), fluid.block);

            if(fluid_material.hasBucket()) {
                IGRegistrationHolder.registeredIGItems.put(IGRegistrationHolder.getRegistryKey(fluid_material, MaterialUseType.BUCKET), fluid.getBucket());
                log.debug("Registering Bucket for fluid: " + fluid_name);
            }

            if(fluid_material.hasFlask()){
                IGRegistrationHolder.registeredIGItems.put(IGRegistrationHolder.getRegistryKey(fluid_material, MaterialUseType.FLASK), fluid.getBucket());
                log.debug("Registering Flask for fluid: " + fluid_name);
            }

            log.debug("Registering Fluid Type: " + fluid_name);
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

    private static void registerBucketItem(Material material, MaterialUseType useType){
        String holder_key = useType.getName()+"_"+material.getName();
        IGBucketItem item = new IGBucketItem(() -> Fluids.EMPTY, material, useType, new Item.Properties().maxStackSize(1).group(ImmersiveGeology.IGGroup));
        item.setRegistryName(holder_key);
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
                    log.debug("Registering special type: " + holder_key);
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
