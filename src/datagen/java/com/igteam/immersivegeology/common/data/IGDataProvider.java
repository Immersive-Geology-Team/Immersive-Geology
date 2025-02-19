package com.igteam.immersivegeology.common.data;

import com.igteam.immersivegeology.common.data.generators.*;
import com.igteam.immersivegeology.common.data.generators.loot.IGBlockLootProvider;
import com.igteam.immersivegeology.common.data.generators.loot.IGLootProvider;
import com.igteam.immersivegeology.common.data.generators.manual.IGManualProvider;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
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
    public static void generate(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        PackOutput out = generator.getPackOutput();
        final var lookup = event.getLookupProvider();
        EXISTING_HELPER = helper;

        log.info("-===== Starting Data Generation for Immersive Geology =====-");
        boolean runServer = event.includeServer();

        generator.addProvider(event.includeClient(), new IGAtlasProvider(out));

        IGBlockStateProvider blockStateProvider = new IGBlockStateProvider(generator, helper);
        generator.addProvider(runServer, blockStateProvider);
        generator.addProvider(runServer, new IGItemModelProvider(generator, helper));
        generator.addProvider(runServer, new IGComplexItemModelProvider(out, helper));
        BlockTagsProvider blockTags = new IGBlockTags(out, lookup, helper);
        generator.addProvider(runServer, blockTags);
        generator.addProvider(runServer, new IGFluidTags(out, lookup, helper));
        generator.addProvider(runServer, new IGItemTags(out, lookup, blockTags.contentsGetter(), helper));
        generator.addProvider(runServer, new IGDynamicModelProvider(blockStateProvider, out, helper));
        generator.addProvider(runServer, new IGLootProvider(out));
        generator.addProvider(runServer, new IGRecipes(out));
        //generator.addProvider(runServer, new IGFeatureRemovalProvider(out));

        if(ModFlags.TFC.isStrictlyLoaded()) {
            generator.addProvider(runServer, new TFCCompatOreProvider(out));
        } else
        {
            IGLib.IG_LOGGER.error("\n============ WARNING =============\nTFC is NOT loaded, this will result in missing TFC Ore Generation\n============ WARNING =============");
        }

        // God I hate this system. ~Muddykat
        for(final DataProvider provider : IGWorldGenerationProvider.makeProviders(out, lookup, helper))
        {
            generator.addProvider(runServer, provider);
        }
    }
}
