package com.igteam.immersivegeology.common.data;

import com.igteam.immersivegeology.common.data.generators.*;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import static com.igteam.immersivegeology.core.material.GeologyMaterial.EXISTING_HELPER;

@Mod.EventBusSubscriber(modid = IGLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IGDataProvider {
    public static Logger log = LogManager.getLogger(IGLib.MODID + "/DataGenerator");

    @SubscribeEvent
    public static void generate(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        final var lookup = event.getLookupProvider();
        EXISTING_HELPER = helper;

        log.info("-===== Starting Data Generation for Immersive Geology =====-");

        if(event.includeServer()){
            IGBlockStateProvider blockStateProvider = new IGBlockStateProvider(generator, helper);
            generator.addProvider(true, blockStateProvider);
            generator.addProvider(true, new IGItemModelProvider(generator, helper));
            BlockTagsProvider blockTags = new IGBlockTags(generator.getPackOutput(), lookup, helper);
            generator.addProvider(true, blockTags);
            generator.addProvider(true, new IGItemTags(generator.getPackOutput(), lookup, blockTags.contentsGetter(), helper));
            generator.addProvider(true, new IGDynamicModelProvider(blockStateProvider, generator.getPackOutput(), helper));
            generator.addProvider(true, new IGRecipes(generator.getPackOutput()));
        }
    }

}
