package com.igteam.immersivegeology.common.data;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.common.data.generators.IGBlockStateProvider;
import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = IGLib.MODID, bus = Bus.MOD)
public class IGDataProvider {
    public static Logger log = LogManager.getLogger(IGLib.MODID + "/DataGenerator");

    @SubscribeEvent
    public static void generate(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();

        log.info("-===== Starting Data Generation for Immersive Geology =====-");

        if(event.includeServer()){
            generator.addProvider(true, new IGBlockStateProvider(generator, helper));
        }
    }

}
