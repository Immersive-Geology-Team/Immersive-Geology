package com.igteam.immersivegeology.core.registration;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.client.IEManual;
import blusunrize.immersiveengineering.client.manual.IEManualInstance;
import blusunrize.immersiveengineering.client.manual.ManualElementMultiblock;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualEntry.SpecialElementData;
import blusunrize.lib.manual.ManualInstance;
import blusunrize.lib.manual.Tree.InnerNode;
import com.igteam.immersivegeology.common.block.multiblocks.IGCoreDrillMultiblock;
import com.igteam.immersivegeology.common.block.multiblocks.IGCrystalizerMultiblock;
import com.igteam.immersivegeology.common.tag.IGTags;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.ArrayList;
import java.util.List;

public class IGContent {

    public static void modContruction(IEventBus event){
        IGLib.IG_LOGGER.info("Registering Multiblocks to Immersive Engineering");
        IGMultiblockProvider.forceClassLoad();
        IGRegistrationHolder.initialize();
        IGTags.initialize();
        IGRecipeTypes.init();
    }

    public static void initializeManualEntries()
    {
        ManualInstance instance = ManualHelper.getManual();
        InnerNode<ResourceLocation, ManualEntry> parent_category = instance.getRoot().getOrCreateSubnode(new ResourceLocation(IGLib.MODID, "main"), 99);

        ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(ManualHelper.getManual());
        builder.readFromFile(new ResourceLocation(IGLib.MODID, "intro"));
        instance.addEntry(parent_category, builder.create());

        InnerNode<ResourceLocation, ManualEntry> multiblock_category = parent_category.getOrCreateSubnode(new ResourceLocation(IGLib.MODID, "ig_multiblocks"), 0);
        multiblockEntry(instance, multiblock_category, "crystallizer");
        multiblockEntry(instance, multiblock_category, "coredrill");
        multiblockEntry(instance, multiblock_category, "gravityseparator");
        multiblockEntry(instance, multiblock_category, "industrial_sluice");
        multiblockEntry(instance, multiblock_category, "rotarykiln");
        multiblockEntry(instance, multiblock_category, "reverberation_furnace");
        multiblockEntry(instance, multiblock_category, "bloomery");
        multiblockEntry(instance, multiblock_category, "chemical_reactor");
    }

    private static void multiblockEntry(ManualInstance instance, InnerNode<ResourceLocation, ManualEntry> category, String id){
        ManualEntry.ManualEntryBuilder multiblock = new ManualEntry.ManualEntryBuilder(ManualHelper.getManual());
        multiblock.readFromFile(new ResourceLocation(IGLib.MODID, id));
        instance.addEntry(category, multiblock.create());
    }

    public static void initialize(ParallelDispatchEvent event){

    }
}
