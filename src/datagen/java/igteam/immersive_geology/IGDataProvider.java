package igteam.immersive_geology;

import blusunrize.immersiveengineering.api.EnumMetals;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.material.MetalEnum;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = IGLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IGDataProvider {

    public static ExistingFileHelper EXISTING_HELPER;
    public static Logger logger = ImmersiveGeology.getNewLogger();
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper exhelper = event.getExistingFileHelper();
        EXISTING_HELPER = exhelper;

        if(event.includeServer()){

        }

        if(event.includeClient()){

        }
    }
}

