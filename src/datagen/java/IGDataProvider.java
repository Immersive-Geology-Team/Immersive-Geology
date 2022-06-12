import blusunrize.immersiveengineering.common.blocks.multiblocks.StaticTemplateManager;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import generators.IGBlockStateProvider;
import generators.IGItemModelProvider;
import generators.loot.BlockLootProvider;
import generators.recipe.IGRecipeProvider;
import generators.tags.IGBlockTagProvider;
import generators.tags.IGFluidTagProvider;
import generators.tags.IGItemTagProvider;
import igteam.immersive_geology.IGApi;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = IGLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IGDataProvider {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper exhelper = event.getExistingFileHelper();
        StaticTemplateManager.EXISTING_HELPER = exhelper;

        CapabilityFluidHandler.register();

        if(event.includeServer()){
            generator.addProvider(new BlockLootProvider(generator));
            BlockTagsProvider blockTagGen = new IGBlockTagProvider(generator, exhelper);
            generator.addProvider(blockTagGen);
            generator.addProvider(new IGItemTagProvider(generator,blockTagGen, exhelper));
            generator.addProvider(new IGFluidTagProvider(generator, exhelper));

            IGRegistrationHolder.buildRecipes(); //Only used in the Recipe Provider, so we should build them here...
            generator.addProvider(new IGRecipeProvider(generator));
        }

        if(event.includeClient()){
            generator.addProvider(new IGBlockStateProvider(generator, exhelper));
            generator.addProvider(new IGItemModelProvider(generator, exhelper));
        }
    }
}
