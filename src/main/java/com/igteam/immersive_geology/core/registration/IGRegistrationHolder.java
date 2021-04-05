package com.igteam.immersive_geology.core.registration;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.StoneEnum;
import net.minecraft.block.Block;
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
        Arrays.stream(MaterialUseType.values()).forEach(materialUseType -> {
            IGVariantHolder.createVariants(materialUseType);
        });

        registeredIGBlocks.values().forEach((block) ->{
            event.getRegistry().register(block);
        });
    }

    public static Item getItemByMaterial(Material material, MaterialUseType useType){
        return registeredIGItems.get(getRegistryKey(material, useType));
    }

    public static Item getItemByMaterial(Material material, MaterialUseType useType, StoneEnum stoneType){
        return registeredIGItems.get(getRegistryKey(material, useType, stoneType));
    }

    public static String getRegistryKey(Material mat, MaterialUseType useType){
        return (useType.getName() + "_" + mat.getName()).toLowerCase();
    }

    public static String getRegistryKey(Material mat, MaterialUseType useType, StoneEnum stone){
        return (useType.getName() + "_" + stone.name() + "_" + mat.getName()).toLowerCase();
    }
}
