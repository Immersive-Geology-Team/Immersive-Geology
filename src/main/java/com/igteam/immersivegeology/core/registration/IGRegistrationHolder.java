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
import com.igteam.immersivegeology.common.item.helper.IGFlagItem;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.lib.ResourceUtils;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialChemical;
import com.igteam.immersivegeology.core.material.helper.flags.*;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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

import static com.igteam.immersivegeology.client.menu.IGItemGroup.selectedGroup;

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
    public static final HashMap<String, TemplateMultiblock> MB_TEMPLATE_MAP = new HashMap<>();
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
            .displayItems(IGRegistrationHolder::fillIGTab)
            .build());

    private static void fillIGTab(IGItemGroup.ItemDisplayParameters parms, IGItemGroup.Output out)
    {
        HashMap<IFlagType<?>, ArrayList<Item>> itemMap = new HashMap<>();
        for (Item item : IGRegistrationHolder.getIGItems()) {
            if(item instanceof IGFlagItem type) {
                IFlagType<?> pattern = type.getFlag();
                if(type.getSubGroup() == selectedGroup) {
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

    public static void initialize()
    {
        initializeMultiblocks();
        for (MaterialInterface<?> material : IGLib.getGeologyMaterials()) {
            for(IFlagType<?> flags : material.getFlags()){
                // checks is the material has any ModFlags (e.g. Beyond Earth), if it does, check if none are loaded, if so skip material
                boolean hasExistingImplementation = material.instance().checkExistingImplementation(flags);
                if(flags instanceof BlockCategoryFlags blockCategory) {
                    switch (blockCategory) {
                        case DEFAULT_BLOCK, STORAGE_BLOCK, SHEETMETAL_BLOCK, DUST_BLOCK, GEODE_BLOCK -> {
                            if(hasExistingImplementation) continue;
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
                                if(checkModMaterialsForOverlap(base, material.instance(), flags)) continue;
                                // After all checks, now we can generate the different ore levels
                                for(OreRichness richness : OreRichness.values()){
                                    String registryKey = blockCategory.getRegistryKey(material, base, richness);
                                    Supplier<Block> blockProvider = () -> new IGOreBlock(blockCategory, base, material, richness);
                                    registerBlock(registryKey, blockProvider);
                                    registerItem(registryKey, () -> new IGGenericBlockItem((IGBlockType) getBlock.apply(registryKey)));
                                }
                            }
                        }

                        case SLAB -> {
                            if(hasExistingImplementation) continue;
                            String registryKey = blockCategory.getRegistryKey(material);
                            Supplier<Block> blockProvider = () -> new IGSlabBlock(blockCategory, material);
                            registerBlock(registryKey, blockProvider);
                            registerItem(registryKey, () -> new IGGenericBlockItem((IGBlockType) getBlock.apply(registryKey)));
                        }
                        case STAIRS -> {
                            if(hasExistingImplementation) continue;
                            String registryKey = blockCategory.getRegistryKey(material);
                            // A tad dirty of a hack, but this should cover 99% of cases for stairs at least
                            boolean isSheetmetal = material.getFlags().contains(BlockCategoryFlags.SHEETMETAL_BLOCK);
                            Supplier<Block> blockProvider = () -> new IGStairBlock(() -> getBlock.apply((isSheetmetal ? BlockCategoryFlags.SHEETMETAL_BLOCK : BlockCategoryFlags.STORAGE_BLOCK).getRegistryKey(material)).defaultBlockState(), material);
                            registerBlock(registryKey, blockProvider);
                            registerItem(registryKey, () -> new IGGenericBlockItem((IGBlockType) getBlock.apply(registryKey)));
                        }
                        case FLUID -> {
                            if(hasExistingImplementation) continue;
                            String registryKey = blockCategory.getRegistryKey(material);

                            // Still
                            registerFluid(registryKey, () -> new IGFluid.Source(material, null));
                            // Flowing
                            registerFluid(registryKey + "_flowing", () -> new IGFluid.Flowing(material, null));

                            // Fluid Type Registration
                            registerFluidType(registryKey, () -> getFluid.apply(registryKey).getFluidType());
                            registerItem(ItemCategoryFlags.BUCKET.getRegistryKey(material, blockCategory), () -> new IGGenericBucketItem(() -> getFluid.apply(registryKey), blockCategory, material));
                            registerBlock(registryKey + "_block", () -> new IGFluidBlock(() -> (FlowingFluid) getFluid.apply(registryKey), material, BlockBehaviour.Properties.copy(Blocks.WATER)));
                        }
                        case SLURRY ->
                        {
                            if(material.instance() instanceof MaterialChemical chemical)
                            {
                                for(MetalEnum metal : MetalEnum.values())
                                {
                                    if(!chemical.hasSlurryMetal(metal)) continue;
                                    String registryKey = blockCategory.getRegistryKey(material, metal);

                                    // Still
                                    registerFluid(registryKey, () -> new IGFluid.Source(material, metal));

                                    // Flowing
                                    registerFluid(registryKey + "_flowing", () -> new IGFluid.Flowing(material, metal));

                                    // Fluid Type Registration
                                    registerFluidType(registryKey, () -> getFluid.apply(registryKey).getFluidType());
                                    registerItem(ItemCategoryFlags.BUCKET.getRegistryKey(material, metal), () -> new IGGenericBucketItem(() -> getFluid.apply(registryKey), blockCategory, material, metal));
                                    registerBlock(registryKey + "_block", () -> new IGFluidBlock(() -> (FlowingFluid) getFluid.apply(registryKey), material, BlockBehaviour.Properties.copy(Blocks.WATER)));
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
                        default -> {
                            if(hasExistingImplementation) continue;
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
        registerMB("crystallizer", IGCrystalizerMultiblock.INSTANCE, IGMultiblockProvider.CRYSTALLIZER);
        registerMB("gravityseparator", IGGravitySeparatorMultiblock.INSTANCE, IGMultiblockProvider.GRAVITY_SEPARATOR);
        registerMB("rotarykiln", IGRotaryKilnMultiblock.INSTANCE, IGMultiblockProvider.ROTARYKILN);
        registerMB("coredrill", IGCoreDrillMultiblock.INSTANCE, IGMultiblockProvider.COREDRILL);
        registerMB("reverberation_furnace", IGReverberationFurnaceMultiblock.INSTANCE, IGMultiblockProvider.REVERBERATION_FURNACE);
        registerMB("industrial_sluice", IGIndustrialSluiceMultiblock.INSTANCE, IGMultiblockProvider.INDUSTRIAL_SLUICE);
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

    public static HashMap<String, RegistryObject<Fluid>> getFluidRegistryMap()
    {
        return FLUID_REGISTRY_MAP;
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
