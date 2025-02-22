package com.igteam.immersivegeology.core.registration;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler;
import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler.ChemthrowerEffect;
import blusunrize.immersiveengineering.client.IEManual;
import blusunrize.immersiveengineering.client.manual.IEManualInstance;
import blusunrize.immersiveengineering.client.manual.ManualElementMultiblock;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import blusunrize.immersiveengineering.common.register.IEBlocks.MetalDevices;
import blusunrize.lib.manual.ManualElementItem;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualEntry.EntryData;
import blusunrize.lib.manual.ManualEntry.SpecialElementData;
import blusunrize.lib.manual.ManualInstance;
import blusunrize.lib.manual.Tree.InnerNode;
import com.igteam.immersivegeology.client.manual.IGRecipeOverview;
import com.igteam.immersivegeology.client.menu.multiblock.BloomeryScreen;
import com.igteam.immersivegeology.client.menu.multiblock.ReverberationScreen;
import com.igteam.immersivegeology.client.menu.multiblock.SchematicsScreen;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.common.block.multiblocks.IGCoreDrillMultiblock;
import com.igteam.immersivegeology.common.block.multiblocks.IGCrystalizerMultiblock;
import com.igteam.immersivegeology.common.tag.IGTags;
import com.igteam.immersivegeology.common.world.IGWorldGen;
import com.igteam.immersivegeology.common.world.IGWorldSubscription;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeChain;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
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
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

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
        IGLib.IG_LOGGER.info("Finished");
    }

    public static void registerContainersAndScreens()
    {
        MenuScreens.register(IGMenuTypes.BLOOMERY.getType(), BloomeryScreen::new);
        MenuScreens.register(IGMenuTypes.REVERBERATION_FURNACE.getType(), ReverberationScreen::new);
        MenuScreens.register(IGMenuTypes.SCHEMATICS.getType(), SchematicsScreen::new);
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
        multiblockEntry(instance, multiblock_category, "trommel");
        multiblockEntry(instance, multiblock_category, "rotarykiln");
        multiblockEntry(instance, multiblock_category, "reverberation_furnace");
        multiblockEntry(instance, multiblock_category, "bloomery");
        multiblockEntry(instance, multiblock_category, "chemical_reactor");
        multiblockEntry(instance, multiblock_category, "ballmill");
        multiblockEntry(instance, multiblock_category, "centrifuge");
        multiblockEntry(instance, multiblock_category, "pelletizer");

        // Build the manual entry for the contributors
        builder.readFromFile(new ResourceLocation(IGLib.MODID, "bug_bounty_contributors"));
        instance.addEntry(parent_category, builder.create());

        InnerNode<ResourceLocation, ManualEntry> processing_chains = parent_category.getOrCreateSubnode(new ResourceLocation(IGLib.MODID, "ig_processing_chains"), 1);
        InnerNode<ResourceLocation, ManualEntry> metal_entries = processing_chains.getOrCreateSubnode(new ResourceLocation(IGLib.MODID, "ig_metal_chains"), 1);
        InnerNode<ResourceLocation, ManualEntry> mineral_entries = processing_chains.getOrCreateSubnode(new ResourceLocation(IGLib.MODID, "ig_mineral_chains"), 2);
        InnerNode<ResourceLocation, ManualEntry> chemical_entries = processing_chains.getOrCreateSubnode(new ResourceLocation(IGLib.MODID, "ig_chemical_chains"), 3);

        for(MineralEnum m : MineralEnum.values())  mineralTreeEntry(instance, mineral_entries, m);
        for(MetalEnum m : MetalEnum.values())  metalTreeEntry(instance, metal_entries, m);
        for(ChemicalEnum m : ChemicalEnum.values())  chemicalTreeEntry(instance, chemical_entries, m);
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

    private static void createRecipeChainPage(StringBuilder contentBuilder, ArrayList<SpecialElementData> itemList, MaterialInterface<?> material)
    {
        List<IGRecipeChain> recipe_chain_data = material.instance().getRecipeChains().stream().sorted(Comparator.comparingInt(IGRecipeChain::getPriority)).toList();
        String process_info = I18n.get("manual.immersivegeology." + material.getName() + ".desc");
        contentBuilder.append("<&item_display>").append(process_info).append("<np>");

        for(int i = 0; i < recipe_chain_data.size(); i++)
        {
            IGRecipeChain chain = recipe_chain_data.get(i);
            contentBuilder.append("<&").append(chain.getName()).append(">");
            if(i < (recipe_chain_data.size() - 1))
                contentBuilder.append("<np>");

            itemList.add(new SpecialElementData(chain.getName(), 0, new IGRecipeOverview(ManualHelper.getManual(), material.instance(), chain)));
        }

        NonNullList<ItemStack> displayStacks = NonNullList.create();

        if(material.hasFlag(BlockCategoryFlags.ORE_BLOCK))
        {
            displayStacks.add(material.getStack(ItemCategoryFlags.POOR_ORE));
            displayStacks.add(material.getStack(ItemCategoryFlags.NORMAL_ORE));
            displayStacks.add(material.getStack(ItemCategoryFlags.RICH_ORE));
        }

        if(material.hasFlag(ItemCategoryFlags.INGOT))
        {
            displayStacks.add(material.getStack(ItemCategoryFlags.INGOT));
            material.getOriginMaterials();


        }

        itemList.add(new SpecialElementData("item_display", 0, new ManualElementItem(ManualHelper.getManual(), displayStacks)));
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
