package igteam.immersive_geology;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.common.block.helper.StaticTemplateManager;
import com.igteam.immersive_geology.core.lib.IGLib;
import igteam.immersive_geology.generators.IGBlockStateProvider;
import igteam.immersive_geology.generators.IGItemModelProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = IGLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IGDataProvider {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper exHelper = event.getExistingFileHelper();
        StaticTemplateManager.EXISTING_HELPER = exHelper;

        if(event.includeServer()){
            generator.addProvider(new IGBlockStateProvider(generator, exHelper));
            generator.addProvider(new IGItemModelProvider(generator, exHelper));
        }
    }
}

