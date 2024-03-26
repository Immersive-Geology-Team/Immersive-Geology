package com.igteam.immersivegeology.core.registration;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.MultiblockBEType;
import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.IGApi;
import com.igteam.immersivegeology.common.block.IGGenericBlock;
import com.igteam.immersivegeology.common.block.IGOreBlock;
import com.igteam.immersivegeology.common.block.IGSlabBlock;
import com.igteam.immersivegeology.common.block.IGStairBlock;
import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.common.block.helper.IGClientTickableTile;
import com.igteam.immersivegeology.common.block.helper.IGCommonTickableTile;
import com.igteam.immersivegeology.common.block.helper.IGServerTickableTile;
import com.igteam.immersivegeology.common.configuration.helper.ConfigurationHelper;
import com.igteam.immersivegeology.common.item.IGGenericBlockItem;
import com.igteam.immersivegeology.common.item.IGGenericBucketItem;
import com.igteam.immersivegeology.common.item.IGGenericItem;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.flags.*;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class IGRegistrationHolder {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, IGLib.MODID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, IGLib.MODID);
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, IGLib.MODID);
    private static final DeferredRegister<BlockEntityType<?>> TE_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, IGLib.MODID);

    private static final HashMap<String, RegistryObject<Block>> BLOCK_REGISTRY_MAP = new HashMap<>();
    private static final HashMap<String, RegistryObject<Item>> ITEM_REGISTRY_MAP = new HashMap<>();
    private static final HashMap<String, RegistryObject<Fluid>> FLUID_REGISTRY_MAP = new HashMap<>();

    public static Function<String, Item> getItem = (key) -> ITEM_REGISTRY_MAP.get(key).get();
    public static Function<String, Block> getBlock = (key) -> BLOCK_REGISTRY_MAP.get(key).get();
    public static Function<String, Fluid> getFluid = (key) -> FLUID_REGISTRY_MAP.get(key).get();

    public static void initialize(){
        for (MaterialInterface<?> material : ImmersiveGeology.getGeologyMaterials()) {
            for(IFlagType<?> flags : material.getFlags()){
                if(material.getFlags().contains(MaterialFlags.EXISTING_IMPLEMENTATION)) break;

                // checks is the material has any ModFlags (e.g. Beyond Earth), if it does, check if none are loaded, if so skip material
                if(Arrays.stream(ModFlags.values()).anyMatch(material.getFlags()::contains) && material.getFlags().stream().noneMatch(ModFlags::isLoaded)) continue;


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

                                // checks is the material has any ModFlags (e.g. Beyond Earth)
                                if(Arrays.stream(ModFlags.values()).anyMatch(base.getFlags()::contains) && base.getFlags().stream().noneMatch(ModFlags::isLoaded)) continue;

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
                        case FLUID -> {
                            String registryKey = blockCategory.getRegistryKey(material);
                            ForgeFlowingFluid.Properties flowingProperties = material.getFluidProperties();

                            registerFluid(registryKey, () -> new ForgeFlowingFluid.Source(flowingProperties));
                            registerFluid(registryKey + "_flowing", () -> new ForgeFlowingFluid.Flowing(flowingProperties));

                            registerItem(ItemCategoryFlags.BUCKET.getRegistryKey(material), () -> new IGGenericBucketItem(() -> getFluid.apply(registryKey), ItemCategoryFlags.BUCKET, material));
                            registerBlock(registryKey, () -> new LiquidBlock(() -> (FlowingFluid) getFluid.apply(registryKey), BlockBehaviour.Properties.of(Material.WATER)));
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

    public static void registerFluid(String registry_name,  Supplier<Fluid> fluidSupplier){
        FLUID_REGISTRY_MAP.put(registry_name, FLUIDS.register(registry_name, fluidSupplier));
    }

    public static void registerItem(String registry_name,  Supplier<Item> itemSupplier){
        ITEM_REGISTRY_MAP.put(registry_name, ITEMS.register(registry_name, itemSupplier));
    }

    public static void registerBlock(String registry_name,  Supplier<Block> blockSupplier){
        BLOCK_REGISTRY_MAP.put(registry_name, BLOCKS.register(registry_name, blockSupplier));
    }


    // Mostly Multiblock stuff past this point
    public static <T extends BlockEntity & IEBlockInterfaces.IGeneralMultiblock> MultiblockBEType<T> registerMultiblock(String name, MultiblockBEType.BEWithTypeConstructor<T> factory, Supplier<? extends Block> valid){
        return new MultiblockBEType<>(name, TE_REGISTER, factory, valid, state -> state.hasProperty(IEProperties.MULTIBLOCKSLAVE) && !state.getValue(IEProperties.MULTIBLOCKSLAVE));
    }

    public static <T extends Block> RegistryObject<T> registerMultiblockBlock(String name, Supplier<T> blockConstructor){
        return registerBlock(name, blockConstructor, block -> new BlockItem(block, new Item.Properties()));
    }

    public static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> blockConstructor, @Nullable Function<T, ? extends BlockItem> blockItem){
        RegistryObject<T> block = BLOCKS.register(name, blockConstructor);
        if(blockItem != null){
            registerItem(name, () -> blockItem.apply(block.get()));
        }
        return block;
    }

    public static <T extends BlockEntity & IEBlockInterfaces.IGeneralMultiblock> MultiblockBEType<T> registerMultiblockTE(String name, MultiblockBEType.BEWithTypeConstructor<T> factory, Supplier<? extends Block> valid){
        return new MultiblockBEType<>(name, TE_REGISTER, factory, valid, state -> state.hasProperty(IEProperties.MULTIBLOCKSLAVE) && !state.getValue(IEProperties.MULTIBLOCKSLAVE));
    }

    public static HashMap<String, RegistryObject<Item>> getItemRegistryMap() {
        return ITEM_REGISTRY_MAP;
    }
    public static HashMap<String, RegistryObject<Block>> getBlockRegistryMap() {
        return BLOCK_REGISTRY_MAP;
    }
    public static HashMap<String, RegistryObject<Fluid>> getFluidRegistryMap() {
        return FLUID_REGISTRY_MAP;
    }

    public static Supplier<List<Item>> supplyDeferredItems(){
        return () -> ITEMS.getEntries().stream().map(RegistryObject::get).toList();
    }

    public static Supplier<List<Block>> supplyDeferredBlocks(){
        return () -> BLOCKS.getEntries().stream().map(RegistryObject::get).toList();
    }

    public static Supplier<List<Fluid>> supplyDeferredFluids(){
        return () -> FLUIDS.getEntries().stream().map(RegistryObject::get).toList();
    }

    public static DeferredRegister<Item> getItemRegister(){
        return ITEMS;
    }

    public static DeferredRegister<BlockEntityType<?>> getTeRegister(){
        return TE_REGISTER;
    }

    public static DeferredRegister<Block> getBlockRegister(){
        return BLOCKS;
    }

    public static DeferredRegister<Fluid> getFluidRegister(){
        return FLUIDS;
    }

    @Nullable
    public static <E extends BlockEntity & IGCommonTickableTile, A extends BlockEntity> BlockEntityTicker<A> createCommonTicker(boolean isClient, BlockEntityType<A> actual, RegistryObject<BlockEntityType<E>> expected){
        return createCommonTicker(isClient, actual, expected.get());
    }

    @Nullable
    public static <E extends BlockEntity & IGCommonTickableTile, A extends BlockEntity> BlockEntityTicker<A> createCommonTicker(boolean isClient, BlockEntityType<A> actual, BlockEntityType<E> expected){
        if(isClient){
            return createClientTicker(actual, expected);
        }else{
            return createServerTicker(actual, expected);
        }
    }

    @Nullable
    public static <E extends BlockEntity & IGClientTickableTile, A extends BlockEntity> BlockEntityTicker<A> createClientTicker(BlockEntityType<A> actual, BlockEntityType<E> expected){
        return createTickerHelper(actual, expected, IGClientTickableTile::makeTicker);
    }

    @Nullable
    public static <E extends BlockEntity & IGServerTickableTile, A extends BlockEntity> BlockEntityTicker<A> createServerTicker(BlockEntityType<A> actual, BlockEntityType<E> expected){
        return createTickerHelper(actual, expected, IGServerTickableTile::makeTicker);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> actual, BlockEntityType<E> expected, Supplier<BlockEntityTicker<? super E>> ticker){
        return expected == actual ? (BlockEntityTicker<A>) ticker.get() : null;
    }
}