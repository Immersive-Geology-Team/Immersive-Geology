package com.igteam.immersivegeology.core.registration;

import com.igteam.immersivegeology.core.material.data.stone.IGStoneTypes;
import com.igteam.immersivegeology.core.material.helper.material.IStoneType;
import blusunrize.immersiveengineering.api.IEApi;
import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.api.excavator.ExcavatorHandler;
import blusunrize.immersiveengineering.api.excavator.MineralMix;
import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler;
import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler.ChemthrowerEffect;
import blusunrize.immersiveengineering.common.fluids.IEFluid;
import blusunrize.immersiveengineering.common.register.IEFluids;
import blusunrize.immersiveengineering.common.register.IEPotions;
import blusunrize.lib.manual.*;
import blusunrize.lib.manual.ManualEntry.EntryData;
import blusunrize.lib.manual.ManualEntry.SpecialElementData;
import blusunrize.lib.manual.Tree.InnerNode;
import com.igteam.immersivegeology.client.manual.IGMaterialShowcase;
import com.igteam.immersivegeology.client.manual.IGRecipeOverview;
import com.igteam.immersivegeology.client.menu.IGCrateScreen;
import com.igteam.immersivegeology.client.menu.IGMetalDetectorScreen;
import com.igteam.immersivegeology.client.menu.multiblock.*;
import com.igteam.immersivegeology.common.block.entity.cable.IGEnergyPipeEntity;
import com.igteam.immersivegeology.common.block.helper.IOreBlock;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.common.config.IGServerConfig.Ores.OreConfig;
import com.igteam.immersivegeology.common.world.features.helper.noise.IGGenerationType;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeChain;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.data.ForgeRecipeProvider;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;

public class IGContent {

    public static void initializeIETweaks()
    {
        IGLib.IG_LOGGER.info("======== Registration of Immersive Geology IE Tweaks ========");
        IGLib.IG_LOGGER.info("- Custom Chemical Thrower Recipes and Effects");

        ChemthrowerHandler.registerEffect(ChemicalEnum.ChemicalWaste.getFluidTag(), new ChemthrowerEffect()
        {
            @Override
            public void applyToEntity(LivingEntity livingEntity, @Nullable Player player, ItemStack itemStack, Fluid fluid)
            {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 50));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 140));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 100,1));

				RandomSource rand = livingEntity.getRandom();
                if(rand.nextFloat() > 0.5f) livingEntity.addEffect(new MobEffectInstance(MobEffects.HARM, 1));
            }

            @Override
            public void applyToBlock(Level level, HitResult hitResult, @Nullable Player player, ItemStack itemStack, Fluid fluid)
            {
                Vec3 vec = hitResult.getLocation();
                BlockPos loc = new BlockPos((int)vec.x(), (int)vec.y(), (int)vec.z()).below();
                BlockState state = level.getBlockState(new BlockPos(loc));
                RandomSource random = level.getRandom();
                if(state.is(Blocks.GRASS_BLOCK))
                {
                    level.setBlock(loc, Blocks.DIRT.defaultBlockState(), 3);
                }
                if(state.is(Blocks.TALL_GRASS) || state.is(Blocks.GRASS) || state.is(Blocks.FERN) || state.is(Blocks.LARGE_FERN))
                {
                    level.setBlock(loc, Blocks.AIR.defaultBlockState(), 0);
                }
                if(state.is(Blocks.COARSE_DIRT))
                {
                    level.setBlock(loc, Blocks.DIRT.defaultBlockState(), 3);
                }
                if(state.is(Blocks.DIRT) && random.nextInt(10) == 1)
                {
                    level.setBlock(loc, Blocks.GRAVEL.defaultBlockState(), 3);
                }

                if(state.is(Blocks.GRAVEL) && random.nextInt(15) == 1)
                {
                    level.setBlock(loc, Blocks.SAND.defaultBlockState(), 3);
                }
            }
        });

        ChemthrowerHandler.registerEffect(ChemicalEnum.SulfuricAcid.getFluidTag(), new ChemthrowerEffect()
        {
            @Override
            public void applyToEntity(LivingEntity livingEntity, @Nullable Player player, ItemStack itemStack, Fluid fluid)
            {
                if(!(livingEntity instanceof Skeleton))
                {
                    livingEntity.setSecondsOnFire(5);
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, 10, 0));
                    livingEntity.addEffect(new MobEffectInstance(IEPotions.FLAMMABLE.get(), 20 * 5,0));
                }
            }

            @Override
            public void applyToBlock(Level level, HitResult hitResult, @Nullable Player player, ItemStack itemStack, Fluid fluid)
            {

            }
        });

        IGEnergyPipeEntity.initCovers();
        IGLib.IG_LOGGER.info("Finished");
    }

    public static void registerContainersAndScreens()
    {
        MenuScreens.register(IGMenuTypes.BLOOMERY.getType(), BloomeryScreen::new);
        MenuScreens.register(IGMenuTypes.REVERBERATION_FURNACE.getType(), ReverberationScreen::new);
        MenuScreens.register(IGMenuTypes.GEOTHERMAL_EXCHANGER.getType(), GeothermalExchangerScreen::new);
        MenuScreens.register(IGMenuTypes.CRYSTALLIZER.getType(), CrystallizerScreen::new);
        MenuScreens.register(IGMenuTypes.CHEMICAL_REACTOR.getType(), ChemicalReactorScreen::new);
        MenuScreens.register(IGMenuTypes.SMALL_CHEMICAL_REACTOR.getType(), SmallChemicalReactorScreen::new);
        MenuScreens.register(IGMenuTypes.ROTARY_KILN.getType(), RotaryKilnScreen::new);
        MenuScreens.register(IGMenuTypes.BULK_BLAST_FURNACE.getType(), BulkBlastFurnaceScreen::new);
        MenuScreens.register(IGMenuTypes.FOUNDRY.getType(), FoundryScreen::new);
        MenuScreens.register(IGMenuTypes.CRATE.get(), IGCrateScreen.StandardIGCrate::new);
        MenuScreens.register(IGMenuTypes.METAL_DETECTOR.get(), IGMetalDetectorScreen::new);
    }

    public static void initializeManualEntries()
    {
        ManualInstance instance = ManualHelper.getManual();

        instance.registerSpecialElement(new ResourceLocation(IGLib.MODID, "recipe_overview"), s ->
        {
            String mineral_name = GsonHelper.getAsString(s, "mineral");
            GeologyMaterial material = MineralEnum.valueOf(mineral_name).instance();
            int priority = GsonHelper.getAsInt(s, "priority");
            return new IGRecipeOverview(instance, material, priority);
        });

        InnerNode<ResourceLocation, ManualEntry> parent_category = instance.getRoot().getOrCreateSubnode(new ResourceLocation(IGLib.MODID, "main"), 99);

        ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(ManualHelper.getManual());
        builder.readFromFile(new ResourceLocation(IGLib.MODID, "intro"));
        instance.addEntry(parent_category, builder.create());

        InnerNode<ResourceLocation, ManualEntry> multiblock_category = parent_category.getOrCreateSubnode(new ResourceLocation(IGLib.MODID, "ig_multiblocks"), 0);
        multiblockEntry(instance, multiblock_category, "crystallizer");
        multiblockEntry(instance, multiblock_category, "coredrill");
        multiblockEntry(instance, multiblock_category, "gravity_separator");
        multiblockRotaryKilnEntry(instance, multiblock_category, "rotary_kiln");
        multiblockEntry(instance, multiblock_category, "reverberation_furnace");
        multiblockEntry(instance, multiblock_category, "bulk_blast_furnace");
        multiblockEntry(instance, multiblock_category, "geothermal_exchanger");
        multiblockEntry(instance, multiblock_category, "bloomery");
        multiblockEntry(instance, multiblock_category, "foundry");
        multiblockEntry(instance, multiblock_category, "chemical_reactor");
        multiblockEntry(instance, multiblock_category, "ballmill");
        multiblockEntry(instance, multiblock_category, "centrifuge");
        multiblockEntry(instance, multiblock_category, "pelletizer");
        multiblockEntry(instance, multiblock_category, "steam_turbine");
        multiblockEntry(instance, multiblock_category, "alternator");
        multiblockEntry(instance, multiblock_category, "small_chemical_reactor");

        // Build the manual entry for the contributors
        builder.readFromFile(new ResourceLocation(IGLib.MODID, "getting_started"));
        instance.addEntry(parent_category, builder.create());

        builder.readFromFile(new ResourceLocation(IGLib.MODID, "bug_bounty_contributors"));
        instance.addEntry(parent_category, builder.create());
        InnerNode<ResourceLocation, ManualEntry> geology = parent_category.getOrCreateSubnode(new ResourceLocation(IGLib.MODID, "ig_geology"), 2);

        InnerNode<ResourceLocation, ManualEntry> overworld = geology.getOrCreateSubnode(new ResourceLocation(IGLib.MODID, "overworld"), 0);
        InnerNode<ResourceLocation, ManualEntry> nether = geology.getOrCreateSubnode(new ResourceLocation(IGLib.MODID, "nether"), 1);
        InnerNode<ResourceLocation, ManualEntry> the_end = geology.getOrCreateSubnode(new ResourceLocation(IGLib.MODID, "the_end"), 2);

        Map<MaterialInterface<?>, ManualEntry> materialEntries = new LinkedHashMap<>();
        List<MaterialInterface<?>> materials = IGLib.getGeneratedMaterials();
        for(MaterialInterface<?> mineral : materials)
        {
            List<InnerNode<ResourceLocation, ManualEntry>> hosts = new ArrayList<>();
            if(mineral.instance().acceptableStoneType(StoneEnum.MCStone)) hosts.add(overworld);
            if(mineral.instance().acceptableStoneType(StoneEnum.MCNetherrack)) hosts.add(nether);
            if(mineral.instance().acceptableStoneType(StoneEnum.MCEndStone)) hosts.add(the_end);
            if(hosts.isEmpty()) continue;

            ManualEntry entry = materialEntries.computeIfAbsent(mineral, IGContent::materialEntry);
            for(InnerNode<ResourceLocation, ManualEntry> host : hosts) instance.addEntry(host, entry);
        }

        InnerNode<ResourceLocation, ManualEntry> metal_category = parent_category.getOrCreateSubnode(new ResourceLocation(IGLib.MODID, "metals"), 3);
        for(MaterialInterface<?> metal : metals)
        {
            if(itemForms(metal).isEmpty()) continue;
            instance.addEntry(metal_category, materialEntries.computeIfAbsent(metal, IGContent::materialEntry));
        }

//        InnerNode<ResourceLocation, ManualEntry> chemical_entries = processing_chains.getOrCreateSubnode(new ResourceLocation(IGLib.MODID, "ig_chemical_chains"), 3);
    }

    private static final Set<String> LINKABLE_ENTRIES = new HashSet<>();

    private static ManualEntry materialEntry(MaterialInterface<?> material)
    {
        LINKABLE_ENTRIES.add(material.getName());
        ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(ManualHelper.getManual());
        builder.setLocation(new ResourceLocation(IGLib.MODID, material.getName()));
        builder.setContent(() -> createMineralContent(material));
        return builder.create();
    }

    protected static EntryData createMineralContent(MaterialInterface<?> material)
    {
        ArrayList<SpecialElementData> itemList = new ArrayList<>();
        StringBuilder contentBuilder = new StringBuilder();

        createRecipeChainPage(contentBuilder, itemList, material);

        String translatedTitle = I18n.get("manual.immersivegeology." + material.getName());
        String formattedContent = contentBuilder.toString().replaceAll("\r\n|\r|\n", "\n");
        return new EntryData(translatedTitle, "", formattedContent, itemList);
    }
    static List<MaterialInterface<?>> metals = List.of(MetalEnum.values());
    private static void createRecipeChainPage(StringBuilder contentBuilder, ArrayList<SpecialElementData> itemList, MaterialInterface<?> material)
    {
        List<IGRecipeChain> recipe_chain_data = material.instance().getRecipeChains().stream().sorted(Comparator.comparingInt(IGRecipeChain::getPriority)).toList();
        OreConfig config = IGServerConfig.ORES.ores.get(material.getConfig());
        List<MaterialInterface<?>> primarySources = sourcesOf(material, true);
        List<MaterialInterface<?>> secondarySources = sourcesOf(material, false);

        contentBuilder.append("<&item_display>").append(describeMaterial(material, config, primarySources, secondarySources));
        String derivedString = joinNames(material.getDerivedMaterials().stream().toList());

        if(config!=null)
        {
            contentBuilder.append("<np>").append("<&list>");
            itemList.add(new SpecialElementData("list", 0, new ManualElementTable(ManualHelper.getManual(), formatTable(getOreConfigTable(config, material.getDefaultNoiseProbability()), ""), true)));
        }

        appendPrimarySourcePage(contentBuilder, itemList, material, primarySources);
        appendSecondarySourcePage(contentBuilder, material, secondarySources);
        appendExtraSourcePage(contentBuilder, material);

        if(!derivedString.isEmpty())
        {
            contentBuilder.append("<np>").append(Component.translatable("manual.immersivegeology.generic.pre_chain_desc", material.getTranslationName(), derivedString).getString());
        }

        for(int i = 0; i < recipe_chain_data.size(); i++)
        {
            if(i == 0) contentBuilder.append("<np>");
            IGRecipeChain chain = recipe_chain_data.get(i);
            contentBuilder.append("<&").append(chain.getName()).append(">");
            if(i < (recipe_chain_data.size() - 1))
                contentBuilder.append("<np>");

            itemList.add(new SpecialElementData(chain.getName(), 0, new IGRecipeOverview(ManualHelper.getManual(), material.instance(), chain)));
        }

        NonNullList<ItemStack> displayStacks = NonNullList.create();

        if(material.instance() instanceof MaterialMetal)
        {
            addStack(displayStacks, material, BlockCategoryFlags.STORAGE_BLOCK);
            addStack(displayStacks, material, ItemCategoryFlags.INGOT);
        }

        if(displayStacks.isEmpty()&&material.hasFlag(BlockCategoryFlags.ORE_BLOCK))
        {
            for(IStoneType stone : IGStoneTypes.all())
            {
                if(!material.instance().acceptableStoneType(stone.instance())) continue;
                if(!stone.isVanilla()) continue;
                IOreBlock ore = material.getOreBlock(stone, OreRichness.NORMAL);
                if(ore == null) continue;
                displayStacks.add(new ItemStack(ore.asIGItem(), 1));
            }
        }

        if(displayStacks.isEmpty()) displayStacks.addAll(itemForms(material));

        itemList.add(new SpecialElementData("item_display", 0, new ManualElementItem(ManualHelper.getManual(), displayStacks)));
    }

    private static String describeMaterial(MaterialInterface<?> material, OreConfig config, List<MaterialInterface<?>> primarySources, List<MaterialInterface<?>> secondarySources)
    {
        StringJoiner description = new StringJoiner(Component.translatable("formatting.space").getString());
        if(config!=null)
            description.add(Component.translatable("manual.immersivegeology.generic.desc", material.getTranslationName()).getString());

        List<MaterialInterface<?>> named = primarySources.isEmpty()?secondarySources: primarySources;
        if(!named.isEmpty())
            description.add(Component.translatable("manual.immersivegeology.generic.source_desc",
                    material.getTranslationName(), joinNames(named)).getString());
        else if(config==null)
            description.add(Component.translatable("manual.immersivegeology.generic.no_source_desc",
                    material.getTranslationName()).getString());

        return description.toString();
    }

    private static void appendPrimarySourcePage(StringBuilder contentBuilder, ArrayList<SpecialElementData> itemList, MaterialInterface<?> material, List<MaterialInterface<?>> sources)
    {
        if(sources.isEmpty()) return;

        contentBuilder.append("<np>")
                .append(Component.translatable("manual.immersivegeology.generic.primary_sources").getString())
                .append("\n")
                .append(Component.translatable("manual.immersivegeology.generic.primary_sources.desc",
                        material.getTranslationName(), linkNames(sources)).getString());

        List<ItemStack> variants = oreVariants(sources);
        if(variants.isEmpty()) return;

        contentBuilder.append("<&primary_sources>");
        itemList.add(new SpecialElementData("primary_sources", 0, new IGMaterialShowcase(ManualHelper.getManual(), variants)));
    }

    private static void appendSecondarySourcePage(StringBuilder contentBuilder, MaterialInterface<?> material, List<MaterialInterface<?>> sources)
    {
        if(sources.isEmpty()) return;

        contentBuilder.append("<np>")
                .append(Component.translatable("manual.immersivegeology.generic.secondary_sources").getString())
                .append("\n")
                .append(Component.translatable("manual.immersivegeology.generic.secondary_sources.desc",
                        material.getTranslationName(), linkNames(sources)).getString());
    }

    private static void appendExtraSourcePage(StringBuilder contentBuilder, MaterialInterface<?> material)
    {
        String key = "manual.immersivegeology."+material.getName()+".extra_source";
        if(!I18n.exists(key)) return;
        contentBuilder.append("<np>").append(I18n.get(key));
    }

    private static List<ItemStack> oreVariants(List<MaterialInterface<?>> sources)
    {
        List<ItemStack> variants = new ArrayList<>();
        for(MaterialInterface<?> source : sources)
        {
            if(!source.hasFlag(BlockCategoryFlags.ORE_BLOCK))
            {
                ItemStack stack = representativeStack(source);
                if(!stack.isEmpty()) variants.add(stack);
                continue;
            }

            for(IStoneType stone : IGStoneTypes.all())
            {
                if(!source.instance().acceptableStoneType(stone.instance())) continue;
                IOreBlock ore = source.getOreBlock(stone, OreRichness.NORMAL);
                if(ore==null) continue;
                variants.add(new ItemStack(ore.asIGItem(), 1));
            }
        }
        return variants;
    }

    private static void addStack(NonNullList<ItemStack> stacks, MaterialInterface<?> material, IFlagType<?> flag)
    {
        if(!material.hasFlag(flag)) return;
        ItemStack stack = material.getStack(flag);
        if(stack==null||stack.isEmpty()||stack.is(Items.COOKIE)) return;
        stacks.add(stack);
    }

    private static List<MaterialInterface<?>> sourcesOf(MaterialInterface<?> product, boolean primary)
    {
        List<MaterialInterface<?>> sources = new ArrayList<>();
        for(MaterialInterface<?> source : IGLib.getGeologyMaterials())
        {
            if(source.equals(product)) continue;
            int index = source.getDerivedMaterials().stream().toList().indexOf(product);
            if(index < 0) continue;
            if((index==0)!=primary) continue;
            sources.add(source);
        }
        return sources;
    }

    private static ItemStack representativeStack(MaterialInterface<?> material)
    {
        if(material.hasFlag(BlockCategoryFlags.ORE_BLOCK))
            for(IStoneType stone : IGStoneTypes.all())
            {
                if(!material.instance().acceptableStoneType(stone.instance())) continue;
                if(!stone.isVanilla()) continue;
                IOreBlock ore = material.getOreBlock(stone, OreRichness.NORMAL);
                if(ore==null) continue;
                return new ItemStack(ore.asIGItem(), 1);
            }

        NonNullList<ItemStack> forms = itemForms(material);
        return forms.isEmpty()?ItemStack.EMPTY: forms.get(0);
    }

    private static String linkNames(List<MaterialInterface<?>> materials)
    {
        List<String> names = new ArrayList<>();
        for(MaterialInterface<?> material : materials)
            names.add(LINKABLE_ENTRIES.contains(material.getName())
                    ?"<link;"+IGLib.MODID+":"+material.getName()+";"+material.getTranslationName()+">"
                    : material.getTranslationName());
        return joinStrings(names);
    }

    private static String joinNames(List<MaterialInterface<?>> materials)
    {
        return joinStrings(materials.stream().map(MaterialInterface::getTranslationName).toList());
    }

    private static String joinStrings(List<String> entries)
    {
        StringBuilder names = new StringBuilder();
        for(int index = 0; index < entries.size(); index++)
        {
            names.append(entries.get(index));
            if(entries.size() <= 1) continue;
            if(index==entries.size()-2)
            {
                names.append(Component.translatable("formatting.space").getString());
                names.append(Component.translatable("formatting.and").getString());
                names.append(Component.translatable("formatting.space").getString());
            }
            else if(index < entries.size()-1) names.append(", ");
        }
        return names.toString();
    }

    private static final List<ItemCategoryFlags> ITEM_FORMS = List.of(
            ItemCategoryFlags.INGOT, ItemCategoryFlags.NUGGET, ItemCategoryFlags.PLATE,
            ItemCategoryFlags.ROD, ItemCategoryFlags.WIRE, ItemCategoryFlags.GEAR,
            ItemCategoryFlags.GRIT, ItemCategoryFlags.POWDER, ItemCategoryFlags.PELLET,
            ItemCategoryFlags.METAL_OXIDE, ItemCategoryFlags.COMPOUND_DUST
    );

    private static NonNullList<ItemStack> itemForms(MaterialInterface<?> material)
    {
        NonNullList<ItemStack> stacks = NonNullList.create();
        for(ItemCategoryFlags flag : ITEM_FORMS)
        {
            if(!material.hasFlag(flag)) continue;
            ItemStack stack = material.getStack(flag);
            if(stack.isEmpty()||stack.is(Items.COOKIE)) continue;
            stacks.add(stack);
        }
        return stacks;
    }

    public static HashMap<Component, Double> getOreConfigTable(OreConfig config, float noise_probability) {
        LinkedHashMap<Component, Double> map = new LinkedHashMap<>();

        map.put(Component.translatable("manual.immersivegeology.can_spawn"), config.canSpawn.get() ? 1.0 : 0.0);

        double chunk_probability = (double) config.generationChance.get()/2_000_000;
        double finalProb = noise_probability*chunk_probability*(64 * 64);
        map.put(Component.translatable("manual.immersivegeology.generation_probability"), finalProb);

        map.put(Component.translatable("manual.immersivegeology.min_y"), Double.valueOf(config.minY.get()));
        map.put(Component.translatable("manual.immersivegeology.max_y"), Double.valueOf(config.maxY.get()));

        map.put(Component.translatable("manual.immersivegeology.min_temp"), config.min_temp.get());
        map.put(Component.translatable("manual.immersivegeology.max_temp"), config.max_temp.get());

        map.put(Component.translatable("manual.immersivegeology.min_rainfall"), config.min_downfall.get());
        map.put(Component.translatable("manual.immersivegeology.max_rainfall"), config.max_downfall.get());

        map.put(Component.translatable("manual.immersivegeology.density"), config.density.get());
        map.put(Component.translatable("manual.immersivegeology.vein_size"), config.veinSize.get().doubleValue());
        map.put(Component.translatable("manual.immersivegeology.generation_type"), (double)config.generationPattern.get().ordinal());

        return map;
    }

    static Component[][] formatTable(Map<Component, Double> map, String valueType) {
        List<Map.Entry<Component, Double>> sortedMapArray = new ArrayList<>(map.entrySet());
        ArrayList<Component[]> list = new ArrayList<>();

        try {

			for(Entry<Component, Double> entry : sortedMapArray)
			{
				Component item = entry.getKey();
				if(item==null)
				{
					item = Component.nullToEmpty((entry.getKey()).toString());
				}

				String bt = String.valueOf(entry.getValue());
                if(item.toString().contains("manual.immersivegeology.generation_type"))
                {
                    int ordinal = entry.getValue().intValue();
                    bt = IGGenerationType.values()[ordinal].name();
                }
                if(item.toString().contains("can_spawn"))
                {
                    bt = entry.getValue().intValue() == 0 ? "False" : "True";
                }
                if(item.toString().contains("density") || item.toString().contains("generation_probability"))
                {
                    bt = new DecimalFormat("###.##").format((entry.getValue() * 100)) + "%";
                }

				Component am = Component.nullToEmpty(""+bt+" "+valueType);
				list.add(new Component[]{item, am});
			}
        } catch (Exception var9) {
        }
        return (Component[][])list.toArray(new Component[0][]);
    }
    static Component[][] formatBasicTable(Map<Component, String> map, String valueType) {
        List<Map.Entry<Component, String>> sortedMapArray = new ArrayList<>(map.entrySet());
        ArrayList<Component[]> list = new ArrayList<>();

        try {

            for(Entry<Component, String> entry : sortedMapArray)
            {
                Component item = entry.getKey();
                if(item==null)
                {
                    item = Component.nullToEmpty((entry.getKey()).toString());
                }

                String bt = String.valueOf(entry.getValue());
                Component am = Component.nullToEmpty(""+bt+" "+valueType);
                list.add(new Component[]{item, am});
            }
        } catch (Exception var9) {
        }
        return (Component[][])list.toArray(new Component[0][]);
    }

    public static HashMap<Component, String> getEnergyRates() {
        LinkedHashMap<Component, String> map = new LinkedHashMap<>();

        map.put(Component.translatable("manual.immersivegeology.lv_heat"), "< 750");
        map.put(Component.translatable("manual.immersivegeology.mv_heat"), "< 3000");
        map.put(Component.translatable("manual.immersivegeology.hv_heat"), "< 12000");
        map.put(Component.translatable("manual.immersivegeology.ehv_heat"),"> 12000");

        return map;
    }

    private static void multiblockEntry(ManualInstance instance, InnerNode<ResourceLocation, ManualEntry> category, String id){
        ManualEntry.ManualEntryBuilder multiblock = new ManualEntry.ManualEntryBuilder(ManualHelper.getManual());
        multiblock.readFromFile(new ResourceLocation(IGLib.MODID, id));
        instance.addEntry(category, multiblock.create());
    }

    private static void multiblockRotaryKilnEntry(ManualInstance instance, InnerNode<ResourceLocation, ManualEntry> category, String id){
        ManualEntry.ManualEntryBuilder multiblock = new ManualEntry.ManualEntryBuilder(ManualHelper.getManual());
        multiblock.readFromFile(new ResourceLocation(IGLib.MODID, id));
        multiblock.addSpecialElement(new SpecialElementData("list", 0, new ManualElementTable(instance, formatBasicTable(getEnergyRates(), "fe/t"), true)));
        instance.addEntry(category, multiblock.create());
    }

    ChemthrowerEffect acidic = new ChemthrowerEffect()
    {
        @Override
        public void applyToEntity(LivingEntity livingEntity, @Nullable Player player, ItemStack itemStack, Fluid fluid)
        {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40));
            livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 40, 1));
        }

        @Override
        public void applyToBlock(Level level, HitResult hitResult, @Nullable Player player, ItemStack itemStack, Fluid fluid)
        {
            Vec3 vec = hitResult.getLocation();
            BlockPos loc = new BlockPos((int)vec.x(), (int)vec.y(), (int)vec.z());
            BlockState state = level.getBlockState(new BlockPos(loc));
            if(state.is(Blocks.GRASS_BLOCK))
            {
                level.setBlock(loc, Blocks.DIRT.defaultBlockState(), 0);
            }

            if(state.is(Blocks.TALL_GRASS) || state.is(Blocks.GRASS) || state.is(Blocks.FERN) || state.is(Blocks.LARGE_FERN))
            {
                level.setBlock(loc, Blocks.AIR.defaultBlockState(), 0);
            }
        }
    };
}
