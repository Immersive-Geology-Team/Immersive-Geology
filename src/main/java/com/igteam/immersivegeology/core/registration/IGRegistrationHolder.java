package com.igteam.immersivegeology.core.registration;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistrationBuilder;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.ComparatorManager;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IMultiblockComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import com.igteam.immersivegeology.client.menu.CreativeMenuHandler;
import com.igteam.immersivegeology.client.menu.IGItemGroup;
import com.igteam.immersivegeology.common.block.*;
import com.igteam.immersivegeology.common.block.IGOreBlock.OreRichness;
import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.common.block.multiblocks.*;
import com.igteam.immersivegeology.common.fluid.IGFluid;
import com.igteam.immersivegeology.common.item.IGGenericBlockItem;
import com.igteam.immersivegeology.common.item.IGGenericBucketItem;
import com.igteam.immersivegeology.common.item.IGGenericItem;
import com.igteam.immersivegeology.common.item.IGGenericOreItem;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.lib.ResourceUtils;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.flags.*;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class IGRegistrationHolder {
    private static final DeferredRegister<Block> BLOCK_REGISTER = DeferredRegister.create(Registries.BLOCK, IGLib.MODID);
    private static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(Registries.ITEM, IGLib.MODID);
    private static final DeferredRegister<Fluid> FLUID_REGISTER = DeferredRegister.create(Registries.FLUID, IGLib.MODID);
    private static final DeferredRegister<FluidType> FLUIDTYPE_REGISTER = DeferredRegister.create(Keys.FLUID_TYPES, IGLib.MODID);


    private static final DeferredRegister<BlockEntityType<?>> TE_REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, IGLib.MODID);
    public static final DeferredRegister<CreativeModeTab> TAB_REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, IGLib.MODID);



    private static final HashMap<String, RegistryObject<Block>> BLOCK_REGISTRY_MAP = new HashMap<>();
    private static final HashMap<String, RegistryObject<Item>> ITEM_REGISTRY_MAP = new HashMap<>();
    private static final HashMap<String, RegistryObject<Fluid>> FLUID_REGISTRY_MAP = new HashMap<>();
    private static final HashMap<String, RegistryObject<FluidType>> FLUID_TYPE_REGISTRY_MAP = new HashMap<>();

    public static HashMap<String, MultiblockRegistration<?>> MB_REGISTRY_MAP = new HashMap<>();
    private static HashMap<String, TemplateMultiblock> MB_TEMPLATE_MAP = new HashMap<>();
    private static <T extends MultiblockHandler.IMultiblock>
    T registerMultiblock(T multiblock) {
        MultiblockHandler.registerMultiblock(multiblock);
        return multiblock;
    }

    public static Function<String, Item> getItem = (key) -> ITEM_REGISTRY_MAP.get(key).get();
    public static Function<String, Block> getBlock = (key) -> BLOCK_REGISTRY_MAP.get(key).get();

    public static Function<String, TemplateMultiblock> getMBTemplate = (key) -> MB_TEMPLATE_MAP.get(key);
    public static Function<String, Fluid> getFluid = (key) -> FLUID_REGISTRY_MAP.get(key).get();

    public static final RegistryObject<CreativeModeTab> IG_BASE_TAB = TAB_REGISTER.register("main", () -> new IGItemGroup.Builder(CreativeModeTab.Row.TOP, 0)
            .icon(IGRegistrationHolder.getItem.apply(ItemCategoryFlags.GEAR.getRegistryKey(MetalEnum.Cobalt))::getDefaultInstance)
            .title(Component.translatable("itemGroup.immersivegeology"))
            .displayItems(CreativeMenuHandler::fillIGTab)
            .build());

    private static final List<Consumer<IEventBus>> MOD_BUS_CALLBACKS = new ArrayList<>();

    private static boolean checkModFlagsForMaterial(StoneEnum stoneType, GeologyMaterial ore)
    {
        Set<ModFlags> stoneModFlags = new HashSet<>();
        for(IFlagType<?> flag : stoneType.getFlags())
        {
            if(flag.getValue() instanceof ModFlags modFlag)
            {
                stoneModFlags.add(modFlag);
            }
        }
        Set<ModFlags> oreModFlags = new HashSet<>();
        for(IFlagType<?> flag : ore.getFlags())
        {
            if(flag.getValue() instanceof ModFlags modFlag)
            {
                oreModFlags.add(modFlag);
            }
        }

        Set<ModFlags> intersection = new HashSet<>(stoneModFlags);
        intersection.retainAll(oreModFlags);
        IGLib.IG_LOGGER.info("[{} {}]Retained Flags for {}, {}: Generate? {} intersection [{}]", stoneType.getName(), ore.getName(), stoneModFlags, oreModFlags, intersection.isEmpty(), intersection);
        return intersection.isEmpty();
    }

    public static void initialize()
    {
        initializeMultiblocks();
        for (MaterialInterface<?> material : IGLib.getGeologyMaterials()) {
            for(IFlagType<?> flags : material.getFlags()){
                // checks is the material has any ModFlags (e.g. Beyond Earth), if it does, check if none are loaded, if so skip material
                if(Arrays.stream(ModFlags.values()).anyMatch(material.getFlags()::contains) && material.getFlags().stream().noneMatch(ModFlags::isLoaded)) continue;

                if(flags instanceof BlockCategoryFlags blockCategory) {
                    switch (blockCategory) {
                        case DEFAULT_BLOCK, STORAGE_BLOCK, SHEETMETAL_BLOCK, DUST_BLOCK, GEODE_BLOCK -> {
                            if(material.getFlags().contains(MaterialFlags.EXISTING_IMPLEMENTATION)) continue;

                            String registryKey = blockCategory.getRegistryKey(material);
                            Supplier<Block> blockProvider = () -> new IGGenericBlock(blockCategory, material);
                            registerBlock(registryKey, blockProvider);
                            registerItem(registryKey, () -> new IGGenericBlockItem((IGGenericBlock) getBlock.apply(registryKey)));
                        }
                        case ORE_BLOCK -> {
                            // for each stone type: stoneMaterial needs to be implemented for each ore block
                            for (StoneEnum base : StoneEnum.values()) {
                                // checks is the material has any ModFlags (e.g. Beyond Earth)
                                if(!base.hasFlag(MaterialFlags.IS_ORE_BEARING)) continue;
                                if(!material.instance().acceptableStoneType(base.instance())) continue;
                                if(!checkModFlagsForMaterial(base, material.instance())) continue;
                                // After all checks, now we can generate the different ore levels
                                for(OreRichness richness : OreRichness.values()){
                                    String registryKey = blockCategory.getRegistryKey(material, base, richness);
                                    Supplier<Block> blockProvider = () -> new IGOreBlock(blockCategory, base, material, richness);
                                    registerBlock(registryKey, blockProvider);
                                    registerItem(registryKey, () -> new IGGenericBlockItem((IGBlockType)getBlock.apply(registryKey)));
                                }
                            }
                        }
                        case SLAB -> {
                            if(material.hasFlag(MaterialFlags.EXISTING_IMPLEMENTATION)) continue;
                            String registryKey = blockCategory.getRegistryKey(material);
                            Supplier<Block> blockProvider = () -> new IGSlabBlock(blockCategory, material);
                            registerBlock(registryKey, blockProvider);
                            registerItem(registryKey, () -> new IGGenericBlockItem((IGBlockType) getBlock.apply(registryKey)));
                        }
                        case STAIRS -> {
                            if(material.hasFlag(MaterialFlags.EXISTING_IMPLEMENTATION)) continue;
                            String registryKey = blockCategory.getRegistryKey(material);
                            // A tad dirty of a hack, but this should cover 99% of cases for stairs at least
                            boolean isSheetmetal = material.getFlags().contains(BlockCategoryFlags.SHEETMETAL_BLOCK);
                            Supplier<Block> blockProvider = () -> new IGStairBlock(() -> getBlock.apply((isSheetmetal ? BlockCategoryFlags.SHEETMETAL_BLOCK : BlockCategoryFlags.STORAGE_BLOCK).getRegistryKey(material)).defaultBlockState(), material);
                            registerBlock(registryKey, blockProvider);
                            registerItem(registryKey, () -> new IGGenericBlockItem((IGBlockType) getBlock.apply(registryKey)));
                        }
                        case FLUID -> {
                            String registryKey = blockCategory.getRegistryKey(material);

                            // Fluid Type Registration
                            registerFluidType(registryKey, () -> getFluid.apply(registryKey).getFluidType());

                            // Still
                            registerFluid(registryKey, () -> new IGFluid.Source(material));

                            // Flowing
                            registerFluid(registryKey + "_flowing", () -> new IGFluid.Flowing(material));

                            registerItem(ItemCategoryFlags.BUCKET.getRegistryKey(material), () -> new IGGenericBucketItem(() -> getFluid.apply(registryKey), ItemCategoryFlags.BUCKET, material));

                            registerBlock(registryKey + "_block", () -> new IGFluidBlock(() -> (FlowingFluid) getFluid.apply(registryKey), material, BlockBehaviour.Properties.copy(Blocks.WATER)));
                        }
                    }
                }

                if(flags instanceof ItemCategoryFlags itemCategoryFlags) {
                    switch (itemCategoryFlags) {
                        case POOR_ORE, NORMAL_ORE, RICH_ORE ->
                        {
                            if(material.hasFlag(MaterialFlags.EXISTING_IMPLEMENTATION)) continue;
                            registerItem(itemCategoryFlags.getRegistryKey(material), () -> new IGGenericOreItem(itemCategoryFlags, material));
                        }
                        case INGOT, PLATE, CRYSTAL, DUST, COMPOUND_DUST, CLAY, CRUSHED_ORE, DIRTY_CRUSHED_ORE, GEAR, SLAG, ROD, WIRE, NUGGET, METAL_OXIDE -> {

                            if(material.hasFlag(MaterialFlags.EXISTING_IMPLEMENTATION)) continue;
                            registerItem(itemCategoryFlags.getRegistryKey(material), () -> new IGGenericItem(itemCategoryFlags, material));
                        }
                    }
                }
            }
        }

    }

    public static MultiblockRegistration<?> getMB(String key){
        return MB_REGISTRY_MAP.get(key);
    }

    private static void initializeMultiblocks()
    {
        registerMB("crystallizer", new IGCrystalizerMultiblock(), IGMultiblockProvider.CRYSTALLIZER);
        registerMB("gravityseparator", new IGGravitySeparatorMultiblock(), IGMultiblockProvider.GRAVITY_SEPARATOR);
        registerMB("rotarykiln", new IGRotaryKilnMultiblock(), IGMultiblockProvider.ROTARYKILN);
        registerMB("coredrill", new IGCoreDrillMultiblock(), IGMultiblockProvider.COREDRILL);
        registerMB("reverberation_furnace", new IGReverberationFurnaceMultiblock(), IGMultiblockProvider.REVERBERATION_FURNACE);
    }

    private static void registerMB(String registry_name, IGTemplateMultiblock block, MultiblockRegistration<?> registration){
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
        BLOCK_REGISTER.register(eventBus);
        ITEM_REGISTER.register(eventBus);
        FLUID_REGISTER.register(eventBus);
        FLUIDTYPE_REGISTER.register(eventBus);
        TE_REGISTER.register(eventBus);
        TAB_REGISTER.register(eventBus);

        MOD_BUS_CALLBACKS.forEach(e -> e.accept(eventBus));
    }

    public static List<Item> getIGItems()
    {
        return ITEM_REGISTER.getEntries().stream().map(RegistryObject::get).collect(Collectors.toList());
    }

    public static <S extends IMultiblockState> MultiblockRegistration<S> registerMetalMultiblock(String name, IMultiblockLogic<S> logic, Supplier<TemplateMultiblock> structure){
        return registerMetalMultiblock(name, logic, structure, null);
    }

    public static <S extends IMultiblockState> MultiblockRegistration<S> registerMetalMultiblock(String name, IMultiblockLogic<S> logic, Supplier<TemplateMultiblock> structure, @Nullable Consumer<MultiblockBuilder<S>> extras){
        BlockBehaviour.Properties prop = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL)
                .strength(3, 15)
                .requiresCorrectToolForDrops()
                .isViewBlocking((state, blockReader, pos) -> false)
                .noOcclusion()
                .dynamicShape()
                .pushReaction(PushReaction.BLOCK);

        return registerMultiblock(name, logic, structure, extras, prop);
    }

    public static <S extends IMultiblockState> MultiblockRegistration<S> registerMultiblock(String name, IMultiblockLogic<S> logic, Supplier<TemplateMultiblock> structure, @Nullable Consumer<MultiblockBuilder<S>> extras, BlockBehaviour.Properties prop){
        MultiblockBuilder<S> builder = new MultiblockBuilder<>(logic, name)
                .structure(structure)
                .defaultBEs(TE_REGISTER)
                .defaultBlock(BLOCK_REGISTER, ITEM_REGISTER, prop);

        if(extras != null){
            extras.accept(builder);
        }

        return builder.build();
    }

    public static HashMap<String, RegistryObject<Item>> getItemRegistryMap() {
        return ITEM_REGISTRY_MAP;
    }

    public static HashMap<String, RegistryObject<Block>> getBlockRegistryMap() {
        return BLOCK_REGISTRY_MAP;
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
