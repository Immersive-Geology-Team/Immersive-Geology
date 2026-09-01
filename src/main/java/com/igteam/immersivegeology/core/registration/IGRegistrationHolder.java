/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */
package com.igteam.immersivegeology.core.registration;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler.IMultiblock;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistrationBuilder;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.ComparatorManager;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IMultiblockComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.blocks.metal.MetalScaffoldingType;
import com.google.common.collect.ImmutableSet;
import com.igteam.immersivegeology.client.menu.IGItemGroup;
import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.item.IGMetalDetectorItem;
import com.igteam.immersivegeology.common.item.IGUshankaItem;
import com.igteam.immersivegeology.common.block.*;
import com.igteam.immersivegeology.common.block.entity.cable.IGEnergyPipe;
import com.igteam.immersivegeology.common.block.entity.cable.IGEnergyPipeEntity;
import com.igteam.immersivegeology.common.block.entity.crate.IGCrateEntity;
import com.igteam.immersivegeology.common.block.entity.device.IGMetalDetector;
import com.igteam.immersivegeology.common.block.entity.device.IGMetalDetectorEntity;
import com.igteam.immersivegeology.common.block.entity.crate.IGCrateEntityType;
import com.igteam.immersivegeology.common.block.entity.vent.IGHydroVent;
import com.igteam.immersivegeology.common.block.entity.vent.IGHydroVentEntity;
import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.common.block.multiblocks.*;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGMultiblockBuilder;
import com.igteam.immersivegeology.common.block.multiblocks.skins.*;
import com.igteam.immersivegeology.common.block.multiblocks.skins.helpers.IIGMultiSkinHelper;
import com.igteam.immersivegeology.common.block.ore.IGCrystalBlock;
import com.igteam.immersivegeology.common.block.ore.IGEvaporateMineralBlock;
import com.igteam.immersivegeology.common.block.ore.IGOreBlock;
import com.igteam.immersivegeology.common.block.ore.IGWeatheringOreBlock;
import com.igteam.immersivegeology.common.block.structural.IGFenceBlock;
import com.igteam.immersivegeology.common.block.structural.IGScaffoldingBlock;
import com.igteam.immersivegeology.common.block.structural.IGSlabBlock;
import com.igteam.immersivegeology.common.block.structural.IGStairBlock;
import com.igteam.immersivegeology.common.fluid.IGFluid;
import com.igteam.immersivegeology.common.fluid.IGFluidBlock;
import com.igteam.immersivegeology.common.item.*;
import com.igteam.immersivegeology.common.item.helper.IGFlagItem;
import com.igteam.immersivegeology.common.loot.IGLootModifier;
import com.igteam.immersivegeology.common.particle.IGParticles;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.lib.ResourceUtils;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.chemical.MaterialHydrochloricAcid;
import com.igteam.immersivegeology.core.material.data.enums.*;
import com.igteam.immersivegeology.core.material.data.misc.MaterialSilicon;
import com.igteam.immersivegeology.core.material.data.types.MaterialChemical;
import com.igteam.immersivegeology.core.material.helper.flags.*;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class IGRegistrationHolder {
    private static final DeferredRegister<Block> BLOCK_REGISTER = DeferredRegister.create(Registries.BLOCK, IGLib.MODID);
    private static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(Registries.ITEM, IGLib.MODID);
    private static final DeferredRegister<Fluid> FLUID_REGISTER = DeferredRegister.create(ForgeRegistries.FLUIDS, IGLib.MODID);
    private static final DeferredRegister<FluidType> FLUIDTYPE_REGISTER = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, IGLib.MODID);

    private static final DeferredRegister<BlockEntityType<?>> TE_REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, IGLib.MODID);
    public static final DeferredRegister<CreativeModeTab> TAB_REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, IGLib.MODID);
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_SERIALIZER_REGISTER = DeferredRegister.create(Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, IGLib.MODID);

    private static final LinkedHashMap<String, RegistryObject<Block>> BLOCK_REGISTRY_MAP = new LinkedHashMap<>();
    private static final LinkedHashMap<String, RegistryObject<BlockEntityType<?>>> TE_REGISTRY_MAP = new LinkedHashMap<>();
    private static final LinkedHashMap<String, RegistryObject<Item>> ITEM_REGISTRY_MAP = new LinkedHashMap<>();
    private static final LinkedHashMap<String, RegistryObject<Fluid>> FLUID_REGISTRY_MAP = new LinkedHashMap<>();
    private static final LinkedHashMap<String, RegistryObject<FluidType>> FLUID_TYPE_REGISTRY_MAP = new LinkedHashMap<>();

    public static LinkedHashMap<String, MultiblockRegistration<?>> MB_REGISTRY_MAP = new LinkedHashMap<>();
    public static final LinkedHashMap<String, TemplateMultiblock> MB_TEMPLATE_MAP = new LinkedHashMap<>();

    private static <T extends MultiblockHandler.IMultiblock>
    T registerMultiblock(T multiblock) {
        MultiblockHandler.registerMultiblock(multiblock);
        return multiblock;
    }

    public static DeferredRegister<Block> getBlockRegister()
    {
        return BLOCK_REGISTER;
    }

    public static DeferredRegister<Item> getItemRegister()
    {
        return ITEM_REGISTER;
    }

    public static DeferredRegister<BlockEntityType<?>> getTeRegister()
    {
        return TE_REGISTER;
    }

    public static Function<String, Item> getItem = (key) -> ITEM_REGISTRY_MAP.get(key).get();
    public static Function<String, Block> getBlock = (key) -> BLOCK_REGISTRY_MAP.get(key).get();
    public static Function<String, BlockEntityType<?>> getTE = (key) -> TE_REGISTRY_MAP.get(key).get();

    public static Function<String, TemplateMultiblock> getMBTemplate = MB_TEMPLATE_MAP::get;
    public static Function<String, Fluid> getFluid = (key) -> FLUID_REGISTRY_MAP.get(key).get();

    public static final RegistryObject<CreativeModeTab> IG_BASE_TAB = TAB_REGISTER.register("main", () -> new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, 0)
            .icon(() -> IGRegistrationHolder.getItem.apply("prospector_kit").getDefaultInstance())
            .title(Component.translatable("itemGroup.immersivegeology"))
            .displayItems(IGRegistrationHolder::fillIGTab)
            .withTabFactory(IGItemGroup::new)
            .withSearchBar()
            .build());

    private static void fillIGTab(IGItemGroup.ItemDisplayParameters parms, IGItemGroup.Output out)
    {
        HashMap<IFlagType<?>, ArrayList<Item>> itemMap = new HashMap<>();
        for (Item item : IGRegistrationHolder.getIGItems()) {
            if(item instanceof IGFlagItem type) {
                IFlagType<?> pattern = type.getFlag();
                if (itemMap.containsKey(pattern)) {
                    ArrayList<Item> list = itemMap.get(pattern);
                    list.add(item);
                    itemMap.replace(pattern, list);
                } else {
                    ArrayList<Item> list = new ArrayList<>();
                    list.add(item);
                    itemMap.put(pattern, list);
                }
            }
        }

        ArrayList<IFlagType<?>> allPatternList = new ArrayList<>(Arrays.asList(ItemCategoryFlags.values()));
        allPatternList.addAll(Arrays.asList(BlockCategoryFlags.values()));

        for (IFlagType<?> pattern : allPatternList)
        {
            if(itemMap.containsKey(pattern)){
                ArrayList<Item> list = itemMap.get(pattern);
                for (Item item : list) {
                    out.accept(new ItemStack(item));
                }
            }
        }
    }

    private static final List<Consumer<IEventBus>> MOD_BUS_CALLBACKS = new ArrayList<>();

    private static boolean checkModMaterialsForOverlap(StoneEnum stoneType, GeologyMaterial ore, IFlagType<?> flag)
    {
        Map<ModFlags, Map<IFlagType<?>, MaterialHelper>> ore_map = ore.getExistingImplementationMap();
        for(ModFlags mod : ModFlags.values()) {
            if(ore_map.containsKey(mod) && ore_map.get(mod).containsKey(flag))
            {
                // TFC is the mod, it has the category, likely 'ORE_BLOCK'
                // Now we check if the Stone is also from the same mod 'TFC'
                // If it is!, then we don't need to generate an ore for this combination
                if(stoneType.hasFlag(mod)) return true;
            }
        }

        return false;
    }


    private static IMultiblock[] formationFormat(List<IMultiblock> list)
    {
        IMultiblock[] array = new IMultiblock[list.size()];
        array = list.toArray(array);
        return array;
    }

    private static void registerBlockAndItem(String registryKey, BlockCategoryFlags blockCategory, MaterialInterface<?> material)
    {
        Supplier<Block> blockProvider = () -> new IGGenericBlock(blockCategory, material);
        registerBlock(registryKey, blockProvider);
        registerItem(registryKey, () -> new IGGenericBlockItem((IGGenericBlock) getBlock.apply(registryKey)){
            @Override
            public @NotNull Component getName(ItemStack pStack)
            {
                return Component.translatable(this.getDescriptionId(pStack));
            }
        });
    }

    private static void registerBlockAndItem(String registryKey, BlockCategoryFlags blockCategory, MaterialInterface<?> material, BlockBehaviour.Properties properties)
    {
        Supplier<Block> blockProvider = () -> new IGGenericBlock(blockCategory, material, properties);
        registerBlock(registryKey, blockProvider);
        registerItem(registryKey, () -> new IGGenericBlockItem((IGGenericBlock) getBlock.apply(registryKey)){
            @Override
            public @NotNull Component getName(ItemStack pStack)
            {
                return Component.translatable(this.getDescriptionId(pStack));
            }
        });
    }


    public static <T extends BlockEntity> Supplier<BlockEntityType<T>> makeType(BlockEntityType.BlockEntitySupplier<T> create, Supplier<? extends Block> valid)
    {
        return makeTypeMultipleBlocks(create, ImmutableSet.of(valid));
    }

    public static <T extends BlockEntity> Supplier<BlockEntityType<T>> makeTypeMultipleBlocks(
            BlockEntityType.BlockEntitySupplier<T> create, Collection<? extends Supplier<? extends Block>> valid
    )
    {
        return () -> new BlockEntityType<>(
                create, ImmutableSet.copyOf(valid.stream().map(Supplier::get).collect(Collectors.toList())), null
        );
    }

    public static RegistryObject<BlockEntityType<IGEnergyPipeEntity>> ENERGY_PIPE;
    public static RegistryObject<BlockEntityType<IGMetalDetectorEntity>> METAL_DETECTOR;
    public static final String METAL_DETECTOR_KEY = "metal_detector";
    public static final String USHANKA_KEY = "armor_russian_helmet";
    public static RegistryObject<BlockEntityType<IGHydroVentEntity>> IG_HYDROVENT;
    public static void initialize()
    {

        IGLib.IG_LOGGER.info("- Static Items and Blocks");
        registerBlock(METAL_DETECTOR_KEY, IGMetalDetector::new);
        registerItem(METAL_DETECTOR_KEY, () -> new IGMetalDetectorItem(getBlock.apply(METAL_DETECTOR_KEY)));

        IGItemGroup.addLooseItem(ItemSubGroup.structural, () -> getItem.apply(METAL_DETECTOR_KEY));
        registerItem(USHANKA_KEY, IGUshankaItem::new);
        IGItemGroup.addLooseItem(ItemSubGroup.structural, () -> getItem.apply(USHANKA_KEY));
        registerItem("prospector_kit", () -> new IGMineralTestingItem(ItemCategoryFlags.MISC, StoneEnum.MCStone, 128));
        registerItem("prospector_kit_steel", () -> new IGMineralTestingItem(ItemCategoryFlags.MISC, MetalEnum.StainlessSteel, 4096));
        registerItem(ItemCategoryFlags.HAMMER.getRegistryKey(MetalEnum.Bronze), () -> new IGMBFormationItem(ItemCategoryFlags.HAMMER, MetalEnum.Bronze, 256));
        registerItem(ItemCategoryFlags.HAMMER.getRegistryKey(MetalEnum.StainlessSteel), () -> new IGMBFormationItem(ItemCategoryFlags.HAMMER, MetalEnum.StainlessSteel, 2048));
        registerItem(ItemCategoryFlags.HAMMER.getRegistryKey(StoneEnum.MCStone), () -> new IGMBFormationItem(ItemCategoryFlags.HAMMER, StoneEnum.MCStone, 32));
        registerItem("raw_fire_clay", () -> new IGGenericItem(ItemCategoryFlags.MISC, StoneEnum.MCStone, new Item.Properties().fireResistant()).setCustomLangString("raw_fire_clay"));
        registerItem("refractory_brick", () -> new IGGenericItem(ItemCategoryFlags.MISC, MiscEnum.Refractory, new Item.Properties().fireResistant()).setCustomLangString("refractory_brick"));
        LinkedHashSet<MaterialInterface<?>> slurry_material_set = new LinkedHashSet<>(List.of(MetalEnum.values()));
        slurry_material_set.addAll(List.of(MineralEnum.values()));
        slurry_material_set.add(MiscEnum.Silicon);
        IGLib.IG_LOGGER.info("- Material Based Items, Blocks and Fluids");
        for (MaterialInterface<?> material : IGLib.getGeologyMaterials()) {
            for(IFlagType<?> flags : material.getFlags()){
                // checks is the material has any ModFlags (e.g. Beyond Earth), if it does, check if none are loaded, if so skip material
                boolean hasExistingImplementation = material.instance().checkExistingImplementation(flags) &! DatagenModLoader.isRunningDataGen();
                if(flags instanceof BlockCategoryFlags blockCategory) {
                    switch (blockCategory) {
                        case DEFAULT_BLOCK, STORAGE_BLOCK, SHEETMETAL_BLOCK, DUST_BLOCK, GEODE_BLOCK, ENGINEERING_BLOCK, ADVANCED_ENGINEERING_BLOCK -> {
                            if(hasExistingImplementation) continue;
                            String registryKey = blockCategory.getRegistryKey(material);
                            Supplier<Block> blockProvider = () -> new IGGenericBlock(blockCategory, material);
                            registerBlock(registryKey, blockProvider);
                            registerItem(registryKey, () -> new IGGenericBlockItem((IGGenericBlock) getBlock.apply(registryKey)));
                        }
                        case EVAPORATE_CRYSTAL ->
                        {
                            String registryKey = BlockCategoryFlags.EVAPORATE_CRYSTAL.getRegistryKey(material);
                            Supplier<Block> blockProvider = () -> new IGCrystalBlock(BlockCategoryFlags.EVAPORATE_CRYSTAL, material);
                            registerBlock(registryKey, blockProvider);
                            registerItem(registryKey, () -> new IGGenericBlockItem((IGBlockType) getBlock.apply(registryKey)));
                        }
                        case EVAPORATE -> {
                            if(hasExistingImplementation) continue;
                            String registryKey = blockCategory.getRegistryKey(material);
                            Supplier<Block> blockProvider = () -> new IGEvaporateMineralBlock(blockCategory, material, () -> {return (IGCrystalBlock) material.getBlock(BlockCategoryFlags.EVAPORATE_CRYSTAL);});
                            registerBlock(registryKey, blockProvider);
                            registerItem(registryKey, () -> new IGGenericBlockItem((IGGenericBlock) getBlock.apply(registryKey)));
                        }
                        case ENERGY_PIPE ->
                        {
                            String registryKey = blockCategory.getRegistryKey(material);
                            registerBlock(registryKey, () -> new IGEnergyPipe(blockCategory, material));
                            registerItem(registryKey, () -> new IGGenericBlockItem((IGBlockType) getBlock.apply(registryKey)));
                        }
                        case HYDROVENT ->
                        {
                            String registryKey = blockCategory.getRegistryKey(material);
                            RegistryObject<BlockEntityType<IGHydroVentEntity>> TYPE = TE_REGISTER.register(material.getName() + "_vent_entity_type", makeType(IGHydroVentEntity::new, ()-> getBlock.apply(registryKey)));

                            @SuppressWarnings("unchecked")
                            RegistryObject<BlockEntityType<?>> typeCast = (RegistryObject<BlockEntityType<?>>)(Object) TYPE;
                            TE_REGISTRY_MAP.put(registryKey, typeCast);

                            registerBlock(registryKey, () -> new IGHydroVent(blockCategory, material, TYPE));
                            registerItem(registryKey, () -> new IGGenericBlockItem((IGBlockType) getBlock.apply(registryKey)));
                        }
                        case CRATE ->
                        {
                            String registryKey = blockCategory.getRegistryKey(material);

                            RegistryObject<BlockEntityType<IGCrateEntity>> TYPE = TE_REGISTER.register(material.getName() + "_crate_entity_type", makeType(IGCrateEntity::new, ()-> getBlock.apply(registryKey)));
                            // Because RegistryObject is invariant in its type parameter we need to do this hack
                            // basically we just need to explicitly tell the compiler that yes this is what you're looking for.
                            @SuppressWarnings("unchecked")
                            RegistryObject<BlockEntityType<?>> typeCast = (RegistryObject<BlockEntityType<?>>)(Object) TYPE;
                            TE_REGISTRY_MAP.put(registryKey, typeCast);

                            registerBlock(registryKey, () -> new IGCrateEntityType(blockCategory, material, TYPE));
                            registerItem(registryKey, () -> new IGBlockContainerItem((IGBlockType) getBlock.apply(registryKey)));
                        }
                        case ORE_BLOCK -> {
                            // for each stone type: stoneMaterial needs to be implemented for each ore block
                            for (StoneEnum base : StoneEnum.values()) {
                                // checks is the material has any ModFlags (e.g. Beyond Earth)
                                if(!base.hasFlag(MaterialFlags.IS_ORE_BEARING)) continue;
                                if(!material.instance().acceptableStoneType(base.instance())) continue;
                                if(Arrays.stream(ModFlags.values()).anyMatch((m) -> !m.isStrictlyLoaded() && base.hasFlag(m)) &! DatagenModLoader.isRunningDataGen()) continue;
                                if(checkModMaterialsForOverlap(base, material.instance(), flags)) continue;
                                // After all checks, now we can generate the different ore levels
                                for(OreRichness richness : OreRichness.values()){
                                    String registryKey = blockCategory.getRegistryKey(material, base, richness);
                                    Supplier<Block> blockProvider = () -> (material.canTarnish() ? new IGWeatheringOreBlock(blockCategory, base, material, richness) : new IGOreBlock(blockCategory, base, material, richness));
                                    registerBlock(registryKey, blockProvider);
                                    registerItem(registryKey, () -> new IGGenericBlockItem((IGBlockType) getBlock.apply(registryKey)));
                                }
                            }
                        }

                        case SLAB, SHEETMETAL_SLAB -> {
                            if(hasExistingImplementation) continue;
                            String registryKey = blockCategory.getRegistryKey(material);
                            Supplier<Block> blockProvider = () -> new IGSlabBlock(blockCategory, material);
                            registerBlock(registryKey, blockProvider);
                            registerItem(registryKey, () -> new IGGenericBlockItem((IGBlockType) getBlock.apply(registryKey)));
                        }
                        case SCAFFOLDING ->
                        {
                            if(hasExistingImplementation) continue;
                            for(MetalScaffoldingType type : MetalScaffoldingType.values())
                            {
                                String registryKey = blockCategory.getRegistryKey(material) + "_" + type.name().toLowerCase(Locale.ROOT);
                                Supplier<Block> blockProvider = () -> new IGScaffoldingBlock(type, material);
                                registerBlock(registryKey, blockProvider);
                                registerItem(registryKey, () -> new IGGenericBlockItem((IGBlockType) getBlock.apply(registryKey)));
                            }

                        }
                        case FENCE ->
                        {
                            if(hasExistingImplementation) continue;
                            String registryKey = blockCategory.getRegistryKey(material);
                            Supplier<Block> blockProvider = () -> new IGFenceBlock(blockCategory, material);
                            registerBlock(registryKey, blockProvider);
                            registerItem(registryKey, () -> new IGGenericBlockItem((IGBlockType) getBlock.apply(registryKey)));
                        }
                        case STAIRS, SHEETMETAL_STAIRS -> {
                            if(hasExistingImplementation) continue;

                            String registryKey = blockCategory.getRegistryKey(material);

                            Supplier<BlockState> stateSupplier = Blocks.IRON_BLOCK::defaultBlockState;
                            Supplier<Block> blockProvider = () -> new IGStairBlock(stateSupplier, material, blockCategory);
                            registerBlock(registryKey, blockProvider);
                            registerItem(registryKey, () -> new IGGenericBlockItem((IGBlockType) getBlock.apply(registryKey)));
                        }
                        case FLUID -> {
                            if(hasExistingImplementation) continue;
                            String registryKey = blockCategory.getRegistryKey(material);
                            ItemCategoryFlags bucket_type = material instanceof MetalEnum ? ItemCategoryFlags.BUCKET : ItemCategoryFlags.CLEAN_FLASK;
                            // Still
                            registerFluid(registryKey, () -> new IGFluid.Source(material, null, blockCategory, bucket_type));
                            // Flowing
                            registerFluid(registryKey + "_flowing", () -> new IGFluid.Flowing(material, null, blockCategory, bucket_type));

                            // Fluid Type Registration
                            registerFluidType(registryKey, () -> getFluid.apply(registryKey).getFluidType());
                            registerItem(bucket_type.getRegistryKey(material, blockCategory), () -> new IGGenericBucketItem(() -> getFluid.apply(registryKey), blockCategory, bucket_type, material));
                            registerBlock(registryKey + "_block", () -> new IGFluidBlock(() -> (FlowingFluid) getFluid.apply(registryKey), material, BlockBehaviour.Properties.copy(Blocks.WATER)));
                        }
                        case SLURRY ->
                        {
                            if(material.instance() instanceof MaterialChemical chemical)
                            {
                                for(MaterialInterface<?> slurry_material : slurry_material_set)
                                {
                                    if(!chemical.hasSlurryWith(slurry_material)) continue;

                                    String registryKey = blockCategory.getRegistryKey(material, slurry_material);
                                    // Fluid Type Registration
                                    registerFluidType(registryKey, () -> getFluid.apply(registryKey).getFluidType());

                                    // Still
                                    registerFluid(registryKey, () -> new IGFluid.Source(material, slurry_material, blockCategory, ItemCategoryFlags.CLEAN_FLASK));

                                    // Flowing
                                    registerFluid(registryKey + "_flowing", () -> new IGFluid.Flowing(material, slurry_material, blockCategory, ItemCategoryFlags.CLEAN_FLASK));

                                    registerBlock(registryKey + "_block", () -> new IGFluidBlock(() -> (FlowingFluid) getFluid.apply(registryKey), material, BlockBehaviour.Properties.copy(Blocks.WATER)));

                                    registerItem(ItemCategoryFlags.CLEAN_FLASK.getRegistryKey(material, slurry_material), () -> new IGGenericBucketItem(() -> getFluid.apply(registryKey), blockCategory, ItemCategoryFlags.CLEAN_FLASK, material, slurry_material));
                                }
                            }
                        }
                        case CLOUDY_SLURRY ->
                        {
                            if(material.instance() instanceof MaterialChemical chemical)
                            {
                                for(MaterialInterface<?> slurry_material : slurry_material_set)
                                {
                                    if(!chemical.hasSlurryWith(slurry_material)) continue;

                                    String registryKey = blockCategory.getRegistryKey(material, slurry_material);
                                    // Fluid Type Registration
                                    registerFluidType(registryKey, () -> getFluid.apply(registryKey).getFluidType());

                                    // Still
                                    registerFluid(registryKey, () -> new IGFluid.Source(material, slurry_material, blockCategory, ItemCategoryFlags.CLOUDY_FLASK));

                                    // Flowing
                                    registerFluid(registryKey + "_flowing", () -> new IGFluid.Flowing(material, slurry_material, blockCategory, ItemCategoryFlags.CLOUDY_FLASK));

                                    registerBlock(registryKey + "_block", () -> new IGFluidBlock(() -> (FlowingFluid) getFluid.apply(registryKey), material, BlockBehaviour.Properties.copy(Blocks.WATER)));

                                    registerItem(ItemCategoryFlags.CLOUDY_FLASK.getRegistryKey(material, slurry_material), () -> new IGGenericBucketItem(() -> getFluid.apply(registryKey), blockCategory, ItemCategoryFlags.CLOUDY_FLASK, material, slurry_material));
                                }
                            }
                        }
                    }
                }

                if(flags instanceof ItemCategoryFlags itemCategoryFlags) {
                    switch (itemCategoryFlags) {
                        // Ore Item Types
                        case POOR_ORE, NORMAL_ORE, RICH_ORE->
                        {
                            if(hasExistingImplementation) continue;
                            registerItem(itemCategoryFlags.getRegistryKey(material), () -> new IGGenericOreItem(itemCategoryFlags, material));
                        }
                        case PELLET,OXIDE_PELLET ->
                        {
                            if(hasExistingImplementation) continue;
                            registerItem(itemCategoryFlags.getRegistryKey(material), () -> new IGItemPellet(itemCategoryFlags, material));
                        }
                        case DRILL_HEAD ->
                        {
                            registerItem(itemCategoryFlags.getRegistryKey(material), () -> new IGGenericDrillHead(itemCategoryFlags, material));
                        }
                        case TOOL_HOE ->
                        {
                            registerItem(itemCategoryFlags.getRegistryKey(material), () -> new IGCustomTool(material.getToolTier(), material.getToolDamage(), material.getToolSpeed(), itemCategoryFlags, material));
                        }
                        default -> {
                            if(hasExistingImplementation) continue;
                            registerItem(itemCategoryFlags.getRegistryKey(material), () -> new IGGenericItem(itemCategoryFlags, material));
                        }
                    }
                }
            }
        }

        ENERGY_PIPE = TE_REGISTER.register("energy_pipe_type", makeType(IGEnergyPipeEntity::new, () -> MiscEnum.Cable.getBlock(BlockCategoryFlags.ENERGY_PIPE)));
        METAL_DETECTOR = TE_REGISTER.register("metal_detector_type", makeType((pos, state) -> new IGMetalDetectorEntity(METAL_DETECTOR.get(), pos, state), () -> getBlock.apply(METAL_DETECTOR_KEY)));

        IGLib.IG_LOGGER.info("Finished");
    }

    public static MultiblockRegistration<?> getMB(String key){
        return MB_REGISTRY_MAP.get(key);
    }

    public static void initializeMultiblocks()
    {
        IGLib.IG_LOGGER.info("- Multiblocks");
        registerMB("gravity_separator", IGGravitySeparatorMultiblock.INSTANCE, IGMultiblockProvider.GRAVITY_SEPARATOR, IGGravitySeparatorSkins.class);
        registerMB("crystallizer", IGCrystalizerMultiblock.INSTANCE, IGMultiblockProvider.CRYSTALLIZER, IGCrystallizerSkins.class);
        registerMB("bloomery", IGBloomeryMultiblock.INSTANCE, IGMultiblockProvider.BLOOMERY, IGBloomerySkins.class);
        registerMB("chemical_reactor", IGChemicalReactorMultiblock.INSTANCE, IGMultiblockProvider.CHEMICAL_REACTOR, IGChemicalReactorSkins.class);
        registerMB("rotary_kiln", IGRotaryKilnMultiblock.INSTANCE, IGMultiblockProvider.ROTARYKILN, IGRotaryKilnSkins.class);
        registerMB("reverberation_furnace", IGReverberationFurnaceMultiblock.INSTANCE, IGMultiblockProvider.REVERBERATION_FURNACE, IGRevFurnaceSkins.class);
        registerMB("geothermal_exchanger", IGGeothermalExchangerMultiblock.INSTANCE, IGMultiblockProvider.GEOTHERMAL_EXCHANGER, IGGeothermalSkins.class);
        registerMB("pelletizer", IGPelletizerMultiblock.INSTANCE, IGMultiblockProvider.PELLETIZER, IGPelletizerSkins.class);
        registerMB("steam_turbine", IGSteamTurbineMultiblock.INSTANCE, IGMultiblockProvider.STEAM_TURBINE, IGSteamTurbineSkins.class);
        registerMB("small_chemical_reactor", IGSmallChemicalReactorMultiblock.INSTANCE, IGMultiblockProvider.SMALL_CHEMICAL_REACTOR, IGSmallChemicalReactorSkins.class);
        registerMB("alternator", IGAlternatorMultiblock.INSTANCE, IGMultiblockProvider.ALTERNATOR, IGAlternatorSkins.class);
        registerMB("coredrill", IGCoreDrillMultiblock.INSTANCE, IGMultiblockProvider.COREDRILL, IGCoreDrillSkins.class);

        registerMB("centrifuge", IGCentrifugeMultiblock.INSTANCE, IGMultiblockProvider.CENTRIFUGE);
        registerMB("ballmill", IGBallmillMultiblock.INSTANCE, IGMultiblockProvider.BALLMILL);
        registerMB("foundry", IGFoundryMultiblock.INSTANCE, IGMultiblockProvider.FOUNDRY);
    }

    private static <T extends Enum<T> & IIGMultiSkinHelper & StringRepresentable> void registerMB(String registry_name, TemplateMultiblock block, MultiblockRegistration<?> registration, Class<T> skins){

        for(T skin : skins.getEnumConstants())
        {
            registerItem(registry_name + "_multiblock_skin_" + skin.getSerializedName(), () -> new IGMultiblockSkinItem<>(ItemCategoryFlags.SKIN_COMPONENT, MetalEnum.Cobalt, skin, registry_name + "_multiblock_skin_" + skin.getSerializedName()));
        }

        registerMB(registry_name, block, registration);
    }
    private static void registerMB(String registry_name, TemplateMultiblock block, MultiblockRegistration<?> registration){
        registerMultiblockTemplate(registry_name, block);
        MB_REGISTRY_MAP.put(registry_name, registration);
    }

    public static Supplier<List<? extends Item>> supplyDeferredItems(){
        return () -> ITEM_REGISTER.getEntries().stream().map(RegistryObject::get).toList();
    }

    public static Supplier<List<? extends Block>> supplyDeferredBlocks(){
        return () -> BLOCK_REGISTER.getEntries().stream().map(RegistryObject::get).toList();
    }

    public static Supplier<List<? extends Fluid>> supplyDeferredFluids(){
        return () -> FLUID_REGISTER.getEntries().stream().map(RegistryObject::get).toList();
    }

    public static void registerMultiblockTemplate(String registry_name, TemplateMultiblock template)
    {
        MB_TEMPLATE_MAP.put(registry_name, registerMultiblock(template));
    }

    public static void registerItem(String registry_name,  Supplier<Item> itemSupplier){
        ITEM_REGISTRY_MAP.put(registry_name, ITEM_REGISTER.register(registry_name, itemSupplier));
    }

    public static void registerBlock(String registry_name,  Supplier<Block> blockSupplier){
        BLOCK_REGISTRY_MAP.put(registry_name, BLOCK_REGISTER.register(registry_name, blockSupplier));
    }

    public static void registerFluid(String registry_name,  Supplier<Fluid> fluidSupplier){
        FLUID_REGISTRY_MAP.put(registry_name, FLUID_REGISTER.register(registry_name, fluidSupplier));
    }

    public static void registerFluidType(String registry_name, Supplier<FluidType> fluidTypeSupplier)
    {
        FLUID_TYPE_REGISTRY_MAP.put(registry_name, FLUIDTYPE_REGISTER.register(registry_name, fluidTypeSupplier));
    }

    public static void addRegistersToEventBus(final IEventBus eventBus){

        IGLib.IG_LOGGER.info("======== Registration of Immersive Geology Items, Blocks and Fluids ========");
        IGLib.IG_LOGGER.info("- Block Registration");
        BLOCK_REGISTER.register(eventBus);
        IGLib.IG_LOGGER.info("- Item Registration");
        ITEM_REGISTER.register(eventBus);
        IGLib.IG_LOGGER.info("- Fluid Registration");
        FLUID_REGISTER.register(eventBus);
        IGLib.IG_LOGGER.info("- Fluid Type Registration");
        FLUIDTYPE_REGISTER.register(eventBus);
        IGLib.IG_LOGGER.info("- Tile Entity Registration");
        TE_REGISTER.register(eventBus);
        IGLib.IG_LOGGER.info("- Custom Creative Tab Registration");
        TAB_REGISTER.register(eventBus);
        IGLib.IG_LOGGER.info("- Custom Global Loot Modifier");
        LOOT_SERIALIZER_REGISTER.register(eventBus);
        IGLib.IG_LOGGER.info("- Custom Particle Type Registration");
        IGParticles.register(eventBus);
        IGLib.IG_LOGGER.info("- Custom Menu Type Registration");
        IGMenuTypes.REGISTER.register(eventBus);

        MOD_BUS_CALLBACKS.forEach(e -> e.accept(eventBus));
    }

    public static List<Item> getIGItems()
    {
        return ITEM_REGISTER.getEntries().stream().map(RegistryObject::get).collect(Collectors.toList());
    }

    public static <S extends IMultiblockState> MultiblockRegistration<S> registerMetalMultiblock(String name, IMultiblockLogic<S> logic, Supplier<TemplateMultiblock> structure){
        return registerMetalMultiblock(name, logic, structure, null);
    }

    public static <S extends IMultiblockState> MultiblockRegistration<S> registerMetalMultiblock(String name, IMultiblockLogic<S> logic, Supplier<TemplateMultiblock> structure, @Nullable Consumer<IGMultiblockBuilder<S>> extras){
        BlockBehaviour.Properties prop = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL)
                .strength(3, 15)
                .requiresCorrectToolForDrops()
                .isViewBlocking((state, blockReader, pos) -> false)
                .noOcclusion()
                .dynamicShape()
                .pushReaction(PushReaction.BLOCK);

        return registerMultiblock(name, logic, structure, extras, prop);
    }

    public static <S extends IMultiblockState> MultiblockRegistration<S> registerMultiblock(String name, IMultiblockLogic<S> logic, Supplier<TemplateMultiblock> structure, @Nullable Consumer<IGMultiblockBuilder<S>> extras, BlockBehaviour.Properties prop){
        IGMultiblockBuilder<S> builder = new IGMultiblockBuilder<>(logic, name)
                .structure(structure)
                .defaultBEs(TE_REGISTER)
                .defaultBlock(BLOCK_REGISTER, ITEM_REGISTER, prop);

        if(extras != null){
            extras.accept(builder);
        }

        return builder.build();
    }

    public static LinkedHashMap<String, RegistryObject<Item>> getItemRegistryMap() {
        return ITEM_REGISTRY_MAP;
    }

    public static LinkedHashMap<String, RegistryObject<Fluid>> getFluidRegistryMap()
    {
        return FLUID_REGISTRY_MAP;
    }

    public static HashMap<String, RegistryObject<Block>> getBlockRegistryMap() {
        return BLOCK_REGISTRY_MAP;
    }

    public static void buildMaterialRecipes()
    {
        IGLib.IG_LOGGER.info("- Building Material Recipes");
        IGLib.getGeologyMaterials().forEach(MaterialInterface::buildRecipe);
        MaterialHelper.logRecipeStages();
        IGLib.IG_LOGGER.info("- Complete");
    }

    public static RegistryObject<Codec<? extends IGlobalLootModifier>> IG_LOOT_MODIFICATION;

    public static void initializeLootModifications()
    {
        IG_LOOT_MODIFICATION = LOOT_SERIALIZER_REGISTER.register("ig_loot_modification", ()->IGLootModifier.CODEC);
    }

    protected static class MultiblockBuilder<S extends IMultiblockState> extends MultiblockRegistrationBuilder<S, MultiblockBuilder<S>>{
        public MultiblockBuilder(IMultiblockLogic<S> logic, String name){
            super(logic, ResourceUtils.ig(name));
        }

        public MultiblockBuilder<S> redstone(IMultiblockComponent.StateWrapper<S, RedstoneControl.RSState> getState, BlockPos... positions){
            redstoneAware();
            return selfWrappingComponent(new RedstoneControl<>(getState, positions));
        }

        public MultiblockBuilder<S> comparator(ComparatorManager<S> comparator){
            withComparator();
            return super.selfWrappingComponent(comparator);
        }

        @Override
        protected MultiblockBuilder<S> self(){
            return this;
        }
    }
    public static ResourceLocation getRegistryNameOf(Block block){
        return BuiltInRegistries.BLOCK.getKey(block);
    }
}
