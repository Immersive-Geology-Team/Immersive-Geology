package com.igteam.immersivegeology.core.registration;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler;
import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler.ChemthrowerEffect;
import blusunrize.lib.manual.ManualElementItem;
import blusunrize.lib.manual.ManualElementTable;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualEntry.EntryData;
import blusunrize.lib.manual.ManualEntry.SpecialElementData;
import blusunrize.lib.manual.ManualInstance;
import blusunrize.lib.manual.Tree.InnerNode;
import com.igteam.immersivegeology.client.manual.IGRecipeOverview;
import com.igteam.immersivegeology.client.menu.multiblock.BloomeryScreen;
import com.igteam.immersivegeology.client.menu.multiblock.ReverberationScreen;
import com.igteam.immersivegeology.common.block.energypipe.IGEnergyPipeEntity;
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
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
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

                if(state.is(Blocks.GRASS_BLOCK))
                {
                    level.setBlock(loc, Blocks.DIRT.defaultBlockState(), 3);
                }

                if(state.is(Blocks.DIRT))
                {
                    level.setBlock(loc, Blocks.COARSE_DIRT.defaultBlockState(), 3);
                }
            }
        });

        IGEnergyPipeEntity.initCovers();

        IGLib.IG_LOGGER.info("Finished");
    }

    public static void registerContainersAndScreens()
    {
        MenuScreens.register(IGMenuTypes.BLOOMERY.getType(), BloomeryScreen::new);
        MenuScreens.register(IGMenuTypes.REVERBERATION_FURNACE.getType(), ReverberationScreen::new);
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
        multiblockEntry(instance, multiblock_category, "gravityseparator");
        multiblockEntry(instance, multiblock_category, "rotarykiln");
        multiblockEntry(instance, multiblock_category, "reverberation_furnace");
        multiblockEntry(instance, multiblock_category, "bloomery");
        multiblockEntry(instance, multiblock_category, "chemical_reactor");
        multiblockEntry(instance, multiblock_category, "ballmill");
        multiblockEntry(instance, multiblock_category, "centrifuge");
        multiblockEntry(instance, multiblock_category, "pelletizer");

        // Build the manual entry for the contributors
        builder.readFromFile(new ResourceLocation(IGLib.MODID, "getting_started"));
        instance.addEntry(parent_category, builder.create());

        builder.readFromFile(new ResourceLocation(IGLib.MODID, "bug_bounty_contributors"));
        instance.addEntry(parent_category, builder.create());
        InnerNode<ResourceLocation, ManualEntry> geology = parent_category.getOrCreateSubnode(new ResourceLocation(IGLib.MODID, "ig_geology"), 2);

        InnerNode<ResourceLocation, ManualEntry> overworld = geology.getOrCreateSubnode(new ResourceLocation(IGLib.MODID, "overworld"), 0);
        InnerNode<ResourceLocation, ManualEntry> nether = geology.getOrCreateSubnode(new ResourceLocation(IGLib.MODID, "nether"), 1);
        InnerNode<ResourceLocation, ManualEntry> the_end = geology.getOrCreateSubnode(new ResourceLocation(IGLib.MODID, "the_end"), 2);

        List<MaterialInterface<?>> materials = IGLib.getGeneratedMaterials();
        for(MaterialInterface<?> mineral : materials)
        {
            if(mineral.instance().acceptableStoneType(StoneEnum.MCStone)) mineralTreeEntry(instance, overworld, mineral);
            if(mineral.instance().acceptableStoneType(StoneEnum.MCNetherrack)) mineralTreeEntry(instance, nether, mineral);
            if(mineral.instance().acceptableStoneType(StoneEnum.MCEndStone)) mineralTreeEntry(instance, the_end, mineral);
        }

//        InnerNode<ResourceLocation, ManualEntry> chemical_entries = processing_chains.getOrCreateSubnode(new ResourceLocation(IGLib.MODID, "ig_chemical_chains"), 3);
    }

    private static void mineralTreeEntry(ManualInstance instance, InnerNode<ResourceLocation, ManualEntry> category, MaterialInterface<?> material)
    {
        ManualEntry.ManualEntryBuilder mineral = new ManualEntry.ManualEntryBuilder(ManualHelper.getManual());
        mineral.setLocation(new ResourceLocation(IGLib.MODID, material.getName()));
        mineral.setContent(() -> createMineralContent(material));

        instance.addEntry(category, mineral.create());
    }

    private static void metalTreeEntry(ManualInstance instance, InnerNode<ResourceLocation, ManualEntry> category, MaterialInterface<?> material)
    {
        ManualEntry.ManualEntryBuilder mineral = new ManualEntry.ManualEntryBuilder(ManualHelper.getManual());
        mineral.setLocation(new ResourceLocation(IGLib.MODID, material.getName()));
        mineral.setContent(() -> createMineralContent(material));

        instance.addEntry(category, mineral.create());
    }

    private static void chemicalTreeEntry(ManualInstance instance, InnerNode<ResourceLocation, ManualEntry> category, MaterialInterface<?> material)
    {
        ManualEntry.ManualEntryBuilder mineral = new ManualEntry.ManualEntryBuilder(ManualHelper.getManual());
        mineral.setLocation(new ResourceLocation(IGLib.MODID, material.getName()));
        mineral.setContent(() -> createMineralContent(material));

        instance.addEntry(category, mineral.create());
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
        String process_info = Component.translatable("manual.immersivegeology.generic.desc", material.getTranslationName()).getString();
        contentBuilder.append("<&item_display>").append(process_info);
        StringBuilder derivedString = new StringBuilder();
        List<MaterialInterface<?>> derivedMaterials = material.getDerivedMaterials().stream().toList();
        int size = material.getDerivedMaterials().size();
        for(int index = 0; index < size; index++)
        {
            MaterialInterface<?> derived = derivedMaterials.get(index);
            derivedString.append(derived.getTranslationName());
            if(size > 1)
            {
                if(index==(size-2))
                {
                    derivedString.append(Component.translatable("formatting.space").getString());
                    derivedString.append(Component.translatable("formatting.and").getString());
                    derivedString.append(Component.translatable("formatting.space").getString());
                }
                else if(index < size-1)
                {
                    derivedString.append(", ");
                }
            }
        }

        contentBuilder.append("<&list>");
        if(!derivedString.isEmpty())
        {
            String finalDerived = derivedString.toString();
            contentBuilder.append("<np>").append(Component.translatable("manual.immersivegeology.generic.pre_chain_desc", material.getTranslationName(), finalDerived).getString());
        }
        itemList.add(new SpecialElementData("list", 0, new ManualElementTable(ManualHelper.getManual(), formatTable(getOreConfigTable(config, material.getDefaultNoiseProbability()), ""), true)));

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

        if(material.hasFlag(BlockCategoryFlags.ORE_BLOCK))
        {
            for(StoneEnum stone : StoneEnum.values())
            {
                if(!material.instance().acceptableStoneType(stone.instance())) continue;
                if(!stone.isVanilla()) continue;
                IOreBlock ore = material.getOreBlock(stone, OreRichness.NORMAL);
                if(ore == null) continue;
                displayStacks.add(new ItemStack(ore.asItem(), 1));
            }
        }

        itemList.add(new SpecialElementData("item_display", 0, new ManualElementItem(ManualHelper.getManual(), displayStacks)));
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

    private static void multiblockEntry(ManualInstance instance, InnerNode<ResourceLocation, ManualEntry> category, String id){
        ManualEntry.ManualEntryBuilder multiblock = new ManualEntry.ManualEntryBuilder(ManualHelper.getManual());
        multiblock.readFromFile(new ResourceLocation(IGLib.MODID, id));
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
        }
    };
}
