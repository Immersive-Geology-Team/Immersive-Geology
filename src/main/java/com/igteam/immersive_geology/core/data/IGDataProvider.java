package com.igteam.immersive_geology.core.data;

import blusunrize.immersiveengineering.common.blocks.multiblocks.StaticTemplateManager;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.core.data.generators.IGBlockStateProvider;
import com.igteam.immersive_geology.core.data.generators.IGItemModelProvider;
import com.igteam.immersive_geology.core.data.generators.loot.IGLootTableProvider;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = IGLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IGDataProvider {

    public static final Logger log = ImmersiveGeology.getNewLogger();

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper exhelper = event.getExistingFileHelper();
        StaticTemplateManager.EXISTING_HELPER = exhelper;

        if(event.includeServer()){
            generator.addProvider(new IGLootTableProvider(generator));
        }

        if(event.includeClient()){
            generator.addProvider(new IGItemModelProvider(generator, exhelper));
            generator.addProvider(new IGBlockStateProvider(generator, exhelper));
        }
    }
}
