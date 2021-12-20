package com.igteam.immersive_geology.core.registration;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialBrickBase;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialFluidBase;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMetalBase;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMineralBase;
import com.igteam.immersive_geology.api.materials.material_data.fluids.slurry.MaterialSlurryWrapper;
import com.igteam.immersive_geology.common.block.IGBaseBlock;
import com.igteam.immersive_geology.common.block.IGOreBlock;
import com.igteam.immersive_geology.common.block.IGSlabBlock;
import com.igteam.immersive_geology.common.block.IGStairsBlock;
import com.igteam.immersive_geology.common.fluid.IGFluid;
import com.igteam.immersive_geology.common.item.IGBucketItem;
import com.igteam.immersive_geology.common.item.IGOreItem;
import com.igteam.immersive_geology.common.item.ItemBase;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;

public class IGVariantHolder {

    private static Logger log = ImmersiveGeology.getNewLogger();

    public static void createVariants(Material mat) {
        log.debug("Creating " +  mat.getName().toUpperCase() + " Variants");
        for(MaterialUseType type : MaterialUseType.values()) {
            if(shouldGenerate(type) && mat.hasSubtype(type)) {
                log.debug("Registering " + type.getName().toUpperCase());
                if (type.isBlock()) {
                    createBlockVariants(mat, type);
                } else {
                    switch(type) {
                        case FLUIDS:
                            registerFluidType(mat);
                            break;
                        case SLURRY:
                            registerSlurryType(mat);
                            break;
                        case MACHINE:
                            break;
                        default:
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
                break;
            case DIRTY_CRUSHED_ORE:
                registerDirtyOreItem(material, type);
                break;
            case FLUIDS:
            case SLURRY:
                break;
            case FLASK:
                registerBucketItem(material, type);
                break;
            default:
                registerBasicItem(material, type);
        }
    }

    private static void registerDirtyOreItem(Material materialOre, MaterialUseType type){
        Material materialStone = null;
        for(MaterialEnum container : MaterialEnum.stoneValues()){
            materialStone = container.getMaterial();
            if(materialStone != null){
                if(materialStone.hasSubtype(MaterialUseType.STONE)){
                    String holder_key = getOreType(type).getName() + "_" + materialStone.getName() + "_" + materialOre.getName();
                    log.debug("Registering special type: " + holder_key);
                    IGOreItem item = new IGOreItem(holder_key, new Material[]{materialStone, materialOre}, getOreType(type));
                    IGRegistrationHolder.registeredIGItems.put(holder_key, item);
                }
            }
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


    private static void registerSlurryType(Material material){
        if(material instanceof MaterialSlurryWrapper) {
            MaterialSlurryWrapper wrapper = (MaterialSlurryWrapper) material;
            String fluid_name = wrapper.getName();

            IGFluid fluid;
            fluid = new IGFluid(wrapper, IGFluid.createBuilder((int) wrapper.getDensity(), wrapper.getViscosity(), wrapper.getRarity(), wrapper.getColor(0), wrapper.getFluidType().isGas()), true);
            if (wrapper.getContactEffect() != null) {
                fluid.block.setEffect(wrapper.getContactEffect(), wrapper.getContactEffectDuration(), wrapper.getContactEffectLevel());
            }

            IGRegistrationHolder.registeredIGFluids.put(IGRegistrationHolder.getSlurryKey(wrapper.getSoluteMaterial(), wrapper.getBaseFluidMaterial(), false), fluid);
            IGRegistrationHolder.registeredIGFluids.put(IGRegistrationHolder.getSlurryKey(wrapper.getSoluteMaterial(), wrapper.getBaseFluidMaterial(), true), fluid.getFlowingFluid());
            IGRegistrationHolder.registeredIGBlocks.put(IGRegistrationHolder.getSlurryKey(wrapper.getSoluteMaterial(), wrapper.getBaseFluidMaterial(), false), fluid.block);

            if(fluid.hasBucket()) {
                log.info("Registering Bucket for fluid: " + fluid_name);
                IGRegistrationHolder.registeredIGItems.put(IGRegistrationHolder.getRegistryKey(wrapper.getSoluteMaterial(), wrapper.getBaseFluidMaterial(), MaterialUseType.BUCKET), fluid.getBucket());
            }

            log.info("Registered Slurry Type: " + fluid_name);
            return;
        }
        log.warn("Tried to register a slurry that isn't a slurry type material: " + material.getName());
    }


    private static void registerFluidType(Material material){
        if(material instanceof MaterialFluidBase) {
            MaterialFluidBase fluid_material = (MaterialFluidBase) material;
            String fluid_name = fluid_material.getName();

            IGFluid fluid;
            fluid = new IGFluid(fluid_material,IGFluid.createBuilder((int) fluid_material.getDensity(), fluid_material.getViscosity(), fluid_material.getRarity(), fluid_material.getColor(0), fluid_material.getFluidType().isGas()), false);
            if(fluid_material.getContactEffect() != null) {
                fluid.block.setEffect(fluid_material.getContactEffect(), fluid_material.getContactEffectDuration(), fluid_material.getContactEffectLevel());
            }
            IGRegistrationHolder.registeredIGFluids.put(IGRegistrationHolder.getRegistryKey(fluid_material, MaterialUseType.FLUIDS), fluid);
            IGRegistrationHolder.registeredIGFluids.put(IGRegistrationHolder.getRegistryKey(fluid_material, MaterialUseType.FLUIDS) + "_flowing", fluid.getFlowingFluid());
            IGRegistrationHolder.registeredIGBlocks.put(IGRegistrationHolder.getRegistryKey(fluid_material, MaterialUseType.FLUIDS), fluid.block);

            if(fluid_material.hasBucket()) {
                IGRegistrationHolder.registeredIGItems.put(IGRegistrationHolder.getRegistryKey(fluid_material, MaterialUseType.BUCKET), fluid.getBucket());
                log.info("Registering Bucket for fluid: " + fluid_name);
            }

            if(fluid_material.hasFlask()){
                IGRegistrationHolder.registeredIGItems.put(IGRegistrationHolder.getRegistryKey(fluid_material, MaterialUseType.FLASK), fluid.getFlask());
                log.info("Registering Flask for fluid: " + fluid_name);
            }

            log.info("Registering Fluid Type: " + fluid_name);
        }
    }

    private static MaterialUseType getOreType(MaterialUseType type){
        switch(type){
            case ROCK_BIT:
                return MaterialUseType.ORE_BIT;
            case CHUNK:
                return MaterialUseType.ORE_CHUNK;
            default:
                return type;
        }
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
            case SLAB:
                registerSlabBlock(material);
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
        IGBaseBlock block = new IGBaseBlock(holder_key, material, type, MaterialUseType.RAW_CRYSTAL, material.getMinDrops(), material.getMaxDrops());
        IGRegistrationHolder.registeredIGBlocks.put(holder_key, block);
        IGRegistrationHolder.registeredIGItems.put(holder_key, block.asItem());
    }

    private static void registerSlabBlock(Material material)
    {
        MaterialUseType type = MaterialUseType.SLAB;
        String holder_key = type.getName() + "_" + material.getName();
        IGSlabBlock block = new IGSlabBlock(holder_key, material,type);
        if (material instanceof MaterialMetalBase)
        {
            block.setCoverTexturePath("block/greyscale/metal/sheetmetal");
            block.setSideTexturePath("block/greyscale/metal/sheetmetal");
        }

        if (material instanceof MaterialBrickBase)
        {
            block.setCoverTexturePath("block/static_block/"+material.getName()+"_brick");
            block.setSideTexturePath("block/static_block/"+material.getName()+"_brick");
        }

        IGRegistrationHolder.registeredIGBlocks.put(holder_key, block);
        IGRegistrationHolder.registeredIGItems.put(holder_key, block.asItem());
    }

    private static void registerStairsBlock(Material material)
    {
        MaterialUseType type = MaterialUseType.SHEETMETAL_STAIRS;
        String holder_key = type.getName() + "_" + material.getName();
        IGStairsBlock block = new IGStairsBlock(holder_key, material,type);
        if (material instanceof MaterialMetalBase)
        {
            block.setParentTexture("block/greyscale/metal/sheetmetal");
        }
        if (material instanceof MaterialBrickBase)
        {
            block.setParentTexture("block/static_block/"+material.getName()+"_brick");
        }
        IGRegistrationHolder.registeredIGBlocks.put(holder_key, block);
        IGRegistrationHolder.registeredIGItems.put(holder_key, block.asItem());
    }

    private static void registerBasicBlock(Material material, MaterialUseType type){
        String holder_key = type.getName() + "_" + material.getName();
        IGBaseBlock block = new IGBaseBlock(holder_key, material, type);
        IGRegistrationHolder.registeredIGBlocks.put(holder_key, block);
        IGRegistrationHolder.registeredIGItems.put(holder_key, block.asItem());
    }

}
