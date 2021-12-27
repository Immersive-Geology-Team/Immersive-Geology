package com.igteam.immersive_geology.core.registration;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.fluid.FluidEnum;
import com.igteam.immersive_geology.api.materials.fluid.SlurryEnum;
import com.igteam.immersive_geology.api.materials.material_data.fluids.slurry.MaterialSlurryWrapper;
import com.igteam.immersive_geology.common.block.IGStaticBlock;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class IGRegistrationHolder {

    public static HashMap<String, Item> registeredIGItems = new HashMap<>();
    public static HashMap<String, Block> registeredIGBlocks = new HashMap<>();
    public static HashMap<String, Fluid> registeredIGFluids = new HashMap<>();
    private static Logger log = ImmersiveGeology.getNewLogger();

    @SubscribeEvent
    public static void itemRegistration(final RegistryEvent.Register<Item> event){
        log.info("Adding Static Item Registries");

        Items.initializeStaticItems();

        log.info("Applying Item Registration");
        registeredIGItems.values().forEach((item) -> {
                event.getRegistry().register(item);
        });
    }

    @SubscribeEvent
    public static void blockRegistration(final RegistryEvent.Register<Block> event){
        log.info("Adding Static Block Registries");

        Blocks.initializeStaticBlocks();

        log.info("Applying Block Registries");
        registeredIGBlocks.values().forEach((block) ->{
                event.getRegistry().register(block);
        });
    }

    public static boolean variantsGenerated = false;

    public static void generateVariants(){
        if(!variantsGenerated) {
            Arrays.stream(MaterialEnum.values()).forEach(material -> {
                IGVariantHolder.createVariants(material.getMaterial());
            });

            for (FluidEnum wrapper : FluidEnum.values()) {
                IGVariantHolder.createVariants(wrapper.getMaterial()); //Create and Register all basic fluids
            }

            for (SlurryEnum wrapper : SlurryEnum.values()) {
                for (MaterialSlurryWrapper slurry : wrapper.getEntries()) {
                    IGVariantHolder.createVariants(slurry); //Create and Register all Slurries
                }
            }
        }
        variantsGenerated = true;
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

    public static Fluid getSlurryByMaterials(Material soluteMaterial, Material fluidMaterial, boolean isFlowing){
        return registeredIGFluids.get(getRegistryKey(soluteMaterial, fluidMaterial, MaterialUseType.SLURRY) + (isFlowing ? "_flowing" : ""));
    }

    public static String getSlurryKey(Material soluteMaterial, Material fluidMaterial, boolean isFlowing){
        return getRegistryKey(soluteMaterial, fluidMaterial, MaterialUseType.SLURRY) + (isFlowing ? "_flowing" : "");
    }

    public static Block getBlockByMaterial(MaterialUseType useType, Material material) {
        return registeredIGBlocks.get(getRegistryKey(material, useType));
    }

    public static Block getBlockByMaterial(Material base_material, Material ore_material, MaterialUseType type){
        return registeredIGBlocks.get(getRegistryKey(base_material, ore_material, type));
    }

    public static class Items {
        //Example ~ remove once another static item has been added. This is the only thing it needs to run
        //public static Item testItem = new IGStaticItem("testing_name", ItemSubGroup.misc);

        //Method used for any static item intialization needed that can't be done in the constructor.
        public static void initializeStaticItems() {

        }
    }

    public static class Blocks {
        public static Block reinforcedRefractoryBrick = new IGStaticBlock("reinforced_refractory_brick", net.minecraft.block.material.Material.ROCK);
        public static Block electronicEngineering = new IGStaticBlock("electronic_engineering", net.minecraft.block.material.Material.IRON);

        public static void initializeStaticBlocks() {

        }
    }
}
