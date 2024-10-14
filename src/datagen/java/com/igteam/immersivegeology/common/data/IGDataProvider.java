package com.igteam.immersivegeology.common.data;

import com.igteam.immersivegeology.common.data.generators.*;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.Collections;
import java.util.List;

import static com.igteam.immersivegeology.core.material.GeologyMaterial.EXISTING_HELPER;

@Mod.EventBusSubscriber(modid = IGLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IGDataProvider {
    public static Logger log = LogManager.getLogger(IGLib.MODID + "/DataGenerator");

    @SubscribeEvent
    public static void generate(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        PackOutput out = generator.getPackOutput();
        final var lookup = event.getLookupProvider();
        EXISTING_HELPER = helper;
        
        log.info("-===== Starting Data Generation for Immersive Geology =====-");
        boolean runServer = event.includeServer();

        IGBlockStateProvider blockStateProvider = new IGBlockStateProvider(generator, helper);
        generator.addProvider(runServer, blockStateProvider);
        generator.addProvider(runServer, new IGItemModelProvider(generator, helper));
        generator.addProvider(runServer, new IGComplexItemModelProvider(out, helper));
        BlockTagsProvider blockTags = new IGBlockTags(out, lookup, helper);
        generator.addProvider(runServer, blockTags);
        generator.addProvider(runServer, new IGFluidTags(out, lookup, helper));
        generator.addProvider(runServer, new IGItemTags(out, lookup, blockTags.contentsGetter(), helper));
        generator.addProvider(runServer, new IGDynamicModelProvider(blockStateProvider, out, helper));
        generator.addProvider(runServer, new LootTableProvider(out, Collections.emptySet(), List.of(new SubProviderEntry(IGBlockLootProvider::new, LootContextParamSets.BLOCK))));
        generator.addProvider(runServer, new IGRecipes(out));

    }

}
