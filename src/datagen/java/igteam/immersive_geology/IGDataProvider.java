package igteam.immersive_geology;

import com.igteam.immersive_geology.core.lib.IGLib;
import igteam.immersive_geology.generators.IGBlockStateProvider;
import igteam.immersive_geology.generators.IGItemModelProvider;
import igteam.immersive_geology.generators.recipes.IGRecipeProvider;
import igteam.immersive_geology.generators.tags.IGBlockTagProvider;
import igteam.immersive_geology.generators.tags.IGItemTagProvider;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = IGLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IGDataProvider {

    public static ExistingFileHelper EXISTING_HELPER;

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper exhelper = event.getExistingFileHelper();
        EXISTING_HELPER = exhelper;

        if(event.includeServer()){
            BlockTagsProvider blockTags = new IGBlockTagProvider(generator, exhelper);
            generator.addProvider(blockTags);
            generator.addProvider(new IGItemTagProvider(generator, blockTags, exhelper));

            generator.addProvider(new IGRecipeProvider(generator));
        }

        if(event.includeClient()){
            generator.addProvider(new IGBlockStateProvider(generator, exhelper));
            generator.addProvider(new IGItemModelProvider(generator, exhelper));
        }
    }
}

