package com.igteam.immersivegeology.core.registration;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.common.block.IGGenericBlock;
import com.igteam.immersivegeology.common.block.IGOreBlock;
import com.igteam.immersivegeology.common.block.IGSlabBlock;
import com.igteam.immersivegeology.common.block.IGStairBlock;
import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.common.item.IGGenericBlockItem;
import com.igteam.immersivegeology.common.item.IGGenericItem;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class IGRegistrationHolder {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, IGLib.MODID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, IGLib.MODID);

    private static final HashMap<String, RegistryObject<Block>> BLOCK_REGISTRY_MAP = new HashMap<>();
    private static final HashMap<String, RegistryObject<Item>> ITEM_REGISTRY_MAP = new HashMap<>();

    public static Function<String, Item> getItem = (key) -> ITEM_REGISTRY_MAP.get(key).get();
    public static Function<String, Block> getBlock = (key) -> BLOCK_REGISTRY_MAP.get(key).get();

    public static void initialize(){
        for (MaterialInterface<?> material : ImmersiveGeology.getGeologyMaterials()) {
            for(IFlagType<?> flags : material.getFlags()){
                if(material.getFlags().contains(MaterialFlags.EXISTING_IMPLEMENTATION)) break;
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
                            for (StoneEnum base : StoneEnum.values()) {
                                String registryKey = blockCategory.getRegistryKey(material, base);
                                Supplier<Block> blockProvider = () -> new IGOreBlock(blockCategory, base, material);
                                registerBlock(registryKey, blockProvider);
                                registerItem(registryKey, () -> new IGGenericBlockItem((IGGenericBlock) getBlock.apply(registryKey)));
                            }
                        }
                        case SLAB -> {
                            String registryKey = blockCategory.getRegistryKey(material);
                            Supplier<Block> blockProvider = () -> new IGSlabBlock(blockCategory, material);
                            registerBlock(registryKey, blockProvider);
                            registerItem(registryKey, () -> new IGGenericBlockItem((IGGenericBlock) getBlock.apply(registryKey)));
                        }
                        case STAIRS -> {
                            String registryKey = blockCategory.getRegistryKey(material);
                            Supplier<Block> blockProvider = () -> new IGStairBlock(() -> getBlock.apply(BlockCategoryFlags.SHEETMETAL_BLOCK.getRegistryKey(material)).defaultBlockState(), material);
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

    public static Supplier<List<Item>> supplyDeferredItems(){
        return () -> ITEMS.getEntries().stream().map(RegistryObject::get).toList();
    }

    public static Supplier<List<Block>> supplyDeferredBlocks(){
        return () -> BLOCKS.getEntries().stream().map(RegistryObject::get).toList();
    }

    public static DeferredRegister<Item> getItemRegister(){
        return ITEMS;
    }

    public static DeferredRegister<Block> getBlockRegister(){
        return BLOCKS;
    }
}