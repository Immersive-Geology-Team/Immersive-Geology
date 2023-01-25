package com.igteam.immersive_geology.core.registration;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.common.block.IGGenericBlock;
import com.igteam.immersive_geology.common.item.IGGenericBlockItem;
import com.igteam.immersive_geology.common.item.IGGenericItem;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class IGRegistrationHolder {
    private static Logger logger = ImmersiveGeology.getNewLogger();
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, IGLib.MODID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, IGLib.MODID);

    public static void registerItem(String registry_name,  Supplier<Item> itemSupplier){
        ITEMS.register(registry_name, itemSupplier);
    }

    public static void registerBlock(String registry_name,  Supplier<Block> blockSupplier){
        BLOCKS.register(registry_name, blockSupplier);
    }

    public static Map<ResourceLocation, Item> getItemRegistry() {
        return ITEMS.getEntries().stream().collect(Collectors.toMap(RegistryObject::getId, RegistryObject::get));
    }

    public static Map<ResourceLocation, Block> getBlockRegistry() {
        return BLOCKS.getEntries().stream().collect(Collectors.toMap(RegistryObject::getId, RegistryObject::get));
    }
}