package com.igteam.immersive_geology.core.registration;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.common.block.IGGenericBlock;
import com.igteam.immersive_geology.common.block.IGOreBlock;
import com.igteam.immersive_geology.common.block.IGSlabBlock;
import com.igteam.immersive_geology.common.block.IGStairBlock;
import com.igteam.immersive_geology.common.block.helper.IGBlockType;
import com.igteam.immersive_geology.common.item.IGGenericBlockItem;
import com.igteam.immersive_geology.common.item.IGGenericItem;
import com.igteam.immersive_geology.core.lib.IGLib;
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
import java.util.function.Function;
import java.util.function.Supplier;

public class IGRegistrationHolder {
    private static final Logger logger = ImmersiveGeology.getNewLogger();
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, IGLib.MODID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, IGLib.MODID);

    private static final HashMap<String, RegistryObject<Block>> BLOCK_REGISTRY_MAP = new HashMap<>();
    private static final HashMap<String, RegistryObject<Item>> ITEM_REGISTRY_MAP = new HashMap<>();

    public static Function<String, Item> getItem = (key) -> {
        return ITEM_REGISTRY_MAP.get(key).get();
    };
    public static Function<String, Block> getBlock = (key) -> {
        return BLOCK_REGISTRY_MAP.get(key).get();
    };

    public static void registerItem(String registry_name,  Supplier<Item> itemSupplier){
        ITEM_REGISTRY_MAP.put(registry_name, ITEMS.register(registry_name, itemSupplier));
    }

    public static void registerBlock(String registry_name,  Supplier<Block> blockSupplier){
        BLOCK_REGISTRY_MAP.put(registry_name, BLOCKS.register(registry_name, blockSupplier));
    }

    public static HashMap<String, RegistryObject<Item>> getItemRegistryMap() {
        return ITEM_REGISTRY_MAP;
    }

    public static HashMap<String, RegistryObject<Block>> getBlockRegistryMap() {
        return BLOCK_REGISTRY_MAP;
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
                        case DEFAULT_BLOCK, STORAGE_BLOCK, SHEETMETAL_BLOCK, DUST_BLOCK, GEODE_BLOCK, RAW_ORE_BLOCK -> {
                            String registryKey = blockCategory.getRegistryKey(material);
                            Supplier<Block> blockProvider = () -> new IGGenericBlock(blockCategory, material);
                            registerBlock(registryKey, blockProvider);
                            registerItem(registryKey, () -> new IGGenericBlockItem((IGGenericBlock) getBlock.apply(registryKey)));
                        }
                        case ORE_BLOCK -> {
                            // for each stone type: stoneMaterial needs to be implemented for each ore block
                            String registryKey = blockCategory.getRegistryKey(material);
                            Supplier<Block> blockProvider = () -> new IGOreBlock(blockCategory, material);
                            registerBlock(registryKey, blockProvider);
                            registerItem(registryKey, () -> new IGGenericBlockItem((IGGenericBlock) getBlock.apply(registryKey)));
                        }
                        case SLAB -> {
                            String registryKey = blockCategory.getRegistryKey(material);
                            Supplier<Block> blockProvider = () -> new IGSlabBlock(blockCategory, material);
                            registerBlock(registryKey, blockProvider);
                            registerItem(registryKey, () -> new IGGenericBlockItem((IGGenericBlock) getBlock.apply(registryKey)));
                        }
                        case STAIRS -> {
                            String registryKey = blockCategory.getRegistryKey(material);
                            Supplier<Block> blockProvider = () -> new IGStairBlock(() -> getBlock.apply(BlockCategoryFlags.STORAGE_BLOCK.getRegistryKey(material)).defaultBlockState(), material);
                            registerBlock(registryKey, blockProvider);
                            registerItem(registryKey, () -> new IGGenericBlockItem((IGBlockType) getBlock.apply(registryKey)));
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