package com.igteam.immersive_geology.core.registration;

import blusunrize.immersiveengineering.common.util.IELogger;
import blusunrize.immersiveengineering.common.util.fluids.IEFluid;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialStoneBase;
import com.igteam.immersive_geology.common.block.helpers.IGBlockType;
import com.igteam.immersive_geology.common.fluid.IGFluid;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.SortedMap;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class IGRegistrationHolder {

    public static HashMap<String, Item> registeredIGItems = new HashMap<>();
    public static HashMap<String, IGBlockType> registeredIGBlocks = new HashMap<>();
    public static HashMap<String, Fluid> registeredIGFluids = new HashMap<>();


    private static Logger log = ImmersiveGeology.getNewLogger();

    @SubscribeEvent
    public static void itemRegistration(final RegistryEvent.Register<Item> event){
        log.warn("Applying Registration");
        registeredIGItems.values().forEach((item) -> {
                event.getRegistry().register(item);
        });
    }

    @SubscribeEvent
    public static void blockRegistration(final RegistryEvent.Register<Block> event){
        //Best spot to create all data for items and blocks, Blocks are registered first, then Items after that it's alphabetical
        Arrays.stream(MaterialEnum.values()).forEach(material -> {
            IGVariantHolder.createVariants(material.getMaterial());
        });

        registeredIGBlocks.values().forEach((block) ->{
            event.getRegistry().register(block.getSelf());
        });
    }

    @SubscribeEvent
    public static void fluidRegistration(final RegistryEvent.Register<Fluid> event){
        for(Fluid fluid : registeredIGFluids.values())
            try {
                event.getRegistry().register(fluid);
            } catch (Throwable e){
                log.error("Failed to register a fluid. ({}, {})", fluid, fluid.getRegistryName());
                throw e;
            }
    }

    public static Item getItemByMaterial(Material material, MaterialUseType useType){
        return registeredIGItems.get(getRegistryKey(material, useType));
    }

    public static Item getItemByMaterial(Material materialBase, Material materialOre, MaterialUseType type){
        return registeredIGItems.get(getRegistryKey(materialBase, materialOre, type));
    }

    public static String getRegistryKey(Material mat, MaterialUseType useType){
        return (useType.getName() + "_" + mat.getName()).toLowerCase();
    }

    public static String getRegistryKey(Material materialBase, Material materialOre, MaterialUseType type){
        return (type.getName() + "_" + materialBase.getName() + "_" + materialOre.getName());
    }

    public static Fluid getFluidByMaterial(Material material, boolean isFlowing){
        return registeredIGFluids.get(getRegistryKey(material, MaterialUseType.FLUIDS) + (isFlowing ? "_flowing" : ""));
    }

    public static Block getBlockByMaterial(MaterialUseType useType, Material material) {
        return registeredIGBlocks.get(getRegistryKey(material, useType)).getSelf();
    }

    public static Block getBlockByMaterial(Material base_material, Material ore_material, MaterialUseType type){
        return registeredIGBlocks.get(getRegistryKey(base_material, ore_material, type)).getSelf();
    }
}
