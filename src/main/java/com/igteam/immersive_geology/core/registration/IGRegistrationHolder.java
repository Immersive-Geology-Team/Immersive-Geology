package com.igteam.immersive_geology.core.registration;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.common.block.IGGenericBlock;
import com.igteam.immersive_geology.common.item.IGGenericBlockItem;
import com.igteam.immersive_geology.common.item.IGGenericItem;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.material.MetalEnum;
import com.igteam.immersive_geology.core.material.helper.BlockCategoryFlags;
import com.igteam.immersive_geology.core.material.helper.IFlagType;
import com.igteam.immersive_geology.core.material.helper.ItemCategoryFlags;
import com.igteam.immersive_geology.core.material.helper.MaterialInterface;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class IGRegistrationHolder {
    private static Logger logger = ImmersiveGeology.getNewLogger();
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, IGLib.MODID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, IGLib.MODID);

    public static HashMap<String, RegistryObject<Block>> BLOCK_REGISTRY = new HashMap<>();
    public static HashMap<String, RegistryObject<Item>> ITEM_REGISTRY = new HashMap<>();


    public static void registerItem(String registry_name,  Supplier<Item> itemSupplier){
        ITEM_REGISTRY.put(registry_name, ITEMS.register(registry_name, itemSupplier));
    }

    public static void registerBlock(String registry_name,  Supplier<Block> blockSupplier){
        BLOCK_REGISTRY.put(registry_name, BLOCKS.register(registry_name, blockSupplier));
    }

    public static HashMap<String, RegistryObject<Item>> getItemRegistry() {
        return ITEM_REGISTRY;
    }

    public static HashMap<String, RegistryObject<Block>> getBlockRegistry() {
        return BLOCK_REGISTRY;
    }

    public static DeferredRegister<Item> getDeferredItems() {
        return ITEMS;
    }

    public static DeferredRegister<Block> getDeferredBlocks() {
        return BLOCKS;
    }

    public static void initialize(){
        for (MaterialInterface<?> material : ImmersiveGeology.getGeologyMaterials()) {
            for(IFlagType<?> flags : IFlagType.getAllRegistryFlags()){
                if(flags instanceof BlockCategoryFlags blockCategory) {
                    switch (blockCategory) {
                        case DEFAULT_BLOCK, STORAGE_BLOCK, SHEETMETAL_BLOCK, DUST_BLOCK -> {
                            String registryKey = blockCategory.getRegistryKey(material);
                            registerBlock(registryKey, IGGenericBlock::new);
                            registerItem(registryKey, () -> new IGGenericBlockItem(getBlockRegistry().get(registryKey).get()));
                        }
                    }
                }

                if(flags instanceof ItemCategoryFlags itemCategoryFlags) {
                    switch (itemCategoryFlags) {
                        case INGOT, PLATE, CRYSTAL, DUST, COMPOUND_DUST, CLAY, CRUSHED_ORE, DIRTY_CRUSHED_ORE, GEAR, RAW_ORE, SLAG, ROD, WIRE, NUGGET, METAL_OXIDE -> {
                            registerItem(itemCategoryFlags.getRegistryKey(material), () -> new IGGenericItem(itemCategoryFlags, material));
                        }
                    }
                }
            }
        }
    }
}