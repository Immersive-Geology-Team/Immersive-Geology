package com.igteam.immersivegeology.common.data;

import com.igteam.immersivegeology.common.data.generators.IGItemModelProvider;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.common.data.generators.IGBlockStateProvider;
import net.minecraft.data.DataGenerator;
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
        EXISTING_HELPER = helper;

        log.info("-===== Starting Data Generation for Immersive Geology =====-");

        if(event.includeServer()){
            generator.addProvider(true, new IGBlockStateProvider(generator, helper));
            generator.addProvider(true, new IGItemModelProvider(generator, helper));
        }
    }

}
